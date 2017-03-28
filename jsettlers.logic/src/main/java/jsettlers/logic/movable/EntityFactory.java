package jsettlers.logic.movable;

import com.sun.net.httpserver.Authenticator;

import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
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
        @Override
        public NodeStatus run(Entity target) {
            return NodeStatus.Success;
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
