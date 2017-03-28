package jsettlers.logic.movable;

import jsettlers.algorithms.path.Path;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.MutablePoint2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.mutables.MutableDouble;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.simplebehaviortree.nodes.Action;
import jsettlers.logic.movable.simplebehaviortree.nodes.Guard;
import jsettlers.logic.movable.simplebehaviortree.nodes.INodeAction;
import jsettlers.logic.movable.simplebehaviortree.nodes.INodeCondition;
import jsettlers.logic.movable.simplebehaviortree.nodes.NodeStatus;
import jsettlers.logic.movable.simplebehaviortree.nodes.Root;
import jsettlers.logic.movable.simplebehaviortree.nodes.Selector;
import jsettlers.logic.player.Player;

/**
 * Created by jt-1 on 3/28/2017.
 */

public final class EntityFactory {
    private EntityFactory() {}

    public static Movable2 CreateMovable(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        switch (movableType) {
            case GEOLOGIST:
                return CreateGeologist(grid, movableType, position, player);
            default:
                return new Movable2();
        }
    }

    private static Movable2 CreateGeologist(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Movable2 entity = new Movable2();
        entity.add(new AnimationComponent());
        entity.add(new AttackableComponent(false));
        entity.add(new BehaviorComponent(CreateGeologistBehaviorTree()));
        entity.add(new MaterialComponent());
        EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
        entity.add(new MovableComponent(movableType, player.playerId, position, dir));


        return entity;
    }

    static class isWorking implements INodeCondition<Entity> {
        @Override
        public boolean run(Entity target) { return target.get(WorkComponent.class).isWorking(); }
    }

    static class FindAndGoToWorkablePosition implements INodeAction<Entity> {

        private ShortPoint2D getCloseWorkablePos(Entity target) {
            MovableComponent movC = target.get(MovableComponent.class);
            GameFieldComponent gameC = target.get(GameFieldComponent.class);
            WorkComponent workC = target.get(WorkComponent.class);

            MutablePoint2D bestNeighbourPos = new MutablePoint2D(-1, -1);
            MutableDouble bestNeighbourDistance = new MutableDouble(Double.MAX_VALUE); // distance from start point

            HexGridArea.streamBorder(movC.getPos(), 2).filter((x, y) -> {
                    boolean isValidPosition = gameC.getGrid().isValidPosition(movC, x, y);
                    boolean canWorkOnPos = gameC.getGrid().fitsSearchType(movC, x, y, ESearchType.RESOURCE_SIGNABLE);
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

        @Override
        public NodeStatus run(Entity target) {
            MovableComponent movC = target.get(MovableComponent.class);
            GameFieldComponent gameC = target.get(GameFieldComponent.class);
            WorkComponent workC = target.get(WorkComponent.class);
            SteeringComponent steerC = target.get(SteeringComponent.class);

            ShortPoint2D closeWorkablePos = getCloseWorkablePos(target);

            if (closeWorkablePos != null && steerC.goToPos(closeWorkablePos)) {
                gameC.getGrid().setMarked(closeWorkablePos, true);
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

    private static Root CreateGeologistBehaviorTree() {
        new Root(new Selector(
            new Guard(new isWorking(), true, new Action(
                new FindAndGoToWorkablePosition()
            ))
        ));

        return null;
    }
}
