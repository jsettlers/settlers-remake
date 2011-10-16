package jsettlers.logic.buildings;

import java.util.Collections;
import java.util.LinkedList;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.RelativeBricklayer;
import jsettlers.common.buildings.RelativeStack;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapCircleBorder;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.buildings.spawn.BigLivinghouse;
import jsettlers.logic.buildings.spawn.MediumLivinghouse;
import jsettlers.logic.buildings.spawn.SmallLivinghouse;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.management.workers.construction.IConstructableBuilding;
import jsettlers.logic.map.newGrid.interfaces.IHexMovable;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.Timer100Milli;
import random.RandomSingleton;

public abstract class Building implements IConstructableBuilding, IPlayerable, IBuilding, ITimerable {
	private final byte player;
	private EBuildingState state = EBuildingState.CREATED;

	private ISPosition2D pos;
	private ISPosition2D door;
	private boolean selected;
	protected IBuildingsGrid grid;

	private float constructionProgress = 0.0f;
	private FreeMapArea buildingArea;
	private byte heightAvg;

	private short delayCtr = 0;
	private final EBuildingType type;

	protected Building(EBuildingType type, byte player) {
		this.type = type;
		this.player = player;
	}

	public final void constructAt(IBuildingsGrid grid, ISPosition2D pos) {
		assert state == EBuildingState.CREATED : "building can not be positioned in this state";

		boolean itWorked = positionAt(grid, pos);

		if (itWorked) {
			stacks = createStacks(true);
			placeStacks(stacks, true);

			placeAdditionalMapObjects(grid, pos, true);

			this.state = EBuildingState.POSITIONED;

			requestDiggers();
		}
	}

	private void placeAdditionalMapObjects(IBuildingsGrid grid, ISPosition2D pos, boolean place) {
		if (place) {
			grid.getMapObjectsManager().addSimpleMapObject(pos, EMapObjectType.BUILDINGSITE_SIGN, false, (byte) -1);
		} else {
			grid.getMapObjectsManager().removeMapObjectType(pos, EMapObjectType.BUILDINGSITE_SIGN);
		}

		for (RelativePoint curr : type.getBuildmarks()) {
			if (place) {
				grid.getMapObjectsManager().addSimpleMapObject(curr.calculatePoint(pos), EMapObjectType.BUILDINGSITE_POST, false, (byte) -1);
			} else {
				grid.getMapObjectsManager().removeMapObjectType(curr.calculatePoint(pos), EMapObjectType.BUILDINGSITE_POST);
			}
		}
	}

	private boolean positionAt(IBuildingsGrid grid, ISPosition2D pos) {
		boolean couldBePlaced = grid.setBuilding(pos, this);
		if (couldBePlaced) {
			this.pos = pos;
			this.grid = grid;

			this.door = getBuildingType().getDoorTile().calculatePoint(pos);

			Timer100Milli.add(this);

			if (getFlagType() == EMapObjectType.FLAG_DOOR) {
				placeFlag(true);
			}

			positionedEvent(pos);
		}
		return couldBePlaced;
	}

	protected void placeFlag(boolean place) {
		ISPosition2D flagPosition = type.getFlag().calculatePoint(pos);

		if (place) {
			grid.getMapObjectsManager().addSimpleMapObject(flagPosition, getFlagType(), false, player);
		} else {
			grid.getMapObjectsManager().removeMapObjectType(flagPosition, getFlagType());
		}
	}

	protected abstract void positionedEvent(ISPosition2D pos);

	public final void appearAt(IBuildingsGrid grid, ISPosition2D pos) {
		this.state = EBuildingState.CONSTRUCTED;

		positionAt(grid, pos);

		finishConstruction();
	}

	private void requestDiggers() {
		RelativePoint[] blocked = getBuildingType().getBlockedTiles();
		LinkedList<ISPosition2D> positions = new LinkedList<ISPosition2D>();
		int heightSum = 0;

		for (RelativePoint curr : blocked) {
			ISPosition2D currPos = curr.calculatePoint(this.pos);
			positions.add(currPos);
			heightSum += this.grid.getHeightAt(currPos);
		}

		Collections.shuffle(positions, RandomSingleton.get());

		this.buildingArea = new FreeMapArea(positions);

		this.heightAvg = (byte) (heightSum / blocked.length);
		int numberOfDiggers = (int) Math.ceil(((float) blocked.length) / Constants.TILES_PER_DIGGER);

		for (int i = 0; i < numberOfDiggers; i++)
			GameManager.requestDigger(this.buildingArea, this.heightAvg, this.player);
	}

