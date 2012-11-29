package jsettlers.logic.buildings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.buildings.RelativeBricklayer;
import jsettlers.common.buildings.RelativeStack;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapCircleBorder;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.logic.algorithms.fogofwar.IViewDistancable;
import jsettlers.logic.buildings.military.Barrack;
import jsettlers.logic.buildings.military.OccupyingBuilding;
import jsettlers.logic.buildings.spawn.BigLivinghouse;
import jsettlers.logic.buildings.spawn.MediumLivinghouse;
import jsettlers.logic.buildings.spawn.SmallLivinghouse;
import jsettlers.logic.buildings.spawn.SpawnBuilding;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.buildings.workers.MineBuilding;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.objects.AbstractHexMapObject;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IConstructableBuilding;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.newmovable.interfaces.IDebugable;
import jsettlers.logic.player.Player;
import jsettlers.logic.stack.LimittedRequestStack;
import jsettlers.logic.stack.RequestStack;
import jsettlers.logic.timer.ITimerable;
import jsettlers.logic.timer.Timer100Milli;

public abstract class Building extends AbstractHexMapObject implements IConstructableBuilding, IPlayerable, IBuilding, ITimerable, IDebugable,
		IDiggerRequester, IViewDistancable {
	private static final long serialVersionUID = 4379555028512391595L;

	private static final byte STATE_CREATED = 0;
	private static final byte STATE_POSITIONED = 1;
	private static final byte STATE_WAITING_FOR_MATERIAL = 2;
	private static final byte STATE_CONSTRUCTED = 3;
	private static final byte STATE_DESTROYED = 4;
	private static final byte STATE_BRICKLAYERS_REQUESTED = 5;

	private static final float BUILDING_DESTRUCTION_SMOKE_DURATION = 1.2f;
	private static final short UNOCCUPIED_VIEW_DISTANCE = 5;
	private static final short UNCONSTRUCTED_VIEW_DISTANCE = 0;

	private static final ConcurrentLinkedQueue<Building> allBuildings = new ConcurrentLinkedQueue<Building>();

	private final EBuildingType type;

	private ShortPoint2D pos;
	private IBuildingsGrid grid;
	private Player player;
	private byte state = STATE_CREATED;

	transient private boolean selected;

	private float constructionProgress = 0.0f;
	private byte heightAvg;

	private short delayCtr = 0;
	private List<RequestStack> stacks;

	protected Building(EBuildingType type, Player player) {
		this.type = type;
		this.player = player;

		allBuildings.offer(this);
	}

	private synchronized void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		allBuildings.add(this);
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

	// TODO @Andreas Eberle: refactor building creating
	public final void constructAt(IBuildingsGrid grid, ShortPoint2D pos, boolean fullyConstructed) {
		if (fullyConstructed) {
			appearAt(grid, pos);
		} else {
			assert state == STATE_CREATED : "building can not be positioned in this state";

			boolean itWorked = positionAt(grid, pos);

			if (itWorked) {
				stacks = createConstructionStacks();

				placeAdditionalMapObjects(grid, pos, true);

				this.state = STATE_POSITIONED;

				requestDiggers();
			}
		}
	}

	private final void appearAt(IBuildingsGrid grid, ShortPoint2D pos) {
		this.state = STATE_CONSTRUCTED;

		positionAt(grid, pos);

		if (this.pos != null) {
			grid.setBlocked(getBuildingArea(), true);
			finishConstruction();
		}
		appearedEvent();
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

	protected void createWorkStacks() {
		RelativeStack[] requestStacks = type.getRequestStacks();
		List<RequestStack> result = new LinkedList<RequestStack>();

		for (int i = 0; i < requestStacks.length; i++) {
			RelativeStack currStack = requestStacks[i];
			if (currStack.requiredForBuild() == 0) {
				result.add(new RequestStack(grid.getRequestStackGrid(), currStack.calculatePoint(this.pos), currStack.getType()));
			}
		}

		this.stacks = result;
	}

	protected void placeAdditionalMapObjects(IBuildingsGrid grid, ShortPoint2D pos, boolean place) {
		if (place) {
			grid.getMapObjectsManager().addSimpleMapObject(pos, EMapObjectType.BUILDINGSITE_SIGN, false, null);
		} else {
			grid.getMapObjectsManager().removeMapObjectType(pos.x, pos.y, EMapObjectType.BUILDINGSITE_SIGN);
		}

		for (RelativePoint curr : type.getBuildmarks()) {
			if (place) {
				grid.getMapObjectsManager().addSimpleMapObject(curr.calculatePoint(pos), EMapObjectType.BUILDINGSITE_POST, false, null);
			} else {
				ShortPoint2D postPos = curr.calculatePoint(pos);
				grid.getMapObjectsManager().removeMapObjectType(postPos.x, postPos.y, EMapObjectType.BUILDINGSITE_POST);
			}
		}
	}

	private boolean positionAt(IBuildingsGrid grid, ShortPoint2D pos) {
		boolean couldBePlaced = grid.setBuilding(pos, this);
		if (couldBePlaced) {
			this.pos = pos;
			this.grid = grid;

			Timer100Milli.add(this);

			if (getFlagType() == EMapObjectType.FLAG_DOOR) {
				placeFlag(true);
			}

			positionedEvent(pos);
		}
		return couldBePlaced;
	}

	protected void placeFlag(boolean place) {
		ShortPoint2D flagPosition = type.getFlag().calculatePoint(pos);

		if (place) {
			grid.getMapObjectsManager().addSimpleMapObject(flagPosition, getFlagType(), false, player);
		} else {
			grid.getMapObjectsManager().removeMapObjectType(flagPosition.x, flagPosition.y, getFlagType());
		}
	}

	protected abstract void positionedEvent(ShortPoint2D pos);

	protected void appearedEvent() {
	}

	private void requestDiggers() {
		if (shouldBeFlatened()) {
			RelativePoint[] protectedTiles = getBuildingType().getProtectedTiles();
			int heightSum = 0;

			for (RelativePoint curr : protectedTiles) {
				ShortPoint2D currPos = curr.calculatePoint(this.pos);
				heightSum += this.grid.getHeightAt(currPos);
			}

			this.heightAvg = (byte) (heightSum / protectedTiles.length);
			byte numberOfDiggers = (byte) Math.ceil(((float) protectedTiles.length) / Constants.TILES_PER_DIGGER);

			grid.requestDiggers(this, numberOfDiggers);
		}
	}

	private void requestBricklayers() {
		RelativeBricklayer[] bricklayers = type.getBricklayers();
		for (RelativeBricklayer curr : bricklayers) {
			grid.requestBricklayer(this, curr.getPosition().calculatePoint(pos), curr.getDirection());
		}
	}

	protected boolean shouldBeFlatened() {
		return true;
	}

	private boolean isFlatened() {
		if (shouldBeFlatened()) {
			for (RelativePoint currPos : type.getBlockedTiles()) {
				if (grid.getHeightAt(currPos.calculatePoint(this.pos)) != heightAvg) {
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
		case STATE_CREATED:
			assert false : "this should never happen!";
			break;
		case STATE_POSITIONED:
			if (waitedSecond()) {
				if (isFlatened()) {
					placeAdditionalMapObjects(grid, pos, false);
					grid.setBlocked(getBuildingArea(), true);
					this.state = STATE_WAITING_FOR_MATERIAL;
				}
			}
			break;

		case STATE_WAITING_FOR_MATERIAL:
			if (waitedSecond()) {
				if (isMaterialAvailable()) {
					requestBricklayers();
					state = STATE_BRICKLAYERS_REQUESTED;
				}
			}
			break;

		case STATE_BRICKLAYERS_REQUESTED:
			// the state changes are handled by tryToTakeMaterial()
			break;

		case STATE_CONSTRUCTED:
			subTimerEvent();
			break;

		case STATE_DESTROYED:
			break;
		}
	}

	/**
	 * This method will be called every 100 ms when the building has finished construction and is not destroyed yet.
	 */
	protected abstract void subTimerEvent();

	private boolean isMaterialAvailable() {
		if (stacks == null)
			return true;

		for (RequestStack stack : stacks) {
			if (stack.hasMaterial())
				return true;
		}

		return false;
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
	public ShortPoint2D calculateRealPoint(short dx, short dy) {
		return new RelativePoint(dx, dy).calculatePoint(pos);
	}

	@Override
	public final EBuildingType getBuildingType() {
		return type;
	}

	@Override
	public byte getPlayerId() {
		return player.playerId;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public final Player getPlayer() {
		return player;
	}

	@Override
	public boolean tryToTakeMaterial() {
		if (state != STATE_BRICKLAYERS_REQUESTED) {
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
					state = STATE_WAITING_FOR_MATERIAL;
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

		this.state = STATE_CONSTRUCTED;
		if (getFlagType() == EMapObjectType.FLAG_DOOR) { // this building has no worker
			createWorkStacks();
		}
		constructionFinishedEvent();
	}

	protected abstract void constructionFinishedEvent();

	@Override
	public float getStateProgress() {
		return constructionProgress;
	}

	@Override
	public ShortPoint2D getPos() {
		return pos;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
		drawWorkAreaCircle(selected);
	}

	@Override
	public final boolean isConstructionFinished() {
		return state == STATE_CONSTRUCTED || state == STATE_DESTROYED;
	}

	protected abstract EMapObjectType getFlagType();

	public final ShortPoint2D getDoor() {
		return getBuildingType().getDoorTile().calculatePoint(pos);
	}

	@Override
	public void kill() {
		System.out.println("building killed");
		Timer100Milli.remove(this);

		placeReusableMaterials();
		releaseRequestStacks();

		this.state = STATE_DESTROYED;

		grid.removeBuildingAt(pos);
		grid.getMapObjectsManager().addSelfDeletingMapObject(pos, EMapObjectType.BUILDING_DECONSTRUCTION_SMOKE, BUILDING_DESTRUCTION_SMOKE_DURATION,
				player);
		drawWorkAreaCircle(false);
		placeAdditionalMapObjects(grid, pos, false);
		placeFlag(false);

		allBuildings.remove(this);

		killedEvent();
	}

	private void placeReusableMaterials() {
		int posIdx = 0;
		FreeMapArea buildingArea = new FreeMapArea(this.pos, type.getBlockedTiles());

		if (isConstructionFinished()) {
			for (RelativeStack curr : type.getRequestStacks()) {
				if (curr.requiredForBuild() > 0) {
					ShortPoint2D position = buildingArea.get(posIdx);
					posIdx += 2;
					grid.pushMaterialsTo(position, curr.getType(), (byte) Math.min(curr.requiredForBuild() / 2, Constants.STACK_SIZE));
				}
			}
		} else {
			for (RequestStack stack : stacks) {
				posIdx += 2;
				int numberOfPopped = ((LimittedRequestStack) stack).getNumberOfPopped() / 2;
				if (numberOfPopped > 0) {
					ShortPoint2D position = buildingArea.get(posIdx);
					grid.pushMaterialsTo(position, stack.getMaterialType(), (byte) Math.min(numberOfPopped, Constants.STACK_SIZE));
				}
			}
		}
	}

	protected void killedEvent() {
	}

	protected void releaseRequestStacks() {
		if (stacks != null) {
			for (RequestStack curr : stacks) {
				curr.releaseRequests();
			}
			stacks = new LinkedList<RequestStack>();
		}
	}

	public void setWorkAreaCenter(@SuppressWarnings("unused") ShortPoint2D workAreaCenter) {
	}

	/**
	 * @param draw
	 *            true if the circle should be drawn<br>
	 *            false if it should be removed.
	 * @param center
	 * @param radius
	 */
	public void drawWorkAreaCircle(boolean draw) {
		ShortPoint2D center = getWorkAreaCenter();
		if (center != null) {
			short radius = type.getWorkradius();
			for (ShortPoint2D pos : getCircle(grid, center, radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 1.0f);
			}
			for (ShortPoint2D pos : getCircle(grid, center, .75f * radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 0.66f);
			}
			for (ShortPoint2D pos : getCircle(grid, center, .5f * radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 0.33f);
			}
			for (ShortPoint2D pos : getCircle(grid, center, .25f * radius)) {
				addOrRemoveMarkObject(draw, grid, pos, 0f);
			}
		}
	}

	protected ShortPoint2D getWorkAreaCenter() {
		return null;
	}

	private void addOrRemoveMarkObject(boolean draw, IBuildingsGrid grid, ShortPoint2D pos, float progress) {
		if (draw) {
			grid.getMapObjectsManager().addBuildingWorkAreaObject(pos, progress);
		} else {
			grid.getMapObjectsManager().removeMapObjectType(pos.x, pos.y, EMapObjectType.WORKAREA_MARK);
		}
	}

	private MapShapeFilter getCircle(IBuildingsGrid grid, ShortPoint2D center, float radius) {
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
	public boolean isDiggerRequestActive() {
		return !isConstructionFinished();
	}

	protected FreeMapArea getBuildingArea() {
		return new FreeMapArea(this.pos, type.getBlockedTiles());
	}

	@Override
	public byte getAverageHeight() {
		return this.heightAvg;
	}

	public final boolean isNotDestroyed() {
		return state != STATE_DESTROYED;
	}

	public static Building getBuilding(EBuildingType type, Player player) {
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

			// XXX This is only for testing!
		case LAGERHAUS:
			return new SpawnBuilding(type, player) {
				/**
                 * 
                 */
				private static final long serialVersionUID = 2605958844824648130L;

				@Override
				protected byte getProduceLimit() {
					return 0;
				}

				@Override
				protected EMovableType getMovableType() {
					return EMovableType.BEARER;
				}
			};

		default:
			System.err.println("couldn't create new building, because type is unknown: " + type);
			return null;
		}
	}

	public IBuildingsGrid getGrid() {
		return grid;
	}

	protected List<RequestStack> getStacks() {
		return stacks;
	}

	public static ConcurrentLinkedQueue<? extends IViewDistancable> getAllBuildings() {
		return allBuildings;
	}

	public static void dropAllBuildings() {
		allBuildings.clear();
		OccupyingBuilding.dropAllBuildings();
	}

	@Override
	public final short getViewDistance() {
		if (isConstructionFinished()) {
			if (isOccupied()) {
				return UNOCCUPIED_VIEW_DISTANCE;
			} else {
				return getBuildingType().getViewDistance();
			}
		} else {
			return UNCONSTRUCTED_VIEW_DISTANCE;
		}
	}

	@Override
	public ESelectionType getSelectionType() {
		return ESelectionType.BUILDING;
	}

	@Override
	public List<IBuildingMaterial> getMaterials() {
		ArrayList<IBuildingMaterial> materials = new ArrayList<IBuildingMaterial>();
		materials.addAll(stacks);
		// TODO @Andreas: Add a list of offering stacks to this building.
		return materials;
	}

}
