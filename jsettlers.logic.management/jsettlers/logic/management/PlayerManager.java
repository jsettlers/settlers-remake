package jsettlers.logic.management;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.bearer.BearerJobCenter;
import jsettlers.logic.management.bearer.BearerJobCreator;
import jsettlers.logic.management.bearer.IBearerJobable;
import jsettlers.logic.management.workers.IWorkerJobable;
import jsettlers.logic.management.workers.WorkerJobCenter;
import jsettlers.logic.management.workers.building.BuildingWorkerRequest;
import jsettlers.logic.management.workers.building.IWorkerRequestBuilding;
import jsettlers.logic.management.workers.construction.AbstractConstructionWorkerRequest;
import jsettlers.logic.management.workers.construction.BricklayerRequest;
import jsettlers.logic.management.workers.construction.DiggerRequest;
import jsettlers.logic.management.workers.construction.IConstructableBuilding;

class PlayerManager {
	private final BearerJobCenter bearerJobCenter;
	private final BearerJobCreator bearerJobCreator;
	private final WorkerJobCenter<BuildingWorkerRequest> buildingWorkerJobCenter;
	private final WorkerJobCenter<AbstractConstructionWorkerRequest> consturctionWorkerJobCenter;

	public PlayerManager() {
		this.bearerJobCenter = new BearerJobCenter();
		bearerJobCenter.start();

		this.bearerJobCreator = new BearerJobCreator(this.bearerJobCenter);
		bearerJobCreator.start();

		this.buildingWorkerJobCenter = new WorkerJobCenter<BuildingWorkerRequest>();
		buildingWorkerJobCenter.start();

		this.consturctionWorkerJobCenter = new WorkerJobCenter<AbstractConstructionWorkerRequest>();
		consturctionWorkerJobCenter.start();
	}

	public void cancelMangement() {
		bearerJobCenter.cancel();
		bearerJobCreator.cancel();
		buildingWorkerJobCenter.cancel();
		consturctionWorkerJobCenter.cancel();
	}

	public void addJobless(IBearerJobable jobless) {
		bearerJobCenter.addJobless(jobless);
	}

	public void requestMovable(EMovableType movableType) {
		bearerJobCreator.requestMovable(movableType);
	}

	public void offerMaterial(MaterialJobPart offer) {
		bearerJobCreator.offer(offer);
	}

	public void requestMaterial(MaterialJobPart request) {
		bearerJobCreator.requestMaterial(request);
	}

	public void addJoblessWorker(IWorkerJobable<AbstractConstructionWorkerRequest> jobless) {
		consturctionWorkerJobCenter.addJobless(jobless);
	}

	public void addJoblessBuildingWorker(IWorkerJobable<BuildingWorkerRequest> jobless) {
		buildingWorkerJobCenter.addJobless(jobless);
	}

	public void requestBuildingWorker(EMovableType type, IWorkerRequestBuilding building) {
		buildingWorkerJobCenter.request(new BuildingWorkerRequest(type, building));
	}

	public void requestBricklayer(IConstructableBuilding constructionSite, ISPosition2D position, EDirection lookDirection) {
		consturctionWorkerJobCenter.request(new BricklayerRequest(constructionSite, position, lookDirection));
	}

	public void requestDigger(FreeMapArea buildingArea, byte heightAvg, byte player) {
		consturctionWorkerJobCenter.request(new DiggerRequest(buildingArea, heightAvg, player));
	}
}