	private boolean isFlatened() {
		for (ISPosition2D pos : buildingArea) {
			if (grid.getHeightAt(pos) != heightAvg) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void timerEvent() {
		switch (state) {
		case CREATED:
			assert false : "this should never happen!";
			break;
		case POSITIONED:
			if (waitedSecond()) {
				if (isFlatened()) {
					placeAdditionalMapObjects(grid, pos, false);

					this.state = EBuildingState.WAITING_FOR_MATERIAL;
					System.out.println("flatened!");
				}
			}
			break;

		case WAITING_FOR_MATERIAL:
			if (waitedSecond()) {
				if (isMaterialAvailable()) {
					requestBricklayers();
					state = EBuildingState.BRICKLAYERS_REQUESTED;
				}
			}
			break;

		case BRICKLAYERS_REQUESTED:
			// the state changes are handled by tryToTakeMaterial()
			break;

		case CONSTRUCTED:
			if (door != null) {
				IHexMovable movableAtDoor = grid.getMovable(door);
				if (movableAtDoor != null) {
					movableAtDoor.push(null);
				}
			}
			subTimerEvent();
			break;

		case DESTROYED:

			break;
		}
	}

	protected abstract void subTimerEvent();

	private void requestBricklayers() {
		RelativeBricklayer[] bricklayers = type.getBricklayers();
		for (RelativeBricklayer curr : bricklayers) {
			GameManager.requestBricklayer(this, curr.getPosition().calculatePoint(pos), curr.getDirection());
		}
	}

	private boolean isMaterialAvailable() {
		return getStackWithMaterial() != null;
	}

	private AbstractStack getStackWithMaterial() {
		for (AbstractStack curr : stacks) {
			if (!curr.isEmpty()) {
				return curr;
			}
		}
		return null;
	}

	private boolean areAllStacksFullfilled() {
		for (AbstractStack curr : stacks) {
			if (!curr.isFulfilled()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * creates the request stacks
	 * 
	 * @param constructionStacks
	 *            if true, only the build stacks will be placed<br>
	 *            if false, only the normal stacks needed for working will be placed<br>
	 */
	private void createStacks(boolean constructionStacks) {
		LinkedList<AbstractStack> result = new LinkedList<AbstractStack>();

		for (RelativeStack curr : getBuildingType().getRequestStacks()) {
			ISPosition2D stackPos = curr.calculatePoint(pos);

			short requiredForBuild = curr.requiredForBuild();
			if (!constructionStacks && requiredForBuild == 0) {
				result.add(new SingleMaterialStack(curr.getType(), stackPos, EStackType.REQUEST, player));
			}
			if (constructionStacks && requiredForBuild > 0) {
				result.add(new SingleMaterialStack(curr.getType(), stackPos, EStackType.LIMITED_REQUEST, player, requiredForBuild));
			}
		}

		return result;
	}

	/**
	 * Positions or removes the given stacks on the grid.
	 * 
	 * @param stacks
	 *            stacks to be handled.
	 * @param place
	 *            if true, the stacks will be placed.<br>
	 *            if false the stacks will be removed.
	 */
	private void placeStacks(LinkedList<AbstractStack> stacks, boolean place) {
		assert stacks != null : "stacks mustn't be null here!";
		for (AbstractStack curr : stacks) {
			if (place) {
				grid.placeStack(curr.getPos(), curr);
			} else {
				grid.removeStack(curr.getPos());
			}
		}
	}

	private boolean waitedSecond() {
		delayCtr++;
		if (delayCtr > 10) {
			delayCtr = 0;
			return true;
		}
		return false;
	}

	public static Building getBuilding(EBuildingType type, byte player) {
		switch (type) {
		case BIG_LIVINGHOUSE:
			return new BigLivinghouse(player);
		case MEDIUM_LIVINGHOUSE:
			return new MediumLivinghouse(player);
		case SMALL_LIVINGHOUSE:
			return new SmallLivinghouse(player);
		case CHARCOAL_BURNER:
		case COALMINE:
		case BAKER:
		case FARM:
		case FISHER:
		case FORESTER:
		case GOLDMELT:
		case GOLDMINE:
		case IRONMELT:
		case IRONMINE:
		case LUMBERJACK:
		case MILL:
		case PIG_FARM:
		case SAWMILL:
		case SLAUGHTERHOUSE:
		case STONECUTTER:
		case TOOLSMITH:
		case WEAPONSMITH:
		case WATERWORKS:
		case WINEGROWER:
			return new WorkerBuilding(type, player);

		case TOWER:
			return new Tower(player);

		default:
			System.err.println("couldn't create new building, because type is unknown: " + type);
		}

		return new TestBuilding(player, type);
	}

	@Override
	public ISPosition2D calculateRealPoint(short dx, short dy) {
		return new RelativePoint(dx, dy).calculatePoint(pos);
	}

	@Override
	public final EBuildingType getBuildingType() {
		return type;
	}

	@Override
	public byte getPlayer() {
		return player;
	}

	@Override
	public boolean tryToTakeMaterial() {
		if (state != EBuildingState.BRICKLAYERS_REQUESTED) {
			return false;
		}

		delayCtr--;
		constructionProgress += 1f / (Constants.BRICKLAYER_ACTIONS_PER_MATERIAL * getBuildingType().getNumberOfConstructionMaterials());
		if (delayCtr > 0) {
			return true;
		} else {
			AbstractStack stack = getStackWithMaterial();
			if (stack != null) {
				stack.pop(stack.getMaterial());
				delayCtr = Constants.BRICKLAYER_ACTIONS_PER_MATERIAL;
				return true;
			} else {
				if (areAllStacksFullfilled()) {
					finishConstruction();
				} else {
					state = EBuildingState.WAITING_FOR_MATERIAL;
				}
				return false;
			}
		}
	}

	private void finishConstruction() {
		constructionProgress = 1;

		if (stacks != null) {
			// they are null if this method was called by appear
			placeStacks(stacks, false);
		}
		stacks = createStacks(false);
		placeStacks(stacks, true);

		this.state = EBuildingState.CONSTRUCTED;

		constructionFinishedEvent();
	}

	protected abstract void constructionFinishedEvent();

	@Override
	public float getStateProgress() {
		return constructionProgress;
	}

	@Override
	public ISPosition2D getPos() {
		return pos;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	private static enum EBuildingState {
		CREATED,
		POSITIONED,
		WAITING_FOR_MATERIAL,
		CONSTRUCTED,
		DESTROYED,
		BRICKLAYERS_REQUESTED
	}

	protected boolean isConstructed() {
		return state == EBuildingState.CONSTRUCTED;
	}

	protected abstract EMapObjectType getFlagType();

	protected ISPosition2D getDoor() {
		return door;
	}

	@Override
	public void kill() {
		// TODO Auto-generated method stub

	}

	public void setWorkAreaCenter(ISPosition2D workAreaCenter) {
	}

	/**
	 * @param draw
	 *            true if the circle should be drawn<br>
	 *            false if it should be removed.
	 * @param center
	 * @param radius
	 */
	public void drawWorkAreaCircle(boolean draw) {
		ISPosition2D center = getWorkAreaCenter();
		if (center != null) {
			short radius = type.getWorkradius();
			for (ISPosition2D pos : getCircle(grid, center, radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 1.0f);
			}
			for (ISPosition2D pos : getCircle(grid, center, .75f * radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 0.66f);
			}
			for (ISPosition2D pos : getCircle(grid, center, .5f * radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 0.33f);
			}
			for (ISPosition2D pos : getCircle(grid, center, .25f * radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 0f);
			}
		}
	}

	protected ISPosition2D getWorkAreaCenter() {
		return null;
	}

	private void addOrRemoveMarkObject(boolean draw, IBuildingsGrid grid, ISPosition2D pos, float progress) {
		if (draw) {
			grid.getMapObjectsManager().addBuildingWorkAreaObject(pos, progress);
		} else {
			grid.getMapObjectsManager().removeMapObjectType(pos, EMapObjectType.WORKAREA_MARK);
		}
	}

	private MapShapeFilter getCircle(IBuildingsGrid grid, ISPosition2D center, float radius) {
		MapCircle baseCircle = new MapCircle(center, radius);
		MapCircleBorder border = new MapCircleBorder(baseCircle);
		return new MapShapeFilter(border, grid.getWidth(), grid.getHeight());
	}

}
