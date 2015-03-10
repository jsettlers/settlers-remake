package jsettlers.logic.movable.strategies;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.messages.SimpleMessage;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;
import networklib.synchronic.random.RandomSingleton;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class BuildingWorkerStrategy extends MovableStrategy implements IManageableWorker {
	private static final long serialVersionUID = 5949318243804026519L;

	private final EMovableType movableType;

	private transient IBuildingJob currentJob = null;
	private IWorkerRequestBuilding building;

	private boolean done;

	private EMaterialType poppedMaterial;
	private int searchFailedCtr = 0;

	public BuildingWorkerStrategy(Movable movable, EMovableType movableType) {
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

		switch (currentJob.getType()) {
		case GO_TO:
			gotoAction();
			break;

		case IS_PRODUCTIVE:
			if (isProductive()) {
				decreaseResourceAmount();
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case WAIT: {
			short waitTime = (short) (currentJob.getTime() * 1000);
			super.sleep(waitTime);
			jobFinished();
			break;
		}

		case WALK:
			super.forceGoInDirection(currentJob.getDirection());
			jobFinished();
			break;

		case SHOW: {
			if (building.getPriority() == EPriority.STOPPED) {
				break;
			}

			ShortPoint2D pos = getCurrentJobPos();
			super.setPosition(pos);
			super.setVisible(true);
			jobFinished();
			break;
		}

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

		case POP_WEAPON:
			popWeaponRequestAction();
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

	private void popWeaponRequestAction() {
		float random = RandomSingleton.nextF();
		if (random < 0.5) {
			poppedMaterial = EMaterialType.SWORD;
		} else if (random < 0.65) {
			poppedMaterial = EMaterialType.SPEAR;
		} else {
			poppedMaterial = EMaterialType.BOW;
		}
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

		ShortPoint2D workAreaCenter = building.getWorkAreaCenter();

		boolean pathFound = super.preSearchPath(dijkstra, workAreaCenter.x, workAreaCenter.y, building.getBuildingType()
				.getWorkradius(), currentJob.getSearchType());

		if (pathFound) {
			jobFinished();
			searchFailedCtr = 0;
		} else {
			jobFailed();
			searchFailedCtr++;

			if (searchFailedCtr > 10) {
				super.getPlayer().showMessage(SimpleMessage.cannotFindWork(building));
			}
		}
	}

	private boolean isProductive() {
		switch (building.getBuildingType()) {
		case FISHER:
			EDirection fishDirection = super.getMovable().getDirection();
			return hasProductiveResource(fishDirection.getNextHexPoint(super.getPos()), EResourceType.FISH, 1);
		case COALMINE:
			return hasProductiveResource(building.getPos(), EResourceType.COAL, 1);
		case IRONMINE:
			return hasProductiveResource(building.getPos(), EResourceType.IRON, 1);
		case GOLDMINE:
			return hasProductiveResource(building.getPos(), EResourceType.GOLD, 1);

		default:
			return false;
		}
	}

	private boolean hasProductiveResource(ShortPoint2D position, EResourceType type, int radius) {
		float percentage = super.getStrategyGrid().getResourceProbabilityAround(position.x, position.y, type, radius);
		return RandomSingleton.get().nextFloat() < percentage;
	}

	private void decreaseResourceAmount() {
		switch (building.getBuildingType()) {
		case FISHER:
			EDirection fishDirection = super.getMovable().getDirection();
			decreaseResourceAround(fishDirection.getNextHexPoint(super.getPos()), EResourceType.FISH, 1);
			break;
		case COALMINE:
			decreaseResourceAround(building.getPos(), EResourceType.COAL, 1);
			break;
		case IRONMINE:
			decreaseResourceAround(building.getPos(), EResourceType.IRON, 1);
			break;
		case GOLDMINE:
			decreaseResourceAround(building.getPos(), EResourceType.GOLD, 1);
			break;
		default:
			break;
		}
	}

	private void decreaseResourceAround(ShortPoint2D position, EResourceType resourceType, int radius) {
		super.getStrategyGrid().decreaseResourceAround(position.x, position.y, resourceType, radius, 1);
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
