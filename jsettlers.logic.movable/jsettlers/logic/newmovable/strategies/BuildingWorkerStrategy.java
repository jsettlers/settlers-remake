package jsettlers.logic.newmovable.strategies;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.NewMovableStrategy;
import random.RandomSingleton;

public final class BuildingWorkerStrategy extends NewMovableStrategy implements IManageableWorker {
	private static final long serialVersionUID = 5949318243804026519L;

	private final EMovableType movableType;

	private transient IBuildingJob currentJob = null;
	private IWorkerRequestBuilding building;

	private boolean done;
	private short delayCtr;

	private EMaterialType poppedMaterial;

	public BuildingWorkerStrategy(NewMovable movable, EMovableType movableType) {
		super(movable);
		this.movableType = movableType;

		reportAsJobless();
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		String currentJobName = ois.readUTF();
		if (currentJobName.equals("null")) {
			currentJob = null;
		} else {
			currentJob = building.getBuildingType().getJobByName(currentJobName);
		}
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		oos.defaultWriteObject();
		if (currentJob != null) {
			oos.writeUTF(currentJob.getName());
		} else {
			oos.writeUTF("null");
		}
	}

	@Override
	protected void action() {
		if (isJobless())
			return;

		if (!building.isNotDestroyed()) { // check if building is still ok
			buildingDestroyed();
			return;
		}

		// if (movableType == EMovableType.PIG_FARMER) {
		// System.err.println("Pigfarmer action() with jobType: " + currentJob.getType() + "  and name: " + currentJob.getName());
		// if (currentJob.getType() == EBuildingJobType.DROP) {
		// System.out.println();
		// }
		// }

		switch (currentJob.getType()) {
		case GO_TO:
			gotoAction();
			break;

		case IS_PRODUCTIVE:
			if (isProductive()) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case WAIT:
			waitSeconds();
			break;

		case WALK:
			super.forceGoInDirection(currentJob.getDirection());
			jobFinished();
			break;

		case SHOW:
			ShortPoint2D pos = getCurrentJobPos();
			super.setPosition(pos);
			super.setVisible(true);
			jobFinished();
			break;

		case HIDE:
			super.setVisible(false);
			jobFinished();
			break;

		case SET_MATERIAL:
			super.setMaterial(currentJob.getMaterial());
			jobFinished();
			break;

		case TAKE:
			takeAction();
			break;
		case REMOTETAKE:
			if (this.building.popMaterial(getCurrentJobPos(), currentJob.getMaterial())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case DROP:
			dropAction(currentJob.getMaterial());
			break;
		case DROP_POPPED:
			dropAction(poppedMaterial);
			break;

		case PRE_SEARCH:
			preSearchPathAction(true);
			break;

		case PRE_SEARCH_IN_AREA:
			preSearchPathAction(false);
			break;

		case FOLLOW_SEARCHED:
			followPreSearchedAction();
			break;

		case LOOK_AT_SEARCHED:
			lookAtSearched();
			break;
		case LOOK_AT:
			super.lookInDirection(currentJob.getDirection());
			jobFinished();
			break;

		case EXECUTE:
			executeAction();
			break;

		case PLAY_ACTION1:
			super.playAction(EAction.ACTION1, currentJob.getTime());
			jobFinished();
			break;
		case PLAY_ACTION2:
			super.playAction(EAction.ACTION2, currentJob.getTime());
			jobFinished();
			break;

		case AVAILABLE:
			if (super.getStrategyGrid().canPop(getCurrentJobPos(), currentJob.getMaterial())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case NOT_FULL:
			if (super.getStrategyGrid().canPushMaterial(getCurrentJobPos())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case SMOKE_ON:
		case SMOKE_OFF: {
			super.getStrategyGrid().placeSmoke(getCurrentJobPos(), currentJob.getType() == EBuildingJobType.SMOKE_ON);
			jobFinished();
			break;
		}

		case START_WORKING:
		case STOP_WORKING:
			if (building instanceof MillBuilding) {
				((MillBuilding) building).setRotating(currentJob.getType() == EBuildingJobType.START_WORKING);
			}
			jobFinished();
			break;

		case PIG_IS_ADULT:
			if (super.getStrategyGrid().isPigAdult(getCurrentJobPos())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case PIG_IS_THERE:
			if (super.getStrategyGrid().hasPigAt(getCurrentJobPos())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case PIG_PLACE:
		case PIG_REMOVE:
			placeOrRemovePigAction();
			break;

		case POP_TOOL:
			popToolRequestAction();
			break;

		}
	}

	private boolean isJobless() {
		return currentJob == null;
	}

	private void followPreSearchedAction() {
		super.followPresearchedPath();
		jobFinished();
	}

	private void placeOrRemovePigAction() {
		ShortPoint2D pos = getCurrentJobPos();
		super.getStrategyGrid().placePigAt(pos, currentJob.getType() == EBuildingJobType.PIG_PLACE);
		jobFinished();
	}

	private void popToolRequestAction() {
		ShortPoint2D pos = building.getDoor();
		poppedMaterial = super.getStrategyGrid().popToolProductionRequest(pos);
		if (poppedMaterial != null) {
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void executeAction() {
		if (super.getStrategyGrid().executeSearchType(super.getPos(), currentJob.getSearchType())) {
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void takeAction() {
		super.playAction(EAction.TAKE, Constants.MOVABLE_TAKE_DROP_DURATION);
		this.building.popMaterial(super.getPos(), currentJob.getMaterial());
		jobFinished();
	}

	private void dropAction(EMaterialType materialType) {
		if (!done) {
			super.playAction(EAction.DROP, Constants.MOVABLE_TAKE_DROP_DURATION);
			if (materialType == EMaterialType.NO_MATERIAL) { // if materialType == NO_MATERIAL then, don't drop anything, just play the animation
				jobFinished();
			} else {
				done = true;
			}
		} else {
			super.getStrategyGrid().dropMaterial(super.getPos(), materialType, true);
			jobFinished();
		}
	}

	/**
	 * 
	 * @param dijkstra
	 *            if true, dijkstra algorithm is used<br>
	 *            if false, in area finder is used.
	 */
	private void preSearchPathAction(boolean dijkstra) {
		super.setPosition(getCurrentJobPos());

		boolean pathFound = super.preSearchPath(dijkstra, building.getWorkAreaCenterX(), building.getWorkAreaCenterY(), building.getBuildingType()
				.getWorkradius(), currentJob.getSearchType());
		if (pathFound) {
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void waitSeconds() {
		if (!done) {
			done = true;
			delayCtr = (short) (currentJob.getTime() * Constants.MOVABLE_INTERRUPTS_PER_SECOND);
		} else {
			delayCtr--;
			if (delayCtr <= 0) {
				jobFinished();
			}
		}
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
		float amount = super.getStrategyGrid().getResourceAmountAround(pos.getX(), pos.getY(), type);
		return RandomSingleton.get().nextFloat() < amount;
	}

	private void gotoAction() {
		if (!done) {
			this.done = true;
			if (!super.goToPos(getCurrentJobPos())) {
				jobFailed();
			}
		} else {
			jobFinished(); // start next action
		}
	}

	private void jobFinished() {
		this.currentJob = this.currentJob.getNextSucessJob();
		done = false;
	}

	private void jobFailed() {
		this.currentJob = this.currentJob.getNextFailJob();
		done = false;
	}

	private ShortPoint2D getCurrentJobPos() {
		return building.calculateRealPoint(currentJob.getDx(), currentJob.getDy());
	}

	private void lookAtSearched() {
		EDirection direction = super.getStrategyGrid().getDirectionOfSearched(super.getPos(), currentJob.getSearchType());
		if (direction != null) {
			super.lookInDirection(direction);
			jobFinished();
		} else {
			jobFailed();
		}
	}

	@Override
	public EMovableType getMovableType() {
		return movableType;
	}

	@Override
	public void setWorkerJob(IWorkerRequestBuilding building) {
		this.building = building;
		this.currentJob = building.getBuildingType().getStartJob();
		super.enableNothingToDoAction(false);
		this.done = false;
		building.occupyBuilding(this);
	}

	@Override
	public void buildingDestroyed() {
		super.setVisible(true);

		reportAsJobless();

		dropCurrMaterial();
	}

	private void dropCurrMaterial() {
		EMaterialType material = super.getMaterial();
		if (material.isDroppable()) {
			super.getStrategyGrid().dropMaterial(super.getPos(), material, true);
			super.setMaterial(EMaterialType.NO_MATERIAL);
		}
	}

	private void reportAsJobless() {
		super.getStrategyGrid().addJobless(this);
		super.enableNothingToDoAction(true);
		this.currentJob = null;
		this.building = null;
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) { // used in overriding methods
		dropCurrMaterial();

		if (building != null) {
			building.leaveBuilding(this);
		}

		if (isJobless()) {
			super.getStrategyGrid().removeJobless(this);
		}
	}
}
