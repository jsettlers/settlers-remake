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

import static jsettlers.ai.construction.BestStoneCutterConstructionPositionFinder.MAX_STONE_DISTANCE;
import static jsettlers.common.buildings.EBuildingType.BAKER;
import static jsettlers.common.buildings.EBuildingType.BARRACK;
import static jsettlers.common.buildings.EBuildingType.BIG_LIVINGHOUSE;
import static jsettlers.common.buildings.EBuildingType.COALMINE;
import static jsettlers.common.buildings.EBuildingType.FARM;
import static jsettlers.common.buildings.EBuildingType.FISHER;
import static jsettlers.common.buildings.EBuildingType.FORESTER;
import static jsettlers.common.buildings.EBuildingType.GOLDMELT;
import static jsettlers.common.buildings.EBuildingType.GOLDMINE;
import static jsettlers.common.buildings.EBuildingType.IRONMELT;
import static jsettlers.common.buildings.EBuildingType.IRONMINE;
import static jsettlers.common.buildings.EBuildingType.LUMBERJACK;
import static jsettlers.common.buildings.EBuildingType.MEDIUM_LIVINGHOUSE;
import static jsettlers.common.buildings.EBuildingType.MILL;
import static jsettlers.common.buildings.EBuildingType.PIG_FARM;
import static jsettlers.common.buildings.EBuildingType.SAWMILL;
import static jsettlers.common.buildings.EBuildingType.SLAUGHTERHOUSE;
import static jsettlers.common.buildings.EBuildingType.SMALL_LIVINGHOUSE;
import static jsettlers.common.buildings.EBuildingType.STONECUTTER;
import static jsettlers.common.buildings.EBuildingType.TEMPLE;
import static jsettlers.common.buildings.EBuildingType.TOOLSMITH;
import static jsettlers.common.buildings.EBuildingType.TOWER;
import static jsettlers.common.buildings.EBuildingType.WATERWORKS;
import static jsettlers.common.buildings.EBuildingType.WEAPONSMITH;
import static jsettlers.common.buildings.EBuildingType.WINEGROWER;
import static jsettlers.common.material.EMaterialType.HAMMER;
import static jsettlers.common.material.EMaterialType.PICK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.ai.construction.BuildingCount;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.network.client.interfaces.ITaskScheduler;

public class RomanWhatToDoAi implements IWhatToDoAi {

	private final MainGrid mainGrid;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	private final AiStatistics aiStatistics;
	private final List<BuildingCount> buildMaterialEconomy;
	private final List<BuildingCount> foodEconomy;
	private final List<BuildingCount> weaponsEconomy;
	private final List<BuildingCount> manaEconomy;
	private final List<BuildingCount> goldEconomy;
	private final List<List<BuildingCount>> economiesOrder;
	BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory;

	public RomanWhatToDoAi(byte playerId, AiStatistics aiStatistics, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
		this.taskScheduler = taskScheduler;
		this.aiStatistics = aiStatistics;
		bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
		buildMaterialEconomy = new ArrayList<BuildingCount>();
		foodEconomy = new ArrayList<BuildingCount>();
		weaponsEconomy = new ArrayList<BuildingCount>();
		manaEconomy = new ArrayList<BuildingCount>();
		goldEconomy = new ArrayList<BuildingCount>();
		economiesOrder = new ArrayList<List<BuildingCount>>();
		initializeEconomies();

	}

