package jsettlers.logic.movable.components;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.algorithms.path.Path;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.menu.messages.SimpleMessage;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.DockPosition;
import jsettlers.logic.buildings.workers.DockyardBuilding;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.movable.Context;
import jsettlers.logic.movable.ManageableWorkerWrapper;
import jsettlers.logic.movable.Notification;
import jsettlers.logic.movable.Requires;

/**
 * @author homoroselaps
 */
@Requires({
	GameFieldComponent.class,
	MovableComponent.class,
	SteeringComponent.class,
})
public class BuildingWorkerComponent extends Component {
	private static final long serialVersionUID = -5007619305126786807L;
	private ShortPoint2D markedPosition;

	public static class BuildingDestroyed extends Notification { }

	private transient IBuildingJob currentJob = null;
	protected IWorkerRequestBuilding building;
	private EMaterialType poppedMaterial = EMaterialType.NO_MATERIAL;
	private Path preSearchedPath = null;
	private int searchFailedCount = 0;

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
	protected void onWakeUp() {
		reportAsJobless();
	}

	public boolean hasJob() { return currentJob != null; }

	public IBuildingJob getCurrentJob() { return currentJob; }

    public ShortPoint2D getCurrentJobPos() {
        return currentJob.calculatePoint(building);
    }

	public void jobFinished() {
		this.currentJob = this.currentJob.getNextSucessJob();
	}

	public void jobFailed() {
		this.currentJob = this.currentJob.getNextFailJob();
	}

	public EMaterialType getPoppedMaterial() {
		return poppedMaterial;
	}

	public void setPoppedMaterial(EMaterialType material) {
		poppedMaterial = material;
	}

	public Path getPreSearchedPath() {
		return preSearchedPath;
	}

	public boolean preSearchPath(boolean searchInArea) {
		assert entity.movableComponent().getPosition() == getCurrentJob();

		entity.movableComponent().setPos(getCurrentJobPos());
		ShortPoint2D workAreaCenter = building.getWorkAreaCenter();
		preSearchedPath = entity.steeringComponent().preSearchPath(!searchInArea, workAreaCenter.x, workAreaCenter.y, building.getBuildingType().getWorkRadius(), currentJob.getSearchType());
		return preSearchedPath != null;
	}

	public void workSearchSucceeded() {
		searchFailedCount = 0;
		this.building.setCannotWork(false);
	}

	public void workSearchFailed() {
		searchFailedCount++;
		if (searchFailedCount > 10) {
			this.building.setCannotWork(true);
			entity.movableComponent().getPlayer().showMessage(SimpleMessage.cannotFindWork(building));
		}
	}

	public void mark(ShortPoint2D position) {
		clearMark();
		markedPosition = position;
		entity.gameFieldComponent().movableGrid.setMarked(position, true);
	}

	public void clearMark() {
		if (markedPosition != null) {
			entity.gameFieldComponent().movableGrid.setMarked(markedPosition, false);
			markedPosition = null;
		}
	}

	public EPriority getBuildingPriority() {
		if (building != null) {
			return building.getPriority();
		} else {
			return null;
		}
	}

	public void addMapObjectCleanupPosition(ShortPoint2D pos, EMapObjectType objectType){
		building.addMapObjectCleanupPosition(pos, objectType);
	}

	public EBuildingType getBuildingType() {
		if (building != null) {
			return building.getBuildingType();
		} else {
			return null;
		}
	}

	public IWorkerRequestBuilding getBuilding() {
		return building;
	}

	public void setWorkerJob(IWorkerRequestBuilding building) {
		this.building = building;
		this.currentJob = building.getBuildingType().getStartJob();
		this.building.occupyBuilding(new ManageableWorkerWrapper(entity));
	}

	public void buildingDestroyed() {
		entity.raiseNotification(new BuildingDestroyed());
	}

	public void reportAsJobless() {
		entity.gameFieldComponent().movableGrid.addJobless(new ManageableWorkerWrapper(entity));
		currentJob = null;
		building = null;
	}

	public boolean tryTakingResource() {
		switch (building.getBuildingType()) {
			case FISHER:
				MovableComponent movableComponent = this.entity.movableComponent();
				EDirection fishDirection = movableComponent.getViewDirection();
				return this.entity.gameFieldComponent().movableGrid.tryTakingResource(fishDirection.getNextHexPoint(movableComponent.getPosition()), EResourceType.FISH);
			case COALMINE:
			case IRONMINE:
			case GOLDMINE:
				return building.tryTakingResource();
			default:
				return false;
		}
	}

	public boolean tryTakingFood() {
		return building.tryTakingFood(currentJob.getFoodOrder());
	}
}
