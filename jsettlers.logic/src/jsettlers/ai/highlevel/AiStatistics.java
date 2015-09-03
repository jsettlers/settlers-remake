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
import static jsettlers.common.landscape.ELandscapeType.RIVER1;
import static jsettlers.common.landscape.ELandscapeType.RIVER2;
import static jsettlers.common.landscape.ELandscapeType.RIVER3;
import static jsettlers.common.landscape.ELandscapeType.RIVER4;
import static jsettlers.common.mapobject.EMapObjectType.STONE;
import static jsettlers.common.mapobject.EMapObjectType.TREE_ADULT;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L1;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L2;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.flags.FlagsGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.map.grid.objects.ObjectsGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.objects.stack.StackMapObject;
import jsettlers.logic.player.Player;

/**
 * This class calculates statistics based on the grids which are used by highlevel and lowlevel KI. The statistics are calculated once and read
 * multiple times within one AiExecutor step triggerd by the game clock.
 * 
 * @author codingberlin
 */
public class AiStatistics {

	private static final short BORDER_LAND_WIDTH = 10;

	private final Queue<Building> buildings;
	private Map<Integer, Map<EBuildingType, Integer>> totalBuildingsNumbers;
	private Map<Integer, Map<EBuildingType, Integer>> buildingsNumbers;
	private Map<Integer, Map<EBuildingType, Integer>> unoccupiedBuildingsNumbers;
	private Map<Integer, Map<EMaterialType, Integer>> materialNumbers;
	private Map<Integer, Map<EMovableType, List<ShortPoint2D>>> movablePositions;
	private Map<Integer, Integer> numberOfNotFinishedBuildings;
	private Map<Integer, Integer> numberOfTotalBuildings;
	private Map<Integer, Integer> numberOfNotOccupiedTowers;
	private Map<Integer, List<ShortPoint2D>> stones;
	private Map<Integer, List<ShortPoint2D>> trees;
	private Map<Integer, List<ShortPoint2D>> rivers;
	private Map<EMapObjectType, Map<Integer, List<Integer>>> sortedCuttableObjectsInDefaultPartition;
	private Map<EResourceType, Map<Integer, List<Integer>>> sortedResourceTypes;
	private Map<Integer, List<Integer>> sortedRiversInDefaultPartition;
	private Map<Integer, List<ShortPoint2D>> land;
	private Map<Integer, List<ShortPoint2D>> borderLandNextToFreeLand;
	private Map<Integer, Map<EBuildingType, List<ShortPoint2D>>> buildingPositions;
	private final MainGrid mainGrid;
	private final LandscapeGrid landscapeGrid;
	private final ObjectsGrid objectsGrid;
	private final PartitionsGrid partitionsGrid;
	private final MovableGrid movableGrid;
	private final FlagsGrid flagsGrid;
	private final AbstractConstructionMarkableMap constructionMarksGrid;

	public AiStatistics(MainGrid mainGrid) {
		this.buildings = Building.getAllBuildings();
		this.mainGrid = mainGrid;
		this.landscapeGrid = mainGrid.getLandscapeGrid();
		this.objectsGrid = mainGrid.getObjectsGrid();
		this.partitionsGrid = mainGrid.getPartitionsGrid();
		this.movableGrid = mainGrid.getMovableGrid();
		this.flagsGrid = mainGrid.getFlagsGrid();
		this.constructionMarksGrid = mainGrid.getConstructionMarksGrid();
	}

	public byte getFlatternEffortAtPositionForBuilding(final ShortPoint2D position, final EBuildingType buildingType) {
		byte flattenEffort = constructionMarksGrid.getConstructionMarkValue(position.x, position.y, buildingType.getProtectedTiles());
		if (flattenEffort == -1) {
			return Byte.MAX_VALUE;
		}
		return flattenEffort;
	}

	public void updateStatistics() {
		updateBuildingStatistics();
		updateMapStatistics();
	}

