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
import static jsettlers.common.buildings.EBuildingType.COALMINE;
import static jsettlers.common.buildings.EBuildingType.FARM;
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
import static jsettlers.common.buildings.EBuildingType.WEAPONSMITH;
import static jsettlers.common.buildings.EBuildingType.WINEGROWER;
import static jsettlers.common.material.EMaterialType.GOLD;
import static jsettlers.common.material.EMaterialType.HAMMER;
import static jsettlers.common.material.EMaterialType.PICK;
import static jsettlers.logic.constants.Constants.TOWER_SEARCH_RADIUS;

import java.util.*;

import jsettlers.ai.army.ArmyGeneral;
import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.ai.construction.BuildingCount;
import jsettlers.ai.economy.EconomyMinister;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.DestroyBuildingGuiTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.input.tasks.WorkAreaGuiTask;
import jsettlers.logic.buildings.military.OccupyingBuilding;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.network.client.interfaces.ITaskScheduler;

/**
 * This WhatToDoAi is a high level KI. It delegates the decision which building is build next to its economy minister. However this WhatToDoAi takes
 * care against lack of settlers and it builds a toolsmith when needed but as late as possible. Furthermore it builds towers continously to spread the
 * land. It destroys not needed living houses and stonecutters to get back building materials. Which soldiers to levy and to command the soldiers is
 * delegated to its armay general.
 *
 * @author codingberling
 */
public class WhatToDoAi implements IWhatToDoAi {

	public static final int NUMBER_OF_SMALL_LIVINGHOUSE_BEDS = 10;
	public static final int NUMBER_OF_MEDIUM_LIVINGHOUSE_BEDS = 30;
	public static final int NUMBER_OF_BIG_LIVINGHOUSE_BEDS = 100;
	public static final int MINIMUM_NUMBER_OF_BEARERS = 10;
	public static final int NUMBER_OF_BEARERSS_PER_HOUSE = 5;
	public static final int MAXIMUM_STONECUTTER_WORK_RADIUS_FACTOR = 2;
	private final MainGrid mainGrid;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	private final AiStatistics aiStatistics;
	private final Map<EBuildingType, List<BuildingCount>> buildingNeeds;
	private final Map<EBuildingType, List<EBuildingType>> buildingIsNeededBy;
	private final ArmyGeneral armyGeneral;
	private final BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory;
	private final EconomyMinister economyMinister;

