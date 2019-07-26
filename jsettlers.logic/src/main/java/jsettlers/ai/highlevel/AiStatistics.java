/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Vector;

import java8.util.Comparators;
import java8.util.J8Arrays;
import java8.util.Maps;
import java8.util.Objects;
import java8.util.stream.Collectors;
import jsettlers.ai.highlevel.AiPositions.AiPositionFilter;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableAction;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.player.IPlayer;
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
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.logic.player.Player;

import static java8.util.stream.StreamSupport.stream;
import static jsettlers.common.buildings.EBuildingType.BIG_TOWER;
import static jsettlers.common.buildings.EBuildingType.CASTLE;
import static jsettlers.common.buildings.EBuildingType.LUMBERJACK;
import static jsettlers.common.buildings.EBuildingType.TOWER;
import static jsettlers.common.mapobject.EMapObjectType.STONE;
import static jsettlers.common.mapobject.EMapObjectType.TREE_ADULT;
import static jsettlers.common.mapobject.EMapObjectType.TREE_GROWING;
import static jsettlers.common.movable.EMovableType.BEARER;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L1;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L2;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L3;

/**
 * This class calculates statistics based on the grids which are used by highlevel and lowlevel KI. The statistics are calculated once and read multiple times within one AiExecutor step triggerd by
 * the game clock.
 *
 * @author codingberlin
 */
public class AiStatistics {

	private static final EBuildingType[] REFERENCE_POINT_FINDER_BUILDING_ORDER = { LUMBERJACK, TOWER, BIG_TOWER, CASTLE };
	public static final int NEAR_STONE_DISTANCE = 5;

	private final MainGrid mainGrid;
	private final Queue<Building> buildings;
	private final PlayerStatistic[] playerStatistics;
	private final Map<EMapObjectType, AiPositions> sortedCuttableObjectsInDefaultPartition;
	private final AiPositions[] sortedResourceTypes;
	private final AiPositions sortedRiversInDefaultPartition;
	private final LandscapeGrid landscapeGrid;
	private final ObjectsGrid objectsGrid;
	private final PartitionsGrid partitionsGrid;
	private final MovableGrid movableGrid;
	private final FlagsGrid flagsGrid;
	private final AbstractConstructionMarkableMap constructionMarksGrid;
	private final AiMapInformation aiMapInformation;
	private final long[] resourceCountInDefaultPartition;
	private final List<Player> players;