	private void updateBuildingStatistics() {
		totalBuildingsNumbers = new HashMap<Integer, Map<EBuildingType, Integer>>();
		buildingsNumbers = new HashMap<Integer, Map<EBuildingType, Integer>>();
		unoccupiedBuildingsNumbers = new HashMap<Integer, Map<EBuildingType, Integer>>();
		numberOfNotFinishedBuildings = new HashMap<Integer, Integer>();
		numberOfTotalBuildings = new HashMap<Integer, Integer>();
		numberOfNotOccupiedTowers = new HashMap<Integer, Integer>();
		buildingPositions = new HashMap<Integer, Map<EBuildingType, List<ShortPoint2D>>>();
		for (Building building : buildings) {
			Integer playerId = Integer.valueOf(building.getPlayerId());
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
			unoccupiedBuildingsNumbers.put(playerId, new HashMap<EBuildingType, Integer>());
		}
		if (!totalBuildingsNumbers.get(playerId).containsKey(type)) {
			totalBuildingsNumbers.get(playerId).put(type, 0);
			buildingsNumbers.get(playerId).put(type, 0);
			unoccupiedBuildingsNumbers.get(playerId).put(type, 0);
		}
		totalBuildingsNumbers.get(playerId).put(type, totalBuildingsNumbers.get(playerId).get(type) + 1);
		if (building.getStateProgress() == 1f) {
			buildingsNumbers.get(playerId).put(type, buildingsNumbers.get(playerId).get(type) + 1);
		}
		if (!building.isOccupied()) {
			unoccupiedBuildingsNumbers.get(playerId).put(type, unoccupiedBuildingsNumbers.get(playerId).get(type) + 1);
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

	private void updateMapStatistics() {
		stones = new HashMap<Integer, List<ShortPoint2D>>();
		trees = new HashMap<Integer, List<ShortPoint2D>>();
		rivers = new HashMap<Integer, List<ShortPoint2D>>();
		sortedRiversInDefaultPartition = new HashMap<Integer, List<Integer>>();
		sortedCuttableObjectsInDefaultPartition = new HashMap<EMapObjectType, Map<Integer, List<Integer>>>();
		land = new HashMap<Integer, List<ShortPoint2D>>();
		borderLandNextToFreeLand = new HashMap<Integer, List<ShortPoint2D>>();
		movablePositions = new HashMap<Integer, Map<EMovableType, List<ShortPoint2D>>>();
		materialNumbers = new HashMap<Integer, Map<EMaterialType, Integer>>();
		sortedResourceTypes = new HashMap<EResourceType, Map<Integer, List<Integer>>>();
		for (EResourceType type : EResourceType.values()) {
			sortedResourceTypes.put(type, new HashMap<Integer, List<Integer>>());
		}

		for (short x = 0; x < mainGrid.getWidth(); x++) {
			for (short y = 0; y < mainGrid.getHeight(); y++) {
				Integer xInteger = Integer.valueOf(x);
				Integer yInteger = Integer.valueOf(y);
				EResourceType resourceType = landscapeGrid.getResourceTypeAt(x, y);
				if (landscapeGrid.getResourceAmountAt(x, y) > 0) {
					if (!sortedResourceTypes.get(resourceType).containsKey(xInteger)) {
						sortedResourceTypes.get(resourceType).put(xInteger, new ArrayList<Integer>());
					}
					sortedResourceTypes.get(resourceType).get(xInteger).add(yInteger);
				}
				Player player = partitionsGrid.getPlayerAt(x, y);
				if (player == null) {
					if (objectsGrid.hasCuttableObject(x, y, TREE_ADULT)) {
						if (!sortedCuttableObjectsInDefaultPartition.containsKey(TREE_ADULT)) {
							sortedCuttableObjectsInDefaultPartition.put(TREE_ADULT, new HashMap<Integer, List<Integer>>());
						}
						if (!sortedCuttableObjectsInDefaultPartition.get(TREE_ADULT).containsKey(xInteger)) {
							sortedCuttableObjectsInDefaultPartition.get(TREE_ADULT).put(xInteger, new ArrayList<Integer>());
						}
						sortedCuttableObjectsInDefaultPartition.get(TREE_ADULT).get(xInteger).add(yInteger);
					}
					if (objectsGrid.hasCuttableObject(x, y, STONE)) {
						if (!sortedCuttableObjectsInDefaultPartition.containsKey(STONE)) {
							sortedCuttableObjectsInDefaultPartition.put(STONE, new HashMap<Integer, List<Integer>>());
						}
						if (!sortedCuttableObjectsInDefaultPartition.get(STONE).containsKey(xInteger)) {
							sortedCuttableObjectsInDefaultPartition.get(STONE).put(xInteger, new ArrayList<Integer>());
						}
						sortedCuttableObjectsInDefaultPartition.get(STONE).get(xInteger).add(yInteger);
					}
					if (landscapeGrid.getLandscapeTypeAt(x, y) == RIVER1 || landscapeGrid.getLandscapeTypeAt(x, y) == RIVER2
							|| landscapeGrid.getLandscapeTypeAt(x, y) == RIVER3 || landscapeGrid.getLandscapeTypeAt(x, y) == RIVER4) {
						if (!sortedRiversInDefaultPartition.containsKey(xInteger)) {
							sortedRiversInDefaultPartition.put(xInteger, new ArrayList<Integer>());
						}
						sortedRiversInDefaultPartition.get(xInteger).add(yInteger);
					}
				} else {
					Integer playerId = Integer.valueOf(partitionsGrid.getPlayerAt(x, y).playerId);
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
					if (!rivers.containsKey(playerId)) {
						rivers.put(playerId, new ArrayList<ShortPoint2D>());
					}
					if (landscapeGrid.getLandscapeTypeAt(x, y) == RIVER1 || landscapeGrid.getLandscapeTypeAt(x, y) == RIVER2
							|| landscapeGrid.getLandscapeTypeAt(x, y) == RIVER3 || landscapeGrid.getLandscapeTypeAt(x, y) == RIVER4) {
						rivers.get(playerId).add(point);
					}
					StackMapObject stack = (StackMapObject) objectsGrid.getMapObjectAt(x, y, EMapObjectType.STACK_OBJECT);
					if (stack != null) {
						EMaterialType materialType = stack.getMaterialType();
						if (!materialNumbers.containsKey(playerId)) {
							materialNumbers.put(playerId, new HashMap<EMaterialType, Integer>());
						}
						if (!materialNumbers.get(playerId).containsKey(materialType)) {
							materialNumbers.get(playerId).put(materialType, 0);
						}
						materialNumbers.get(playerId).put(materialType, materialNumbers.get(playerId).get(materialType) + stack.getSize());
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
		short east = (short) Math.min(mainGrid.getWidth() - 1, point.x + BORDER_LAND_WIDTH);
		short north = (short) Math.max(0, point.y - BORDER_LAND_WIDTH);
		short south = (short) Math.min(mainGrid.getHeight() - 1, point.y + BORDER_LAND_WIDTH);
		if (partitionsGrid.getPlayerAt(west, point.y) == null ||
				partitionsGrid.getPlayerAt(east, point.y) == null ||
				partitionsGrid.getPlayerAt(point.x, north) == null ||
				partitionsGrid.getPlayerAt(point.x, south) == null) {
			borderLandNextToFreeLand.get(playerId).add(point);
		}
	}

	public List<ShortPoint2D> getHinterlandTowerPositionsOfPlayer(byte playerId) {
		List<ShortPoint2D> hinterlandTowerPositions = new ArrayList<ShortPoint2D>();
		for (ShortPoint2D towerPosition : getBuildingPositionsOfTypeForPlayer(EBuildingType.TOWER, playerId)) {
			Building tower = getBuildingAt(towerPosition);
			if (isTowerInHinterland(tower, playerId)) {
				hinterlandTowerPositions.add(towerPosition);
			}
		}
		return hinterlandTowerPositions;
	}

	private boolean isTowerInHinterland(Building tower, byte playerId) {
		for (ShortPoint2D occupiedPosition : new MapCircle(tower.getPos(), CommonConstants.TOWER_RADIUS)) {
			if (getBorderLandNextToFreeLandForPlayer(playerId).contains(occupiedPosition)
					&& partitionsGrid.getTowerCountAt(occupiedPosition.x, occupiedPosition.y) == 1) {
				return false;
			}
		}
		return true;
	}

	public Building getBuildingAt(ShortPoint2D point) {
		return (Building) objectsGrid.getMapObjectAt(point.x, point.y, EMapObjectType.BUILDING);
	}

	public ShortPoint2D getNearestResourcePointForPlayer(ShortPoint2D point, EResourceType resourceType, byte playerId,
			int currentNearestPointDistance) {
		return getNearestPointInDefaultPartitionOutOfSortedMap(point, sortedResourceTypes.get(resourceType), playerId, currentNearestPointDistance);
	}

	public ShortPoint2D getNearestResourcePointInDefaultPartitionFor(ShortPoint2D point, EResourceType resourceType,
			int currentNearestPointDistance) {
		return getNearestResourcePointForPlayer(point, resourceType, (byte) -1, currentNearestPointDistance);
	}

	public ShortPoint2D getNearestCuttableObjectPointInDefaultPartitionFor(ShortPoint2D point, EMapObjectType cuttableObject,
			int currentNearestPointDistance) {
		return getNearestCuttableObjectPointForPlayer(point, cuttableObject, currentNearestPointDistance, (byte) -1);
	}

	public ShortPoint2D getNearestCuttableObjectPointForPlayer(ShortPoint2D point, EMapObjectType cuttableObject,
			int currentNearestPointDistance, byte playerId) {
		Map<Integer, List<Integer>> sortedResourcePoints = sortedCuttableObjectsInDefaultPartition.get(cuttableObject);
		return getNearestPointInDefaultPartitionOutOfSortedMap(point, sortedResourcePoints, playerId, currentNearestPointDistance);
	}

	private ShortPoint2D getNearestPointInDefaultPartitionOutOfSortedMap(ShortPoint2D point, Map<Integer, List<Integer>> sortedPoints, byte playerId,
			int currentNearestPointDistance) {
		ShortPoint2D result = null;
		ShortPoint2D nearestRightPoint = getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(point, sortedPoints, currentNearestPointDistance,
				Integer.valueOf(point.x), 1, Integer.valueOf(mainGrid.getWidth() + 1), playerId);
		if (nearestRightPoint != null) {
			currentNearestPointDistance = point.getOnGridDistTo(nearestRightPoint);
			result = nearestRightPoint;
		}
		ShortPoint2D nearestLeftPoint = getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(point, sortedPoints, currentNearestPointDistance,
				Integer.valueOf(point.x - 1), -1, -1, playerId);
		if (nearestLeftPoint != null) {
			result = nearestLeftPoint;
		}
		return result;
	}

	private ShortPoint2D getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(ShortPoint2D point, Map<Integer, List<Integer>> sortedPoints,
			int currentNearestPointDistance, Integer x, Integer increment, Integer border, byte playerId) {
		if (x.equals(border) || Math.abs(x - point.x) > currentNearestPointDistance) {
			return null;
		}
		if (!sortedPoints.containsKey(x)) {
			return getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(point, sortedPoints, currentNearestPointDistance, x + increment,
					increment, border, playerId);
		}
		ShortPoint2D result = null;
		ShortPoint2D southYPoint = getNearestPoinInDefaultPartitionOutOfSortedMapInYDirection(sortedPoints.get(x), point,
				currentNearestPointDistance, x, Integer.valueOf(point.y), 1, Integer.valueOf(mainGrid.getHeight() + 1), playerId);
		if (southYPoint != null) {
			result = southYPoint;
			currentNearestPointDistance = point.getOnGridDistTo(southYPoint);
		}
		ShortPoint2D northYPoint = getNearestPoinInDefaultPartitionOutOfSortedMapInYDirection(sortedPoints.get(x), point,
				currentNearestPointDistance, x, Integer.valueOf(point.y - 1), -1, -1, playerId);
		if (northYPoint != null) {
			result = northYPoint;
			currentNearestPointDistance = point.getOnGridDistTo(northYPoint);
		}
		if (Math.abs(point.x - (x + increment)) < currentNearestPointDistance) {
			ShortPoint2D nextPoint = getNearestPoinInDefaultPartionOutOfSortedMapInXDirection(point, sortedPoints, currentNearestPointDistance, x
					+ increment, increment, border, playerId);
			if (nextPoint != null) {
				result = nextPoint;
			}
		}
		return result;
	}

	private ShortPoint2D getNearestPoinInDefaultPartitionOutOfSortedMapInYDirection(List<Integer> ypsilons, ShortPoint2D point,
			int currentNearestPointDistance, Integer x, Integer y, Integer increment, Integer border, byte playerId) {
		if (y.equals(border) || Math.abs(y - point.y) > currentNearestPointDistance) {
			return null;
		}
		if (!ypsilons.contains(y) || partitionsGrid.getPartitionAt(x, y).getPlayerId() != playerId) {
			return getNearestPoinInDefaultPartitionOutOfSortedMapInYDirection(ypsilons, point, currentNearestPointDistance, x, y + increment,
					increment, border, playerId);
		}
		if (point.getOnGridDistTo(new ShortPoint2D(x, y)) > currentNearestPointDistance) {
			return null;
		}
		return new ShortPoint2D(x, y);
	}

	public List<ShortPoint2D> getMovablePositionsByTypeForPlayer(EMovableType movableType, byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!movablePositions.containsKey(playerIdInteger) || !movablePositions.get(playerIdInteger).containsKey(movableType)) {
			return new ArrayList<ShortPoint2D>();
		}
		return movablePositions.get(playerIdInteger).get(movableType);
	}

	public int getTotalNumberOfBuildingTypeForPlayer(EBuildingType type, byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!totalBuildingsNumbers.containsKey(playerIdInteger)) {
			return 0;
		}
		if (!totalBuildingsNumbers.get(playerIdInteger).containsKey(type)) {
			return 0;
		}
		return totalBuildingsNumbers.get(playerIdInteger).get(type);
	}

	public int getNumberOfBuildingTypeForPlayer(EBuildingType type, byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!buildingsNumbers.containsKey(playerIdInteger)) {
			return 0;
		}
		if (!buildingsNumbers.get(playerIdInteger).containsKey(type)) {
			return 0;
		}
		return buildingsNumbers.get(playerIdInteger).get(type);
	}

	public int getNumberOfNotFinishedBuildingsForPlayer(byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!numberOfNotFinishedBuildings.containsKey(playerIdInteger)) {
			return 0;
		}
		return numberOfNotFinishedBuildings.get(playerIdInteger);
	}

	public int getNumberOfTotalBuildingsForPlayer(byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!numberOfTotalBuildings.containsKey(playerIdInteger)) {
			return 0;
		}
		return numberOfTotalBuildings.get(playerIdInteger);
	}

	public List<ShortPoint2D> getBuildingPositionsOfTypeForPlayer(EBuildingType type, byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!buildingPositions.containsKey(playerIdInteger) || !buildingPositions.get(playerIdInteger).containsKey(type)) {
			return new ArrayList<ShortPoint2D>();
		}
		return buildingPositions.get(playerIdInteger).get(type);
	}

