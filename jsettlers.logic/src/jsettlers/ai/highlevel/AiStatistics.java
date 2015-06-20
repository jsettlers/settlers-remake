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
package jsettlers.ai.highlevel;

import static jsettlers.common.buildings.EBuildingType.FARM;
import static jsettlers.common.buildings.EBuildingType.LUMBERJACK;
import static jsettlers.common.buildings.EBuildingType.WINEGROWER;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L1;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L2;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.map.grid.objects.ObjectsGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.player.Player;

public class AiStatistics {

	private static final short BORDER_LAND_WIDTH = 10;

	private final Queue<Building> buildings;
	private Map<Integer, Map<EBuildingType, Integer>> totalBuildingsNumbers;
	private Map<Integer, Map<EBuildingType, Integer>> buildingsNumbers;
	private Map<Integer, Map<EMovableType, List<ShortPoint2D>>> movablePositions;
	private Map<Integer, Integer> numberOfNotFinishedBuildings;
	private Map<Integer, Integer> numberOfTotalBuildings;
	private Map<Integer, Integer> numberOfNotOccupiedTowers;
	private Map<Integer, List<ShortPoint2D>> stones;
	private Map<Integer, List<ShortPoint2D>> trees;
	private Map<Integer, List<ShortPoint2D>> land;
	private Map<Integer, List<ShortPoint2D>> borderLandNextToFreeLand;
	private Map<Integer, Map<EBuildingType, List<ShortPoint2D>>> buildingPositions;
	private final MainGrid mainGrid;
	private final LandscapeGrid landscapeGrid;
	private final ObjectsGrid objectsGrid;
	private final PartitionsGrid partitionsGrid;
	private final MovableGrid movableGrid;

	public AiStatistics(MainGrid mainGrid) {
		this.buildings = Building.getAllBuildings();
		this.mainGrid = mainGrid;
		this.landscapeGrid = mainGrid.getLandscapeGrid();
		this.objectsGrid = mainGrid.getObjectsGrid();
		this.partitionsGrid = mainGrid.getPartitionsGrid();
		this.movableGrid = mainGrid.getMovableGrid();
	}

	public ShortPoint2D getNearestResourcePointInDefaultPartitionFor(ShortPoint2D point, EResourceType resourceType) {
		Map<Integer, List<Integer>> sortedResourcePoints = landscapeGrid.getSortedMapForResourceType(resourceType);
		return getNearestPointInDefaultPartitionOutOfSortedMap(point, sortedResourcePoints);
	}

	private ShortPoint2D getNearestPointInDefaultPartitionOutOfSortedMap(ShortPoint2D point, Map<Integer, List<Integer>> sortedPoints) {
		ShortPoint2D result = null;
		double currentNearestPointDistance = Double.MAX_VALUE;
		ShortPoint2D nearestRightPoint = getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(point, sortedPoints, currentNearestPointDistance,
				new Integer(point.x), 1, new Integer(mainGrid.getWidth() + 1));
		if (nearestRightPoint != null) {
			currentNearestPointDistance = getDistance(point, nearestRightPoint);
			result = nearestRightPoint;
		}
		ShortPoint2D nearestLeftPoint = getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(point, sortedPoints, currentNearestPointDistance,
				new Integer(point.x - 1), -1, -1);
		if (nearestLeftPoint != null) {
			result = nearestLeftPoint;
		}
		return result;
	}

	private ShortPoint2D getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(ShortPoint2D point, Map<Integer, List<Integer>> sortedPoints,
			double currentNearestPointDistance, Integer x, Integer increment, Integer border) {
		if (x.equals(border) || Math.abs(x - point.x) > currentNearestPointDistance) {
			return null;
		}
		if (!sortedPoints.containsKey(x)) {
			return getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(point, sortedPoints, currentNearestPointDistance, x + increment,
					increment, border);
		}
		ShortPoint2D result = null;
		ShortPoint2D southYPoint = getNearestPoinInDefaultPartitionOutOfSortedMapInYDirection(sortedPoints.get(x), point,
				currentNearestPointDistance, x, new Integer(point.y), 1, new Integer(mainGrid.getHeight() + 1));
		if (southYPoint != null) {
			result = southYPoint;
			currentNearestPointDistance = getDistance(point, southYPoint);
		}
		ShortPoint2D northYPoint = getNearestPoinInDefaultPartitionOutOfSortedMapInYDirection(sortedPoints.get(x), point,
				currentNearestPointDistance, x, new Integer(point.y - 1), -1, -1);
		if (northYPoint != null) {
			result = northYPoint;
			currentNearestPointDistance = getDistance(point, northYPoint);
		}
		if (Math.abs(point.x - (x + increment)) < currentNearestPointDistance) {
			ShortPoint2D nextPoint = getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(point, sortedPoints, currentNearestPointDistance, x
					+ increment, increment, border);
			if (nextPoint != null) {
				result = nextPoint;
			}
		}
		return result;
	}

