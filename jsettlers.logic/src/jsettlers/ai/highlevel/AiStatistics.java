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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import jsettlers.ai.highlevel.AiPositions.AiPositionFilter;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.partition.IPartitionData;
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
	private PlayerStatistic[] playerStatistics;
	private Map<EMapObjectType, AiPositions> sortedCuttableObjectsInDefaultPartition;
	private Map<EResourceType, AiPositions> sortedResourceTypes;
	private AiPositions sortedRiversInDefaultPartition;
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
		this.playerStatistics = new PlayerStatistic[mainGrid.getGuiInputGrid().getNumberOfPlayers()];
		for (byte i = 0; i < mainGrid.getGuiInputGrid().getNumberOfPlayers(); i++) {
			this.playerStatistics[i] = new PlayerStatistic();
		}
		sortedRiversInDefaultPartition = new AiPositions();
		sortedCuttableObjectsInDefaultPartition = new HashMap<EMapObjectType, AiPositions>();
		sortedResourceTypes = new HashMap<EResourceType, AiPositions>();
		for (EResourceType type : EResourceType.values()) {
			sortedResourceTypes.put(type, new AiPositions());
		}

	}

	public byte getFlatternEffortAtPositionForBuilding(final ShortPoint2D position, final EBuildingType buildingType) {
		byte flattenEffort = constructionMarksGrid.getConstructionMarkValue(position.x, position.y, buildingType.getProtectedTiles());
		if (flattenEffort == -1) {
			return Byte.MAX_VALUE;
		}
		return flattenEffort;
	}

	public void updateStatistics() {
		for (PlayerStatistic playerStatistic : playerStatistics) {
			playerStatistic.clearAll();
		}
		sortedRiversInDefaultPartition.clear();
		sortedCuttableObjectsInDefaultPartition.clear();
		for (AiPositions xCoordinatesMap : sortedResourceTypes.values()) {
			xCoordinatesMap.clear();
		}

		updateBuildingStatistics();
		updateMapStatistics();
	}

	private void updateBuildingStatistics() {
		for (Building building : buildings) {
			PlayerStatistic playerStatistic = playerStatistics[building.getPlayerId()];
			EBuildingType type = building.getBuildingType();
			updateNumberOfNotFinishedBuildings(playerStatistic, building);
			updateBuildingsNumbers(playerStatistic, building, type);
			updateBuildingPositions(playerStatistic, type, building);
		}
	}

	private void updateBuildingPositions(PlayerStatistic playerStatistic, EBuildingType type, Building building) {

		if (!playerStatistic.buildingPositions.containsKey(type)) {
			playerStatistic.buildingPositions.put(type, new ArrayList<ShortPoint2D>());
		}
		playerStatistic.buildingPositions.get(type).add(building.getPos());
	}

	private void updateBuildingsNumbers(PlayerStatistic playerStatistic, Building building, EBuildingType type) {
		playerStatistic.totalBuildingsNumbers[type.ordinal] = playerStatistic.totalBuildingsNumbers[type.ordinal] + 1;
		if (building.getStateProgress() == 1f) {
			playerStatistic.buildingsNumbers[type.ordinal] = playerStatistic.buildingsNumbers[type.ordinal] + 1;
		}
		if (!building.isOccupied()) {
			playerStatistic.unoccupiedBuildingsNumbers[type.ordinal] = playerStatistic.unoccupiedBuildingsNumbers[type.ordinal] + 1;
		}
	}

	private void updateNumberOfNotFinishedBuildings(PlayerStatistic playerStatistic, Building building) {
		playerStatistic.numberOfTotalBuildings++;
		if (building.getStateProgress() < 1f) {
			playerStatistic.numberOfNotFinishedBuildings++;
			if (building.getBuildingType() == EBuildingType.TOWER) {
				playerStatistic.numberOfNotOccupiedTowers++;
			}
		} else if (building.getBuildingType() == EBuildingType.TOWER && !building.isOccupied()) {
			playerStatistic.numberOfNotOccupiedTowers++;
		}
	}

	private void updateMapStatistics() {
		updatePartitionIdsToBuildOn();
		for (short x = 0; x < mainGrid.getWidth(); x++) {
			for (short y = 0; y < mainGrid.getHeight(); y++) {
				EResourceType resourceType = landscapeGrid.getResourceTypeAt(x, y);
				if (landscapeGrid.getResourceAmountAt(x, y) > 0) {
					sortedResourceTypes.get(resourceType).addNoCollission(x, y);
				}
				Player player = partitionsGrid.getPlayerAt(x, y);
				if (player == null) {
					if (objectsGrid.hasCuttableObject(x, y, TREE_ADULT)) {
						if (!sortedCuttableObjectsInDefaultPartition.containsKey(TREE_ADULT)) {
							sortedCuttableObjectsInDefaultPartition.put(TREE_ADULT, new AiPositions());
						}
						sortedCuttableObjectsInDefaultPartition.get(TREE_ADULT).addNoCollission(x, y);
					}
					if (objectsGrid.hasCuttableObject(x, y, STONE)) {
						if (!sortedCuttableObjectsInDefaultPartition.containsKey(STONE)) {
							sortedCuttableObjectsInDefaultPartition.put(STONE, new AiPositions());
						}
						sortedCuttableObjectsInDefaultPartition.get(STONE).addNoCollission(x, y);
					}
					if (landscapeGrid.getLandscapeTypeAt(x, y).isRiver()) {
						sortedRiversInDefaultPartition.addNoCollission(x, y);
					}
				} else {
					int playerId = partitionsGrid.getPlayerAt(x, y).playerId;
					PlayerStatistic playerStatistic = playerStatistics[playerId];
					ShortPoint2D point = new ShortPoint2D(x, y);
					updateBorderlandNextToFreeLand(playerStatistic, point);
					playerStatistic.land.add(point);
					if (objectsGrid.hasCuttableObject(x, y, EMapObjectType.STONE)) {
						playerStatistic.stones.add(point);
					}
					if (objectsGrid.hasCuttableObject(x, y, EMapObjectType.TREE_ADULT)) {
						playerStatistic.trees.add(point);
					}
					if (landscapeGrid.getLandscapeTypeAt(x, y) == RIVER1 || landscapeGrid.getLandscapeTypeAt(x, y) == RIVER2
							|| landscapeGrid.getLandscapeTypeAt(x, y) == RIVER3 || landscapeGrid.getLandscapeTypeAt(x, y) == RIVER4) {
						playerStatistic.rivers.add(point);
					}
					Movable movable = movableGrid.getMovableAt(x, y);
					if (movable != null) {
						EMovableType movableType = movable.getMovableType();
						if (!playerStatistic.movablePositions.containsKey(movableType)) {
							playerStatistic.movablePositions.put(movableType, new ArrayList<ShortPoint2D>());
						}
						playerStatistic.movablePositions.get(movableType).add(point);
					}
				}
			}
		}
	}

	private void updatePartitionIdsToBuildOn() {
		for (byte playerId = 0; playerId < playerStatistics.length; playerId++) {
			ShortPoint2D referencePosition = null;
			if (getTotalNumberOfBuildingTypeForPlayer(EBuildingType.LUMBERJACK, playerId) > 0) {
				referencePosition = getBuildingPositionsOfTypeForPlayer(EBuildingType.LUMBERJACK, playerId).get(0);
			} else if (getTotalNumberOfBuildingTypeForPlayer(EBuildingType.TOWER, playerId) > 0) {
				referencePosition = getBuildingPositionsOfTypeForPlayer(EBuildingType.TOWER, playerId).get(0);
			}
			if (referencePosition != null) {
				playerStatistics[playerId].partitionIdToBuildOn = partitionsGrid.getPartitionIdAt(referencePosition.x, referencePosition.y);
				playerStatistics[playerId].materials = partitionsGrid.getPartitionDataForManagerAt(referencePosition.x, referencePosition.y);
			}
		}
	}

	private void updateBorderlandNextToFreeLand(PlayerStatistic playerStatistic, ShortPoint2D point) {
		short west = (short) Math.max(0, point.x - BORDER_LAND_WIDTH);
		short east = (short) Math.min(mainGrid.getWidth() - 1, point.x + BORDER_LAND_WIDTH);
		short north = (short) Math.max(0, point.y - BORDER_LAND_WIDTH);
		short south = (short) Math.min(mainGrid.getHeight() - 1, point.y + BORDER_LAND_WIDTH);
		if (partitionsGrid.getPlayerAt(west, point.y) == null ||
				partitionsGrid.getPlayerAt(east, point.y) == null ||
				partitionsGrid.getPlayerAt(point.x, north) == null ||
				partitionsGrid.getPlayerAt(point.x, south) == null) {
			playerStatistic.borderLandNextToFreeLand.add(point);
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
		AiPositions sortedResourcePoints = sortedCuttableObjectsInDefaultPartition.get(cuttableObject);
		return getNearestPointInDefaultPartitionOutOfSortedMap(point, sortedResourcePoints, playerId, currentNearestPointDistance);
	}

	private ShortPoint2D getNearestPointInDefaultPartitionOutOfSortedMap(ShortPoint2D point, AiPositions sortedPoints, final byte playerId,
			int currentNearestPointDistance) {
		return sortedPoints.getNearestPoint(point, currentNearestPointDistance, new AiPositionFilter() {

			@Override
			public boolean contains(int x, int y) {
				return partitionsGrid.getPartitionAt(x, y).getPlayerId() == playerId;
			}
		});
	}

	public List<ShortPoint2D> getMovablePositionsByTypeForPlayer(EMovableType movableType, byte playerId) {
		if (!playerStatistics[playerId].movablePositions.containsKey(movableType)) {
			return Collections.emptyList();
		}
		return playerStatistics[playerId].movablePositions.get(movableType);
	}

	public int getTotalNumberOfBuildingTypeForPlayer(EBuildingType type, byte playerId) {
		return playerStatistics[playerId].totalBuildingsNumbers[type.ordinal];
	}

	public int getNumberOfBuildingTypeForPlayer(EBuildingType type, byte playerId) {
		return playerStatistics[playerId].buildingsNumbers[type.ordinal];
	}

	public int getNumberOfNotFinishedBuildingsForPlayer(byte playerId) {
		return playerStatistics[playerId].numberOfNotFinishedBuildings;
	}

	public int getNumberOfTotalBuildingsForPlayer(byte playerId) {
		return playerStatistics[playerId].numberOfTotalBuildings;
	}

	public List<ShortPoint2D> getBuildingPositionsOfTypeForPlayer(EBuildingType type, byte playerId) {
		if (!playerStatistics[playerId].buildingPositions.containsKey(type)) {
			return Collections.emptyList();
		}
		return playerStatistics[playerId].buildingPositions.get(type);
	}

	public List<ShortPoint2D> getStonesForPlayer(byte playerId) {
		return playerStatistics[playerId].stones;
	}

	public List<ShortPoint2D> getTreesForPlayer(byte playerId) {
		return playerStatistics[playerId].trees;
	}

	public List<ShortPoint2D> getLandForPlayer(byte playerId) {
		return playerStatistics[playerId].land;
	}

	public List<ShortPoint2D> getBorderLandNextToFreeLandForPlayer(byte playerId) {
		return playerStatistics[playerId].borderLandNextToFreeLand;
	}

	public int getNumberOfNotOccupiedTowers(short playerId) {
		return playerStatistics[playerId].numberOfNotOccupiedTowers;
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
		if (points.isEmpty()) {
			return null;
		}

		return detectNearestPointsFromList(referencePoint, points, 1).get(0);
	}

	public List<ShortPoint2D> detectNearestPointsFromList(final ShortPoint2D referencePoint, List<ShortPoint2D> points, int amountOfPointsToDetect) {
		if (amountOfPointsToDetect <= 0) {
			return Collections.EMPTY_LIST;
		}

		if (points.size() <= amountOfPointsToDetect) {
			return points;
		}

		Collections.sort(points, new Comparator<ShortPoint2D>() {
			@Override
			public int compare(ShortPoint2D o1, ShortPoint2D o2) {
				return o1.getOnGridDistTo(referencePoint) - o2.getOnGridDistTo(referencePoint);
			}
		});

		return points.subList(0, amountOfPointsToDetect);
	}

	public int getNumberOfMaterialTypeForPlayer(EMaterialType type, byte playerId) {
		if (playerStatistics[playerId].materials == null) {
			return 0;
		}

		return playerStatistics[playerId].materials.getAmountOf(type);
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
		return playerStatistics[playerId].rivers;
	}

	public int getNumberOfUnoccupiedBuildingTypeForPlayer(EBuildingType buildingType, byte playerId) {
		return playerStatistics[playerId].unoccupiedBuildingsNumbers[buildingType.ordinal];
	}

	private class PlayerStatistic {
		private int[] totalBuildingsNumbers;
		private int[] buildingsNumbers;
		private int[] unoccupiedBuildingsNumbers;
		private Map<EBuildingType, List<ShortPoint2D>> buildingPositions;
		private short partitionIdToBuildOn;
		private IPartitionData materials;
		private List<ShortPoint2D> land;
		private List<ShortPoint2D> borderLandNextToFreeLand;
		private Map<EMovableType, List<ShortPoint2D>> movablePositions;
		private List<ShortPoint2D> stones;
		private List<ShortPoint2D> trees;
		private List<ShortPoint2D> rivers;
		private int numberOfNotFinishedBuildings;
		private int numberOfTotalBuildings;
		private int numberOfNotOccupiedTowers;

		PlayerStatistic() {
			buildingPositions = new HashMap<EBuildingType, List<ShortPoint2D>>();
			stones = new ArrayList<ShortPoint2D>();
			trees = new ArrayList<ShortPoint2D>();
			rivers = new ArrayList<ShortPoint2D>();
			land = new ArrayList<ShortPoint2D>();
			borderLandNextToFreeLand = new ArrayList<ShortPoint2D>();
			movablePositions = new HashMap<EMovableType, List<ShortPoint2D>>();
			totalBuildingsNumbers = new int[EBuildingType.NUMBER_OF_BUILDINGS];
			buildingsNumbers = new int[EBuildingType.NUMBER_OF_BUILDINGS];
			unoccupiedBuildingsNumbers = new int[EBuildingType.NUMBER_OF_BUILDINGS];
			clearIntegers();
		}

		public void clearAll() {
			materials = null;
			buildingPositions.clear();
			stones.clear();
			trees.clear();
			rivers.clear();
			land.clear();
			borderLandNextToFreeLand.clear();
			movablePositions.clear();
			clearIntegers();
		}

		private void clearIntegers() {
			clearIntegerArray(totalBuildingsNumbers);
			clearIntegerArray(buildingsNumbers);
			clearIntegerArray(unoccupiedBuildingsNumbers);
			numberOfNotFinishedBuildings = 0;
			numberOfTotalBuildings = 0;
			numberOfNotOccupiedTowers = 0;
			partitionIdToBuildOn = Short.MIN_VALUE;
		}

		private void clearIntegerArray(int[] theArray) {
			for (int i = 0; i < theArray.length; i++) {
				theArray[i] = 0;
			}
		}
	}
}
