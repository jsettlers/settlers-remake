package jsettlers.logic.movable.workers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;
import random.RandomSingleton;

public class BuildingWorkerStrategy extends PathableStrategy implements IManageableWorker {
	private static final long serialVersionUID = 4541691091116877212L;

	private final EMovableType movableType;
	private transient IBuildingJob currentJob;

	private boolean done = true;
	private boolean recordedJobless = false;
	private IWorkerRequestBuilding building;
	private Path path;

	private EMaterialType lastPopped = null;

	public BuildingWorkerStrategy(IMovableGrid grid, Movable movable, EMovableType movableType) {
		super(grid, movable);
		this.movableType = movableType;

		makeJobless();
	}

	private final void makeJobless() {
		if (!recordedJobless) {
			super.getGrid().addJobless(this);
			this.recordedJobless = true;
		}
	}

	@Override
	public boolean needsPlayersGround() {
		return true;
	}

	@Override
	protected void pathRequestFailed() {
		super.setAction(EAction.NO_ACTION, -1);
		this.path = null;
		if (currentJob != null)
			jobFailed();
	}

	@Override
	protected void setCalculatedPath(Path path) {
		if (currentJob != null
				&& (currentJob.getType() == EBuildingJobType.PRE_SEARCH || currentJob.getType() == EBuildingJobType.PRE_SEARCH_IN_AREA)) {
			this.path = path;
			super.getGrid().setMarked(path.getTargetPos(), true);
			jobFinished();
		} else {
			super.setCalculatedPath(path);
		}
	}

	@Override
	protected boolean actionFinished() {
		if (!super.actionFinished()) {
			if (currentJob != null) {
				boolean success = true;
				if (currentJob.getType() == EBuildingJobType.DROP && currentJob.getMaterial() != EMaterialType.NO_MATERIAL) {
					success = super.getGrid().pushMaterial(super.getPos(), currentJob.getMaterial(), true);
				} else if (currentJob.getType() == EBuildingJobType.DROP_POPPED && lastPopped != null) {
					success = super.getGrid().pushMaterial(super.getPos(), lastPopped, true);
					lastPopped = null;
				}

				if (success) {
					jobFinished();
				} else {
					jobFailed();
				}
			} else {
				super.setAction(EAction.NO_ACTION, -1);
			}
		}
		return true;
	}

	@Override
	protected void pathFinished() {
		if (currentJob != null) {
			pathOrActionFinished();
		} else {
			super.setAction(EAction.NO_ACTION, -1);
		}
	}

	@Override
	protected boolean noActionEvent() {
		if (!super.noActionEvent()) {
			if (currentJob != null) {
				pathOrActionFinished();
			} else {
				if (!done) {
					checkForDroppingMaterial();
					done = true;
				}
			}
		}
		return true;
	}

