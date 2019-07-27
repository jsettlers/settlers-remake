package jsettlers.logic.movable;

import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.AttackableComponent;
import jsettlers.logic.movable.components.BearerBehaviorComponent;
import jsettlers.logic.movable.components.BearerComponent;
import jsettlers.logic.movable.components.DonkeyBehaviorComponent;
import jsettlers.logic.movable.components.DonkeyComponent;
import jsettlers.logic.movable.components.GameFieldComponent;
import jsettlers.logic.movable.components.GeologistBehaviorComponent;
import jsettlers.logic.movable.components.MaterialComponent;
import jsettlers.logic.movable.components.MovableComponent;
import jsettlers.logic.movable.components.MultiMaterialComponent;
import jsettlers.logic.movable.components.PlayerComandComponent;
import jsettlers.logic.movable.components.SelectableComponent;
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

	public static ILogicMovable createMovable(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		switch (movableType) {
			case BEARER:
			case GEOLOGIST:
			case DONKEY:
				return new MovableWrapper(createEntity(grid, movableType, position, player));
			default:
				return new Movable(grid, movableType, position, player);
		}
	}

	static Entity createEntity(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		Entity entity = null;
		switch (movableType) {
			case BEARER:
				entity = createBearer(grid, movableType, position, player);
				break;
			case GEOLOGIST:
				entity = createGeologist(grid, movableType, position, player);
				break;
			case DONKEY:
				entity = createDonkey(grid, movableType, position, player);
				break;
		}
		assert entity != null : "Type not found by EntityFactory";
		assert entity.checkComponentDependencies() : "Not all Component dependencies are resolved.";
		entity.setActive(true);
		return entity;
	}

	private static Entity createDonkey(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		Entity entity = new Entity();
		entity.add(new MultiMaterialComponent());
		entity.add(new DonkeyBehaviorComponent());
		entity.add(new AnimationComponent());
		entity.add(new AttackableComponent(movableType));
		EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
		entity.add(new MovableComponent(movableType, player, position, dir));
		entity.add(new SteeringComponent());
		entity.add(new GameFieldComponent(grid));
		entity.add(new DonkeyComponent());
		entity.add(new SelectableComponent(ESelectionType.PEOPLE));
		return entity;
	}

	public static Entity createGeologist(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		Entity entity = new Entity();
		entity.add(new GeologistBehaviorComponent());
		entity.add(new SpecialistComponent());
		entity.add(new AnimationComponent());
		entity.add(new AttackableComponent(movableType));
		entity.add(new MaterialComponent());
		EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
		entity.add(new MovableComponent(movableType, player, position, dir));
		entity.add(new SteeringComponent());
		entity.add(new GameFieldComponent(grid));
		entity.add(new SelectableComponent(ESelectionType.SPECIALISTS));
		entity.add(new PlayerComandComponent());
		return entity;
	}

	public static Entity createBearer(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
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