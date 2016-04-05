/*******************************************************************************
 * Copyright (c) 2015, 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.buildings;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
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
import jsettlers.logic.buildings.military.OccupyingBuilding;
import jsettlers.logic.buildings.others.DefaultBuilding;
import jsettlers.logic.buildings.others.StockBuilding;
import jsettlers.logic.buildings.others.TempleBuilding;
import jsettlers.logic.buildings.spawn.BigLivinghouse;
import jsettlers.logic.buildings.spawn.BigTemple;
import jsettlers.logic.buildings.spawn.MediumLivinghouse;
import jsettlers.logic.buildings.spawn.SmallLivinghouse;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.RequestStack;
import jsettlers.logic.buildings.trading.MarketBuilding;
import jsettlers.logic.buildings.trading.TradingBuilding;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.buildings.workers.MineBuilding;
import jsettlers.logic.buildings.workers.ResourceBuilding;
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

	private static final byte STATE_CREATED = 0;
	private static final byte STATE_IN_FLATTERNING = 1;
	private static final byte STATE_WAITING_FOR_MATERIAL = 2;
	private static final byte STATE_CONSTRUCTED = 3;
	private static final byte STATE_DESTROYED = 4;
	private static final byte STATE_BRICKLAYERS_REQUESTED = 5;

	private static final float BUILDING_DESTRUCTION_SMOKE_DURATION = 1.2f;
	private static final short UNOCCUPIED_VIEW_DISTANCE = 5;
	private static final short UNCONSTRUCTED_VIEW_DISTANCE = 0;

	private static final int IS_FLATTENED_RECHECK_PERIOD = 1000;
	private static final int WAITING_FOR_MATERIAL_PERIOD = 1000;

	private static final EPriority[] SUPPORTED_PRIORITIES_FOR_CONSTRUCTION = new EPriority[] { EPriority.LOW, EPriority.HIGH, EPriority.STOPPED };
	private static final EPriority[] SUPPORTED_PRIORITIES_FOR_NON_WORKERS = new EPriority[0];

	private static final ConcurrentLinkedQueue<Building> allBuildings = new ConcurrentLinkedQueue<Building>();

	protected final EBuildingType type;
	protected final ShortPoint2D pos;
	protected final IBuildingsGrid grid;

	private Player player;
	private byte state = STATE_CREATED;
	private EPriority priority = EPriority.DEFAULT;

	private float constructionProgress = 0.0f;
	private byte heightAvg;

	private short remainingMaterialActions = 0;
	private List<IRequestStack> stacks;

	private transient boolean selected;

	protected Building(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		this.type = type;
		this.player = player;
		this.pos = position;
		this.grid = buildingsGrid;

		allBuildings.add(this);
	}

	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
		ois.defaultReadObject();
		allBuildings.add(this);
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
		assert state == STATE_CREATED : "building can not be constructed in this state";

		boolean itWorked = grid.setBuilding(pos, this);

		if (itWorked) {
			if (getFlagType() == EMapObjectType.FLAG_DOOR) {
				placeFlag(true);
			}
			positionedEvent(pos);

			if (fullyConstructed) {
				appearFullyConstructed();
			} else {
				initConstruction();
			}
		} else {
			kill();
		}
	}

	private void appearFullyConstructed() {
		this.state = STATE_CONSTRUCTED;

		grid.setBlocked(getBuildingArea(), true);
		finishConstruction();

		appearedEvent();

	}

	private void initConstruction() {
		stacks = createConstructionStacks();

		placeAdditionalMapObjects(grid, pos, true);

		this.state = STATE_IN_FLATTERNING;
		RescheduleTimer.add(this, IS_FLATTENED_RECHECK_PERIOD);

		requestDiggers();
	}

	private List<IRequestStack> createConstructionStacks() {
		List<IRequestStack> result = new LinkedList<IRequestStack>();

		for (ConstructionStack stack : type.getConstructionStacks()) {
			result.add(new RequestStack(grid.getRequestStackGrid(), stack.calculatePoint(this.pos), stack.getMaterialType(), type, priority,
					stack.requiredForBuild()));
		}

		return result;
	}

	protected final void initWorkStacks() {
		this.stacks = createWorkStacks();
	}

	protected List<IRequestStack> createWorkStacks() {
		List<IRequestStack> newStacks = new LinkedList<IRequestStack>();

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

		for (RelativePoint curr : type.getBuildmarks()) {
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
	 *            specifies whether the flag should appear or not.
	 */
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
		case STATE_CREATED:
			assert false : "this should never happen!";
			return -1;
		case STATE_IN_FLATTERNING:
			if (!isFlatened()) {
				return IS_FLATTENED_RECHECK_PERIOD;
			} else {
				placeAdditionalMapObjects(grid, pos, false);
				grid.setBlocked(getBuildingArea(), true);
				this.state = STATE_WAITING_FOR_MATERIAL;
				// directly go into the next case!
			}

		case STATE_WAITING_FOR_MATERIAL:
			if (priority != EPriority.STOPPED && isMaterialAvailable()) {
				state = STATE_BRICKLAYERS_REQUESTED;
				requestBricklayers();
				return -1; // no new scheduling
			} else {
				return WAITING_FOR_MATERIAL_PERIOD;
			}

		case STATE_BRICKLAYERS_REQUESTED: // the state changes are handled by tryToTakeMaterial()
			assert false : "Building.timerEvent() should not be called in state: " + state;
			return -1;

		case STATE_CONSTRUCTED:
			return subTimerEvent();

		case STATE_DESTROYED:
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

		remainingMaterialActions--;
		constructionProgress += 1f / (Constants.BRICKLAYER_ACTIONS_PER_MATERIAL * getBuildingType().getNumberOfConstructionMaterials());
		if (remainingMaterialActions > 0) {
			return true;
		} else {
			IRequestStack stack = getStackWithMaterial();
			if (priority != EPriority.STOPPED && stack != null) {
				stack.pop();
				remainingMaterialActions = Constants.BRICKLAYER_ACTIONS_PER_MATERIAL;
				return true;
			} else {
				if (areAllStacksFullfilled()) {
					finishConstruction();
				} else {
					state = STATE_WAITING_FOR_MATERIAL;
					RescheduleTimer.add(this, WAITING_FOR_MATERIAL_PERIOD);
				}
				return false;
			}
		}
	}

	private boolean areAllStacksFullfilled() {
		for (IRequestStack curr : stacks) {
			if (!curr.isFullfilled()) {
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

		this.state = STATE_CONSTRUCTED;
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
	}

	public final boolean isConstructionFinished() {
		return state == STATE_CONSTRUCTED || state == STATE_DESTROYED;
	}

	protected abstract EMapObjectType getFlagType();

	public final ShortPoint2D getDoor() {
		return getBuildingType().getDoorTile().calculatePoint(pos);
	}

	@Override
	public void kill() {
		if (this.state == STATE_DESTROYED) {
			return;
		}

		System.out.println("building killed");

		if (grid != null) {
			grid.removeBuildingAt(pos);
			grid.getMapObjectsManager().addSelfDeletingMapObject(pos,
					EMapObjectType.BUILDING_DECONSTRUCTION_SMOKE, BUILDING_DESTRUCTION_SMOKE_DURATION, player);
			placeAdditionalMapObjects(grid, pos, false);
			placeFlag(false);
			placeReusableMaterials();

			killedEvent();
		}

		releaseRequestStacks();
		allBuildings.remove(this);
		this.state = STATE_DESTROYED;
	}

	private void placeReusableMaterials() {
		int posIdx = 0;
		FreeMapArea buildingArea = new FreeMapArea(this.pos, type.getBlockedTiles());

		if (isConstructionFinished()) {
			for (ConstructionStack curr : type.getConstructionStacks()) {
				ShortPoint2D position = buildingArea.get(posIdx);
				posIdx += 2;
				byte paybackAmount = (byte) Math.min(curr.requiredForBuild() * Constants.BUILDINGS_DESTRUCTION_MATERIALS_PAYBACK_FACTOR,
						Constants.STACK_SIZE);
				grid.pushMaterialsTo(position, curr.getMaterialType(), paybackAmount);
			}
		} else {
			for (IRequestStack stack : stacks) {
				posIdx += 2;
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
		System.out.println("debug");
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
		return state == STATE_IN_FLATTERNING;
	}

	@Override
	public void diggerRequestFailed() {
		if (isDiggerRequestActive()) {
			grid.requestDiggers(this, (byte) 1);
		}
	}

	@Override
	public boolean isBricklayerRequestActive() {
		return state == STATE_BRICKLAYERS_REQUESTED;
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

	public final boolean isNotDestroyed() {
		return state != STATE_DESTROYED;
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
		case SLAUGHTERHOUSE:
		case STONECUTTER:
		case TOOLSMITH:
		case WEAPONSMITH:
		case WATERWORKS:
		case WINEGROWER:
			return new WorkerBuilding(type, player, position, buildingsGrid);

		case MILL:
			return new MillBuilding(type, player, position, buildingsGrid);

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
			return new ResourceBuilding(EBuildingType.FISHER, player, position, buildingsGrid);

		case STOCK:
			return new StockBuilding(player, position, buildingsGrid);

		case TEMPLE:
			return new TempleBuilding(player, position, buildingsGrid);

		case MARKET_PLACE:
			return new MarketBuilding(type, player, position, buildingsGrid);
		case HARBOR:
			return new TradingBuilding(type, player, position, buildingsGrid, true);

		case BIG_TEMPLE:
			return new BigTemple(player, position, buildingsGrid);

		case HOSPITAL:
		case LOOKOUT_TOWER:
		case DOCKYARD:
			return new DefaultBuilding(type, player, position, buildingsGrid);

		default:
			System.err.println("ERROR: couldn't create new building, because type is unknown: " + type);
			return null;
		}
	}

	protected List<IRequestStack> getStacks() {
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
		ArrayList<IBuildingMaterial> materials = new ArrayList<IBuildingMaterial>();

		for (IRequestStack stack : stacks) {
			if (state == STATE_CONSTRUCTED) {
				materials.add(new BuildingMaterial(stack.getMaterialType(), stack.getStackSize(), false));
			} else { // during construction
				materials.add(new BuildingMaterial(stack.getMaterialType(), stack.getStillRequired()));
			}
		}

		if (state == STATE_CONSTRUCTED) {
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
}