	private void pathOrActionFinished() {
		assert currentJob != null : "currentJob should not be null here";

		EBuildingJobType type = currentJob.getType();
		switch (type) {
		case WAIT:
			super.setWaiting(currentJob.getTime());
			break;

		case WALK:
			walkAction();
			break;

		case SHOW:
			showAction();
			break;

		case HIDE:
			super.setVisible(false);
			super.setAction(EAction.NO_ACTION, -1);
			jobFinished();
			break;

		case SET_MATERIAL:
			super.setMaterial(currentJob.getMaterial());
			jobFinished();// start next action
			break;

		case TAKE:
			super.setAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
			this.building.popMaterial(super.getPos(), currentJob.getMaterial());
			break;

		case REMOTETAKE:
			if (this.building.popMaterial(getCurrentJobPos(), currentJob.getMaterial())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case DROP:
			dropAction();
			break;

		case PRE_SEARCH:
			searchDijkstraAction();
			break;

		case PRE_SEARCH_IN_AREA:
			searchInAreaAction();
			break;

		case LOOK_AT_SEARCHED:
			if (lookAtSearched()) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case IS_PRODUCTIVE:
			if (isProductive()) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case FOLLOW_SEARCHED:
			followSearchedAction();
			break;

		case EXECUTE:
			if (super.getGrid().fitsSearchType(super.getPos(), currentJob.getSearchType(), this)) {
				super.getGrid().executeSearchType(super.getPos(), currentJob.getSearchType());
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case GO_TO:
			gotoAction();
			break;

		case LOOK_AT:
			super.setDirection(currentJob.getDirection());
			jobFinished();
			break;

		case PLAY_ACTION1:
			super.setAction(EAction.ACTION1, currentJob.getTime());
			break;

		case PLAY_ACTION2:
			super.setAction(EAction.ACTION2, currentJob.getTime());
			break;

		case AVAILABLE:
			if (super.getGrid().canPop(getCurrentJobPos(), currentJob.getMaterial())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case NOT_FULL:
			if (super.getGrid().canPush(getCurrentJobPos())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case SMOKE_ON:
		case SMOKE_OFF: {
			ShortPoint2D pos = getCurrentJobPos();
			super.getGrid().placeSmoke(pos, type == EBuildingJobType.SMOKE_ON);
			jobFinished();
			break;
		}

		case START_WORKING:
		case STOP_WORKING:
			if (building instanceof MillBuilding) {
				((MillBuilding) building).setRotating(type == EBuildingJobType.START_WORKING);
			}
			jobFinished();
			break;

		case PIG_IS_ADULT: {
			ShortPoint2D pos = getCurrentJobPos();
			if (super.getGrid().isPigAdult(pos)) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;
		}

		case PIG_IS_THERE: {
			ShortPoint2D pos = getCurrentJobPos();
			if (super.getGrid().isPigThere(pos)) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;
		}

		case PIG_PLACE:
		case PIG_REMOVE: {
			ShortPoint2D pos = getCurrentJobPos();
			super.getGrid().placePig(pos, type == EBuildingJobType.PIG_PLACE);
			jobFinished();
			break;
		}

		case POP_TOOL: {
			ShortPoint2D pos = building.getDoor();
			lastPopped = super.getGrid().popToolProduction(pos);
			if (lastPopped != null) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;
		}

		case DROP_POPPED: {
			if (lastPopped != null) {
				dropAction();
			} else {
				jobFailed();
			}
			break;

		}

		default:
			System.err.println("unknown job type in BuildingWorkerStrategy: " + type);
			break;
		}
	}

	private ShortPoint2D getCurrentJobPos() {
		return building.calculateRealPoint(currentJob.getDx(), currentJob.getDy());
	}

	private boolean isProductive() {
		switch (building.getBuildingType()) {
		case FISHER:
			// TODO: look into the water, not at the sand.
			return hasProductiveResources(super.getPos(), EResourceType.FISH);
		case COALMINE:
			return hasProductiveResources(building.getDoor(), EResourceType.COAL);
		case IRONMINE:
			return hasProductiveResources(building.getDoor(), EResourceType.IRON);
		case GOLDMINE:
			return hasProductiveResources(building.getDoor(), EResourceType.GOLD);
		}
		return false;
	}

	private boolean hasProductiveResources(ShortPoint2D pos, EResourceType type) {
		float amount = getGrid().getResourceAmountAround(pos.getX(), pos.getY(), type);
		return RandomSingleton.get().nextFloat() < amount;
	}

	private boolean lookAtSearched() {
		if (currentJob.getSearchType() == ESearchType.FISHABLE) {
			for (EDirection direction : EDirection.values) {
				ShortPoint2D pos = direction.getNextHexPoint(super.getPos());
				if (super.getGrid().isInBounds(pos) && super.getGrid().getLandscapeTypeAt(pos).isWater()) {
					super.setDirection(direction);
					return true;
				}
			}
			return false;
		} else if (currentJob.getSearchType() == ESearchType.RIVER) {
			for (EDirection direction : EDirection.values) {
				ShortPoint2D pos = direction.getNextHexPoint(super.getPos());
				if (super.getGrid().isInBounds(pos)
						&& (super.getGrid().getLandscapeTypeAt(pos) == ELandscapeType.RIVER1
								|| super.getGrid().getLandscapeTypeAt(pos) == ELandscapeType.RIVER2
								|| super.getGrid().getLandscapeTypeAt(pos) == ELandscapeType.RIVER3 || super.getGrid().getLandscapeTypeAt(pos) == ELandscapeType.RIVER4)) {
					super.setDirection(direction);
					return true;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private void followSearchedAction() {
		if (!done) {
			assert path != null : "path needs to be PRE_SEARCHed!";
			super.setCalculatedPath(path);

			this.done = true;
		} else {
			super.getGrid().setMarked(path.getTargetPos(), false);
			path = null;
			super.setAction(EAction.NO_ACTION, -1);
			jobFinished();// start next action
		}
	}

	private void dropAction() {
		if (!done) {
			super.setAction(EAction.DROP, Constants.MOVABLE_TAKE_DROP_DURATION);
			this.done = true;
		}
	}

	private void searchInAreaAction() {
		super.setPos(getCurrentJobPos());
		ShortPoint2D workAreaCenter = new ShortPoint2D(building.getWorkAreaCenterX(), building.getWorkAreaCenterY());
		super.calculateInAreaPath(workAreaCenter, building.getBuildingType().getWorkradius(), currentJob.getSearchType());
	}

	private void searchDijkstraAction() {
		super.setPos(getCurrentJobPos());
		ShortPoint2D workAreaCenter = new ShortPoint2D(building.getWorkAreaCenterX(), building.getWorkAreaCenterY());
		super.calculateDijkstraPath(workAreaCenter, building.getBuildingType().getWorkradius(), currentJob.getSearchType());
	}

	private void gotoAction() {
		if (!done) {
			this.done = true;
			super.calculatePathTo(getCurrentJobPos());
		} else {
			super.setAction(EAction.NO_ACTION, -1);
			jobFinished();// start next action
		}
	}

	private void showAction() {
		ShortPoint2D pos = getCurrentJobPos();
		super.setPos(pos);
		super.setVisible(true);
		jobFinished();
	}

	private void walkAction() {
		EDirection dir = currentJob.getDirection();
		ShortPoint2D nextPos = dir.getNextHexPoint(super.getPos());

		goToTile(nextPos);
	}

	/**
	 * Checks if the given tile can be accessed. <br>
	 * if the nextPos can be accessed, {@link #jobFinished()} will be called <br>
	 * else {@link #jobFailed()} will be called.
	 * 
	 * @param nextPos
	 *            next position to be gone.
	 */
	@Override
	protected void goToTile(ShortPoint2D nextPos) {
		if (!done) {
			super.goToTile(nextPos);
			done = true;
		} else {
			jobFinished(); // start next action
		}
	}

	private void jobFailed() {
		this.currentJob = currentJob.getNextFailJob();
		this.done = false;
		pathOrActionFinished();
	}

	private void jobFinished() {
		this.currentJob = currentJob.getNextSucessJob();
		this.done = false;
		pathOrActionFinished();
	}

	@Override
	public EMovableType getMovableType() {
		return movableType;
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

	@Override
	protected boolean isPathStopable() {
		return false;
	}

	private void writeObject(ObjectOutputStream stream) throws IOException {
		stream.defaultWriteObject();
		if (currentJob == null) {
			stream.writeObject(null);
		} else {
			stream.writeObject(currentJob.getName());
		}
	}

	private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		stream.defaultReadObject();
		String jobname = (String) stream.readObject();
		if (jobname == null) {
			currentJob = null;
		} else {
			currentJob = building.getBuildingType().getJobByName(jobname);
		}
	}

	@Override
	public void buildingDestroyed() {
		this.currentJob = null;
		this.building = null;
		this.path = null;

		if (super.isFollowingPath()) {
			super.getGrid().setMarked(super.getTargetPos(), false);
			super.abortPath();
		}
		this.done = false;
		super.setVisible(true);
	}

	private void checkForDroppingMaterial() {
		EMaterialType material;
		material = super.getMaterial();

		if (material != null && material != EMaterialType.NO_MATERIAL) {
			super.setAction(EAction.DROP, Constants.MOVABLE_TAKE_DROP_DURATION);
			super.setMaterial(EMaterialType.NO_MATERIAL);
			super.getGrid().pushMaterial(super.getPos(), material, true);
		}

		makeJobless();
	}

	@Override
	public void setWorkerJob(IWorkerRequestBuilding building) {
		if (building.isNotDestroyed()) {
			this.building = building;
			this.currentJob = building.getBuildingType().getStartJob();
			this.done = false;
			this.building.occupyBuilding(this);
			recordedJobless = false;
		} else {
			super.getGrid().addJobless(this);
		}
	}

	@Override
	protected boolean checkGoStepPrecondition() {
		return building == null || building.isNotDestroyed();
	}

}