	public List<ShortPoint2D> getStonesForPlayer(byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!stones.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return stones.get(playerIdInteger);
	}

	public List<ShortPoint2D> getTreesForPlayer(byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!trees.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return trees.get(playerIdInteger);
	}

	public List<ShortPoint2D> getLandForPlayer(byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!land.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return land.get(playerIdInteger);
	}

	public List<ShortPoint2D> getBorderLandNextToFreeLandForPlayer(byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!borderLandNextToFreeLand.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return borderLandNextToFreeLand.get(playerIdInteger);
	}

	public int getNumberOfNotOccupiedTowers(short playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (numberOfNotOccupiedTowers.get(playerIdInteger) == null) {
			return 0;
		}
		return numberOfNotOccupiedTowers.get(playerIdInteger);
	}

	public boolean blocksWorkingAreaOfOtherBuilding(ShortPoint2D point) {
		return pointIsBlocked((point.x), (short) (point.y - 12))
				|| pointIsBlocked((short) (point.x - 5), (short) (point.y - 12))
				|| pointIsBlocked((short) (point.x - 10), (short) (point.y - 12))
				|| pointIsBlocked((point.x), (short) (point.y - 6))
				|| pointIsBlocked((short) (point.x - 5), (short) (point.y - 6))
				|| pointIsBlocked((short) (point.x - 10), (short) (point.y - 6));
	}

