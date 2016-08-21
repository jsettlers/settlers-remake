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

import static jsettlers.common.buildings.EBuildingType.BIG_TOWER;
import static jsettlers.common.buildings.EBuildingType.CASTLE;
import static jsettlers.common.buildings.EBuildingType.LUMBERJACK;
import static jsettlers.common.buildings.EBuildingType.TOWER;
import static jsettlers.common.mapobject.EMapObjectType.STONE;
import static jsettlers.common.mapobject.EMapObjectType.TREE_ADULT;
import static jsettlers.common.mapobject.EMapObjectType.TREE_GROWING;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L1;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L2;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

import jsettlers.ai.highlevel.AiPositions.AiPositionFilter;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.WorkAreaBuilding;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.flags.FlagsGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.map.grid.objects.AbstractHexMapObject;
import jsettlers.logic.map.grid.objects.ObjectsGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.player.Player;
import jsettlers.logic.player.Team;

/**
 * This class calculates statistics based on the grids which are used by highlevel and lowlevel KI. The statistics are calculated once and read
 * multiple times within one AiExecutor step triggerd by the game clock.
 *
 * @author codingberlin
 */
public class AiStatistics {

	private static final EBuildingType[] REFERENCE_POINT_FINDER_BUILDING_ORDER = {LUMBERJACK, TOWER, BIG_TOWER, CASTLE };
	public static final int NEAR_STONE_DISTANCE = 5;

