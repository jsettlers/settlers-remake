package jsettlers.logic.movable.components;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.jobs.IBuildingJob;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.movable.ManageableWorkerWrapper;
import jsettlers.logic.movable.Notification;
import jsettlers.logic.movable.Requires;

/**
 * @author homoroselaps
 */
public class BuildingWorkerComponent extends Component {
	private static final long serialVersionUID = -5007619305126786807L;

	public static class BuildingDestroyed extends Notification { }

	private transient IBuildingJob currentJob = null;
	protected IWorkerRequestBuilding building;
	private int searchFailedCtr = 0;

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

	public EBuildingType getBuildingType() {
		if (building != null) {
			return building.getBuildingType();
		} else {
			return null;
		}
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
}