	private boolean pointIsBlocked(short x, short y) {
		IBuilding building = objectsGrid.getBuildingAt(x, y);
		if (building != null && (building.getBuildingType() == LUMBERJACK || building.getBuildingType() == FARM
				|| building.getBuildingType() == WINEGROWER)) {
			return true;
		}
		return false;
	}

	public boolean southIsFreeForPlayer(ShortPoint2D point, byte playerId) {
		return pointIsFreeForPlayer(point.x, (short) (point.y + 12), playerId)
				&& pointIsFreeForPlayer((short) (point.x + 5), (short) (point.y + 12), playerId)
				&& pointIsFreeForPlayer((short) (point.x + 10), (short) (point.y + 12), playerId)
				&& pointIsFreeForPlayer(point.x, (short) (point.y + 6), playerId)
				&& pointIsFreeForPlayer((short) (point.x + 5), (short) (point.y + 6), playerId)
				&& pointIsFreeForPlayer((short) (point.x + 10), (short) (point.y + 6), playerId);
	}

	private boolean pointIsFreeForPlayer(short x, short y, byte playerId) {
		return partitionsGrid.getPlayerIdAt(x, y) == playerId && !objectsGrid.isBuildingAt(x, y) && !flagsGrid.isProtected(x, y)
				&& landscapeGrid.areAllNeighborsOf(x, y, 0, 2, ELandscapeType.GRASS, ELandscapeType.EARTH);
	}

