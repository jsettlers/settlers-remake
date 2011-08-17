package jsettlers.logic.movable.workers;

import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.management.GameManager;
import jsettlers.logic.management.workers.IWorkerJobable;
import jsettlers.logic.management.workers.building.BuildingWorkerRequest;
import jsettlers.logic.management.workers.building.IWorkerRequestBuilding;
import jsettlers.logic.map.hex.HexGrid;
import jsettlers.logic.map.hex.HexTile;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.PathableStrategy;
import random.RandomSingleton;

public class BuildingWorkerStrategy extends PathableStrategy implements
        IWorkerJobable<BuildingWorkerRequest> {

	private final EMovableType movableType;
	private IBuildingJob currentJob;

	private boolean done;
	private IWorkerRequestBuilding building;
	private Path path;

	public BuildingWorkerStrategy(Movable movable, EMovableType movableType) {
		super(movable);
		this.movableType = movableType;
		GameManager.addJoblessBuildingWorker(this);
	}

	@Override
	public boolean needsPlayersGround() {
		return true;
	}

	@Override
	protected void pathRequestFailed() {
		super.setAction(EAction.NO_ACTION, -1);
		path = null;
		if (currentJob != null)
			jobFailed();
	}

	@Override
	protected void setCalculatedPath(Path path) {
		if (currentJob.getType() == EBuildingJobType.PRE_SEARCH
		        || currentJob.getType() == EBuildingJobType.PRE_SEARCH_IN_AREA) {
			this.path = path;
			HexGrid.get().setMarked(path.getLastTile(), true);
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
				if (currentJob.getType() == EBuildingJobType.DROP
				        && currentJob.getMaterial() != EMaterialType.NO_MATERIAL) {
					success =
					        HexGrid.get().pushMaterial(super.getPos(),
					                currentJob.getMaterial());
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
		pathOrActionFinished();
	}

	private void pathOrActionFinished() {
		assert currentJob != null : "currentJob should not be null here";

		switch (currentJob.getType()) {
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
				super.setAction(EAction.TAKE,
				        Constants.MOVABLE_TAKE_DROP_DURATION);
				HexGrid.get().popMaterial(super.getPos(),
				        currentJob.getMaterial());
				break;

			case DROP:
				dropAction();
				break;

			case PRE_SEARCH:
				searchAction();
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
				if (HexGrid.get().fitsSearchType(super.getPos(),
				        currentJob.getSearchType(), this)) {
					HexGrid.get().executeSearchType(super.getPos(),
					        currentJob.getSearchType());
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
				if (HexGrid.get().canPop(
				        building.calculateRealPoint(currentJob.getDx(),
				                currentJob.getDy()), currentJob.getMaterial())) {
					jobFinished();
				} else {
					jobFailed();
				}
				break;

			case NOT_FULL:
				if (HexGrid.get().canPush(
				        building.calculateRealPoint(currentJob.getDx(),
				                currentJob.getDy()), currentJob.getMaterial())) {
					jobFinished();
				} else {
					jobFailed();
				}
				break;

			default:
				System.err
				        .println("unknown job type in BuildingWorkerStrategy: "
				                + currentJob.getType());
				break;
		}
	}

	private boolean isProductive() {
		// TODO: weight
		return RandomSingleton.get().nextBoolean();
	}

	private boolean lookAtSearched() {
		if (currentJob.getSearchType() == ESearchType.FISHABLE) {
			HexGrid grid = HexGrid.get();
			for (EDirection direction : EDirection.values()) {
				HexTile tile =
				        grid.getTile(direction.getNextHexPoint(super.getPos()));
				if (tile != null
				        && tile.getLandscapeType() == ELandscapeType.WATER) {
					super.setDirection(direction);
					return true;
				}
			}
			return false;
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
			HexGrid.get().setMarked(path.getLastTile(), false);
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
		super.setPos(building.calculateRealPoint(currentJob.getDx(),
		        currentJob.getDy()));
		super.calculateInAreaPath(building.getWorkAreaCenter(), building
		        .getBuildingType().getWorkradius(), currentJob.getSearchType());
	}

	private void searchAction() {
		super.setPos(building.calculateRealPoint(currentJob.getDx(),
		        currentJob.getDy()));
		super.calculateDijkstraPath(building.getWorkAreaCenter(), building
		        .getBuildingType().getWorkradius(), currentJob.getSearchType());
	}

	private void gotoAction() {
		if (!done) {
			this.done = true;
			super.calculatePathTo(building.calculateRealPoint(
			        currentJob.getDx(), currentJob.getDy()));
		} else {
			super.setAction(EAction.NO_ACTION, -1);
			jobFinished();// start next action
		}
	}

	private void showAction() {
		ISPosition2D pos =
		        building.calculateRealPoint(currentJob.getDx(),
		                currentJob.getDy());
		if (!HexGrid.get().isBlocked(this, pos.getX(), pos.getY())) {
			super.setPos(pos);
			super.setVisible(true);
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void walkAction() {
		EDirection dir = currentJob.getDirection();
		ISPosition2D nextPos = dir.getNextHexPoint(super.getPos());

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
	protected void goToTile(ISPosition2D nextPos) {
		if (!done) {
			if (!HexGrid.get().isBlocked(this, nextPos.getX(), nextPos.getY())) {
				super.goToTile(nextPos);
				done = true;
			} else {
				jobFailed(); // start next action
			}
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
	public void setWorkerRequest(BuildingWorkerRequest request) {
		this.building = request.getBuilding();
		this.currentJob = building.getBuildingType().getStartJob();
		this.done = false;
		pathOrActionFinished();
	}

	@Override
	protected void stopOrStartWorking(boolean stop) {
		// don't care
	}

	@Override
	protected boolean isGotoJobable() {
		return false;
	}

}