	private ShortPoint2D getNearestPoinInDefaultPartitionOutOfSortedMapInYDirection(List<Integer> ypsilons, ShortPoint2D point,
			double currentNearestPointDistance, Integer x, Integer y, Integer increment, Integer border) {
		if (y.equals(border) || Math.abs(y - point.y) > currentNearestPointDistance) {
			return null;
		}
		if (!ypsilons.contains(y)) {
			return getNearestPoinInDefaultPartitionOutOfSortedMapInYDirection(ypsilons, point, currentNearestPointDistance, x, y + increment,
					increment, border);
		}
		if (getDistance(point, new ShortPoint2D(x, y)) > currentNearestPointDistance) {
			return null;
		}
		return new ShortPoint2D(x, y);
	}

	public double getDistance(ShortPoint2D pointA, ShortPoint2D pointB) {
		if (pointA == null || pointB == null) {
			return 0;
		}
		return Math.sqrt((pointA.x - pointB.x) * (pointA.x - pointB.x) + (pointA.y - pointB.y) * (pointA.y - pointB.y));
	}

	public List<ShortPoint2D> getMovablePositionsByTypeForPlayer(EMovableType movableType, byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!movablePositions.containsKey(playerIdInteger) || !movablePositions.get(playerIdInteger).containsKey(movableType)) {
			return new ArrayList<ShortPoint2D>();
		}
		return movablePositions.get(playerIdInteger).get(movableType);
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

