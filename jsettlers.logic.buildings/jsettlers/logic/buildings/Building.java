package jsettlers.logic.buildings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import jsettlers.logic.buildings.military.Barrack;
import jsettlers.logic.buildings.military.OccupyingBuilding;
import jsettlers.logic.buildings.spawn.BigLivinghouse;
import jsettlers.logic.buildings.spawn.MediumLivinghouse;
import jsettlers.logic.buildings.spawn.SmallLivinghouse;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.buildings.workers.MineBuilding;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.objects.AbstractHexMapObject;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IConstructableBuilding;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.movable.IDebugable;
import jsettlers.logic.stack.LimittedRequestStack;
import jsettlers.logic.stack.RequestStack;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.Timer100Milli;
import random.RandomSingleton;

public abstract class Building extends AbstractHexMapObject implements IConstructableBuilding, IPlayerable, IBuilding, ITimerable, IDebugable,
		IDiggerRequester {
	private static final long serialVersionUID = 4379555028512391595L;

	private final byte player;
	private EBuildingState state = EBuildingState.CREATED;

	private ISPosition2D pos;
	private ISPosition2D door;
	transient private boolean selected;
	private IBuildingsGrid grid;

	private float constructionProgress = 0.0f;
	private FreeMapArea buildingArea;
	private byte heightAvg;

	private short delayCtr = 0;
	private final EBuildingType type;
	private List<RequestStack> stacks;

	protected Building(EBuildingType type, byte player) {
		this.type = type;
		this.player = player;
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		Timer100Milli.add(this); // the building is added to the timer in positionAt(..)
	}

	@Override
	public EMapObjectType getObjectType() {
		return EMapObjectType.BUILDING;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	public final void constructAt(IBuildingsGrid grid, ISPosition2D pos) {
		assert state == EBuildingState.CREATED : "building can not be positioned in this state";

		boolean itWorked = positionAt(grid, pos);

		if (itWorked) {
			stacks = createConstructionStacks();

			placeAdditionalMapObjects(grid, pos, true);

			this.state = EBuildingState.POSITIONED;

			requestDiggers();
		}
	}

	private List<RequestStack> createConstructionStacks() {
		RelativeStack[] requestStacks = type.getRequestStacks();
		List<RequestStack> result = new LinkedList<RequestStack>();

		for (int i = 0; i < requestStacks.length; i++) {
			RelativeStack currStack = requestStacks[i];
			if (currStack.requiredForBuild() > 0) {
				result.add(new LimittedRequestStack(grid.getRequestStackGrid(), currStack.calculatePoint(this.pos), currStack.getType(), currStack
						.requiredForBuild()));
			}
		}

		return result;
	}

	private List<RequestStack> createWorkStacks() {
		RelativeStack[] requestStacks = type.getRequestStacks();
		List<RequestStack> result = new LinkedList<RequestStack>();

		for (int i = 0; i < requestStacks.length; i++) {
			RelativeStack currStack = requestStacks[i];
			if (currStack.requiredForBuild() == 0) {
				result.add(new RequestStack(grid.getRequestStackGrid(), currStack.calculatePoint(this.pos), currStack.getType()));
			}
		}

		return result;
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

			this.buildingArea = new FreeMapArea(this.pos, type.getBlockedTiles());

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

		if (this.pos != null) {
			grid.setBlocked(buildingArea, true);
			finishConstruction();
		}
		appearedEvent();
	}

	protected void appearedEvent() {
	}

	private void requestDiggers() {
		if (shouldBeFlatened()) {
			RelativePoint[] blocked = getBuildingType().getBlockedTiles();
			LinkedList<ISPosition2D> positions = new LinkedList<ISPosition2D>();
			int heightSum = 0;

			for (RelativePoint curr : blocked) {
				ISPosition2D currPos = curr.calculatePoint(this.pos);
				positions.add(currPos);
				heightSum += this.grid.getHeightAt(currPos);
			}

			Collections.shuffle(positions, RandomSingleton.get());

			this.heightAvg = (byte) (heightSum / blocked.length);
			byte numberOfDiggers = (byte) Math.ceil(((float) blocked.length) / Constants.TILES_PER_DIGGER);

			grid.requestDiggers(this, numberOfDiggers);
		}
	}

	protected boolean shouldBeFlatened() {
		return true;
	}

	private boolean isFlatened() {
		if (shouldBeFlatened()) {
			for (ISPosition2D pos : buildingArea) {
				if (grid.getHeightAt(pos) != heightAvg) {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
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
					grid.setBlocked(buildingArea, true);
					this.state = EBuildingState.WAITING_FOR_MATERIAL;
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
			subTimerEvent();
			break;

		case DESTROYED:
			break;
		}
	}

	private boolean isMaterialAvailable() {
		if (stacks == null)
			return true;

		for (RequestStack stack : stacks) {
			if (stack.hasMaterial())
				return true;
		}

		return false;
	}

	/**
	 * This method will be called every 100 ms when the building has finished construction and is not destroyed yet.
	 */
	protected abstract void subTimerEvent();

	private void requestBricklayers() {
		RelativeBricklayer[] bricklayers = type.getBricklayers();
		for (RelativeBricklayer curr : bricklayers) {
			grid.requestBricklayer(this, curr.getPosition().calculatePoint(pos), curr.getDirection());
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
			RequestStack stack = getStackWithMaterial();
			if (stack != null) {
				stack.pop();
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

	private boolean areAllStacksFullfilled() {
		for (RequestStack curr : stacks) {
			if (!curr.isFullfilled()) {
				return false;
			}
		}
		return true;
	}

	protected RequestStack getStackWithMaterial() {
		for (RequestStack curr : stacks) {
			if (curr.hasMaterial()) {
				return curr;
			}
		}
		return null;
	}

	private void finishConstruction() {
		constructionProgress = 1;

		this.state = EBuildingState.CONSTRUCTED;
		stacks = createWorkStacks();

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

	@Override
	public boolean isConstructionFinished() {
		return state == EBuildingState.CONSTRUCTED || state == EBuildingState.DESTROYED;
	}

	protected abstract EMapObjectType getFlagType();

	protected ISPosition2D getDoor() {
		return door;
	}

	@Override
	public void kill() {
		System.out.println("building killed");
		Timer100Milli.remove(this);

		placeReusableMaterials();
		releaseRequestStacks();

		this.state = EBuildingState.DESTROYED;

		grid.removeBuildingAt(pos);
		grid.getMapObjectsManager().addSelfDeletingMapObject(pos, EMapObjectType.BUILDING_DECONSTRUCTION_SMOKE, 5, player);
		drawWorkAreaCircle(false);
		placeAdditionalMapObjects(grid, pos, false);
		placeFlag(false);

		killedEvent();
	}

	private void placeReusableMaterials() {
		int posIdx = 0;
		if (isConstructionFinished()) {
			for (RelativeStack curr : type.getRequestStacks()) {
				if (curr.requiredForBuild() > 0) {
					ISPosition2D position = this.buildingArea.get(posIdx);
					posIdx += 2;
					grid.pushMaterialsTo(position, curr.getType(), (byte) Math.min(curr.requiredForBuild() / 2, Constants.STACK_SIZE));
				}
			}
		} else {
			for (RequestStack stack : stacks) {
				posIdx += 2;
				int numberOfPopped = ((LimittedRequestStack) stack).getNumberOfPopped() / 2;
				if (numberOfPopped > 0) {
					ISPosition2D position = this.buildingArea.get(posIdx);
					grid.pushMaterialsTo(position, stack.getMaterialType(), (byte) Math.min(numberOfPopped, Constants.STACK_SIZE));
				}
			}
		}
	}

	protected void killedEvent() {
	}

	private void releaseRequestStacks() {
		for (RequestStack curr : stacks) {
			curr.releaseRequests();
		}
		stacks = new LinkedList<RequestStack>();
	}

	public void setWorkAreaCenter(@SuppressWarnings("unused") ISPosition2D workAreaCenter) {
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

	@Override
	public void debug() {
		System.out.println("debug");
	}

	@Override
	public boolean isWorking() {
		return false;
	}

	@Override
	public boolean isRequestActive() {
		return !isConstructionFinished();
	}

	@Override
	public FreeMapArea getBuildingArea() {
		return this.buildingArea;
	}

	@Override
	public byte getHeight() {
		return this.heightAvg;
	}

	public final boolean isNotDestroyed() {
		return state != EBuildingState.DESTROYED;
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
		case BAKER:
		case FARM:
		case FISHER:
		case FORESTER:
		case GOLDMELT:
		case IRONMELT:
		case LUMBERJACK:
		case PIG_FARM:
		case SAWMILL:
		case SLAUGHTERHOUSE:
		case STONECUTTER:
		case TOOLSMITH:
		case WEAPONSMITH:
		case WATERWORKS:
		case WINEGROWER:
			return new WorkerBuilding(type, player);

		case MILL:
			return new MillBuilding(type, player);

		case TOWER:
		case BIG_TOWER:
		case CASTLE:
			return new OccupyingBuilding(type, player);

		case BARRACK:
			return new Barrack(player);

		case IRONMINE:
		case GOLDMINE:
		case COALMINE:
			return new MineBuilding(type, player);

		default:
			System.err.println("couldn't create new building, because type is unknown: " + type);
		}

		return new TestBuilding(player, type);
	}

	public IBuildingsGrid getGrid() {
		return grid;
	}

	protected List<RequestStack> getStacks() {
		return stacks;
	}
}
