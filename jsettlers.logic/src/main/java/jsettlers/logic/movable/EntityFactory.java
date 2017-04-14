package jsettlers.logic.movable;

import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.AttackableComponent;
import jsettlers.logic.movable.components.BearerComponent;
import jsettlers.logic.movable.components.BehaviorComponent;
import jsettlers.logic.movable.components.DonkeyComponent;
import jsettlers.logic.movable.components.MaterialComponent;
import jsettlers.logic.movable.components.MovableComponent;
import jsettlers.logic.movable.components.SpecialistComponent;
import jsettlers.logic.movable.components.SteeringComponent;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.player.Player;

/**
 * @author homoroselaps
 */

public final class EntityFactory {
    private EntityFactory() {}

    public static ILogicMovable CreateMovable(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        switch (movableType) {
            case GEOLOGIST:
            case BEARER:
                return new MovableWrapper(CreateEntity(grid, movableType, position, player));
            default:
                return new Movable(grid, movableType, position, player);
        }
    }

    public static Entity CreateEntity(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = null;
        switch (movableType) {
            case BEARER:
                entity = CreateBearer(grid, movableType, position, player);
                break;
            case GEOLOGIST:
                entity = CreateGeologist(grid, movableType, position, player);
                break;
        }
        assert entity != null: "Type not found by EntityFactory";
        assert entity.checkComponentDependencies(): "Not all Component dependencies are resolved.";
        return null;
    }

    public static Entity CreateDonkey(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = new Entity();
        entity.add(new BehaviorComponent(DonkeyBehaviorTreeFactory.create()));
        entity.add(new AnimationComponent());
        entity.add(new MaterialComponent());
        entity.add(new AttackableComponent());
        EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
        entity.add(new MovableComponent(movableType, player, position, dir));
        entity.add(new SteeringComponent());
        entity.add(new DonkeyComponent());
        return entity;
    }

    public static Entity CreateGeologist(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = new Entity();
        entity.add(new BehaviorComponent(GeologistBehaviorTreeFactory.create()));
        entity.add(new SpecialistComponent());
        entity.add(new AnimationComponent());
        entity.add(new AttackableComponent());
        entity.add(new MaterialComponent());
        EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
        entity.add(new MovableComponent(movableType, player, position, dir));
        entity.add(new SteeringComponent());
        return entity;
    }

    public static Entity CreateBearer(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = new Entity();
        entity.add(new BehaviorComponent(BearerBehaviorTreeFactory.create()));
        entity.add(new BearerComponent());
        entity.add(new AnimationComponent());
        entity.add(new MaterialComponent());
        EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
        entity.add(new MovableComponent(movableType, player, position, dir));
        entity.add(new SteeringComponent());
        return entity;
    }
}