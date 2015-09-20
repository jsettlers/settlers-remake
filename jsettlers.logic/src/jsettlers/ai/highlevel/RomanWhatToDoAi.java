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

import static jsettlers.common.buildings.EBuildingType.BAKER;
import static jsettlers.common.buildings.EBuildingType.BARRACK;
import static jsettlers.common.buildings.EBuildingType.BIG_LIVINGHOUSE;
import static jsettlers.common.buildings.EBuildingType.BIG_TEMPLE;
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
import static jsettlers.common.buildings.EBuildingType.STOCK;
import static jsettlers.common.buildings.EBuildingType.STONECUTTER;
import static jsettlers.common.buildings.EBuildingType.TEMPLE;
import static jsettlers.common.buildings.EBuildingType.TOOLSMITH;
import static jsettlers.common.buildings.EBuildingType.TOWER;
import static jsettlers.common.buildings.EBuildingType.WATERWORKS;
import static jsettlers.common.buildings.EBuildingType.WEAPONSMITH;
import static jsettlers.common.buildings.EBuildingType.WINEGROWER;
import static jsettlers.common.material.EMaterialType.GOLD;
import static jsettlers.common.material.EMaterialType.HAMMER;
import static jsettlers.common.material.EMaterialType.PICK;
import static jsettlers.logic.constants.Constants.TOWER_SEARCH_RADIUS;

import java.util.*;

import jsettlers.ai.army.ArmyGeneral;
import jsettlers.ai.army.LooserGeneral;
import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.ai.construction.BuildingCount;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.DestroyBuildingGuiTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.logic.buildings.military.OccupyingBuilding;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.network.client.interfaces.ITaskScheduler;

/**
 * This WhatToDoAi is a high level KI for romans. It builds a longterm economy with 8 lumberjacks first, then mana, food, weapons and gold economy. It
 * spreads out the land in direction of the prioritized needed resources and builds a toolsmith as late as possible.
 *
 * TODOs: Currently it is vulnerable by rushes so adding an early weaponsmith when enemies build fast weaponsmiths could be implemented.
 */
public class RomanWhatToDoAi implements IWhatToDoAi {

	private final MainGrid mainGrid;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	private final AiStatistics aiStatistics;
	private final List<EBuildingType> buildingsToBuild;
	private final Map<EBuildingType, List<BuildingCount>> buildingNeeds;
	private final Map<EBuildingType, List<EBuildingType>> buildingIsNeededBy;
	private final ArmyGeneral armyGeneral;
	BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory;

	public RomanWhatToDoAi(byte playerId, AiStatistics aiStatistics, ArmyGeneral armyGeneral, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
		this.taskScheduler = taskScheduler;
		this.aiStatistics = aiStatistics;
		this.armyGeneral = armyGeneral;
		buildingNeeds = new HashMap<EBuildingType, List<BuildingCount>>();
		buildingIsNeededBy = new HashMap<EBuildingType, List<EBuildingType>>();
		bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
		buildingsToBuild = new ArrayList<EBuildingType>();
		initializeBuildingLists();

	}

