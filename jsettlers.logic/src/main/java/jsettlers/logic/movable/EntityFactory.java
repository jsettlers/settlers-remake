package jsettlers.logic.movable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.movable.components.AnimationComponent;
import jsettlers.logic.movable.components.AttackableComponent;
import jsettlers.logic.movable.components.BearerBehaviorComponent;
import jsettlers.logic.movable.components.BearerComponent;
import jsettlers.logic.movable.components.BricklayerBehaviorComponent;
import jsettlers.logic.movable.components.BricklayerComponent;
import jsettlers.logic.movable.components.BuildingWorkerBehaviorComponent;
import jsettlers.logic.movable.components.BuildingWorkerComponent;
import jsettlers.logic.movable.components.DonkeyBehaviorComponent;
import jsettlers.logic.movable.components.DonkeyComponent;
import jsettlers.logic.movable.components.GameFieldComponent;
import jsettlers.logic.movable.components.GeologistBehaviorComponent;
import jsettlers.logic.movable.components.MarkedPositonComponent;
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
			//case BEARER:
			case BRICKLAYER:
			case GEOLOGIST:
			case SMITH:
			case LUMBERJACK:
			case STONECUTTER:
			case SAWMILLER:
			case FORESTER:
			case MELTER:
			case MINER:
			case FISHERMAN:
			case FARMER:
			case MILLER:
			case BAKER:
			case PIG_FARMER:
			case DONKEY_FARMER:
			case SLAUGHTERER:
			case CHARCOAL_BURNER:
			case WATERWORKER:
			case WINEGROWER:
			case HEALER:
			case DOCKWORKER:
			case DONKEY:
				return new MovableWrapper(createActiveEntity(grid, movableType, position, player));
			default:
				return new Movable(grid, movableType, position, player);
		}
	}

	private static Entity createActiveEntity(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		Entity entity = createEntity(grid, movableType, position, player);
		entity.setActive(true);
		return entity;
	}

	public static Entity createEntity(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		Entity entity = null;
		switch (movableType) {
			case BEARER:
				entity = createBearer(grid, movableType, position, player);
				break;
			case SMITH:
			case LUMBERJACK:
			case STONECUTTER:
			case SAWMILLER:
			case FORESTER:
			case MELTER:
			case MINER:
			case FISHERMAN:
			case FARMER:
			case MILLER:
			case BAKER:
			case PIG_FARMER:
			case DONKEY_FARMER:
			case SLAUGHTERER:
			case CHARCOAL_BURNER:
			case WATERWORKER:
			case WINEGROWER:
			case HEALER:
			case DOCKWORKER:
				entity = createBuildingWorker(grid, movableType, position, player);
				break;
			case GEOLOGIST:
				entity = createGeologist(grid, movableType, position, player);
				break;
			case DONKEY:
				entity = createDonkey(grid, movableType, position, player);
				break;
			case BRICKLAYER:
				entity = createBricklayer(grid, movableType, position, player);
				break;
		}
		assert entity != null : "Type not found by EntityFactory";
		assert entity.checkComponentDependencies() : "Not all Component dependencies are resolved.";
		return entity;
	}

	private static Entity createBricklayer(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		Entity entity = new Entity();
		entity.add(new BricklayerComponent());
		entity.add(new BricklayerBehaviorComponent());
		entity.add(new AnimationComponent());
		EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
		entity.add(new MovableComponent(movableType, player, position, dir));
		entity.add(new SteeringComponent());
		entity.add(new GameFieldComponent(grid));
		entity.add(new SelectableComponent(ESelectionType.PEOPLE));
		return entity;
	}

	private static Entity createBuildingWorker(AbstractMovableGrid grid, EMovableType movableType, ShortPoint2D position, Player player) {
		Entity entity = new Entity();
		entity.add(new MaterialComponent());
		entity.add(new BuildingWorkerComponent());
		entity.add(new BuildingWorkerBehaviorComponent());
		entity.add(new AnimationComponent());
		EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
		entity.add(new MovableComponent(movableType, player, position, dir));
		entity.add(new SteeringComponent());
		entity.add(new GameFieldComponent(grid));
		entity.add(new SelectableComponent(ESelectionType.PEOPLE));
		entity.add(new MarkedPositonComponent());
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
		entity.add(new MarkedPositonComponent());
		EDirection dir = EDirection.VALUES[MatchConstants.random().nextInt(EDirection.NUMBER_OF_DIRECTIONS)];
		entity.add(new MovableComponent(movableType, player, position, dir));
		entity.add(new SteeringComponent());
		entity.add(new GameFieldComponent(grid));
		entity.add(new SelectableComponent(movableType.selectionType));
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
        entity.add(new SelectableComponent(movableType.selectionType));
		return entity;
	}
}