	public AiStatistics(MainGrid mainGrid) {
		this.mainGrid = mainGrid;
		buildings = Building.getAllBuildings();
		landscapeGrid = mainGrid.getLandscapeGrid();
		objectsGrid = mainGrid.getObjectsGrid();
		partitionsGrid = mainGrid.getPartitionsGrid();
		movableGrid = mainGrid.getMovableGrid();
		flagsGrid = mainGrid.getFlagsGrid();
		constructionMarksGrid = mainGrid.getConstructionMarksGrid();
		playerStatistics = new PlayerStatistic[mainGrid.getGuiInputGrid().getNumberOfPlayers()];
		aiMapInformation = new AiMapInformation(partitionsGrid, landscapeGrid);
		for (byte i = 0; i < mainGrid.getGuiInputGrid().getNumberOfPlayers(); i++) {
			this.playerStatistics[i] = new PlayerStatistic();
		}
		sortedRiversInDefaultPartition = new AiPositions();
		sortedCuttableObjectsInDefaultPartition = new HashMap<>();
		sortedResourceTypes = new AiPositions[EResourceType.VALUES.length];
		for (int i = 0; i < sortedResourceTypes.length; i++) {
			sortedResourceTypes[i] = new AiPositions();
		}
		resourceCountInDefaultPartition = new long[EResourceType.VALUES.length];
		players = J8Arrays.stream(partitionsGrid.getPlayers()).filter(Objects::nonNull).collect(Collectors.toList());
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
			PlayerStatistic playerStatistic = playerStatistics[building.getPlayer().getPlayerId()];
			EBuildingType type = building.getBuildingType();
			updateNumberOfNotFinishedBuildings(playerStatistic, building);
			updateBuildingsNumbers(playerStatistic, building, type);
			updateBuildingPositions(playerStatistic, type, building);
		}
	}

	private void updateBuildingPositions(PlayerStatistic playerStatistic, EBuildingType type, Building building) {
		if (!playerStatistic.buildingPositions.containsKey(type)) {
			playerStatistic.buildingPositions.put(type, new ArrayList<>());
		}
		playerStatistic.buildingPositions.get(type).add(building.getPosition());

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
		Arrays.fill(resourceCountInDefaultPartition, 0);

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
						if (fishMapInformationPlayerId == aiMapInformation.resourceAndGrassCount.length - 1) {
							resourceCountInDefaultPartition[resourceType.ordinal]++;
						} else {
							playerStatistics[fishMapInformationPlayerId].resourceCount[resourceType.ordinal]++;
						}
					}
				}
				if (landscapeGrid.getLandscapeTypeAt(x, y).isGrass()) {
					aiMapInformation.resourceAndGrassCount[mapInformationPlayerId][AiMapInformation.GRASS_INDEX]++;
				}
				ILogicMovable movable = movableGrid.getMovableAt(x, y);
				if (movable != null) {
					Player movablePlayer = movable.getPlayer();
					byte movablePlayerId = movablePlayer.playerId;
					PlayerStatistic movablePlayerStatistic = playerStatistics[movablePlayerId];
					EMovableType movableType = movable.getMovableType();
					Maps.computeIfAbsent(movablePlayerStatistic.movablePositions, movableType, key -> new ArrayList<>()).add(movable.getPosition());

					if (movableType == BEARER && movable.getAction() == EMovableAction.NO_ACTION) {
						playerStatistics[movablePlayerId].joblessBearerPositions.add(movable.getPosition());
					}
					if (player != null && player.playerId != movablePlayerId && movableType.isSoldier() && getEnemiesOf(player).contains(movablePlayer)) {
						playerStatistics[player.playerId].enemyTroopsInTown.addNoCollission(movable.getPosition().x, movable.getPosition().y);
					}
				}
				if (player == null) {
					updateFreeLand(x, y);
				} else if (partitionsGrid.getPartitionIdAt(x, y) == playerStatistics[player.playerId].partitionIdToBuildOn) {
					updatePlayerLand(x, y, player);
				}
				if (player != null && hasNeighborIngestibleByPioneersOf(x, y, player)) {
					if (partitionsGrid.getPartitionIdAt(x, y) == playerStatistics[player.playerId].partitionIdToBuildOn) {
						playerStatistics[player.playerId].borderIngestibleByPioneers.add(x, y);
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

	private boolean hasNeighborIngestibleByPioneersOf(int x, int y, Player player) {
		return !MapNeighboursArea.stream(x, y)
				.filterBounds(mainGrid.getWidth(), mainGrid.getHeight())
				.filter((currX, currY) -> isIngestibleByPioneersOf(currX, currY, player))
				.isEmpty();
	}

	private boolean isIngestibleByPioneersOf(int x, int y, Player player) {
		Player otherPlayer = partitionsGrid.getPlayerAt(x, y);
		return !player.hasSameTeam(otherPlayer)
				&& !flagsGrid.isBlocked(x, y)
				&& !partitionsGrid.isEnforcedByTower(x, y);
	}

	private void updatePlayerLand(short x, short y, Player player) {
		byte playerId = player.playerId;
		PlayerStatistic playerStatistic = playerStatistics[playerId];
		if (mainGrid.getFlagsGrid().isProtected(x, y)) {
			AbstractHexMapObject o = objectsGrid.getObjectsAt(x, y);
			if (o != null) {
				if (o.hasCuttableObject(STONE) && isCuttableByPlayer(x, y, player.playerId)) {
					playerStatistic.stones.addNoCollission(x, y);
				} else if (o.hasMapObjectTypes(TREE_GROWING, TREE_ADULT) && isCuttableByPlayer(x, y, player.playerId)) {
					playerStatistic.trees.addNoCollission(x, y);
				}
			}
		} else {
			playerStatistic.landToBuildOn.addNoCollission(x, y);
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
			int currX = dir.getNextTileX(x, NEAR_STONE_DISTANCE);
			int currY = dir.getNextTileY(y, NEAR_STONE_DISTANCE);
			if (mainGrid.isInBounds(currX, currY)) {
				byte playerId = partitionsGrid.getPlayerIdAt(currX, currY);
				if (playerId != -1 && hasPlayersBlockedPartition(playerId, x, y)) {
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
				PlayerStatistic playerStatistic = playerStatistics[playerId];
				playerStatistic.referencePosition = referencePosition;
				playerStatistic.partitionIdToBuildOn = partitionsGrid.getPartitionIdAt(referencePosition.x, referencePosition.y);
				playerStatistic.blockedPartitionId = landscapeGrid.getBlockedPartitionAt(referencePosition.x, referencePosition.y);
				playerStatistic.materialProduction = partitionsGrid.getMaterialProductionAt(referencePosition.x, referencePosition.y);
				playerStatistic.materials = partitionsGrid.getPartitionDataForManagerAt(referencePosition.x, referencePosition.y);
			}
		}
	}

	public Building getBuildingAt(ShortPoint2D point) {
		return (Building) objectsGrid.getMapObjectAt(point.x, point.y, EMapObjectType.BUILDING);
	}

	ShortPoint2D getNearestResourcePointForPlayer(ShortPoint2D point, EResourceType resourceType, byte playerId, int searchDistance, AiPositionFilter filter) {
		return getNearestPointInDefaultPartitionOutOfSortedMap(point, sortedResourceTypes[resourceType.ordinal], playerId, searchDistance, filter);
	}

	public ShortPoint2D getNearestFishPointForPlayer(ShortPoint2D point, final byte playerId, int currentNearestPointDistance) {
		return sortedResourceTypes[EResourceType.FISH.ordinal].getNearestPoint(point, currentNearestPointDistance, new AiPositionFilter() {
			@Override
			public boolean contains(int x, int y) {
				return isPlayerThere(x + 3, y) || isPlayerThere(x - 3, y) || isPlayerThere(x, y + 3) || isPlayerThere(x, y - 3);
			}

			private boolean isPlayerThere(int x, int y) {
				return mainGrid.isInBounds(x, y) && partitionsGrid.getPartitionAt(x, y).getPlayerId() == playerId;
			}
		});
	}

	public ShortPoint2D getNearestResourcePointInDefaultPartitionFor(ShortPoint2D point, EResourceType resourceType, int currentNearestPointDistance, AiPositionFilter filter) {
		return getNearestResourcePointForPlayer(point, resourceType, (byte) -1, currentNearestPointDistance, filter);
	}

	public ShortPoint2D getNearestCuttableObjectPointInDefaultPartitionFor(ShortPoint2D point, EMapObjectType cuttableObject, int searchDistance, AiPositionFilter filter) {
		return getNearestCuttableObjectPointForPlayer(point, cuttableObject, searchDistance, (byte) -1, filter);
	}

	private ShortPoint2D getNearestCuttableObjectPointForPlayer(ShortPoint2D point, EMapObjectType cuttableObject, int searchDistance, byte playerId, AiPositionFilter filter) {
		AiPositions sortedResourcePoints = sortedCuttableObjectsInDefaultPartition.get(cuttableObject);
		if (sortedResourcePoints == null) {
			return null;
		}

		return getNearestPointInDefaultPartitionOutOfSortedMap(point, sortedResourcePoints, playerId, searchDistance, filter);
	}

	private ShortPoint2D getNearestPointInDefaultPartitionOutOfSortedMap(ShortPoint2D point, AiPositions sortedPoints, final byte playerId, int searchDistance, final AiPositionFilter filter) {
		return sortedPoints.getNearestPoint(point, searchDistance, new AiPositions.CombinedAiPositionFilter((x, y) -> partitionsGrid.getPartitionAt(x, y).getPlayerId() == playerId, filter));
	}

	public boolean hasPlayersBlockedPartition(byte playerId, int x, int y) {
		return landscapeGrid.getBlockedPartitionAt(x, y) == playerStatistics[playerId].blockedPartitionId;
	}

	public List<ShortPoint2D> getPositionsOfMovablesWithTypeForPlayer(byte playerId, EMovableType movableType) {
		if (!playerStatistics[playerId].movablePositions.containsKey(movableType)) {
			return Collections.emptyList();
		}
		return playerStatistics[playerId].movablePositions.get(movableType);
	}

	public List<ShortPoint2D> getPositionsOfJoblessBearersForPlayer(byte playerId) {
		return playerStatistics[playerId].joblessBearerPositions;
	}

	public int getCountOfMovablesOfPlayer(IPlayer player, Set<EMovableType> types) {
		byte playerId = player.getPlayerId();
		return stream(types).mapToInt(type -> getPositionsOfMovablesWithTypeForPlayer(playerId, type).size()).sum();
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

	int getNumberOfNotFinishedBuildingsForPlayer(byte playerId) {
		return playerStatistics[playerId].numberOfNotFinishedBuildings;
	}

	int getNumberOfTotalBuildingsForPlayer(byte playerId) {
		return playerStatistics[playerId].numberOfTotalBuildings;
	}

	public List<ShortPoint2D> getBuildingPositionsOfTypeForPlayer(EBuildingType type, byte playerId) {
		if (!playerStatistics[playerId].buildingPositions.containsKey(type)) {
			return Collections.emptyList();
		}
		return playerStatistics[playerId].buildingPositions.get(type);
	}

	public List<ShortPoint2D> getBuildingPositionsOfTypesForPlayer(EnumSet<EBuildingType> buildingTypes, byte playerId) {
		List<ShortPoint2D> buildingPositions = new Vector<>();
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

	public boolean blocksWorkingAreaOfOtherBuilding(int x, int y, byte playerId, EBuildingType buildingType) {
		for (ShortPoint2D workAreaCenter : playerStatistics[playerId].wineGrowerWorkAreas) {
			for (RelativePoint blockedPoint : buildingType.getBlockedTiles()) {
				if (workAreaCenter.getOnGridDistTo(blockedPoint.calculatePoint(x, y)) <= EBuildingType.WINEGROWER.getWorkRadius()) {
					return true;
				}
			}
		}

		for (ShortPoint2D workAreaCenter : playerStatistics[playerId].farmWorkAreas) {
			for (RelativePoint blockedPoint : buildingType.getBlockedTiles()) {
				if (workAreaCenter.getOnGridDistTo(blockedPoint.calculatePoint(x, y)) <= EBuildingType.FARM.getWorkRadius()) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean southIsFreeForPlayer(ShortPoint2D point, byte playerId) {
		return pointIsFreeForPlayer(point.x, (short) (point.y + 12), playerId) &&
				pointIsFreeForPlayer((short) (point.x + 5), (short) (point.y + 12), playerId)
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
				&& landscapeGrid.isHexAreaOfType(x, y, 0, 2, ELandscapeType.GRASS, ELandscapeType.EARTH);
	}

	public boolean wasFishNearByAtGameStart(ShortPoint2D position) {
		return aiMapInformation.wasFishNearByAtGameStart.get(position.x * partitionsGrid.getWidth() + position.y);
	}

	IMovable getNearestSwordsmanOf(ShortPoint2D targetPosition, byte playerId) {
		List<ShortPoint2D> soldierPositions = getPositionsOfMovablesWithTypeForPlayer(playerId, SWORDSMAN_L3);
		if (soldierPositions.size() == 0) {
			soldierPositions = getPositionsOfMovablesWithTypeForPlayer(playerId, SWORDSMAN_L2);
		}
		if (soldierPositions.size() == 0) {
			soldierPositions = getPositionsOfMovablesWithTypeForPlayer(playerId, SWORDSMAN_L1);
		}
		if (soldierPositions.size() == 0) {
			return null;
		}

		ShortPoint2D nearestSoldierPosition = detectNearestPointFromList(targetPosition, soldierPositions);
		if (nearestSoldierPosition != null) {
			return movableGrid.getMovableAt(nearestSoldierPosition.x, nearestSoldierPosition.y);
		} else {
			return null;
		}
	}

	public static ShortPoint2D detectNearestPointFromList(ShortPoint2D referencePoint, List<ShortPoint2D> points) {
		if (points.isEmpty()) {
			return null;
		}

		return detectNearestPointsFromList(referencePoint, points, 1).get(0);
	}

	private static List<ShortPoint2D> detectNearestPointsFromList(final ShortPoint2D referencePoint, List<ShortPoint2D> points, int amountOfPointsToDetect) {
		if (amountOfPointsToDetect <= 0) {
			return Collections.emptyList();
		}

		if (points.size() <= amountOfPointsToDetect) {
			return points;
		}

		Collections.sort(points, Comparators.comparingInt(o -> o.getOnGridDistTo(referencePoint)));

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

	public ShortPoint2D getNearestRiverPointInDefaultPartitionFor(ShortPoint2D referencePoint, int searchDistance, AiPositionFilter filter) {
		return getNearestPointInDefaultPartitionOutOfSortedMap(referencePoint, sortedRiversInDefaultPartition, (byte) -1, searchDistance, filter);
	}

	int getNumberOfNotFinishedBuildingTypesForPlayer(EBuildingType buildingType, byte playerId) {
		return getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId) - getNumberOfBuildingTypeForPlayer(buildingType, playerId);
	}

	public AiPositions getRiversForPlayer(byte playerId) {
		return playerStatistics[playerId].rivers;
	}

	private List<IPlayer> getEnemiesOf(IPlayer player) {
		byte teamId = player.getTeamId();
		return stream(players).filter(currPlayer -> currPlayer.getTeamId() != teamId).collect(Collectors.toList());
	}

	public List<IPlayer> getAliveEnemiesOf(IPlayer player) {
		return stream(getEnemiesOf(player)).filter(this::isAlive).collect(Collectors.toList());
	}

	public static ShortPoint2D calculateAveragePointFromList(List<ShortPoint2D> points) {
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

	public AiPositions getBorderIngestibleByPioneersOf(byte playerId) {
		return playerStatistics[playerId].borderIngestibleByPioneers;
	}

	public AiPositions getOtherPartitionBorderOf(byte playerId) {
		return playerStatistics[playerId].otherPartitionBorder;
	}

	private boolean isAlive(IPlayer player) {
		return isAlive(player.getPlayerId());
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

	List<ShortPoint2D> threatenedBorderOf(byte playerId) {
		if (playerStatistics[playerId].threatenedBorder == null) {
			AiPositions borderOfOtherPlayers = new AiPositions();

			stream(players)
					.filter(currPlayer -> currPlayer.playerId != playerId)
					.filter(this::isAlive)
					.forEach(currPlayer -> borderOfOtherPlayers.addAllNoCollision(getBorderIngestibleByPioneersOf(currPlayer.playerId)));

			playerStatistics[playerId].threatenedBorder = new ArrayList<>();
			AiPositions myBorder = getBorderIngestibleByPioneersOf(playerId);

			for (int i = 0; i < myBorder.size(); i += 10) {
				ShortPoint2D myBorderPosition = myBorder.get(i);
				if (!partitionsGrid.isEnforcedByTower(myBorderPosition.x, myBorderPosition.y)
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
		final Map<EBuildingType, List<ShortPoint2D>> buildingPositions = new HashMap<>();
		final List<ShortPoint2D> farmWorkAreas = new Vector<>();
		final List<ShortPoint2D> wineGrowerWorkAreas = new Vector<>();
		short partitionIdToBuildOn;
		short blockedPartitionId;
		IPartitionData materials;
		final AiPositions landToBuildOn = new AiPositions();
		final AiPositions borderIngestibleByPioneers = new AiPositions();
		final AiPositions otherPartitionBorder = new AiPositions();
		final Map<EMovableType, List<ShortPoint2D>> movablePositions = new HashMap<>();
		final List<ShortPoint2D> joblessBearerPositions = new ArrayList<>();
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
			borderIngestibleByPioneers.clear();
			otherPartitionBorder.clear();
			movablePositions.clear();
			joblessBearerPositions.clear();
			farmWorkAreas.clear();
			wineGrowerWorkAreas.clear();
			threatenedBorder = null;
			clearIntegers();
		}

		private void clearIntegers() {
			Arrays.fill(totalBuildingsNumbers, 0);
			Arrays.fill(buildingsNumbers, 0);
			Arrays.fill(resourceCount, 0);
			numberOfNotFinishedBuildings = 0;
			numberOfTotalBuildings = 0;
			numberOfNotOccupiedMilitaryBuildings = 0;
			wineCount = 0;
			partitionIdToBuildOn = Short.MIN_VALUE;
			blockedPartitionId = Short.MIN_VALUE;
		}
	}

}