	public Movable getNearestSwordsmanOf(ShortPoint2D targetPosition, byte playerId) {
		List<ShortPoint2D> soldierPositions = getMovablePositionsByTypeForPlayer(SWORDSMAN_L3, playerId);
		if (soldierPositions.size() == 0) {
			soldierPositions = getMovablePositionsByTypeForPlayer(SWORDSMAN_L2, playerId);
		}
		if (soldierPositions.size() == 0) {
			soldierPositions = getMovablePositionsByTypeForPlayer(SWORDSMAN_L1, playerId);
		}
		if (soldierPositions.size() == 0) {
			return null;
		}

		ShortPoint2D nearestSoldierPosition = detectNearestPointFromList(targetPosition, soldierPositions);
		return movableGrid.getMovableAt(nearestSoldierPosition.x, nearestSoldierPosition.y);
	}

	public ShortPoint2D detectNearestPointFromList(ShortPoint2D referencePoint, List<ShortPoint2D> points) {
		ShortPoint2D nearestPoint = null;
		int nearestPointDistance = Integer.MAX_VALUE;
		for (ShortPoint2D point : points) {
			int currentPointDistance = referencePoint.getOnGridDistTo(point);
			if (nearestPoint == null || currentPointDistance < nearestPointDistance) {
				nearestPoint = point;
				nearestPointDistance = currentPointDistance;
			}
		}

		return nearestPoint;
	}

