package jsettlers.logic.movable;

import java.util.function.Consumer;

import jsettlers.algorithms.path.Path;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.MutablePoint2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.MutableDouble;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.movable.simplebehaviortree.Node;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Failer;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSequence;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.movable.simplebehaviortree.nodes.Sequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.WaitFor;
import jsettlers.logic.player.Player;

/**
 * Created by jt-1 on 3/28/2017.
 */

public final class EntityFactory {
    private EntityFactory() {}

    public static ILogicMovable CreateMovable(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        switch (movableType) {
            case GEOLOGIST:
                return CreateGeologist(grid, movableType, position, player);
            default:
                return new Movable(grid, movableType, position, player);
        }
    }

    private static ILogicMovable CreateGeologist(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = new Entity();
        entity.add(new AnimationComponent());
        entity.add(new AttackableComponent(false));
        entity.add(new BehaviorComponent(CreateGeologistBehaviorTree(entity)));
        entity.add(new MaterialComponent());
        EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
        entity.add(new MovableComponent(movableType, player, position, dir));


        return new MovableWrapper(entity);
    }

    private static class FindAndGoToWorkablePosition extends Action<Entity> {

        public FindAndGoToWorkablePosition() {
            super(FindAndGoToWorkablePosition::run);
        }

        private static ShortPoint2D getCloseWorkablePos(Entity target) {
            MovableComponent movC = target.get(MovableComponent.class);
            GameFieldComponent gameC = target.get(GameFieldComponent.class);
            WorkComponent workC = target.get(WorkComponent.class);

            MutablePoint2D bestNeighbourPos = new MutablePoint2D(-1, -1);
            MutableDouble bestNeighbourDistance = new MutableDouble(Double.MAX_VALUE); // distance from start point

            HexGridArea.streamBorder(movC.getPos(), 2).filter((x, y) -> {
                        boolean isValidPosition = gameC.getMovableGrid().isValidPosition(movC, x, y);
                        boolean canWorkOnPos = gameC.getMovableGrid().fitsSearchType(movC, x, y, ESearchType.RESOURCE_SIGNABLE);
                        return isValidPosition && canWorkOnPos;
                    }
            ).forEach((x, y) -> {
                double distance = ShortPoint2D.getOnGridDist(x - workC.getCenterOfWork().x, y - workC.getCenterOfWork().y);
                if (distance < bestNeighbourDistance.value) {
                    bestNeighbourDistance.value = distance;
                    bestNeighbourPos.x = x;
                    bestNeighbourPos.y = y;
                }
            });

            if (bestNeighbourDistance.value != Double.MAX_VALUE) {
                return bestNeighbourPos.createShortPoint2D();
            } else {
                return null;
            }
        }

        public static NodeStatus run(Entity target) {
            MovableComponent movC = target.get(MovableComponent.class);
            GameFieldComponent gameC = target.get(GameFieldComponent.class);
            WorkComponent workC = target.get(WorkComponent.class);
            SteeringComponent steerC = target.get(SteeringComponent.class);

            ShortPoint2D closeWorkablePos = getCloseWorkablePos(target);

            if (closeWorkablePos != null && steerC.goToPos(closeWorkablePos)) {
                gameC.getMovableGrid().setMarked(closeWorkablePos, true);
                return NodeStatus.Success;
            }
            workC.setCenterOfWork(null);

            ShortPoint2D pos = movC.getPos();
            Path path = steerC.preSearchPath(true, pos.x, pos.y, (short) 30, ESearchType.RESOURCE_SIGNABLE);
            if (path != null) {
                steerC.followPath(path);
                return NodeStatus.Success;
            }

            return NodeStatus.Failure;
        }
    }

    private static Guard<Entity> TriggerGuard(Class<? extends Notification> type, Node<Entity> child) {
        return new Guard<Entity>(entity -> entity.getNotificationsIt(type).hasNext(), true, child);
    }

