package jsettlers.logic.movable;

import java.util.Dictionary;

import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;
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
        entity.add(new BehaviorComponent(BehaviorTreeFactory.CreateGeologistBehaviorTree(entity)));
        entity.add(new MaterialComponent());
        EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
        entity.add(new MovableComponent(movableType, player, position, dir));

        return new MovableWrapper(entity);
    }

    private static ILogicMovable CreateBearer(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = new Entity();
        return new MovableWrapper(entity);
    }
}