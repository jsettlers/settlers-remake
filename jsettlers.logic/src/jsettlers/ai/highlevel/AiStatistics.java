package jsettlers.ai.highlevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.objects.ObjectsGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.logic.player.Player;

public class AiStatistics {

	private final Queue<Building> buildings;
	private Map<Integer, Map<EBuildingType, Integer>> totalBuildingsNumbers;
	private Map<Integer, Integer> numberOfNotFinishedBuildings;
	private Map<Integer, List<ShortPoint2D>> stones;
	private Map<Integer, List<ShortPoint2D>> trees;
    private final MainGrid mainGrid;
	private final ObjectsGrid objectsGrid;
	private final PartitionsGrid partitionsGrid;
	
	public AiStatistics(MainGrid mainGrid) {
		this.buildings = Building.getAllBuildings();
		this.mainGrid = mainGrid;
		this.objectsGrid = mainGrid.getObjectsGrid();
		this.partitionsGrid = mainGrid.getPartitionsGrid();
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

	public List<ShortPoint2D> getStonesForPlayer(byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!stones.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return stones.get(playerIdInteger);
	}
	
	public List getTreesForPlayer(byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!trees.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return trees.get(playerIdInteger);
	}

	public void updateStatistics() {
		updateBuildingStatistics();
		updateMapObjectsStatistics();
	}
	
	private void updateMapObjectsStatistics() {
		stones = new HashMap<Integer, List<ShortPoint2D>>();
		trees = new HashMap<Integer, List<ShortPoint2D>>();
		for(short x = 0; x < mainGrid.getWidth(); x++) {
			for(short y = 0; y < mainGrid.getHeight(); y++) {
				Player player = partitionsGrid.getPlayerAt(x, y);
				if (player != null) {
					Integer playerId = new Integer(partitionsGrid.getPlayerAt(x, y).playerId);
					if (!stones.containsKey(playerId)) {
						stones.put(playerId, new ArrayList<ShortPoint2D>());
					}
					if (!trees.containsKey(playerId)) {
						trees.put(playerId, new ArrayList<ShortPoint2D>());
					}
					if (objectsGrid.hasCuttableObject(x, y, EMapObjectType.STONE)) {
						stones.get(playerId).add(new ShortPoint2D(x, y));
					}
					if (objectsGrid.hasCuttableObject(x, y, EMapObjectType.TREE_ADULT)) {
						trees.get(playerId).add(new ShortPoint2D(x, y));
					}
				}
			}
		}
	}
	
	private void updateBuildingStatistics() {
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
}
