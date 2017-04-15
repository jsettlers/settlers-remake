package jsettlers.logic.movable;

import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.AttackableComponent;
import jsettlers.logic.movable.components.BearerBehaviorComponent;
import jsettlers.logic.movable.components.BearerComponent;
import jsettlers.logic.movable.components.BehaviorComponent;
import jsettlers.logic.movable.components.DonkeyBehaviorComponent;
import jsettlers.logic.movable.components.DonkeyComponent;
import jsettlers.logic.movable.components.GameFieldComponent;
import jsettlers.logic.movable.components.GeologistBehaviorComponent;
import jsettlers.logic.movable.components.MaterialComponent;
import jsettlers.logic.movable.components.MovableComponent;
import jsettlers.logic.movable.components.MultiMaterialComponent;
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
            //case GEOLOGIST:
            //case BEARER:
            case DONKEY:
                return new MovableWrapper(CreateEntity(grid, movableType, position, player));
            default:
                return new Movable(grid, movableType, position, player);
        }
    }

    public static Entity CreateEntity(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = null;
        switch (movableType) {
            //case BEARER:
            //case GEOLOGIST:
            case DONKEY:
                entity = CreateDonkey(grid, movableType, position, player);
                break;
        }
        assert entity != null: "Type not found by EntityFactory";
        assert entity.checkComponentDependencies(): "Not all Component dependencies are resolved.";
        entity.setActive(true);
        return entity;
    }

    public static Entity CreateDonkey(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = new Entity();
        entity.add(new MultiMaterialComponent());
        entity.add(new DonkeyBehaviorComponent());
        entity.add(new AnimationComponent());
        entity.add(new AttackableComponent());
        EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
        entity.add(new MovableComponent(movableType, player, position, dir));
        entity.add(new SteeringComponent());
        entity.add(new GameFieldComponent(grid));
        entity.add(new DonkeyComponent());
        return entity;
    }

    public static Entity CreateGeologist(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = new Entity();
        entity.add(new GeologistBehaviorComponent());
        entity.add(new SpecialistComponent());
        entity.add(new AnimationComponent());
        entity.add(new AttackableComponent());
        entity.add(new MaterialComponent());
        EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
        entity.add(new MovableComponent(movableType, player, position, dir));
        entity.add(new SteeringComponent());
        entity.add(new GameFieldComponent(grid));
        return entity;
    }

    public static Entity CreateBearer(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
        Entity entity = new Entity();
        entity.add(new BearerBehaviorComponent());
        entity.add(new BearerComponent());
        entity.add(new AnimationComponent());
        entity.add(new MaterialComponent());
        EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
        entity.add(new MovableComponent(movableType, player, position, dir));
        entity.add(new SteeringComponent());
        entity.add(new GameFieldComponent(grid));
        return entity;
    }
}