	private void initializeEconomies() {
		buildMaterialEconomy.add(new BuildingCount(LUMBERJACK, 3));
		buildMaterialEconomy.add(new BuildingCount(SAWMILL, 1));
		buildMaterialEconomy.add(new BuildingCount(FORESTER, 1.5f));
		buildMaterialEconomy.add(new BuildingCount(STONECUTTER, 1.7f));
		foodEconomy.add(new BuildingCount(FARM, 3));
		foodEconomy.add(new BuildingCount(PIG_FARM, 1.4f));
		foodEconomy.add(new BuildingCount(WATERWORKS, 1.5f));
		foodEconomy.add(new BuildingCount(MILL, 0.7f));
		foodEconomy.add(new BuildingCount(BAKER, 2));
		foodEconomy.add(new BuildingCount(SLAUGHTERHOUSE, 0.7f));
		weaponsEconomy.add(new BuildingCount(COALMINE, 2));
		weaponsEconomy.add(new BuildingCount(IRONMINE, 1));
		weaponsEconomy.add(new BuildingCount(IRONMELT, 1));
		weaponsEconomy.add(new BuildingCount(WEAPONSMITH, 1));
		weaponsEconomy.add(new BuildingCount(BARRACK, 0.4f));
		manaEconomy.add(new BuildingCount(WINEGROWER, 1));
		manaEconomy.add(new BuildingCount(TEMPLE, 1));
		goldEconomy.add(new BuildingCount(FISHER, 0.5f));
		goldEconomy.add(new BuildingCount(COALMINE, 0.5f));
		goldEconomy.add(new BuildingCount(GOLDMINE, 0.5f));
		goldEconomy.add(new BuildingCount(GOLDMELT, 1));
		economiesOrder.add(buildMaterialEconomy);
		economiesOrder.add(buildMaterialEconomy);
		economiesOrder.add(buildMaterialEconomy);
		economiesOrder.add(manaEconomy);
		economiesOrder.add(foodEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(goldEconomy);
		economiesOrder.add(foodEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(foodEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(foodEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(weaponsEconomy);
		economiesOrder.add(weaponsEconomy);
	}

	@Override
	public void applyRules() {
		destroyBuildings();
		occupyTowers();
		buildBuildings();
	}

	private void occupyTowers() {
		for (ShortPoint2D towerPosition : aiStatistics.getBuildingPositionsOfTypeForPlayer(TOWER, playerId)) {
			Building tower = aiStatistics.getBuildingAt(towerPosition);
			if (tower.getStateProgress() == 1 && !tower.isOccupied()) {
				ShortPoint2D door = tower.getDoor();
				Movable soldier = aiStatistics.getNearestSwordsmanOf(door, playerId);
				sendMovableTo(soldier, door);
			}
		}
	}

	private void sendMovableTo(Movable movable, ShortPoint2D target) {
		List<Integer> selectedIds = new ArrayList<Integer>();
		if (movable != null) {
			selectedIds.add(movable.getID());
			taskScheduler.scheduleTask(new MoveToGuiTask(playerId, target, selectedIds));
		}
	}

	private void destroyBuildings() {
		// destroy stonecutters
		List<ShortPoint2D> stones = aiStatistics.getStonesForPlayer(playerId);
		for (ShortPoint2D stoneCutterPosition : aiStatistics.getBuildingPositionsOfTypeForPlayer(STONECUTTER, playerId)) {
			ShortPoint2D nearestStone = aiStatistics.detectNearestPointFromList(stoneCutterPosition, stones);
			if (nearestStone != null && aiStatistics.getDistance(stoneCutterPosition, nearestStone) > MAX_STONE_DISTANCE) {
				aiStatistics.getBuildingAt(stoneCutterPosition).kill();
			}
		}
		// TODO: destroy towers which are surrounded by other towers or are in direction of no other player
		// TODO: desroy living houses to get material back
	}

	private void buildBuildings() {
		if (aiStatistics.getNumberOfNotFinishedBuildingsForPlayer(playerId) <= 4) {
			if (buildLivingHouse())
				return;
			if (buildTower())
				return;
			buildEconomy();
		}
	}

	private void buildEconomy() {
		boolean toolsEconomyNeedsToBeChecked = true;
		Map<EBuildingType, Float> playerBuildingPlan = new HashMap<EBuildingType, Float>();
		for (List<BuildingCount> currentEconomy : economiesOrder) {
			for (BuildingCount currentBuildingCount : currentEconomy) {
				addBuildingCountToBuildingPlan(currentBuildingCount, playerBuildingPlan);
				if (buildingNeedsToBeBuild(playerBuildingPlan, currentBuildingCount)) {
					int numberOfAvailableTools = numberOfAvailableToolsForBuildingType(currentBuildingCount.buildingType);
					if (toolsEconomyNeedsToBeChecked && numberOfAvailableTools < 1) {
						if (buildToolsEconomy())
							return;
						toolsEconomyNeedsToBeChecked = false;
					}
					if (numberOfAvailableTools >= 0 && !newBuildingWouldUseReservedTool(currentBuildingCount.buildingType)
							&& construct(currentBuildingCount.buildingType)) {
						return;
					}
				}
			}
		}
	}

	private boolean buildToolsEconomy() {
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(COALMINE, playerId) < 1) {
			return construct(COALMINE);
		}
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(IRONMINE, playerId) < 1) {
			return construct(IRONMINE);
		}
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(IRONMELT, playerId) < 1) {
			return construct(IRONMELT);
		}
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(TOOLSMITH, playerId) < 1) {
			return construct(TOOLSMITH);
		}
		return false;
	}

	private boolean buildingNeedsToBeBuild(Map<EBuildingType, Float> playerBuildingPlan, BuildingCount currentBuildingCount) {
		int currentNumberOfBuildings = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(currentBuildingCount.buildingType, playerId);
		int targetNumberOfBuildings = (int) Math.floor(playerBuildingPlan.get(currentBuildingCount.buildingType));
		int targetNumberOfBuildingsWithMinimum = Math.max(1, targetNumberOfBuildings);
		return currentNumberOfBuildings < targetNumberOfBuildingsWithMinimum;
	}

