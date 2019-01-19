/*******************************************************************************
 * Copyright (c) 2015 - 2017
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.buildings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.algorithms.fogofwar.IViewDistancable;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.buildings.RelativeBricklayer;
import jsettlers.common.buildings.stacks.ConstructionStack;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.EDirection;
import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.selectable.ESelectionType;
import jsettlers.logic.buildings.military.Barrack;
import jsettlers.logic.buildings.military.occupying.OccupyingBuilding;
import jsettlers.logic.buildings.others.DefaultBuilding;
import jsettlers.logic.buildings.others.StockBuilding;
import jsettlers.logic.buildings.others.TempleBuilding;
import jsettlers.logic.buildings.spawn.BigLivinghouse;
import jsettlers.logic.buildings.spawn.BigTemple;
import jsettlers.logic.buildings.spawn.MediumLivinghouse;
import jsettlers.logic.buildings.spawn.SmallLivinghouse;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.RequestStack;
import jsettlers.logic.buildings.trading.HarborBuilding;
import jsettlers.logic.buildings.trading.MarketBuilding;
import jsettlers.logic.buildings.workers.DockyardBuilding;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.buildings.workers.MineBuilding;
import jsettlers.logic.buildings.workers.ResourceBuilding;
import jsettlers.logic.buildings.workers.SlaughterhouseBuilding;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.grid.objects.AbstractHexMapObject;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IConstructableBuilding;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IDiggerRequester;
import jsettlers.logic.movable.interfaces.IDebugable;
import jsettlers.logic.player.Player;
import jsettlers.logic.timer.IScheduledTimerable;
import jsettlers.logic.timer.RescheduleTimer;

public abstract class Building extends AbstractHexMapObject implements IConstructableBuilding, IPlayerable, IBuilding, IScheduledTimerable,
		IDebugable, IDiggerRequester, IViewDistancable {
	private static final long serialVersionUID = 4379555028512391595L;

	private static final float BUILDING_DESTRUCTION_SMOKE_DURATION = 1.2f;
	private static final short UNOCCUPIED_VIEW_DISTANCE = 5;
	private static final short UNCONSTRUCTED_VIEW_DISTANCE = 0;

	private static final int IS_UNSTOPPED_RECHECK_PERIOD = 1000;
	private static final int IS_FLATTENED_RECHECK_PERIOD = 1000;
	private static final int WAITING_FOR_MATERIAL_PERIOD = 1000;

	private static final EPriority[] SUPPORTED_PRIORITIES_FOR_CONSTRUCTION = new EPriority[] { EPriority.LOW, EPriority.HIGH, EPriority.STOPPED };
	private static final EPriority[] SUPPORTED_PRIORITIES_FOR_NON_WORKERS = new EPriority[0];

	private static final ConcurrentLinkedQueue<Building> allBuildings = new ConcurrentLinkedQueue<>();

	protected final EBuildingType type;
	protected final ShortPoint2D pos;
	protected final IBuildingsGrid grid;

	private Player player;
	private EBuildingState state = EBuildingState.CREATED;
	private EPriority priority = EPriority.DEFAULT;

	private float constructionProgress = 0.0f;
	private byte heightAvg;

	private short remainingMaterialActions = 0;
	private List<? extends IRequestStack> stacks;

	private transient boolean selected;

	protected Building(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		this.type = type;
		this.player = player;
		this.pos = position;
		this.grid = buildingsGrid;

		allBuildings.add(this);
	}
	
	@SuppressWarnings("unchecked")
	public static void readStaticState(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		allBuildings.clear();
		allBuildings.addAll((Collection<? extends Building>) ois.readObject());
	}

	public static void writeStaticState(ObjectOutputStream oos) throws IOException {
		oos.writeObject(allBuildings);
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

	public final void construct(boolean fullyConstructed) {
		assert state == EBuildingState.CREATED : "building can not be constructed in this state";

		boolean itWorked = grid.setBuilding(pos, this);

		if (itWorked) {
			if (getFlagType() == EMapObjectType.FLAG_DOOR) {
				showFlag(true);
			}
			placedAtEvent(pos);

			if (fullyConstructed) {
				appearFullyConstructed();
			} else {
				initConstruction();
			}
		} else {
			kill();
		}
	}

	protected void placedAtEvent(ShortPoint2D pos) {
	}

	private void appearFullyConstructed() {
		this.state = EBuildingState.CONSTRUCTED;

		grid.setBlocked(getBuildingArea(), true);
		finishConstruction();

		appearedEvent();
	}

	protected void appearedEvent() {
	}

	private void initConstruction() {
		stacks = createConstructionStacks();

		placeAdditionalMapObjects(grid, pos, true);

		this.state = EBuildingState.CREATED;
		RescheduleTimer.add(this, IS_UNSTOPPED_RECHECK_PERIOD);
	}

	private List<IRequestStack> createConstructionStacks() {
		List<IRequestStack> result = new LinkedList<>();

		for (ConstructionStack stack : type.getConstructionStacks()) {
			result.add(new RequestStack(grid.getRequestStackGrid(), stack.calculatePoint(this.pos), stack.getMaterialType(), type, priority,
					stack.requiredForBuild()));
		}

		return result;
	}

	protected final void initWorkStacks() {
		this.stacks = createWorkStacks();
	}

	protected List<? extends IRequestStack> createWorkStacks() {
		List<RequestStack> newStacks = new LinkedList<>();

		for (RelativeStack stack : type.getRequestStacks()) {
			newStacks.add(new RequestStack(grid.getRequestStackGrid(), stack.calculatePoint(this.pos), stack.getMaterialType(), type, priority));
		}

		return newStacks;
	}

	protected void placeAdditionalMapObjects(IBuildingsGrid grid, ShortPoint2D pos, boolean place) {
		if (place) {
			grid.getMapObjectsManager().addSimpleMapObject(pos, EMapObjectType.BUILDINGSITE_SIGN, false, null);
		} else {
			grid.getMapObjectsManager().removeMapObjectType(pos.x, pos.y, EMapObjectType.BUILDINGSITE_SIGN);
		}

		for (RelativePoint curr : type.getBuildMarks()) {
			if (place) {
				grid.getMapObjectsManager().addSimpleMapObject(curr.calculatePoint(pos), EMapObjectType.BUILDINGSITE_POST, false, null);
			} else {
				ShortPoint2D postPos = curr.calculatePoint(pos);
				grid.getMapObjectsManager().removeMapObjectType(postPos.x, postPos.y, EMapObjectType.BUILDINGSITE_POST);
			}
		}
	}

	/**
	 * Used to set or clear the small red flag atop a building to indicate it is occupied.
	 *
	 * @param place
	 * 		specifies whether the flag should appear or not.
	 */
	protected void showFlag(boolean place) {
		ShortPoint2D flagPosition = type.getFlag().calculatePoint(pos);

		if (place) {
			grid.getMapObjectsManager().addSimpleMapObject(flagPosition, getFlagType(), false, player);
		} else {
			grid.getMapObjectsManager().removeMapObjectType(flagPosition.x, flagPosition.y, getFlagType());
		}
	}

	private void requestDiggers() {
		if (shouldBeFlatened()) {
			RelativePoint[] protectedTiles = getFlattenTiles();
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
			grid.requestBricklayer(this, curr.calculatePoint(pos), curr.getDirection());
		}
	}

	protected boolean shouldBeFlatened() {
		return true;
	}

	private boolean isFlatened() {
		if (shouldBeFlatened()) {
			return grid.isAreaFlattenedAtHeight(pos, getFlattenTiles(), heightAvg);
		} else {
			return true;
		}
	}

	@Override
	public int timerEvent() {
		switch (state) {
		case CREATED:
			if (priority == EPriority.STOPPED) {
				return IS_UNSTOPPED_RECHECK_PERIOD;
			} else {
				state = EBuildingState.IN_FLATTERNING;
				requestDiggers();
			}

		case IN_FLATTERNING:
			if (!isFlatened()) {
				return IS_FLATTENED_RECHECK_PERIOD;
			} else {
				placeAdditionalMapObjects(grid, pos, false);
				grid.setBlocked(getBuildingArea(), true);
				this.state = EBuildingState.WAITING_FOR_MATERIAL;
				// directly go into the next case!
			}

		case WAITING_FOR_MATERIAL:
			if (priority != EPriority.STOPPED && (isMaterialAvailable() || remainingMaterialActions > 0)) {
				state = EBuildingState.BRICKLAYERS_REQUESTED;
				requestBricklayers();
				return -1; // no new scheduling
			} else {
				return WAITING_FOR_MATERIAL_PERIOD;
			}

		case BRICKLAYERS_REQUESTED: // the state changes are handled by tryToTakeMaterial()
			assert false : "Building.timerEvent() should not be called in state: " + state;
			return -1;

		case CONSTRUCTED:
			return subTimerEvent();

		case DESTROYED:
		default:
			return -1;
		}
	}

	/**
	 * This method will be called when the building has finished construction and is not destroyed yet.
	 *
	 * @return Gives the number of milliseconds when this method should be called again. Return -1 to unschedule the building.
	 */
	protected abstract int subTimerEvent();

	private boolean isMaterialAvailable() {
		if (stacks == null)
			return true;

		for (IRequestStack stack : stacks) {
			if (stack.hasMaterial())
				return true;
		}

		return false;
	}

	@Override
	public final EBuildingType getBuildingType() {
		return type;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public final Player getPlayer() {
		return player;
	}

	@Override
	public boolean tryToTakeMaterial() {
		if (state != EBuildingState.BRICKLAYERS_REQUESTED) {
			return false;
		}

		remainingMaterialActions--;
		constructionProgress += 1f / (Constants.BRICKLAYER_ACTIONS_PER_MATERIAL * getBuildingType().getNumberOfConstructionMaterials());
		if (remainingMaterialActions > 0) {
			return true;
		} else {
			IRequestStack stack = getStackWithMaterial();
			if (stack != null) {
				stack.pop();
				remainingMaterialActions = Constants.BRICKLAYER_ACTIONS_PER_MATERIAL;
				return true;
			} else {
				if (areAllStacksFullfilled()) {
					finishConstruction();
				} else {
					state = EBuildingState.WAITING_FOR_MATERIAL;
					RescheduleTimer.add(this, WAITING_FOR_MATERIAL_PERIOD);
				}
				return false;
			}
		}
	}

	private boolean areAllStacksFullfilled() {
		for (IRequestStack curr : stacks) {
			if (!curr.isFulfilled()) {
				return false;
			}
		}
		return true;
	}

	protected IRequestStack getStackWithMaterial() {
		for (IRequestStack curr : stacks) {
			if (curr.hasMaterial()) {
				return curr;
			}
		}
		return null;
	}

	private void finishConstruction() {
		constructionProgress = 1;
		this.setPriority(EPriority.DEFAULT);

		this.state = EBuildingState.CONSTRUCTED;
		if (getFlagType() == EMapObjectType.FLAG_DOOR) { // this building has no worker
			stacks = createWorkStacks();
		} else {
			stacks = new LinkedList<>(); // create a new stacks list
		}
		int timerPeriod = constructionFinishedEvent();
		RescheduleTimer.add(this, timerPeriod);
	}

	protected abstract int constructionFinishedEvent();

	@Override
	public float getStateProgress() {
		return constructionProgress;
	}

	@Override
	public ShortPoint2D getPosition() {
		return pos;
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public boolean isWounded() {
		return false;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public final boolean isConstructionFinished() {
		return state == EBuildingState.CONSTRUCTED || state == EBuildingState.DESTROYED;
	}

	protected abstract EMapObjectType getFlagType();

	public final ShortPoint2D getDoor() {
		return getBuildingType().getDoorTile().calculatePoint(pos);
	}

	@Override
	public void kill() {
		if (this.state == EBuildingState.DESTROYED) {
			return;
		}

		System.out.println("building killed");

		if (grid != null) {
			grid.removeBuildingAt(pos);
			grid.getMapObjectsManager().addSelfDeletingMapObject(pos,
					EMapObjectType.BUILDING_DECONSTRUCTION_SMOKE, BUILDING_DESTRUCTION_SMOKE_DURATION, player);
			placeAdditionalMapObjects(grid, pos, false);
			showFlag(false);
			placeReusableMaterials();

			killedEvent();
		}

		releaseRequestStacks();
		allBuildings.remove(this);
		this.state = EBuildingState.DESTROYED;
		this.selected = false;
	}

	private void placeReusableMaterials() {
		int posIdx = 0;
		FreeMapArea buildingArea = new FreeMapArea(this.pos, type.getBlockedTiles());

		if (isConstructionFinished()) {
			for (ConstructionStack curr : type.getConstructionStacks()) {
				byte paybackAmount = (byte) (curr.requiredForBuild() * Constants.BUILDINGS_DESTRUCTION_MATERIALS_PAYBACK_FACTOR);
				while (paybackAmount > 0) {
					byte paybackForStack = (byte) Math.min(Constants.STACK_SIZE, paybackAmount);
					ShortPoint2D position = buildingArea.get(posIdx);
					grid.pushMaterialsTo(position, curr.getMaterialType(), paybackForStack);
					paybackAmount -= paybackForStack;
					posIdx += 4;
				}
			}
		} else {
			for (IRequestStack stack : stacks) {
				posIdx += 4;
				int paybackAmount = (int) (stack.getNumberOfPopped() * Constants.BUILDINGS_DESTRUCTION_MATERIALS_PAYBACK_FACTOR);
				if (paybackAmount > 0) {
					ShortPoint2D position = buildingArea.get(posIdx);
					grid.pushMaterialsTo(position, stack.getMaterialType(), (byte) Math.min(paybackAmount, Constants.STACK_SIZE));
				}
			}
		}
	}

	protected void killedEvent() {
	}

	protected void releaseRequestStacks() {
		if (stacks != null) {
			for (IRequestStack curr : stacks) {
				curr.releaseRequests();
			}
			stacks = Collections.emptyList();
		}
	}

	public void setWorkAreaCenter(ShortPoint2D workAreaCenter) {
	}

	@Override
	public void debug() {
		System.out.println("debug: building at " + pos);
	}

	@Override
	public EPriority[] getSupportedPriorities() {
		if (!isConstructionFinished()) {
			return SUPPORTED_PRIORITIES_FOR_CONSTRUCTION;
		} else {
			return SUPPORTED_PRIORITIES_FOR_NON_WORKERS;
		}
	}

	@Override
	public EPriority getPriority() {
		return priority;
	}

	@Override
	public boolean isDiggerRequestActive() {
		return state == EBuildingState.IN_FLATTERNING;
	}

	@Override
	public void diggerRequestFailed() {
		if (isDiggerRequestActive()) {
			grid.requestDiggers(this, (byte) 1);
		}
	}

	@Override
	public boolean isBricklayerRequestActive() {
		return state == EBuildingState.BRICKLAYERS_REQUESTED;
	}

	@Override
	public void bricklayerRequestFailed(ShortPoint2D bricklayerTargetPos, EDirection lookDirection) {
		if (isBricklayerRequestActive()) {
			grid.requestBricklayer(this, bricklayerTargetPos, lookDirection);
		}
	}

	protected FreeMapArea getBuildingArea() {
		return new FreeMapArea(this.pos, type.getBlockedTiles());
	}

	@Override
	public byte getAverageHeight() {
		return this.heightAvg;
	}

	public final boolean isDestroyed() {
		return state == EBuildingState.DESTROYED;
	}

	protected List<? extends IRequestStack> getStacks() {
		return stacks;
	}

	public static ConcurrentLinkedQueue<Building> getAllBuildings() {
		return allBuildings;
	}

	public static void clearState() {
		allBuildings.clear();
	}

	@Override
	public final short getViewDistance() {
		if (isConstructionFinished()) {
			if (isOccupied()) {
				return type.getViewDistance();
			} else {
				return UNOCCUPIED_VIEW_DISTANCE;
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
		ArrayList<IBuildingMaterial> materials = new ArrayList<>();

		for (IRequestStack stack : stacks) {
			if (stack.getStillRequired() == Short.MAX_VALUE) {
				materials.add(new BuildingMaterial(stack.getMaterialType(), stack.getStackSize(), false));
			} else { // stacks with a maximum required amount should show the required amount
				materials.add(new BuildingMaterial(stack.getMaterialType(), stack.getStillRequired()));
			}
		}

		if (state == EBuildingState.CONSTRUCTED) {
			for (RelativeStack offerStack : type.getOfferStacks()) {
				byte stackSize = grid.getRequestStackGrid().getStackSize(offerStack.calculatePoint(pos), offerStack.getMaterialType());
				materials.add(new BuildingMaterial(offerStack.getMaterialType(), stackSize, true));
			}
		}

		return materials;
	}

	public void setPriority(EPriority newPriority) {
		this.priority = newPriority;
		if (stacks != null) {
			for (IRequestStack curr : stacks) {
				curr.setPriority(newPriority);
			}
		}

		if (newPriority == EPriority.STOPPED) {
			switch (state) {
			case IN_FLATTERNING:
				state = EBuildingState.CREATED; // we're still scheduled in this state => no rescheduling!
				break;

			case BRICKLAYERS_REQUESTED:
				state = EBuildingState.WAITING_FOR_MATERIAL;
				RescheduleTimer.add(this, WAITING_FOR_MATERIAL_PERIOD); // we're not scheduled atm => reschedule!
				break;
			}
		}
	}

	public final RelativePoint[] getFlattenTiles() {
		if (shouldBeFlatened()) {
			return type.getProtectedTiles();
		} else {
			return new RelativePoint[0];
		}
	}

	public short getPartitionId() {
		return grid.getPartitionIdAt(pos);
	}

	@Override
	public boolean cannotWork() {
		return false;
	}

	public static Building createBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		switch (type) {
		case BIG_LIVINGHOUSE:
			return new BigLivinghouse(player, position, buildingsGrid);
		case MEDIUM_LIVINGHOUSE:
			return new MediumLivinghouse(player, position, buildingsGrid);
		case SMALL_LIVINGHOUSE:
			return new SmallLivinghouse(player, position, buildingsGrid);

		case CHARCOAL_BURNER:
		case BAKER:
		case DONKEY_FARM:
		case FARM:
		case FORESTER:
		case GOLDMELT:
		case IRONMELT:
		case LUMBERJACK:
		case PIG_FARM:
		case SAWMILL:
		case STONECUTTER:
		case TOOLSMITH:
		case WEAPONSMITH:
		case WATERWORKS:
		case WINEGROWER:
			return new WorkerBuilding(type, player, position, buildingsGrid);

		case DOCKYARD:
			return new DockyardBuilding(player, position, buildingsGrid);

		case MILL:
			return new MillBuilding(player, position, buildingsGrid);

		case SLAUGHTERHOUSE:
			return new SlaughterhouseBuilding(type, player, position, buildingsGrid);

		case TOWER:
		case BIG_TOWER:
		case CASTLE:
			return new OccupyingBuilding(type, player, position, buildingsGrid);

		case BARRACK:
			return new Barrack(player, position, buildingsGrid);

		case IRONMINE:
		case GOLDMINE:
		case COALMINE:
			return new MineBuilding(type, player, position, buildingsGrid);

		case FISHER:
			return new ResourceBuilding(type, player, position, buildingsGrid);

		case STOCK:
			return new StockBuilding(player, position, buildingsGrid);

		case TEMPLE:
			return new TempleBuilding(player, position, buildingsGrid);

		case MARKET_PLACE:
			return new MarketBuilding(type, player, position, buildingsGrid);

		case HARBOR:
			return new HarborBuilding(type, player, position, buildingsGrid);

		case BIG_TEMPLE:
			return new BigTemple(player, position, buildingsGrid);

		case HOSPITAL:
		case LOOKOUT_TOWER:
			return new DefaultBuilding(type, player, position, buildingsGrid);

		default:
			System.err.println("ERROR: couldn't create new building, because type is unknown: " + type);
			return null;
		}
	}

	private enum EBuildingState {
		CREATED,
		IN_FLATTERNING,
		WAITING_FOR_MATERIAL,
		CONSTRUCTED,
		DESTROYED,
		BRICKLAYERS_REQUESTED
	}
}
