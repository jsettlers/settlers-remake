package jsettlers.ai.highlevel;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.logic.buildings.Building;

public class AiStatistics {

	private final Queue<Building> buildings;
	private Map<Integer, Map<EBuildingType, Integer>> totalBuildingsNumbers;
	private Map<Integer, Integer> numberOfNotFinishedBuildings;
	
	AiStatistics() {
		this.buildings = Building.getAllBuildings();
	}
	
	public void updateStatistics() {
		totalBuildingsNumbers = new HashMap<Integer, Map<EBuildingType, Integer>>();
		numberOfNotFinishedBuildings = new HashMap<Integer, Integer>();
		for (Building building: buildings) {
			Integer playerId = new Integer(building.getPlayerId());
			EBuildingType type = building.getBuildingType();
			updateNumberOfNotFinishedBuildings(building, playerId);
			updateTotalBuildingsNumbers(playerId, type);
		}
		
	}

	private void updateTotalBuildingsNumbers(Integer playerId, EBuildingType type) {
		if (!totalBuildingsNumbers.containsKey(playerId)) {
			totalBuildingsNumbers.put(playerId, new HashMap<EBuildingType, Integer>());
		}
		if (!totalBuildingsNumbers.get(playerId).containsKey(type)) {
			totalBuildingsNumbers.get(playerId).put(type, 0);
		}
		totalBuildingsNumbers.get(playerId).put(type, totalBuildingsNumbers.get(playerId).get(type) + 1);
	}

	private void updateNumberOfNotFinishedBuildings(Building building, Integer playerId) {
		if (building.getStateProgress() < 1f) {
			if (!numberOfNotFinishedBuildings.containsKey(playerId)) {
				numberOfNotFinishedBuildings.put(playerId, 0);
			}
			numberOfNotFinishedBuildings.put(playerId, numberOfNotFinishedBuildings.get(playerId) + 1);
		}
	}
	
	public int getTotalNumberOfBuildingTypeForPlayer(EBuildingType type, byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!totalBuildingsNumbers.containsKey(playerIdInteger)) {
			return 0;
		}
		if (!totalBuildingsNumbers.get(playerIdInteger).containsKey(type)) {
			return 0;
		}
		return totalBuildingsNumbers.get(playerIdInteger).get(type);
	}
	
	public int getNumberOfNotFinishedBuildingsForPlayer(byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!numberOfNotFinishedBuildings.containsKey(playerIdInteger)) {
			return 0;
		}
		return numberOfNotFinishedBuildings.get(playerIdInteger);
	}
	
}
