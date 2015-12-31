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

import static jsettlers.common.mapobject.EMapObjectType.STONE;
import static jsettlers.common.mapobject.EMapObjectType.TREE_ADULT;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L1;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L2;
import static jsettlers.common.movable.EMovableType.SWORDSMAN_L3;

import java.util.*;

import jsettlers.ai.highlevel.AiPositions.AiPositionFilter;
import jsettlers.algorithms.construction.AbstractConstructionMarkableMap;
import jsettlers.common.CommonConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.MaterialProductionSettings;
import jsettlers.logic.buildings.WorkAreaBuilding;
import jsettlers.logic.buildings.workers.MineBuilding;
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

	private static final short BORDER_LAND_WIDTH = 5;
	private static final int MINE_REMAINING_RESOURCE_AMOUNT_WHEN_DEAD = 200;
	private static final float MINE_PRODUCTIVITY_WHEN_DEAD = 0.1f;
	private static final EBuildingType[] REFERENCE_POINT_FINDER_BUILDING_ORDER = {
			EBuildingType.LUMBERJACK, EBuildingType.TOWER, EBuildingType.BIG_TOWER, EBuildingType.CASTLE};

	private final Queue<Building> buildings;
	private PlayerStatistic[] playerStatistics;
	private Map<EMapObjectType, AiPositions> sortedCuttableObjectsInDefaultPartition;
	private AiPositions[] sortedResourceTypes;
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
		sortedResourceTypes = new AiPositions[EResourceType.values.length];
		for (int i = 0; i < sortedResourceTypes.length; i++) {
			sortedResourceTypes[i] = new AiPositions();
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

		if (building.getBuildingType().isMine() && building.isOccupied()) {
			MineBuilding mine = (MineBuilding) building;
			if (mine.getRemainingResourceAmount() <= MINE_REMAINING_RESOURCE_AMOUNT_WHEN_DEAD
					&& mine.getProductivity() <= MINE_PRODUCTIVITY_WHEN_DEAD) {
				playerStatistic.deadMines.addNoCollission(mine.getPos().x, mine.getPos().y);
			}
		} else if (type == EBuildingType.WINEGROWER) {
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
		if (!building.isOccupied()) {
			playerStatistic.unoccupiedBuildingsNumbers[type.ordinal]++;
		}
	}

	private void updateNumberOfNotFinishedBuildings(PlayerStatistic playerStatistic, Building building) {
		playerStatistic.numberOfTotalBuildings++;
		if (building.getStateProgress() < 1f) {
			playerStatistic.numberOfNotFinishedBuildings++;
			if (building.getBuildingType().isMilitaryBuilding()) {
				playerStatistic.numberOfNotOccupiedMilitaryBuildingss++;
			}
		} else if (building.getBuildingType().isMilitaryBuilding()) {
			if (building.isOccupied()) {
				playerStatistic.isAlive = true;
			} else {
				playerStatistic.numberOfNotOccupiedMilitaryBuildingss++;
			}
		}
	}

	private void updateMapStatistics() {
		updatePartitionIdsToBuildOn();
		updateResources();
		short width = mainGrid.getWidth();
		short height = mainGrid.getHeight();
		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				Player player = partitionsGrid.getPlayerAt(x, y);
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
							&& EMovableType.isSoldier(movableType)
							&& getEnemiesOf(player.playerId).contains(movablePlayerId)) {
						playerStatistics[player.playerId].enemyTroopsInTown.addNoCollission(movable.getPos().x, movable.getPos().y);
					}
				}
				if (player == null) {
					updateFreeLand(x, y);
				} else if (partitionsGrid.getPartitionIdAt(x, y) == playerStatistics[player.playerId].partitionIdToBuildOn) {
					updatePlayerLand(x, y, player);
				}
			}
		}
	}

	private void updatePlayerLand(short x, short y, Player player) {
		int playerId = player.playerId;
		PlayerStatistic playerStatistic = playerStatistics[playerId];
		updateBorderlandNextToFreeLand(playerStatistic, x, y);
		playerStatistic.landToBuildOn.addNoCollission(x, y);
		AbstractHexMapObject o = objectsGrid.getObjectsAt(x, y);
		if (o != null) {
			if (o.hasCuttableObject(STONE) && isCuttableByPlayer(x, y, player.playerId)) {
				playerStatistic.stones.addNoCollission(x, y);
			} else if (o.hasCuttableObject(TREE_ADULT) && isCuttableByPlayer(x, y, player.playerId)) {
				playerStatistic.trees.addNoCollission(x, y);
			}
		}
		ELandscapeType landscape = landscapeGrid.getLandscapeTypeAt(x, y);
		if (landscape.isRiver()) {
			playerStatistic.rivers.addNoCollission(x, y);
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
		}
		ELandscapeType landscape = landscapeGrid.getLandscapeTypeAt(x, y);
		if (landscape.isRiver()) {
			sortedRiversInDefaultPartition.addNoCollission(x, y);
		}
	}

	private void updateResources() {
		short width = mainGrid.getWidth();
		short height = mainGrid.getHeight();
		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				if (landscapeGrid.getResourceAmountAt(x, y) > 0) {
					EResourceType resourceType = landscapeGrid.getResourceTypeAt(x, y);
					sortedResourceTypes[resourceType.ordinal].addNoCollission(x, y);
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

	private void updateBorderlandNextToFreeLand(PlayerStatistic playerStatistic, short x, short y) {
		for (EDirection dir : EDirection.values) {
			int lx = x + dir.gridDeltaX * BORDER_LAND_WIDTH;
			int ly = y + dir.gridDeltaY * BORDER_LAND_WIDTH;
			if (mainGrid.isInBounds(lx, ly)) {
				if (partitionsGrid.isDefaultPartition(partitionsGrid.getPartitionIdAt(lx, ly))) {
					playerStatistic.borderLandNextToFreeLand.addNoCollission(x, y);
					break;
				}
			}
		}
	}

	public List<ShortPoint2D> getHinterlandMilitaryBuildingPositionsOfPlayer(byte playerId) {
		List<ShortPoint2D> hinterlandMilitaryBuildingPositions = new ArrayList<ShortPoint2D>();
		for (ShortPoint2D militaryBuildingPosition : getBuildingPositionsOfTypesForPlayer(EBuildingType.getMilitaryBuildings(), playerId)) {
			Building militaryBuilding = getBuildingAt(militaryBuildingPosition);
			if (isMilitaryBuildingInHinterland(militaryBuilding, playerId)) {
				hinterlandMilitaryBuildingPositions.add(militaryBuildingPosition);
			}
		}
		return hinterlandMilitaryBuildingPositions;
	}

	private boolean isMilitaryBuildingInHinterland(Building militaryBuilding, byte playerId) {
		for (ShortPoint2D occupiedPosition : new MapCircle(militaryBuilding.getPos(), CommonConstants.TOWER_RADIUS)) {
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
		return getNearestPointInDefaultPartitionOutOfSortedMap(point, sortedResourceTypes[resourceType.ordinal], playerId,
				currentNearestPointDistance);
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

	public List<ShortPoint2D> getBuildingPositionsOfTypesForPlayer(EBuildingType[] buildingTypes, byte playerId) {
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

	public AiPositions getBorderLandNextToFreeLandForPlayer(byte playerId) {
		return playerStatistics[playerId].borderLandNextToFreeLand;
	}

	public int getNumberOfNotOccupiedMilitaryBuildings(short playerId) {
		return playerStatistics[playerId].numberOfNotOccupiedMilitaryBuildingss;
	}

	public boolean blocksWorkingAreaOfOtherBuilding(ShortPoint2D point, byte playerId, EBuildingType buildingType) {

		for (ShortPoint2D workAreaCenter : playerStatistics[playerId].wineGrowerWorkAreas) {
			for (RelativePoint blockedPoint : buildingType.getBlockedTiles()) {
				if (workAreaCenter.getOnGridDistTo(blockedPoint.calculatePoint(point)) <= EBuildingType.WINEGROWER.getWorkradius()) {
					return true;
				}
			}
		}

		for (ShortPoint2D workAreaCenter : playerStatistics[playerId].farmWorkAreas) {
			for (RelativePoint blockedPoint : buildingType.getBlockedTiles()) {
				if (workAreaCenter.getOnGridDistTo(blockedPoint.calculatePoint(point)) <= EBuildingType.FARM.getWorkradius()) {
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
		return partitionsGrid.getPlayerIdAt(x, y) == playerId && !objectsGrid.isBuildingAt(x, y) && !flagsGrid.isProtected(x, y)
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

	public ShortPoint2D getNearestRiverPointInDefaultPartitionFor(ShortPoint2D referencePoint, int currentNearestPointDistance) {
		return getNearestPointInDefaultPartitionOutOfSortedMap(referencePoint, sortedRiversInDefaultPartition, (byte) -1,
				currentNearestPointDistance);
	}

	public int getNumberOfNotFinishedBuildingTypesForPlayer(EBuildingType buildingType, byte playerId) {
		return getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId) - getNumberOfBuildingTypeForPlayer(buildingType, playerId);
	}

	public AiPositions getRiversForPlayer(byte playerId) {
		return playerStatistics[playerId].rivers;
	}

	public int getNumberOfUnoccupiedBuildingTypeForPlayer(EBuildingType buildingType, byte playerId) {
		return playerStatistics[playerId].unoccupiedBuildingsNumbers[buildingType.ordinal];
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

	public AiPositions getDeadMinesOf(byte playerId) {
		return playerStatistics[playerId].deadMines;
	}

	public IMaterialProductionSettings getMaterialProduction(byte playerId) {
		return playerStatistics[playerId].materialProduction;
	}

	public boolean isAlive(byte playerId) {
		return playerStatistics[playerId].isAlive;
	}

	public ShortPoint2D getPositionOfPartition(byte playerId) {
		return playerStatistics[playerId].referencePosition;
	}

	class PlayerStatistic {
		ShortPoint2D referencePosition;
		boolean isAlive;
		int[] totalBuildingsNumbers;
		int[] buildingsNumbers;
		int[] unoccupiedBuildingsNumbers;
		Map<EBuildingType, List<ShortPoint2D>> buildingPositions;
		List<ShortPoint2D> farmWorkAreas;
		List<ShortPoint2D> wineGrowerWorkAreas;
		short partitionIdToBuildOn;
		IPartitionData materials;
		AiPositions landToBuildOn;
		AiPositions borderLandNextToFreeLand;
		Map<EMovableType, List<ShortPoint2D>> movablePositions;
		AiPositions stones;
		AiPositions trees;
		AiPositions rivers;
		AiPositions enemyTroopsInTown;
		AiPositions deadMines;
		int numberOfNotFinishedBuildings;
		int numberOfTotalBuildings;
		int numberOfNotOccupiedMilitaryBuildingss;
		MaterialProductionSettings materialProduction;

		PlayerStatistic() {
			buildingPositions = new HashMap<EBuildingType, List<ShortPoint2D>>();
			stones = new AiPositions();
			trees = new AiPositions();
			rivers = new AiPositions();
			landToBuildOn = new AiPositions();
			enemyTroopsInTown = new AiPositions();
			deadMines = new AiPositions();
			borderLandNextToFreeLand = new AiPositions();
			movablePositions = new HashMap<EMovableType, List<ShortPoint2D>>();
			totalBuildingsNumbers = new int[EBuildingType.NUMBER_OF_BUILDINGS];
			buildingsNumbers = new int[EBuildingType.NUMBER_OF_BUILDINGS];
			unoccupiedBuildingsNumbers = new int[EBuildingType.NUMBER_OF_BUILDINGS];
			farmWorkAreas = new Vector<ShortPoint2D>();
			wineGrowerWorkAreas = new Vector<ShortPoint2D>();
			clearIntegers();
		}

		public void clearAll() {
			isAlive = false;
			materials = null;
			buildingPositions.clear();
			enemyTroopsInTown.clear();
			deadMines.clear();
			stones.clear();
			trees.clear();
			rivers.clear();
			landToBuildOn.clear();
			borderLandNextToFreeLand.clear();
			movablePositions.clear();
			farmWorkAreas.clear();
			wineGrowerWorkAreas.clear();
			clearIntegers();
		}

		private void clearIntegers() {
			clearIntegerArray(totalBuildingsNumbers);
			clearIntegerArray(buildingsNumbers);
			clearIntegerArray(unoccupiedBuildingsNumbers);
			numberOfNotFinishedBuildings = 0;
			numberOfTotalBuildings = 0;
			numberOfNotOccupiedMilitaryBuildingss = 0;
			partitionIdToBuildOn = Short.MIN_VALUE;
		}

		private void clearIntegerArray(int[] theArray) {
			for (int i = 0; i < theArray.length; i++) {
				theArray[i] = 0;
			}
		}
	}

}