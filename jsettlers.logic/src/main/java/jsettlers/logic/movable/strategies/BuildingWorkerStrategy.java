/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.movable.strategies;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.workers.DockyardBuilding;
import jsettlers.common.menu.messages.SimpleMessage;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.buildings.workers.SlaughterhouseBuilding;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.movable.EGoInDirectionMode;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

/**
 * @author Andreas Eberle
 */
public final class BuildingWorkerStrategy extends MovableStrategy implements IManageableWorker {
	private static final long serialVersionUID = 5949318243804026519L;

	private transient IBuildingJob currentJob = null;
	protected IWorkerRequestBuilding building;

	private boolean done;
	private boolean killed;

	private EMaterialType poppedMaterial;
	private int searchFailedCtr = 0;

	private ShortPoint2D markedPosition;

	public BuildingWorkerStrategy(Movable movable) {
		super(movable);
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
		if (isJobless()) {
			return;
		}

		if (building.isDestroyed()) { // check if building is still ok
			buildingDestroyed();
			return;
		}

		switch (currentJob.getType()) {
		case GO_TO:
			gotoAction();
			break;

		case TRY_TAKING_RESOURCE:
			clearMark();
			if (tryTakingResource()) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case TRY_TAKING_FOOD:
			if (building.tryTakingFood(currentJob.getFoodOrder())) {
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
			IBuildingJob job = currentJob;
			super.goInDirection(currentJob.getDirection(), EGoInDirectionMode.GO_IF_ALLOWED_WAIT_TILL_FREE);
			if (currentJob == job) { // the path could fail and call abortPath().
				jobFinished();
			}
			break;

		case SHOW: {
			if (building.getPriority() == EPriority.STOPPED) {
				break;
			}

			ShortPoint2D pos = getCurrentJobPos();
			if (currentJob.getDirection() != null) {
				super.lookInDirection(currentJob.getDirection());
			}
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

		case GO_TO_DOCK:
			gotoDockAction();
			break;

		case BUILD_SHIP:
			if (building instanceof DockyardBuilding) {
				((DockyardBuilding) building).buildShipAction();
			}
			jobFinished();
			break;

		case LOOK_AT:
			super.lookInDirection(currentJob.getDirection());
			jobFinished();
			break;

		case EXECUTE:
			executeAction();
			break;

		case PLAY_ACTION1:
			super.playAction(EMovableAction.ACTION1, currentJob.getTime());
			jobFinished();
			break;
		case PLAY_ACTION2:
			super.playAction(EMovableAction.ACTION2, currentJob.getTime());
			jobFinished();
			break;
		case PLAY_ACTION3:
			super.playAction(EMovableAction.ACTION3, currentJob.getTime());
			jobFinished();
			break;

		case AVAILABLE:
			if (super.getGrid().canTakeMaterial(getCurrentJobPos(), currentJob.getMaterial())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case NOT_FULL:
			if (super.getGrid().canPushMaterial(getCurrentJobPos())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case SMOKE_ON:
		case SMOKE_OFF: {
			super.getGrid().placeSmoke(getCurrentJobPos(), currentJob.getType() == EBuildingJobType.SMOKE_ON);
			building.addMapObjectCleanupPosition(getCurrentJobPos(), EMapObjectType.SMOKE);
			jobFinished();
			break;
		}

		case START_WORKING:
		case STOP_WORKING:
			if (building instanceof SlaughterhouseBuilding) {
				((SlaughterhouseBuilding) building).requestSound();
			}
			if (building instanceof MillBuilding) {
				((MillBuilding) building).setRotating(currentJob.getType() == EBuildingJobType.START_WORKING);
			}
			jobFinished();
			break;

		case PIG_IS_ADULT:
			if (super.getGrid().isPigAdult(getCurrentJobPos())) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case PIG_IS_THERE:
			if (super.getGrid().hasPigAt(getCurrentJobPos())) {
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

		case GROW_DONKEY:
			growDonkeyAction();
			break;
		}
	}

	private boolean isJobless() {
		return currentJob == null;
	}

	private void gotoDockAction() {
		DockyardBuilding dockyard = (DockyardBuilding) building;
		if (!done) {
			this.done = true;
			ShortPoint2D dockEndPosition = dockyard.getDock().getEndPosition();
			if (!super.goToPos(dockEndPosition)) {
				jobFailed();
			}
		} else {
			jobFinished(); // start next action
		}
	}

	private void followPreSearchedAction() {
		ShortPoint2D pathTargetPos = super.followPresearchedPath();
		mark(pathTargetPos);
		jobFinished();
	}

	private void placeOrRemovePigAction() {
		ShortPoint2D pos = getCurrentJobPos();
		super.getGrid().placePigAt(pos, currentJob.getType() == EBuildingJobType.PIG_PLACE);
		building.addMapObjectCleanupPosition(pos, EMapObjectType.PIG);
		jobFinished();
	}

	private void growDonkeyAction() {
		ShortPoint2D pos = getCurrentJobPos();
		if (super.getGrid().feedDonkeyAt(pos)) {
			building.addMapObjectCleanupPosition(pos, EMapObjectType.DONKEY);
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void popWeaponRequestAction() {
		poppedMaterial = building.getMaterialProduction().getWeaponToProduce();

		if (poppedMaterial != null) {
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void popToolRequestAction() {
		ShortPoint2D pos = building.getDoor();

		poppedMaterial = building.getMaterialProduction().drawRandomAbsolutelyRequestedTool(); // first priority: Absolutely set tool production requests of user
		if (poppedMaterial == null) {
			poppedMaterial = super.getGrid().popToolProductionRequest(pos); // second priority: Tools needed by settlers (automated production)
		}
		if (poppedMaterial == null) {
			poppedMaterial = building.getMaterialProduction().drawRandomRelativelyRequestedTool(); // third priority: Relatively set tool production requests of user
		}

		if (poppedMaterial != null) {
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void executeAction() {
		clearMark();
		if (super.getGrid().executeSearchType(movable, movable.getPosition(), currentJob.getSearchType())) {
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void takeAction() {
		if (super.take(currentJob.getMaterial(), currentJob.isTakeMaterialFromMap())) {
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void dropAction(EMaterialType materialType) {
		super.drop(materialType);
		if (materialType == EMaterialType.GOLD) {
			movable.getPlayer().getEndgameStatistic().incrementAmountOfProducedGold();
		}
		jobFinished();
	}

	/**
	 * @param dijkstra
	 * 		if true, dijkstra algorithm is used<br>
	 * 		if false, in area finder is used.
	 */
	private void preSearchPathAction(boolean dijkstra) {
		super.setPosition(getCurrentJobPos());

		ShortPoint2D workAreaCenter = building.getWorkAreaCenter();

		boolean pathFound = super.preSearchPath(dijkstra, workAreaCenter.x, workAreaCenter.y, building.getBuildingType().getWorkRadius(),
				currentJob.getSearchType());

		if (pathFound) {
			jobFinished();
			searchFailedCtr = 0;
			this.building.setCannotWork(false);
		} else {
			jobFailed();
			searchFailedCtr++;

			if (searchFailedCtr > 10) {
				this.building.setCannotWork(true);
				movable.getPlayer().showMessage(SimpleMessage.cannotFindWork(building));
			}
		}
	}

	private boolean tryTakingResource() {
		switch (building.getBuildingType()) {
		case FISHER:
			EDirection fishDirection = movable.getDirection();
			return super.getGrid().tryTakingResource(fishDirection.getNextHexPoint(movable.getPosition()), EResourceType.FISH);
		case COALMINE:
		case IRONMINE:
		case GOLDMINE:
			return building.tryTakingResource();

		default:
			return false;
		}
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
		return currentJob.calculatePoint(building);
	}

	private void lookAtSearched() {
		EDirection direction = super.getGrid().getDirectionOfSearched(movable.getPosition(), currentJob.getSearchType());
		if (direction != null) {
			super.lookInDirection(direction);
			jobFinished();
		} else {
			jobFailed();
		}
	}

	@Override
	public EMovableType getMovableType() {
		return movable.getMovableType();
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
		super.abortPath();

		reportAsJobless();
		dropCurrentMaterial();
		clearMark();
	}

	private void dropCurrentMaterial() {
		EMaterialType material = movable.getMaterial();
		if (material.isDroppable()) {
			super.getGrid().dropMaterial(movable.getPosition(), material, true, false);
		}
		super.setMaterial(EMaterialType.NO_MATERIAL);
	}

	private void reportAsJobless() {
		super.getGrid().addJobless(this);
		super.enableNothingToDoAction(true);
		this.currentJob = null;
		this.building = null;
	}

	private void mark(ShortPoint2D position) {
		clearMark();
		markedPosition = position;
		super.getGrid().setMarked(position, true);
	}

	private void clearMark() {
		if (markedPosition != null) {
			super.getGrid().setMarked(markedPosition, false);
			markedPosition = null;
		}
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) { // used in overriding methods
		killed = true;
		dropCurrentMaterial();

		if (isJobless()) {
			super.getGrid().removeJobless(this);
		} else {
			super.enableNothingToDoAction(true);
			currentJob = null;
		}

		if (building != null) {
			building.leaveBuilding(this);
		}

		clearMark();
	}

	@Override
	protected void pathAborted(ShortPoint2D pathTarget) {
		if (currentJob != null) {
			jobFailed();
		}
		clearMark();
	}

	@Override
	protected boolean checkPathStepPreconditions(ShortPoint2D pathTarget, int step) {
		return isJobless() || building != null;
	}

	@Override
	public boolean isAlive() {
		return !killed;
	}

	@Override
	public EBuildingType getBuildingType() {
		if (building != null) {
			return building.getBuildingType();
		} else {
			return null;
		}
	}
}