	private void initializeBuildingLists() {
		for (EBuildingType buildingType : EBuildingType.values()) {
			buildingNeeds.put(buildingType, new ArrayList<BuildingCount>());
			buildingIsNeededBy.put(buildingType, new ArrayList<EBuildingType>());
		}
		buildingNeeds.get(SAWMILL).add(new BuildingCount(LUMBERJACK, 3));
		buildingNeeds.get(TEMPLE).add(new BuildingCount(WINEGROWER, 1));
		buildingNeeds.get(MILL).add(new BuildingCount(FARM, 1));
		buildingNeeds.get(BAKER).add(new BuildingCount(MILL, (float) 1 / 3));
		buildingNeeds.get(PIG_FARM).add(new BuildingCount(FARM, 1));
		buildingNeeds.get(SLAUGHTERHOUSE).add(new BuildingCount(PIG_FARM, (float) 1 / 3));
		buildingNeeds.get(IRONMELT).add(new BuildingCount(IRONMINE, 0.5f));
		buildingNeeds.get(WEAPONSMITH).add(new BuildingCount(IRONMELT, 1));
		buildingNeeds.get(GOLDMELT).add(new BuildingCount(GOLDMINE, 0.5f));
		buildingNeeds.get(BARRACK).add(new BuildingCount(WEAPONSMITH, 4));
		// Ironmine depends of coalmine to prevent iron and coal are build 1:1 when picks are missing
		// otherwise always as loop: one additional ironmine and coalmine would be build for inform toolsmith to produce picks
		buildingNeeds.get(IRONMINE).add(new BuildingCount(COALMINE, 2));
		for (Map.Entry<EBuildingType, List<BuildingCount>> buildingNeedsEntry : buildingNeeds.entrySet()) {
			for (BuildingCount neededBuildingCount : buildingNeedsEntry.getValue()) {
				buildingIsNeededBy.get(neededBuildingCount.buildingType).add(buildingNeedsEntry.getKey());
			}
		}
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(SAWMILL);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(FORESTER);
		buildingsToBuild.add(STONECUTTER);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(FORESTER);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(SAWMILL);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(FORESTER);
		buildingsToBuild.add(STONECUTTER);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(SAWMILL);
		buildingsToBuild.add(FORESTER);
		buildingsToBuild.add(STONECUTTER);
		buildingsToBuild.add(STONECUTTER);
		buildingsToBuild.add(STONECUTTER);
		buildingsToBuild.add(WINEGROWER);
		buildingsToBuild.add(WINEGROWER);
		buildingsToBuild.add(WINEGROWER);
		buildingsToBuild.add(WINEGROWER);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(TEMPLE);
		buildingsToBuild.add(TEMPLE);
		buildingsToBuild.add(TEMPLE);
		buildingsToBuild.add(TEMPLE);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BARRACK);
		buildingsToBuild.add(MILL);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(WATERWORKS);
		buildingsToBuild.add(PIG_FARM);
		buildingsToBuild.add(SLAUGHTERHOUSE);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BIG_TEMPLE);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(WATERWORKS);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BARRACK);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(FISHER);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(GOLDMINE);
		buildingsToBuild.add(GOLDMELT);
		buildingsToBuild.add(PIG_FARM);
		buildingsToBuild.add(MILL);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(WATERWORKS);
		buildingsToBuild.add(PIG_FARM);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BARRACK);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
	}

	@Override
	public void applyRules() {
		destroyBuildings();
		buildBuildings();
		armyGeneral.commandTroops();
		occupyTowers();
	}

	private void occupyTowers() {
		for (ShortPoint2D towerPosition : aiStatistics.getBuildingPositionsOfTypeForPlayer(TOWER, playerId)) {
			OccupyingBuilding tower = (OccupyingBuilding) aiStatistics.getBuildingAt(towerPosition);
			if (tower.getStateProgress() == 1 && !tower.isOccupied()) {
				ShortPoint2D door = tower.getDoor();
				Movable soldier = aiStatistics.getNearestSwordsmanOf(door, playerId);
				if (soldier != null && tower.getPos().getOnGridDistTo(soldier.getPos()) > TOWER_SEARCH_RADIUS) {
					sendMovableTo(soldier, door);
				}
			}
		}
	}

	private void sendMovableTo(Movable movable, ShortPoint2D target) {
		if (movable != null) {
			taskScheduler.scheduleTask(new MoveToGuiTask(playerId, target, Collections.singletonList(movable.getID())));
		}
	}

	private void destroyBuildings() {
		// destroy stonecutters
		List<ShortPoint2D> stones = aiStatistics.getStonesForPlayer(playerId);
		for (ShortPoint2D stoneCutterPosition : aiStatistics.getBuildingPositionsOfTypeForPlayer(STONECUTTER, playerId)) {
			ShortPoint2D nearestStone = aiStatistics.detectNearestPointFromList(stoneCutterPosition, stones);
			if (nearestStone != null && stoneCutterPosition.getOnGridDistTo(nearestStone) > STONECUTTER.getWorkradius() - 2) {
				taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, stoneCutterPosition));
			}
		}
		// TODO: destroy living houses to get material back
		// TODO: destroy mines which have no resources anymore
	}

	private void destroyHinterlandTowers() {
		for (ShortPoint2D towerPositions : aiStatistics.getHinterlandTowerPositionsOfPlayer(playerId)) {
			taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, towerPositions));
		}
	}

	private void buildBuildings() {
		if (aiStatistics.getNumberOfNotFinishedBuildingsForPlayer(playerId) <= 4) {
			if (buildLivingHouse())
				return;
			if (buildTower())
				return;
			if (buildStock())
				return;
			buildEconomy();
		}
	}

	private boolean buildStock() {
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(GOLDMELT, playerId) < 1) {
			return false;
		}
		int stockCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STOCK, playerId);
		int goldCount = aiStatistics.getNumberOfMaterialTypeForPlayer(GOLD, playerId);
		if (stockCount * 6 * 8 - 32 < goldCount) {
			return construct(STOCK);
		}
		return false;
	}

	private void buildEconomy() {
		boolean toolsEconomyNeedsToBeChecked = true;
		Map<EBuildingType, Integer> playerBuildingPlan = new HashMap<EBuildingType, Integer>();
		for (EBuildingType currentBuildingType : buildingsToBuild) {

			addBuildingCountToBuildingPlan(currentBuildingType, playerBuildingPlan);
			if (buildingNeedsToBeBuild(playerBuildingPlan, currentBuildingType)
					&& buildingDependenciesAreFulfilled(currentBuildingType)) {
				int numberOfAvailableTools = numberOfAvailableToolsForBuildingType(currentBuildingType);
				if (toolsEconomyNeedsToBeChecked && numberOfAvailableTools < 1) {
					if (buildToolsEconomy()) {
						return;
					}
					toolsEconomyNeedsToBeChecked = false;
				}
				if (numberOfAvailableTools >= 0 && !newBuildingWouldUseReservedTool(currentBuildingType)
						&& construct(currentBuildingType)) {
					return;
				}
			}
		}
	}

	private boolean buildingDependenciesAreFulfilled(EBuildingType targetBuilding) {
		boolean buildingDependenciesAreFulfilled = true;
		for (BuildingCount neededBuilding : buildingNeeds.get(targetBuilding)) {
			if (!unusedBuildingExists(neededBuilding.buildingType)) {
				buildingDependenciesAreFulfilled = false;
			}
		}
		return buildingDependenciesAreFulfilled;
	}

	private boolean unusedBuildingExists(EBuildingType me) {
		float howOftenAmIUsed = 0;
		for (EBuildingType buildingWhichNeedsMe : buildingIsNeededBy.get(me)) {
			for (BuildingCount buildingCount : buildingNeeds.get(buildingWhichNeedsMe)) {
				if (buildingCount.buildingType == me) {
					howOftenAmIUsed = howOftenAmIUsed + buildingCount.count
							* aiStatistics.getTotalNumberOfBuildingTypeForPlayer(buildingWhichNeedsMe, playerId);
				}
			}
		}
		return aiStatistics.getTotalNumberOfBuildingTypeForPlayer(me, playerId) >= howOftenAmIUsed;
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

	private boolean buildingNeedsToBeBuild(Map<EBuildingType, Integer> playerBuildingPlan, EBuildingType currentBuildingType) {
		int currentNumberOfBuildings = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(currentBuildingType, playerId);
		int targetNumberOfBuildings = playerBuildingPlan.get(currentBuildingType);
		return currentNumberOfBuildings < targetNumberOfBuildings;
	}

	private void addBuildingCountToBuildingPlan(EBuildingType buildingType, Map<EBuildingType, Integer> playerBuildingPlan) {
		if (!playerBuildingPlan.containsKey(buildingType)) {
			playerBuildingPlan.put(buildingType, 0);
		}
		playerBuildingPlan.put(buildingType, playerBuildingPlan.get(buildingType) + 1);
	}

	private boolean buildTower() {
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STONECUTTER, playerId) >= 1
				&& aiStatistics.getNumberOfNotOccupiedTowers(playerId) == 0) {
			destroyHinterlandTowers();
			return construct(TOWER);
		}
		return false;
	}

	private boolean buildLivingHouse() {
		if (aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(SMALL_LIVINGHOUSE, playerId) > 0
				|| aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(MEDIUM_LIVINGHOUSE, playerId) > 0) {
			return false;
		}

		int futureNumberOfBearers = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BEARER, playerId).size()
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(BIG_LIVINGHOUSE, playerId) * 100;
		if (futureNumberOfBearers < 10 || aiStatistics.getNumberOfTotalBuildingsForPlayer(playerId) * 3 > futureNumberOfBearers) {
			if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STONECUTTER, playerId) < 1
					|| aiStatistics.getTotalNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) < 3) {
				return construct(SMALL_LIVINGHOUSE);
			} else if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(WEAPONSMITH, playerId) >= 2) {
				return construct(BIG_LIVINGHOUSE);
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
				if (soldier != null) {
					sendMovableTo(soldier, position);
				}
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