    private static Action<Entity> StartAnimation(EMovableAction animation, float duration) {
        return new Action<Entity>(t->{ t.get(AnimationComponent.class).startAnimation(animation, duration); });
    }

    private static Action<Entity> WorkOnPosIfPossible() {
        return new Action<Entity>(entity -> {
            MovableComponent movC = entity.get(MovableComponent.class);
            WorkComponent workC = entity.get(WorkComponent.class);
            GameFieldComponent gameC = entity.get(GameFieldComponent.class);
            AnimationComponent aniC = entity.get(AnimationComponent.class);

            ShortPoint2D pos = movC.getPos();


            if (workC.getCenterOfWork() == null) {
                workC.setCenterOfWork(pos);
            }

            gameC.getMovableGrid().setMarked(pos, false); // unmark the pos for the following check
            boolean canWorkOnPos =  gameC.getMovableGrid().fitsSearchType(movC, pos.x, pos.y, ESearchType.RESOURCE_SIGNABLE);

            if (canWorkOnPos) {
                gameC.getMovableGrid().setMarked(pos, true);
                return NodeStatus.Success;
            }
            return NodeStatus.Failure;
        }
    );
    }

    private static Action<Entity> MoveToCommandPos() {
        return new Action<Entity>(t->{
            PlayerCommandComponent.MoveToCommand cmd = t.getNotificationsIt(PlayerCommandComponent.MoveToCommand.class).next();
            if (cmd == null) return NodeStatus.Failure;
            t.get(SteeringComponent.class).goToPos(cmd.pos);
            return NodeStatus.Success;
        });
    }

    private static Root<Entity> CreateGeologistBehaviorTree(Entity target) {
        final float ACTION1_DURATION = 1.4f;
        final float ACTION2_DURATION = 1.5f;

        int x = 0;
        Consumer<Integer> c = (i) -> { System.out.println(x); };
        c.accept(3);
        new Root<Entity>(new Selector(
            TriggerGuard(PlayerCommandComponent.MoveToCommand.class,
                new MemSequence<>(
                    new Action<Entity>(t->{t.get(WorkComponent.class).setIsWorking(false);})
                    MoveToCommandPos(),
                    new WaitFor(AnimationComponent.AnimationFinishedTrigger.class)
                )
            ),
            TriggerGuard(PlayerCommandComponent.MoveToAndWorkCommand.class,
                new MemSequence<>(
                    MoveToCommandPos(),
                    new WaitFor(AnimationComponent.AnimationFinishedTrigger.class),
                    new Action<Entity>(t->{t.get(WorkComponent.class).setIsWorking(true);})
                )
            ),
            TriggerGuard(PlayerCommandComponent.StartWorkCommand.class,
                new Action<Entity>(t->{t.get(WorkComponent.class).setIsWorking(true);})
            ),
            new Guard<Entity>(t->t.get(WorkComponent.class).isWorking(), true,
                    new Selector<>(
                            // find a place and work there
                            new MemSequence(
                                    new FindAndGoToWorkablePosition(),
                                    // wait for targetReached, but abort if target not reachable
                                    new Sequence(
                                            TriggerGuard(SteeringComponent.TargetNotReachedTrigger.class, new Failer()),
                                            new WaitFor(SteeringComponent.TargetReachedTrigger.class)
                                    ),
                                    WorkOnPosIfPossible(),
                                    StartAnimation(EMovableAction.ACTION1, ACTION1_DURATION),
                                    new WaitFor(AnimationComponent.AnimationFinishedTrigger.class),
                                    StartAnimation(EMovableAction.ACTION2, ACTION2_DURATION),
                                    new WaitFor(AnimationComponent.AnimationFinishedTrigger.class)
                            ),
                            // stop working if no work could be found
                            new Action<Entity>(t->{t.get(WorkComponent.class).setIsWorking(false);})
                    )
            )
        ));

        return null;
    }
}