	private void addBuildingCountToBuildingPlan(BuildingCount currentBuildingCount, Map<EBuildingType, Float> playerBuildingPlan) {
		if (!playerBuildingPlan.containsKey(currentBuildingCount.buildingType)) {
			playerBuildingPlan.put(currentBuildingCount.buildingType, 0f);
		}
		float foo = playerBuildingPlan.get(currentBuildingCount.buildingType) + currentBuildingCount.count;
		playerBuildingPlan.put(currentBuildingCount.buildingType, foo);
	}

	private boolean buildTower() {
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STONECUTTER, playerId) >= 1
				&& aiStatistics.getNumberOfNotOccupiedTowers(playerId) == 0) {
			return construct(TOWER);
		}
		return false;
	}

	private boolean buildLivingHouse() {
		int futureNumberOfBearers = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BEARER, playerId).size()
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(SMALL_LIVINGHOUSE, playerId) * 10
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(MEDIUM_LIVINGHOUSE, playerId) * 30
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(BIG_LIVINGHOUSE, playerId) * 100;
		if (futureNumberOfBearers < 10 || aiStatistics.getNumberOfTotalBuildingsForPlayer(playerId) * 1.5 > futureNumberOfBearers) {
			if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STONECUTTER,
					playerId) < 2 || aiStatistics.getTotalNumberOfBuildingTypeForPlayer(SAWMILL, playerId) < 1) {
				return construct(SMALL_LIVINGHOUSE);
			} else {
				return construct(MEDIUM_LIVINGHOUSE);
			}
		}
		return false;
	}

	private int numberOfAvailableToolsForBuildingType(EBuildingType buildingType) {
		EMovableType movableType = buildingType.getWorkerType();
		if (movableType == null) {
			return Integer.MAX_VALUE;
		}
		EMaterialType materialType = movableType.getTool();
		if (materialType == EMaterialType.NO_MATERIAL) {
			return Integer.MAX_VALUE;
		}
		return aiStatistics.getNumberOfMaterialTypeForPlayer(materialType, playerId)
				- aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(buildingType, playerId)
				- aiStatistics.getNumberOfUnoccupiedBuildingTypeForPlayer(buildingType, playerId);
	}

	private boolean construct(EBuildingType type) {
		ShortPoint2D position = bestConstructionPositionFinderFactory
				.getBestConstructionPositionFinderFor(type)
				.findBestConstructionPosition(aiStatistics, mainGrid.getConstructionMarksGrid(), playerId);
		if (position != null) {
			taskScheduler.scheduleTask(new ConstructBuildingTask(EGuiAction.BUILD, playerId, position, type));
			if (type == TOWER) {
				Movable soldier = aiStatistics.getNearestSwordsmanOf(position, playerId);
				sendMovableTo(soldier, position);
			}
			return true;
		}
		return false;
	}

	private boolean newBuildingWouldUseReservedTool(EBuildingType buildingType) {
		EMovableType movableType = buildingType.getWorkerType();
		if (movableType == null) {
			return false;
		}
		EMaterialType materialType = movableType.getTool();
		if (materialType == PICK) {
			int numberOfCoalMines = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(COALMINE, playerId);
			int numberOfIronMines = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(IRONMINE, playerId);
			if (numberOfCoalMines >= 1 && numberOfIronMines >= 1) {
				return false;
			}
			if (buildingType == COALMINE && numberOfCoalMines == 0) {
				return false;
			}
			if (buildingType == IRONMINE && numberOfIronMines == 0) {
				return false;
			}
			int requiredPicks = 1;
			requiredPicks += aiStatistics.getNumberOfUnoccupiedBuildingTypeForPlayer(COALMINE, playerId);
			requiredPicks += aiStatistics.getNumberOfUnoccupiedBuildingTypeForPlayer(IRONMINE, playerId);
			requiredPicks += aiStatistics.getNumberOfUnoccupiedBuildingTypeForPlayer(STONECUTTER, playerId);
			requiredPicks += aiStatistics.getNumberOfUnoccupiedBuildingTypeForPlayer(GOLDMINE, playerId);
			if (numberOfCoalMines == 0) {
				requiredPicks++;
			}
			if (numberOfIronMines == 0) {
				requiredPicks++;
			}
			return aiStatistics.getNumberOfMaterialTypeForPlayer(PICK, playerId) < requiredPicks;
		}
		if (materialType == HAMMER) {
			if (buildingType == TOOLSMITH || aiStatistics.getTotalNumberOfBuildingTypeForPlayer(TOOLSMITH, playerId) >= 1) {
				return false;
			}
			int requiredPicks = 2 + aiStatistics.getNumberOfUnoccupiedBuildingTypeForPlayer(WEAPONSMITH, playerId);
			return aiStatistics.getNumberOfMaterialTypeForPlayer(HAMMER, playerId) < requiredPicks;
		}
		return false;
	}

}
