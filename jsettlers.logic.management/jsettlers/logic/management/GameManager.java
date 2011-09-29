package jsettlers.logic.management;

import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.management.bearer.IBearerJobable;
import jsettlers.logic.management.workers.IWorkerJobable;
import jsettlers.logic.management.workers.building.BuildingWorkerRequest;
import jsettlers.logic.management.workers.building.IWorkerRequestBuilding;
import jsettlers.logic.management.workers.construction.AbstractConstructionWorkerRequest;
import jsettlers.logic.management.workers.construction.IConstructableBuilding;

/**
 * This is the manager that watches over all free workers, ...
 * <p>
 * 
 * @author Andreas Eberle
 */
public class GameManager {
	public static GameManager uniInstance = null;

	public static void start(byte players) {
		if (uniInstance == null) {
			uniInstance = new GameManager(players);
		}
	}

	private final PartitionManager[] managers;

	public static void cancel() {
		uniInstance.cancelMangement();
	}

	public GameManager(byte numberOfPlayers) {
		this.managers = new PartitionManager[numberOfPlayers];

		for (int i = 0; i < numberOfPlayers; i++) {
			this.managers[i] = new PartitionManager();
		}
	}

	private void cancelMangement() {
		for (int i = 0; i < managers.length; i++) {
			managers[i].cancelMangement();
		}
	}

	public static void addJobless(IBearerJobable jobless) {
		uniInstance.managers[jobless.getPlayer()].addJobless(jobless);
	}

	public static void requestMovable(EMovableType movableType, byte player) {
		uniInstance.managers[player].requestMovable(movableType);
	}

	public static void offerMaterial(MaterialJobPart offer) {
		uniInstance.managers[offer.getPlayer()].offerMaterial(offer);
	}

	public static void requestMaterial(MaterialJobPart request) {
		uniInstance.managers[request.getPlayer()].requestMaterial(request);
	}

	public static void addJoblessWorker(IWorkerJobable<AbstractConstructionWorkerRequest> jobless) {
		uniInstance.managers[jobless.getPlayer()].addJoblessWorker(jobless);
	}

	public static void addJoblessBuildingWorker(IWorkerJobable<BuildingWorkerRequest> jobless) {
		uniInstance.managers[jobless.getPlayer()].addJoblessBuildingWorker(jobless);
	}

	public static void requestBuildingWorker(EMovableType type, IWorkerRequestBuilding building) {
		uniInstance.managers[building.getPlayer()].requestBuildingWorker(type, building);
	}

	public static void requestBricklayer(IConstructableBuilding building, ISPosition2D position, EDirection lookDirection) {
		uniInstance.managers[building.getPlayer()].requestBricklayer(building, position, lookDirection);
	}

	public static void requestDigger(FreeMapArea buildingArea, byte heightAvg, byte player) {
		uniInstance.managers[player].requestDigger(buildingArea, heightAvg, player);
	}
}