	public int getNumberOfBuildingTypeForPlayer(EBuildingType type, byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!buildingsNumbers.containsKey(playerIdInteger)) {
			return 0;
		}
		if (!buildingsNumbers.get(playerIdInteger).containsKey(type)) {
			return 0;
		}
		return buildingsNumbers.get(playerIdInteger).get(type);
	}

	public int getNumberOfNotFinishedBuildingsForPlayer(byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!numberOfNotFinishedBuildings.containsKey(playerIdInteger)) {
			return 0;
		}
		return numberOfNotFinishedBuildings.get(playerIdInteger);
	}

	public int getNumberOfTotalBuildingsForPlayer(byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!numberOfTotalBuildings.containsKey(playerIdInteger)) {
			return 0;
		}
		return numberOfTotalBuildings.get(playerIdInteger);
	}

	public List<ShortPoint2D> getBuildingPositionsOfTypeForPlayer(EBuildingType type, byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!buildingPositions.containsKey(playerIdInteger) || !buildingPositions.get(playerIdInteger).containsKey(type)) {
			return new ArrayList<ShortPoint2D>();
		}
		return buildingPositions.get(playerIdInteger).get(type);
	}

	public List<ShortPoint2D> getStonesForPlayer(byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!stones.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return stones.get(playerIdInteger);
	}

	public List<ShortPoint2D> getTreesForPlayer(byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!trees.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return trees.get(playerIdInteger);
	}

	public List<ShortPoint2D> getLandForPlayer(byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!land.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return land.get(playerIdInteger);
	}

	public List<ShortPoint2D> getBorderLandNextToFreeLandForPlayer(byte playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (!borderLandNextToFreeLand.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return borderLandNextToFreeLand.get(playerIdInteger);
	}

	public void updateStatistics() {
		updateBuildingStatistics();
		updateMapStatistics();
	}

	public int getNumberOfNotOccupiedTowers(short playerId) {
		Integer playerIdInteger = new Integer(playerId);
		if (numberOfNotOccupiedTowers.get(playerIdInteger) == null) {
			return 0;
		}
		return numberOfNotOccupiedTowers.get(playerIdInteger);
	}

	public boolean blocksWorkingAreaOfOtherBuilding(ShortPoint2D point) {
		return pointIsBlocked((point.x), (short) (point.y - 12))
				|| pointIsBlocked((short) (point.x - 5), (short) (point.y - 12))
				|| pointIsBlocked((short) (point.x - 10), (short) (point.y - 12));
	}

	private boolean pointIsBlocked(short x, short y) {
		IBuilding building = objectsGrid.getBuildingOn(x, y);
		if (building != null && (building.getBuildingType() == LUMBERJACK || building.getBuildingType() == FARM
				|| building.getBuildingType() == WINEGROWER)) {
			return true;
		}
		return false;
	}

	public boolean southIsFreeForPlayer(ShortPoint2D point, byte playerId) {
		return pointIsFreeForPlayer(point.x, (short) (point.y + 12), playerId)
				&& pointIsFreeForPlayer((short) (point.x + 5), (short) (point.y + 12), playerId)
				&& pointIsFreeForPlayer((short) (point.x + 10), (short) (point.y + 12), playerId);
	}

	private boolean pointIsFreeForPlayer(short x, short y, byte playerId) {
		return partitionsGrid.getPlayerIdAt(x, y) == playerId && !objectsGrid.isBuildingAreaAt(x, y);
	}

	private void updateMapStatistics() {
		stones = new HashMap<Integer, List<ShortPoint2D>>();
		trees = new HashMap<Integer, List<ShortPoint2D>>();
		land = new HashMap<Integer, List<ShortPoint2D>>();
		borderLandNextToFreeLand = new HashMap<Integer, List<ShortPoint2D>>();
		movablePositions = new HashMap<Integer, Map<EMovableType, List<ShortPoint2D>>>();

		for (short x = 0; x < mainGrid.getWidth(); x++) {
			for (short y = 0; y < mainGrid.getHeight(); y++) {
				Player player = partitionsGrid.getPlayerAt(x, y);
				if (player != null) {
					Integer playerId = new Integer(partitionsGrid.getPlayerAt(x, y).playerId);
					ShortPoint2D point = new ShortPoint2D(x, y);
					updateBorderlandNextToFreeLand(playerId, point);
					if (!land.containsKey(playerId)) {
						land.put(playerId, new ArrayList<ShortPoint2D>());
					}
					land.get(playerId).add(point);
					if (!stones.containsKey(playerId)) {
						stones.put(playerId, new ArrayList<ShortPoint2D>());
					}
					if (objectsGrid.hasCuttableObject(x, y, EMapObjectType.STONE)) {
						stones.get(playerId).add(point);
					}
					if (!trees.containsKey(playerId)) {
						trees.put(playerId, new ArrayList<ShortPoint2D>());
					}
					if (objectsGrid.hasCuttableObject(x, y, EMapObjectType.TREE_ADULT)) {
						trees.get(playerId).add(point);
					}
					Movable movable = movableGrid.getMovableAt(x, y);
					if (movable != null) {
						EMovableType movableType = movable.getMovableType();
						if (!movablePositions.containsKey(playerId)) {
							movablePositions.put(playerId, new HashMap<EMovableType, List<ShortPoint2D>>());
						}
						if (!movablePositions.get(playerId).containsKey(movableType)) {
							movablePositions.get(playerId).put(movableType, new ArrayList<ShortPoint2D>());
						}
						movablePositions.get(playerId).get(movableType).add(point);
					}
				}
			}
		}
	}

	private void updateBorderlandNextToFreeLand(Integer playerId, ShortPoint2D point) {
		if (!borderLandNextToFreeLand.containsKey(playerId)) {
			borderLandNextToFreeLand.put(playerId, new ArrayList<ShortPoint2D>());
		}
		short west = (short) Math.max(0, point.x - BORDER_LAND_WIDTH);
		short east = (short) Math.min(mainGrid.getWidth(), point.x + BORDER_LAND_WIDTH);
		short north = (short) Math.max(0, point.y - BORDER_LAND_WIDTH);
		short south = (short) Math.min(mainGrid.getHeight(), point.y + BORDER_LAND_WIDTH);
		if (partitionsGrid.getPlayerAt(west, point.y) == null ||
				partitionsGrid.getPlayerAt(east, point.y) == null ||
				partitionsGrid.getPlayerAt(point.x, north) == null ||
				partitionsGrid.getPlayerAt(point.x, south) == null) {
			borderLandNextToFreeLand.get(playerId).add(point);
		}
	}

	private void updateBuildingStatistics() {
		totalBuildingsNumbers = new HashMap<Integer, Map<EBuildingType, Integer>>();
		buildingsNumbers = new HashMap<Integer, Map<EBuildingType, Integer>>();
		numberOfNotFinishedBuildings = new HashMap<Integer, Integer>();
		numberOfTotalBuildings = new HashMap<Integer, Integer>();
		numberOfNotOccupiedTowers = new HashMap<Integer, Integer>();
		buildingPositions = new HashMap<Integer, Map<EBuildingType, List<ShortPoint2D>>>();
		for (Building building : buildings) {
			Integer playerId = new Integer(building.getPlayerId());
			EBuildingType type = building.getBuildingType();
			updateNumberOfNotFinishedBuildings(building, playerId);
			updateBuildingsNumbers(playerId, building, type);
			updateBuildingPositions(playerId, type, building);
		}
	}

	private void updateBuildingPositions(Integer playerId, EBuildingType type, Building building) {
		if (!buildingPositions.containsKey(playerId)) {
			buildingPositions.put(playerId, new HashMap<EBuildingType, List<ShortPoint2D>>());
		}
		if (!buildingPositions.get(playerId).containsKey(type)) {
			buildingPositions.get(playerId).put(type, new ArrayList<ShortPoint2D>());
		}
		buildingPositions.get(playerId).get(type).add(building.getPos());
	}

	private void updateBuildingsNumbers(Integer playerId, Building building, EBuildingType type) {
		if (!totalBuildingsNumbers.containsKey(playerId)) {
			totalBuildingsNumbers.put(playerId, new HashMap<EBuildingType, Integer>());
			buildingsNumbers.put(playerId, new HashMap<EBuildingType, Integer>());
		}
		if (!totalBuildingsNumbers.get(playerId).containsKey(type)) {
			totalBuildingsNumbers.get(playerId).put(type, 0);
			buildingsNumbers.get(playerId).put(type, 0);
		}
		totalBuildingsNumbers.get(playerId).put(type, totalBuildingsNumbers.get(playerId).get(type) + 1);
		if (building.getStateProgress() == 1f) {
			buildingsNumbers.get(playerId).put(type, buildingsNumbers.get(playerId).get(type) + 1);
		}
	}

	private void updateNumberOfNotFinishedBuildings(Building building, Integer playerId) {
		if (!numberOfNotFinishedBuildings.containsKey(playerId)) {
			numberOfNotFinishedBuildings.put(playerId, 0);
			numberOfNotOccupiedTowers.put(playerId, 0);
			numberOfTotalBuildings.put(playerId, 0);
		}
		numberOfTotalBuildings.put(playerId, numberOfTotalBuildings.get(playerId) + 1);
		if (building.getStateProgress() < 1f) {
			numberOfNotFinishedBuildings.put(playerId, numberOfNotFinishedBuildings.get(playerId) + 1);
			if (building.getBuildingType() == EBuildingType.TOWER) {
				numberOfNotOccupiedTowers.put(playerId, numberOfNotOccupiedTowers.get(playerId) + 1);
			}
		} else if (building.getBuildingType() == EBuildingType.TOWER && !building.isOccupied()) {
			numberOfNotOccupiedTowers.put(playerId, numberOfNotOccupiedTowers.get(playerId) + 1);
		}
	}

	public void sendAnySoldierToPosition(ShortPoint2D targetPosition, byte playerId) {
		List<ShortPoint2D> soldierPositions = getMovablePositionsByTypeForPlayer(SWORDSMAN_L3, playerId);
		if (soldierPositions.size() == 0) {
			soldierPositions = getMovablePositionsByTypeForPlayer(SWORDSMAN_L2, playerId);
		}
		if (soldierPositions.size() == 0) {
			soldierPositions = getMovablePositionsByTypeForPlayer(SWORDSMAN_L1, playerId);
		}
		if (soldierPositions.size() == 0) {
			return;
		}

		ShortPoint2D nearestSoldierPosition = detectNearestPointFromList(targetPosition, soldierPositions);
		Movable soldier = movableGrid.getMovableAt(nearestSoldierPosition.x, nearestSoldierPosition.y);
		soldier.moveTo(targetPosition);
	}

	public ShortPoint2D detectNearestPointFromList(ShortPoint2D referencePoint, List<ShortPoint2D> points) {
		ShortPoint2D nearestPoint = null;
		double nearestPointDistance = Double.MAX_VALUE;
		for (ShortPoint2D point : points) {
			double currentPointDistance = getDistance(referencePoint, point);
			if (nearestPoint == null || currentPointDistance < nearestPointDistance) {
				nearestPoint = point;
				nearestPointDistance = currentPointDistance;
			}
		}

		return nearestPoint;
	}

	public ShortPoint2D getNearestTreePointInDefaultPartitionFor(ShortPoint2D point) {
		return null;
	}
}