	public int getNumberOfMaterialTypeForPlayer(EMaterialType type, byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!materialNumbers.containsKey(playerIdInteger)) {
			return 0;
		}
		if (!materialNumbers.get(playerIdInteger).containsKey(type)) {
			return 0;
		}
		return materialNumbers.get(playerIdInteger).get(type);
	}

	public MainGrid getMainGrid() {
		return mainGrid;
	}

	public ShortPoint2D getNearestRiverPointInDefaultPartitionFor(ShortPoint2D referencePoint, int currentNearestPointDistance) {
		return getNearestPointInDefaultPartitionOutOfSortedMap(referencePoint, sortedRiversInDefaultPartition, (byte) -1, currentNearestPointDistance);
	}

	public int getNumberOfNotFinishedBuildingTypesForPlayer(EBuildingType buildingType, byte playerId) {
		return getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId) - getNumberOfBuildingTypeForPlayer(buildingType, playerId);
	}

	public List<ShortPoint2D> getRiversForPlayer(byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!rivers.containsKey(playerIdInteger)) {
			return new ArrayList<ShortPoint2D>();
		}
		return rivers.get(playerIdInteger);
	}

	public int getNumberOfUnoccupiedBuildingTypeForPlayer(EBuildingType buildingType, byte playerId) {
		Integer playerIdInteger = Integer.valueOf(playerId);
		if (!unoccupiedBuildingsNumbers.containsKey(playerIdInteger)) {
			return 0;
		}
		if (!unoccupiedBuildingsNumbers.get(playerIdInteger).containsKey(buildingType)) {
			return 0;
		}
		return unoccupiedBuildingsNumbers.get(playerIdInteger).get(buildingType);
	}
}