	public WhatToDoAi(byte playerId, AiStatistics aiStatistics, EconomyMinister economyMinister, ArmyGeneral armyGeneral, MainGrid mainGrid,
			ITaskScheduler taskScheduler) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
		this.taskScheduler = taskScheduler;
		this.aiStatistics = aiStatistics;
		this.armyGeneral = armyGeneral;
		this.economyMinister = economyMinister;
		buildingNeeds = new HashMap<EBuildingType, List<BuildingCount>>();
		buildingIsNeededBy = new HashMap<EBuildingType, List<EBuildingType>>();
		bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
		initializeBuildingLists();
		System.out.println(this);
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
	}

	@Override
	public void applyRules() {
		if (aiStatistics.isAlive(playerId)) {
			destroyBuildings();
			buildBuildings();
			armyGeneral.levyUnits();
			armyGeneral.commandTroops();
			occupyMilitaryBuildings();
		}
	}

	private void occupyMilitaryBuildings() {
		for (ShortPoint2D militaryBuildingPosition : aiStatistics.getBuildingPositionsOfTypesForPlayer(
				EBuildingType.getMilitaryBuildings(), playerId)) {

			OccupyingBuilding militaryBuilding = (OccupyingBuilding) aiStatistics.getBuildingAt(militaryBuildingPosition);
			if (militaryBuilding.getStateProgress() == 1 && !militaryBuilding.isOccupied()) {
				ShortPoint2D door = militaryBuilding.getDoor();
				IMovable soldier = aiStatistics.getNearestSwordsmanOf(door, playerId);
				if (soldier != null && militaryBuilding.getPos().getOnGridDistTo(soldier.getPos()) > TOWER_SEARCH_RADIUS) {
					sendMovableTo(soldier, door);
				}
			}
		}
	}

	private void sendMovableTo(IMovable movable, ShortPoint2D target) {
		if (movable != null) {
			taskScheduler.scheduleTask(new MoveToGuiTask(playerId, target, Collections.singletonList(movable.getID())));
		}
	}

	private void destroyBuildings() {
		// destroy stonecutters or set their work areas
		for (ShortPoint2D stoneCutterPosition : aiStatistics.getBuildingPositionsOfTypeForPlayer(STONECUTTER, playerId)) {
			if (aiStatistics.getBuildingAt(stoneCutterPosition).cannotWork()) {
				int numberOfStoneCutters = aiStatistics.getNumberOfBuildingTypeForPlayer(STONECUTTER, playerId);
				if (numberOfStoneCutters == 1) {
					ShortPoint2D nearestStone = aiStatistics.getStonesForPlayer(playerId).getNearestPoint(stoneCutterPosition);
					if (nearestStone != null) {
						taskScheduler.scheduleTask(new WorkAreaGuiTask(EGuiAction.SET_WORK_AREA, playerId, nearestStone, stoneCutterPosition));
					} // else wait and check again (next interval maybe there is a new or occupied tower)
				} else {
					ShortPoint2D nearestStone = aiStatistics.getStonesForPlayer(playerId)
							.getNearestPoint(stoneCutterPosition, STONECUTTER.getWorkradius() * MAXIMUM_STONECUTTER_WORK_RADIUS_FACTOR, null);
					if (nearestStone != null && numberOfStoneCutters < economyMinister.getMidGameNumberOfStoneCutters()) {
						taskScheduler.scheduleTask(new WorkAreaGuiTask(EGuiAction.SET_WORK_AREA, playerId, nearestStone, stoneCutterPosition));
					} else {
						taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, stoneCutterPosition));
						break; // destroy only one stone cutter
					}
				}
			}
		}

		// destroy livinghouses
		int numberOfFreeBeds = aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.SMALL_LIVINGHOUSE, playerId)
				* NUMBER_OF_SMALL_LIVINGHOUSE_BEDS
				+ aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.MEDIUM_LIVINGHOUSE, playerId) * NUMBER_OF_MEDIUM_LIVINGHOUSE_BEDS
				+ aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.BIG_LIVINGHOUSE, playerId) * NUMBER_OF_BIG_LIVINGHOUSE_BEDS
				- aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BEARER, playerId).size();
		if (numberOfFreeBeds >= NUMBER_OF_SMALL_LIVINGHOUSE_BEDS + 1
				&& aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.SMALL_LIVINGHOUSE, playerId) > 0) {
			taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId,
					aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.SMALL_LIVINGHOUSE, playerId).get(0)));
		} else if (numberOfFreeBeds >= NUMBER_OF_MEDIUM_LIVINGHOUSE_BEDS + 1
				&& aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.MEDIUM_LIVINGHOUSE, playerId) > 0) {
			taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId,
					aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.MEDIUM_LIVINGHOUSE, playerId).get(0)));
		} else if (numberOfFreeBeds >= NUMBER_OF_BIG_LIVINGHOUSE_BEDS + 1
				&& aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.BIG_LIVINGHOUSE, playerId) > 0) {
			taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId,
					aiStatistics.getBuildingPositionsOfTypeForPlayer(EBuildingType.BIG_LIVINGHOUSE, playerId).get(0)));
		}

		// destroy mines
		for (ShortPoint2D mine : aiStatistics.getDeadMinesOf(playerId)) {
			taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, mine));
		}
	}

	private void destroyHinterlandMilitaryBuildings() {
		for (ShortPoint2D militaryBuildingPositions : aiStatistics.getHinterlandMilitaryBuildingPositionsOfPlayer(playerId)) {
			taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, militaryBuildingPositions));
		}
	}

	private void buildBuildings() {
		if (aiStatistics.getNumberOfNotFinishedBuildingsForPlayer(playerId) < economyMinister.getNumberOfParallelConstructionSides()) {
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
		for (EBuildingType currentBuildingType : economyMinister.getBuildingsToBuild()) {

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
				&& aiStatistics.getNumberOfNotOccupiedMilitaryBuildings(playerId) == 0) {
			destroyHinterlandMilitaryBuildings();
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
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(BIG_LIVINGHOUSE, playerId) * NUMBER_OF_BIG_LIVINGHOUSE_BEDS;
		if (futureNumberOfBearers < MINIMUM_NUMBER_OF_BEARERS
				|| aiStatistics.getNumberOfTotalBuildingsForPlayer(playerId) * NUMBER_OF_BEARERSS_PER_HOUSE > futureNumberOfBearers) {
			if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STONECUTTER, playerId) < 1
					|| aiStatistics.getTotalNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) < 3) {
				return construct(SMALL_LIVINGHOUSE);
			} else if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(WEAPONSMITH, playerId) < 2) {
				return construct(MEDIUM_LIVINGHOUSE);
			} else {
				return construct(BIG_LIVINGHOUSE);
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
			if (type.isMilitaryBuilding()) {
				IMovable soldier = aiStatistics.getNearestSwordsmanOf(position, playerId);
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

	@Override
	public String toString() {
		return "Player " + playerId + " with " + economyMinister.toString() + " and " + armyGeneral.toString();
	}

}
