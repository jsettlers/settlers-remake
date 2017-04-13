package jsettlers.logic.movable;

import jsettlers.algorithms.path.Path;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.position.MutablePoint2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.MutableDouble;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.GameFieldComponent;
import jsettlers.logic.movable.components.MovableComponent;
import jsettlers.logic.movable.components.PlayerCmdComponent;
import jsettlers.logic.movable.components.SpecialistComponent;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.movable.simplebehaviortree.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.MemSequence;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.movable.simplebehaviortree.nodes.WaitFor;

/**
 * @author homoroselaps
 */

public abstract class GeologistBehaviorTreeFactory extends BehaviorTreeFactory {
    private static Action<Entity> WorkOnPosIfPossible() {
        return new Action<>(entity -> {
            ShortPoint2D pos = entity.movC().getPos();

            if (entity.specC().getCenterOfWork() == null) {
                entity.specC().setCenterOfWork(pos);
            }

            entity.gameC().getMovableGrid().setMarked(pos, false); // unmark the pos for the following check
            boolean canWorkOnPos =  entity.gameC().getMovableGrid().fitsSearchType(entity.movC(), pos.x, pos.y, ESearchType.RESOURCE_SIGNABLE);

            if (canWorkOnPos) {
                entity.gameC().getMovableGrid().setMarked(pos, true);
                return NodeStatus.Success;
            }
            return NodeStatus.Failure;
        }
        );
    }

    private static Action<Entity> MoveToCommandPos() {
        return new Action<>(t->{
            PlayerCmdComponent.LeftClickCommand cmd = t.getNotificationsIt(PlayerCmdComponent.LeftClickCommand.class).next();
            if (cmd == null) return NodeStatus.Failure;
            t.get(SteeringComponent.class).goToPos(cmd.pos);
            return NodeStatus.Success;
        });
    }

    private static class Find_GoToWorkablePosition extends Action<Entity> {

        public Find_GoToWorkablePosition() {
            super(Find_GoToWorkablePosition::run);
        }

        private static ShortPoint2D getCloseWorkablePos(Entity target) {
            MovableComponent movC = target.get(MovableComponent.class);
            GameFieldComponent gameC = target.get(GameFieldComponent.class);
            SpecialistComponent workC = target.get(SpecialistComponent.class);

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
            SpecialistComponent workC = target.get(SpecialistComponent.class);
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

    public static Root<Entity> create() {
        final float ACTION1_DURATION = 1.4f;
        final float ACTION2_DURATION = 1.5f;

        return new Root<>(new Selector<>(
                TriggerGuard(PlayerCmdComponent.LeftClickCommand.class,
                        new MemSequence<>(
                                new Action<>(e->{e.get(SpecialistComponent.class).setIsWorking(false);}),
                                MoveToCommandPos(),
                                WaitForTargetReached_FailIfNot()
                        )
                ),
                TriggerGuard(PlayerCmdComponent.AltLeftClickCommand.class,
                        new MemSequence<>(
                                MoveToCommandPos(),
                                WaitForTargetReached_FailIfNot(),
                                new Action<>(e->{e.get(SpecialistComponent.class).setIsWorking(true);})
                        )
                ),
                TriggerGuard(PlayerCmdComponent.StartWorkCommand.class,
                        new Action<>(e->{e.get(SpecialistComponent.class).setIsWorking(true);})
                ),
                new Guard<>(e->e.get(SpecialistComponent.class).isWorking(), true,
                        new Selector<>(
                                // find a place and work there
                                new MemSequence<>(
                                        new Find_GoToWorkablePosition(),
                                        WaitForTargetReached_FailIfNot(),
                                        WorkOnPosIfPossible(),
                                        StartAnimation(EMovableAction.ACTION1, ACTION1_DURATION),
                                        new WaitFor(AnimationComponent.AnimationFinishedTrigger.class),
                                        StartAnimation(EMovableAction.ACTION2, ACTION2_DURATION),
                                        new WaitFor(AnimationComponent.AnimationFinishedTrigger.class)
                                ),
                                // stop working if no work could be found
                                new Action<>(e->{e.get(SpecialistComponent.class).setIsWorking(false);})
                        )
                )
        ));
    }
}
