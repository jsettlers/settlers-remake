/*******************************************************************************
 * Copyright (c) 2015
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

import jsettlers.common.buildings.jobs.EBuildingJobType;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.EAction;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.messages.SimpleMessage;
import jsettlers.logic.buildings.workers.MillBuilding;
import jsettlers.logic.map.grid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class BuildingWorkerStrategy extends MovableStrategy implements IManageableWorker {
	private static final long serialVersionUID = 5949318243804026519L;

	private final EMovableType movableType;

	private transient IBuildingJob currentJob = null;
	protected IWorkerRequestBuilding building;

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

		case TRY_TAKING_RESOURCE:
			if (tryTakingResource()) {
				jobFinished();
			} else {
				jobFailed();
			}
			break;

		case TRY_TAKING_FOOD:
			if (building.tryTakingFoood(currentJob.getFoodOrder())) {
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
			super.forceGoInDirection(currentJob.getDirection());
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
			if (super.getStrategyGrid().canTakeMaterial(getCurrentJobPos(), currentJob.getMaterial())) {
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
			building.addMapObjectCleanupPosition(getCurrentJobPos(), EMapObjectType.SMOKE);
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

		case GROW_DONKEY:
			growDonkeyAction();
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
		building.addMapObjectCleanupPosition(pos, EMapObjectType.PIG);
		jobFinished();
	}

	private void growDonkeyAction() {
		ShortPoint2D pos = getCurrentJobPos();
		if (super.getStrategyGrid().feedDonkeyAt(pos)) {
			building.addMapObjectCleanupPosition(pos, EMapObjectType.DONKEY);
			jobFinished();
		} else {
			jobFailed();
		}
	}

	private void popWeaponRequestAction() {
		poppedMaterial = building.getMaterialProduction().getWeaponToProduce();
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
		if (super.getStrategyGrid().executeSearchType(super.getMovable(), super.getPos(), currentJob.getSearchType())) {
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
		jobFinished();
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

		boolean pathFound = super.preSearchPath(dijkstra, workAreaCenter.x, workAreaCenter.y, building.getBuildingType().getWorkradius(),
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
				super.getPlayer().showMessage(SimpleMessage.cannotFindWork(building));
			}
		}
	}

	private boolean tryTakingResource() {
		switch (building.getBuildingType()) {
		case FISHER:
			EDirection fishDirection = super.getMovable().getDirection();
			return super.getStrategyGrid().tryTakingRecource(fishDirection.getNextHexPoint(super.getPos()), EResourceType.FISH);
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

		dropCurrentMaterial();
	}

	private void dropCurrentMaterial() {
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
		dropCurrentMaterial();

		if (building != null) {
			building.leaveBuilding(this);
		}

		if (isJobless()) {
			super.getStrategyGrid().removeJobless(this);
		}
	}

	@Override
	protected void pathAborted(ShortPoint2D pathTarget) {
		if (currentJob != null) {
			jobFailed();
		}
	}
}