	private final Queue<Building> buildings;
	private final PlayerStatistic[] playerStatistics;
	private final Map<EMapObjectType, AiPositions> sortedCuttableObjectsInDefaultPartition;
	private final AiPositions[] sortedResourceTypes;
	private final AiPositions sortedRiversInDefaultPartition;
	private final MainGrid mainGrid;
	private final LandscapeGrid landscapeGrid;
	private final ObjectsGrid objectsGrid;
	private final PartitionsGrid partitionsGrid;
	private final MovableGrid movableGrid;
	private final FlagsGrid flagsGrid;
	private final AbstractConstructionMarkableMap constructionMarksGrid;
	private final AiMapInformation aiMapInformation;
	private final long[] resourceCountInDefaultPartition;

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
		this.aiMapInformation = new AiMapInformation(this.partitionsGrid);
		for (byte i = 0; i < mainGrid.getGuiInputGrid().getNumberOfPlayers(); i++) {
			this.playerStatistics[i] = new PlayerStatistic();
		}
		sortedRiversInDefaultPartition = new AiPositions();
		sortedCuttableObjectsInDefaultPartition = new HashMap<EMapObjectType, AiPositions>();
		sortedResourceTypes = new AiPositions[EResourceType.VALUES.length];
		for (int i = 0; i < sortedResourceTypes.length; i++) {
			sortedResourceTypes[i] = new AiPositions();
		}
		resourceCountInDefaultPartition = new long[EResourceType.VALUES.length];
	}

	public byte getFlatternEffortAtPositionForBuilding(final ShortPoint2D position, final EBuildingType buildingType) {
		byte flattenEffort = constructionMarksGrid.calculateConstructionMarkValue(position.x, position.y, buildingType.getProtectedTiles());
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
		for (AiPositions xCoordinatesMap : sortedResourceTypes) {
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

		if (type == EBuildingType.WINEGROWER) {
			playerStatistic.wineGrowerWorkAreas.add(((WorkAreaBuilding) building).getWorkAreaCenter());
		} else if (type == EBuildingType.FARM) {
			playerStatistic.farmWorkAreas.add(((WorkAreaBuilding) building).getWorkAreaCenter());
		}
	}

	private void updateBuildingsNumbers(PlayerStatistic playerStatistic, Building building, EBuildingType type) {
		playerStatistic.totalBuildingsNumbers[type.ordinal]++;
		if (building.getStateProgress() == 1f) {
			playerStatistic.buildingsNumbers[type.ordinal]++;
		}
	}

	private void updateNumberOfNotFinishedBuildings(PlayerStatistic playerStatistic, Building building) {
		playerStatistic.numberOfTotalBuildings++;
		if (building.getStateProgress() < 1f) {
			playerStatistic.numberOfNotFinishedBuildings++;
			if (building.getBuildingType().isMilitaryBuilding()) {
				playerStatistic.numberOfNotOccupiedMilitaryBuildings++;
			}
		} else if (building.getBuildingType().isMilitaryBuilding()) {
			if (building.isOccupied()) {
				playerStatistic.isAlive = true;
			} else {
				playerStatistic.numberOfNotOccupiedMilitaryBuildings++;
			}
		}
	}

	private void updateMapStatistics() {
		aiMapInformation.clear();
		updatePartitionIdsToBuildOn();
		short width = mainGrid.getWidth();
		short height = mainGrid.getHeight();
		for (int i = 0; i < resourceCountInDefaultPartition.length; i++) {
			resourceCountInDefaultPartition[i] = 0;
		}
		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				Player player = partitionsGrid.getPlayerAt(x, y);
				int mapInformationPlayerId;
				if (player != null) {
					mapInformationPlayerId = player.playerId;
				} else {
					mapInformationPlayerId = aiMapInformation.resourceAndGrassCount.length - 1;
				}
				if (landscapeGrid.getResourceAmountAt(x, y) > 0) {
					EResourceType resourceType = landscapeGrid.getResourceTypeAt(x, y);
					sortedResourceTypes[resourceType.ordinal].addNoCollission(x, y);
					if (resourceType != EResourceType.FISH) {
						aiMapInformation.resourceAndGrassCount[mapInformationPlayerId][resourceType.ordinal]++;
						if (player != null) {
							playerStatistics[player.playerId].resourceCount[resourceType.ordinal]++;
						} else {
							resourceCountInDefaultPartition[resourceType.ordinal]++;
						}
					} else if (landscapeGrid.getLandscapeTypeAt(x, y) == ELandscapeType.WATER1) {
						int fishMapInformationPlayerId = mapInformationPlayerId;
						if (mapInformationPlayerId == aiMapInformation.resourceAndGrassCount.length - 1) {
							fishMapInformationPlayerId = mapInformationPlayerIdOfPosition((short) (x + 3), y);
							if (fishMapInformationPlayerId == aiMapInformation.resourceAndGrassCount.length - 1) {
								fishMapInformationPlayerId = mapInformationPlayerIdOfPosition((short) (x - 3), y);
								if (fishMapInformationPlayerId == aiMapInformation.resourceAndGrassCount.length - 1) {
									fishMapInformationPlayerId = mapInformationPlayerIdOfPosition(x, (short) (y + 3));
									if (fishMapInformationPlayerId == aiMapInformation.resourceAndGrassCount.length - 1) {
										fishMapInformationPlayerId = mapInformationPlayerIdOfPosition(x, (short) (y - 3));
									}
								}
							}
						}
						aiMapInformation.resourceAndGrassCount[fishMapInformationPlayerId][resourceType.ordinal]++;
						if (fishMapInformationPlayerId != aiMapInformation.resourceAndGrassCount.length - 1) {
							playerStatistics[fishMapInformationPlayerId].resourceCount[resourceType.ordinal]++;
						} else {
							resourceCountInDefaultPartition[resourceType.ordinal]++;
						}
					}
				}
				if (landscapeGrid.getLandscapeTypeAt(x, y).isGrass()) {
					aiMapInformation.resourceAndGrassCount[mapInformationPlayerId][aiMapInformation.GRASS_INDEX]++;
				}
				Movable movable = movableGrid.getMovableAt(x, y);
				if (movable != null) {
					byte movablePlayerId = movable.getPlayerId();
					PlayerStatistic movablePlayerStatistic = playerStatistics[movablePlayerId];
					EMovableType movableType = movable.getMovableType();
					if (!movablePlayerStatistic.movablePositions.containsKey(movableType)) {
						movablePlayerStatistic.movablePositions.put(movableType, new Vector<ShortPoint2D>());
					}
					movablePlayerStatistic.movablePositions.get(movableType).add(movable.getPos());
					if (player != null
							&& player.playerId != movablePlayerId
							&& movableType.isSoldier()
							&& getEnemiesOf(player.playerId).contains(movablePlayerId)) {
						playerStatistics[player.playerId].enemyTroopsInTown.addNoCollission(movable.getPos().x, movable.getPos().y);
					}
				}
				if (player == null) {
					updateFreeLand(x, y);
				} else if (partitionsGrid.getPartitionIdAt(x, y) == playerStatistics[player.playerId].partitionIdToBuildOn) {
					updatePlayerLand(x, y, player);
				}
				if (player != null && isBorderOf(x, y, player.playerId)) {
					if (partitionsGrid.getPartitionIdAt(x, y) == playerStatistics[player.playerId].partitionIdToBuildOn) {
						playerStatistics[player.playerId].border.add(x, y);
					} else {
						playerStatistics[player.playerId].otherPartitionBorder.add(x, y);
					}
				}
			}
		}
	}

	private int mapInformationPlayerIdOfPosition(short x, short y) {
		if (!mainGrid.isInBounds(x, y)) {
			return aiMapInformation.resourceAndGrassCount.length - 1;
		}

		byte playerId = mainGrid.getPartitionsGrid().getPlayerIdAt(x, y);
		if (playerId == -1) {
			return aiMapInformation.resourceAndGrassCount.length - 1;
		}

		return playerId;
	}

	private boolean isBorderOf(int x, int y, byte playerId) {
		return isIngestibleBy(x + 1, y + 1, playerId)
				|| isIngestibleBy(x + 1, y - 1, playerId)
				|| isIngestibleBy(x - 1, y + 1, playerId)
				|| isIngestibleBy(x - 1, y - 1, playerId);
	}

	private boolean isIngestibleBy(int x, int y, byte playerId) {
		return mainGrid.isInBounds(x, y)
				&& partitionsGrid.getPlayerIdAt(x, y) != playerId
				&& !mainGrid.getLandscapeGrid().getLandscapeTypeAt(x, y).isBlocking
				&& !partitionsGrid.isEnforcedByTower(x, y);
	}

	private void updatePlayerLand(short x, short y, Player player) {
		byte playerId = player.playerId;
		PlayerStatistic playerStatistic = playerStatistics[playerId];
		playerStatistic.landToBuildOn.addNoCollission(x, y);
		AbstractHexMapObject o = objectsGrid.getObjectsAt(x, y);
		if (o != null) {
			if (o.hasCuttableObject(STONE) && isCuttableByPlayer(x, y, player.playerId)) {
				playerStatistic.stones.addNoCollission(x, y);
			} else if (o.hasMapObjectTypes(TREE_GROWING, TREE_ADULT) && isCuttableByPlayer(x, y, player.playerId)) {
				playerStatistic.trees.addNoCollission(x, y);
			}
		}
		ELandscapeType landscape = landscapeGrid.getLandscapeTypeAt(x, y);
		if (landscape.isRiver()) {
			playerStatistic.rivers.addNoCollission(x, y);
		}
		if (objectsGrid.hasMapObjectType(x, y, EMapObjectType.WINE_GROWING, EMapObjectType.WINE_HARVESTABLE)) {
			playerStatistic.wineCount++;
		}
	}

	private boolean isCuttableByPlayer(short x, short y, byte playerId) {
		byte[] playerIds = new byte[4];
		playerIds[0] = partitionsGrid.getPlayerIdAt(x - 2, y - 2);
		playerIds[1] = partitionsGrid.getPlayerIdAt(x - 2, y + 2);
		playerIds[2] = partitionsGrid.getPlayerIdAt(x + 2, y - 2);
		playerIds[3] = partitionsGrid.getPlayerIdAt(x + 2, y + 2);
		for (byte positionPlayerId : playerIds) {
			if (positionPlayerId != playerId) {
				return false;
			}
		}
		return true;
	}

	private void updateFreeLand(short x, short y) {
		if (objectsGrid.hasCuttableObject(x, y, TREE_ADULT)) {
			AiPositions trees = sortedCuttableObjectsInDefaultPartition.get(TREE_ADULT);
			if (trees == null) {
				trees = new AiPositions();
				sortedCuttableObjectsInDefaultPartition.put(TREE_ADULT, trees);
			}
			trees.addNoCollission(x, y);
		}
		if (objectsGrid.hasCuttableObject(x, y, STONE)) {
			AiPositions stones = sortedCuttableObjectsInDefaultPartition.get(STONE);
			if (stones == null) {
				stones = new AiPositions();
				sortedCuttableObjectsInDefaultPartition.put(STONE, stones);
			}
			stones.addNoCollission(x, y);
			updateNearStones(x, y);
		}
		ELandscapeType landscape = landscapeGrid.getLandscapeTypeAt(x, y);
		if (landscape.isRiver()) {
			sortedRiversInDefaultPartition.addNoCollission(x, y);
		}
	}

	private void updateNearStones(short x, short y) {
		for (EDirection dir : EDirection.VALUES) {
			int dx = dir.getNextTileX(x, NEAR_STONE_DISTANCE);
			int dy = dir.getNextTileY(y, NEAR_STONE_DISTANCE);
			if (mainGrid.isInBounds(dx, dy)) {
				byte playerId = partitionsGrid.getPlayerIdAt(dx, dy);
				if (playerId != -1) {
					playerStatistics[playerId].stonesNearBy.addNoCollission(x, y);
				}
			}
		}
	}

	private void updatePartitionIdsToBuildOn() {
		for (byte playerId = 0; playerId < playerStatistics.length; playerId++) {
			ShortPoint2D referencePosition = null;
			for (EBuildingType referenceFinderBuildingType : REFERENCE_POINT_FINDER_BUILDING_ORDER) {
				if (getTotalNumberOfBuildingTypeForPlayer(referenceFinderBuildingType, playerId) > 0) {
					referencePosition = getBuildingPositionsOfTypeForPlayer(referenceFinderBuildingType, playerId).get(0);
					break;
				}
			}

			if (referencePosition != null) {
				playerStatistics[playerId].referencePosition = referencePosition;
				playerStatistics[playerId].partitionIdToBuildOn = partitionsGrid.getPartitionIdAt(referencePosition.x, referencePosition.y);
				playerStatistics[playerId].materialProduction = partitionsGrid.getMaterialProductionAt(referencePosition.x, referencePosition.y);
				playerStatistics[playerId].materials = partitionsGrid.getPartitionDataForManagerAt(referencePosition.x, referencePosition.y);
			}
		}
	}

	public Building getBuildingAt(ShortPoint2D point) {
		return (Building) objectsGrid.getMapObjectAt(point.x, point.y, EMapObjectType.BUILDING);
	}

	public ShortPoint2D getNearestResourcePointForPlayer(ShortPoint2D point, EResourceType resourceType, byte playerId, int searchDistance) {
		return getNearestPointInDefaultPartitionOutOfSortedMap(point, sortedResourceTypes[resourceType.ordinal], playerId, searchDistance);
	}

	public ShortPoint2D getNearestFishPointForPlayer(ShortPoint2D point, final byte playerId, int currentNearestPointDistance) {
		return sortedResourceTypes[EResourceType.FISH.ordinal].getNearestPoint(point, currentNearestPointDistance, new AiPositionFilter() {
			@Override
			public boolean contains(int x, int y) {
				return isPlayerThere(x + 3, y) || isPlayerThere(x - 3, y) || isPlayerThere(x, y + 3)
						|| isPlayerThere(x, y - 3);
			}

			private boolean isPlayerThere(int x, int y) {
				return mainGrid.isInBounds(x, y) && partitionsGrid.getPartitionAt(x, y).getPlayerId() == playerId;
			}
		});
	}

	public ShortPoint2D getNearestResourcePointInDefaultPartitionFor(ShortPoint2D point, EResourceType resourceType,
			int currentNearestPointDistance) {
		return getNearestResourcePointForPlayer(point, resourceType, (byte) -1, currentNearestPointDistance);
	}

	public ShortPoint2D getNearestCuttableObjectPointInDefaultPartitionFor(ShortPoint2D point, EMapObjectType cuttableObject, int searchDistance) {
		return getNearestCuttableObjectPointForPlayer(point, cuttableObject, searchDistance, (byte) -1);
	}

	public ShortPoint2D getNearestCuttableObjectPointForPlayer(ShortPoint2D point, EMapObjectType cuttableObject, int searchDistance, byte playerId) {
		AiPositions sortedResourcePoints = sortedCuttableObjectsInDefaultPartition.get(cuttableObject);
		if (sortedResourcePoints == null) {
			return null;
		}

		return getNearestPointInDefaultPartitionOutOfSortedMap(point, sortedResourcePoints, playerId, searchDistance);
	}

	private ShortPoint2D getNearestPointInDefaultPartitionOutOfSortedMap(ShortPoint2D point, AiPositions sortedPoints, final byte playerId,
			int searchDistance) {
		return sortedPoints.getNearestPoint(point, searchDistance, new AiPositionFilter() {
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

	public int getTotalWineCountForPlayer(byte playerId) {
		return playerStatistics[playerId].wineCount;
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

	public List<ShortPoint2D> getBuildingPositionsOfTypesForPlayer(EnumSet<EBuildingType> buildingTypes, byte playerId) {
		List<ShortPoint2D> buildingPositions = new Vector<ShortPoint2D>();
		for (EBuildingType buildingType : buildingTypes) {
			buildingPositions.addAll(getBuildingPositionsOfTypeForPlayer(buildingType, playerId));
		}
		return buildingPositions;
	}

	public AiPositions getStonesForPlayer(byte playerId) {
		return playerStatistics[playerId].stones;
	}

	public AiPositions getTreesForPlayer(byte playerId) {
		return playerStatistics[playerId].trees;
	}

	public AiPositions getLandForPlayer(byte playerId) {
		return playerStatistics[playerId].landToBuildOn;
	}

	public boolean blocksWorkingAreaOfOtherBuilding(ShortPoint2D point, byte playerId, EBuildingType buildingType) {

		for (ShortPoint2D workAreaCenter : playerStatistics[playerId].wineGrowerWorkAreas) {
			for (RelativePoint blockedPoint : buildingType.getBlockedTiles()) {
				if (workAreaCenter.getOnGridDistTo(blockedPoint.calculatePoint(point)) <= EBuildingType.WINEGROWER.getWorkRadius()) {
					return true;
				}
			}
		}

		for (ShortPoint2D workAreaCenter : playerStatistics[playerId].farmWorkAreas) {
			for (RelativePoint blockedPoint : buildingType.getBlockedTiles()) {
				if (workAreaCenter.getOnGridDistTo(blockedPoint.calculatePoint(point)) <= EBuildingType.FARM.getWorkRadius()) {
					return true;
				}
			}
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
		return mainGrid.isInBounds(x, y)
				&& partitionsGrid.getPlayerIdAt(x, y) == playerId
				&& !objectsGrid.isBuildingAt(x, y)
				&& !flagsGrid.isProtected(x, y)
				&& landscapeGrid.areAllNeighborsOf(x, y, 0, 2, ELandscapeType.GRASS, ELandscapeType.EARTH);
	}

	public IMovable getNearestSwordsmanOf(ShortPoint2D targetPosition, byte playerId) {
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

	public static ShortPoint2D detectNearestPointFromList(ShortPoint2D referencePoint, List<ShortPoint2D> points) {
		if (points.isEmpty()) {
			return null;
		}

		return detectNearestPointsFromList(referencePoint, points, 1).get(0);
	}

	public static List<ShortPoint2D> detectNearestPointsFromList(final ShortPoint2D referencePoint, List<ShortPoint2D> points,
			int amountOfPointsToDetect) {
		if (amountOfPointsToDetect <= 0) {
			return Collections.emptyList();
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

	public ShortPoint2D getNearestRiverPointInDefaultPartitionFor(ShortPoint2D referencePoint, int searchDistance) {
		return getNearestPointInDefaultPartitionOutOfSortedMap(referencePoint, sortedRiversInDefaultPartition, (byte) -1, searchDistance);
	}

	public int getNumberOfNotFinishedBuildingTypesForPlayer(EBuildingType buildingType, byte playerId) {
		return getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId) - getNumberOfBuildingTypeForPlayer(buildingType, playerId);
	}

	public AiPositions getRiversForPlayer(byte playerId) {
		return playerStatistics[playerId].rivers;
	}

	public List<Byte> getEnemiesOf(byte playerId) {
		List<Byte> enemies = new ArrayList<Byte>();
		for (Team team : partitionsGrid.getTeams()) {
			if (!team.isMember(playerId)) {
				for (Player player : team.getMembers()) {
					enemies.add(player.playerId);
				}
			}
		}
		return enemies;
	}

	public List<Byte> getAliveEnemiesOf(byte playerId) {
		List<Byte> aliveEnemies = new ArrayList<>();
		for (byte enemyId : getEnemiesOf(playerId)) {
			if (isAlive(enemyId)) {
				aliveEnemies.add(enemyId);
			}
		}
		return aliveEnemies;
	}

	public ShortPoint2D calculateAveragePointFromList(List<ShortPoint2D> points) {
		int averageX = 0;
		int averageY = 0;
		for (ShortPoint2D point : points) {
			averageX += point.x;
			averageY += point.y;
		}
		return new ShortPoint2D(averageX / points.size(), averageY / points.size());
	}

	public AiPositions getEnemiesInTownOf(byte playerId) {
		return playerStatistics[playerId].enemyTroopsInTown;
	}

	public IMaterialProductionSettings getMaterialProduction(byte playerId) {
		return playerStatistics[playerId].materialProduction;
	}

	public ShortPoint2D getPositionOfPartition(byte playerId) {
		return playerStatistics[playerId].referencePosition;
	}

	public AiPositions getBorderOf(byte playerId) {
		return playerStatistics[playerId].border;
	}

	public AiPositions getOtherPartitionBorderOf(byte playerId) {
		return playerStatistics[playerId].otherPartitionBorder;
	}

	public boolean isAlive(byte playerId) {
		return playerStatistics[playerId].isAlive;
	}

	public AiMapInformation getAiMapInformation() {
		return aiMapInformation;
	}

	public long resourceCountInDefaultPartition(EResourceType resourceType) {
		return resourceCountInDefaultPartition[resourceType.ordinal];
	}

	public long resourceCountOfPlayer(EResourceType resourceType, byte playerId) {
		return playerStatistics[playerId].resourceCount[resourceType.ordinal];
	}

	public List<ShortPoint2D> threatenedBorderOf(byte playerId) {
		if (playerStatistics[playerId].threatenedBorder == null) {
			AiPositions borderOfOtherPlayers = new AiPositions();
			for (byte otherPlayerId = 0; otherPlayerId < playerStatistics.length; otherPlayerId++) {
				if (otherPlayerId == playerId || !isAlive(otherPlayerId)) {
					continue;
				}

				borderOfOtherPlayers.addAllNoCollision(getBorderOf(otherPlayerId));
			}
			playerStatistics[playerId].threatenedBorder = new ArrayList<>();
			AiPositions myBorder = getBorderOf(playerId);
			for (int i = 0; i < myBorder.size(); i += 10) {
				ShortPoint2D myBorderPosition = myBorder.get(i);
				if (mainGrid.getPartitionsGrid().getTowerCountAt(myBorderPosition.x, myBorderPosition.y) == 0
						&& borderOfOtherPlayers.getNearestPoint(myBorderPosition, CommonConstants.TOWER_RADIUS) != null) {
					playerStatistics[playerId].threatenedBorder.add(myBorderPosition);
				}
			}
		}
		return playerStatistics[playerId].threatenedBorder;
	}

	public AiPositions getStonesNearBy(byte playerId) {
		return playerStatistics[playerId].stonesNearBy;
	}

	private static class PlayerStatistic {
		ShortPoint2D referencePosition;
		boolean isAlive;
		final int[] totalBuildingsNumbers = new int[EBuildingType.NUMBER_OF_BUILDINGS];
		final int[] buildingsNumbers = new int[EBuildingType.NUMBER_OF_BUILDINGS];
		final Map<EBuildingType, List<ShortPoint2D>> buildingPositions =  new HashMap<EBuildingType, List<ShortPoint2D>>();
		final List<ShortPoint2D> farmWorkAreas = new Vector<ShortPoint2D>();
		final List<ShortPoint2D> wineGrowerWorkAreas = new Vector<ShortPoint2D>();
		short partitionIdToBuildOn;
		IPartitionData materials;
		final AiPositions landToBuildOn = new AiPositions();
		final AiPositions border = new AiPositions();
		final AiPositions otherPartitionBorder = new AiPositions();
		final Map<EMovableType, List<ShortPoint2D>> movablePositions = new HashMap<EMovableType, List<ShortPoint2D>>();
		final AiPositions stones = new AiPositions();
		final AiPositions stonesNearBy = new AiPositions();
		final AiPositions trees = new AiPositions();
		final AiPositions rivers = new AiPositions();
		final AiPositions enemyTroopsInTown = new AiPositions();
		List<ShortPoint2D> threatenedBorder;
		final long[] resourceCount = new long[EResourceType.VALUES.length];
		int numberOfNotFinishedBuildings;
		int numberOfTotalBuildings;
		int numberOfNotOccupiedMilitaryBuildings;
		int wineCount;
		IMaterialProductionSettings materialProduction;

		PlayerStatistic() {
			clearIntegers();
		}

		public void clearAll() {
			isAlive = false;
			materials = null;
			buildingPositions.clear();
			enemyTroopsInTown.clear();
			stones.clear();
			stonesNearBy.clear();
			trees.clear();
			rivers.clear();
			landToBuildOn.clear();
			border.clear();
			otherPartitionBorder.clear();
			movablePositions.clear();
			farmWorkAreas.clear();
			wineGrowerWorkAreas.clear();
			threatenedBorder = null;
			clearIntegers();
		}

		private void clearIntegers() {
			clearIntegerArray(totalBuildingsNumbers);
			clearIntegerArray(buildingsNumbers);
			clearLongArray(resourceCount);
			numberOfNotFinishedBuildings = 0;
			numberOfTotalBuildings = 0;
			numberOfNotOccupiedMilitaryBuildings = 0;
			wineCount = 0;
			partitionIdToBuildOn = Short.MIN_VALUE;
		}

		private void clearLongArray(long[] theArray) {
			for (int i = 0; i < theArray.length; i++) {
				theArray[i] = 0;
			}
		}

		private void clearIntegerArray(int[] theArray) {
			for (int i = 0; i < theArray.length; i++) {
				theArray[i] = 0;
			}
		}
	}

}