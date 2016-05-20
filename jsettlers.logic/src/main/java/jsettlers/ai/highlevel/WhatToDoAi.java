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

import static jsettlers.ai.highlevel.AiBuildingConstants.IRONMELT_TO_WEAPON_SMITH_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.IRON_MINE_TO_IRONMELT_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.WINEGROWER_TO_TEMPLE_RATIO;
import static jsettlers.common.buildings.EBuildingType.BAKER;
import static jsettlers.common.buildings.EBuildingType.BARRACK;
import static jsettlers.common.buildings.EBuildingType.BIG_LIVINGHOUSE;
import static jsettlers.common.buildings.EBuildingType.BIG_TEMPLE;
import static jsettlers.common.buildings.EBuildingType.COALMINE;
import static jsettlers.common.buildings.EBuildingType.FARM;
import static jsettlers.common.buildings.EBuildingType.FORESTER;
import static jsettlers.common.buildings.EBuildingType.GOLDMELT;
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
import static jsettlers.common.buildings.EBuildingType.TOWER;
import static jsettlers.common.buildings.EBuildingType.WATERWORKS;
import static jsettlers.common.buildings.EBuildingType.WEAPONSMITH;
import static jsettlers.common.buildings.EBuildingType.WINEGROWER;
import static jsettlers.common.material.EMaterialType.GOLD;
import static jsettlers.logic.constants.Constants.TOWER_SEARCH_RADIUS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jsettlers.ai.army.ArmyGeneral;
import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.ai.economy.EconomyMinister;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.ConvertGuiTask;
import jsettlers.input.tasks.DestroyBuildingGuiTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.input.tasks.WorkAreaGuiTask;
import jsettlers.logic.buildings.military.OccupyingBuilding;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.network.client.interfaces.ITaskScheduler;

import static jsettlers.ai.highlevel.AiBuildingConstants.COAL_MINE_TO_IRONORE_MINE_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.WEAPON_SMITH_TO_BARRACKS_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_BAKER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_MILL_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_WATERWORKS_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_PIG_FARM_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_SLAUGHTER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.LUMBERJACK_TO_SAWMILL_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.LUMBERJACK_TO_FORESTER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.COAL_MINE_TO_SMITH_RATIO;

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
	public static final int NUMBER_OF_BEARERSS_PER_HOUSE = 2;
	public static final int MAXIMUM_STONECUTTER_WORK_RADIUS_FACTOR = 2;
	public static final float WEAPON_SMITH_FACTOR = 6F;
	private final MainGrid mainGrid;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	private final AiStatistics aiStatistics;
	private final ArmyGeneral armyGeneral;
	private final BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory;
	private final EconomyMinister economyMinister;
	private boolean isEndGame = false;
	private ArrayList<Object> failedConstructingBuildings;

	public WhatToDoAi(byte playerId, AiStatistics aiStatistics, EconomyMinister economyMinister, ArmyGeneral armyGeneral, MainGrid mainGrid,
			ITaskScheduler taskScheduler) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
		this.taskScheduler = taskScheduler;
		this.aiStatistics = aiStatistics;
		this.armyGeneral = armyGeneral;
		this.economyMinister = economyMinister;
		bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
	}

	@Override
	public void applyRules() {
		if (aiStatistics.isAlive(playerId)) {
			economyMinister.update();
			isEndGame = economyMinister.isEndGame();
			failedConstructingBuildings = new ArrayList<>();
			destroyBuildings();
			buildBuildings();
			armyGeneral.levyUnits();
			armyGeneral.commandTroops();
			sendGeologists();
			occupyMilitaryBuildings();
		}
	}

	private void sendGeologists() {
		int geologistsCount = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.GEOLOGIST, playerId).size();
		List<ShortPoint2D> bearersPositions = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BEARER, playerId);
		int bearersCount = bearersPositions.size();
		int stoneCutterCount = aiStatistics.getNumberOfBuildingTypeForPlayer(STONECUTTER, playerId);
		if (geologistsCount == 0 && stoneCutterCount >= 1 && bearersCount - 3 > MINIMUM_NUMBER_OF_BEARERS) {
			Movable coalGeologist = getBearerAt(bearersPositions.get(0));
			Movable ironGeologist = getBearerAt(bearersPositions.get(1));
			Movable goldGeologist = getBearerAt(bearersPositions.get(2));

			List<Integer> targetGeologists = new ArrayList<>();
			targetGeologists.add(coalGeologist.getID());
			targetGeologists.add(ironGeologist.getID());
			targetGeologists.add(goldGeologist.getID());
			taskScheduler.scheduleTask(new ConvertGuiTask(playerId, targetGeologists, EMovableType.GEOLOGIST));

			sendGeologistToNearest(coalGeologist, EResourceType.COAL);
			sendGeologistToNearest(ironGeologist, EResourceType.IRONORE);
			sendGeologistToNearest(goldGeologist, EResourceType.GOLDORE);

		}
	}

	private void sendGeologistToNearest(Movable geologist, EResourceType resourceType) {
		ShortPoint2D resourcePoint = aiStatistics.getNearestResourcePointForPlayer(aiStatistics.getPositionOfPartition(playerId), resourceType,
				playerId, Integer.MAX_VALUE);
		if (resourcePoint == null) {
			resourcePoint = aiStatistics.getNearestResourcePointInDefaultPartitionFor(
					aiStatistics.getPositionOfPartition(playerId), resourceType, Integer.MAX_VALUE);
		}
		if (resourcePoint != null) {
			sendMovableTo(geologist, resourcePoint);
		}
	}

	private Movable getBearerAt(ShortPoint2D point) {
		return mainGrid.getMovableGrid().getMovableAt(point.x, point.y);
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
		if (economyMinister.automaticLivingHousesEnabled()) {
			int numberOfFreeBeds = aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.SMALL_LIVINGHOUSE, playerId)
					* NUMBER_OF_SMALL_LIVINGHOUSE_BEDS
					+ aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.MEDIUM_LIVINGHOUSE, playerId) * NUMBER_OF_MEDIUM_LIVINGHOUSE_BEDS
					+ aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.BIG_LIVINGHOUSE, playerId) * NUMBER_OF_BIG_LIVINGHOUSE_BEDS
					- aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BEARER, playerId).size();
			if (numberOfFreeBeds >= NUMBER_OF_SMALL_LIVINGHOUSE_BEDS + 1 && !destroyLivingHouse(SMALL_LIVINGHOUSE)) {
				if (numberOfFreeBeds >= NUMBER_OF_MEDIUM_LIVINGHOUSE_BEDS + 1 && !destroyLivingHouse(MEDIUM_LIVINGHOUSE)) {
					if (numberOfFreeBeds >= NUMBER_OF_BIG_LIVINGHOUSE_BEDS + 1) {
						destroyLivingHouse(BIG_LIVINGHOUSE);
					}
				}
			}
		}

		// destroy not necessary buildings to get enough space for livinghouses in end-game
		if (isEndGame && isWoodJam()) {
			List<ShortPoint2D> forresters = aiStatistics.getBuildingPositionsOfTypeForPlayer(FORESTER, playerId);
			if (forresters.size() > 1) {
				for (int i = 1; i < forresters.size(); i++) {
					taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, forresters.get(i)));
				}
			}
			for (ShortPoint2D lumberJackPosition : aiStatistics.getBuildingPositionsOfTypeForPlayer(LUMBERJACK, playerId)) {
				if (aiStatistics.getBuildingAt(lumberJackPosition).cannotWork()) {
					taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, lumberJackPosition));
				}
			}
			if ((aiStatistics.getNumberOfBuildingTypeForPlayer(SAWMILL, playerId) * 3 - 2) > aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK,
					playerId)) {
				taskScheduler.scheduleTask(
						new DestroyBuildingGuiTask(playerId, aiStatistics.getBuildingPositionsOfTypeForPlayer(SAWMILL, playerId).get(0)));
			}
			for (ShortPoint2D bigTemple : aiStatistics.getBuildingPositionsOfTypeForPlayer(BIG_TEMPLE, playerId)) {
				taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, bigTemple));
			}
		}
	}

	private boolean destroyLivingHouse(EBuildingType livingHouseType) {
		for (ShortPoint2D livingHousePosition : aiStatistics.getBuildingPositionsOfTypeForPlayer(livingHouseType, playerId)) {
			if (aiStatistics.getBuildingAt(livingHousePosition).cannotWork()) {
				taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, livingHousePosition));
				return true;
			}
		}
		return false;
	}

	private boolean isWoodJam() {
		return aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.TRUNK,
				playerId) > aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) * 2;
	}

	private void destroyHinterlandMilitaryBuildings() {
		for (ShortPoint2D militaryBuildingPositions : aiStatistics.getHinterlandMilitaryBuildingPositionsOfPlayer(playerId)) {
			taskScheduler.scheduleTask(new DestroyBuildingGuiTask(playerId, militaryBuildingPositions));
		}
	}

	private void buildBuildings() {
		if (aiStatistics.getNumberOfNotFinishedBuildingsForPlayer(playerId) < economyMinister.getNumberOfParallelConstructionSites()) {
			if (economyMinister.automaticLivingHousesEnabled() && buildLivingHouse())
				return;
			if (economyMinister.automaticTowersEnabled() && aiStatistics.getNumberOfNotOccupiedMilitaryBuildings(playerId) == 0 && buildTower())
				return;
			if (isEndGame) {
				return;
			}
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
		Map<EBuildingType, Integer> playerBuildingPlan = new HashMap<EBuildingType, Integer>();
		;
		for (EBuildingType currentBuildingType : economyMinister.getBuildingsToBuild()) {
			addBuildingCountToBuildingPlan(currentBuildingType, playerBuildingPlan);
			if (buildingNeedsToBeBuild(playerBuildingPlan, currentBuildingType)
					&& buildingDependenciesAreFulfilled(currentBuildingType)) {
				int numberOfAvailableTools = numberOfAvailableToolsForBuildingType(currentBuildingType);
				if (numberOfAvailableTools >= 0 && construct(currentBuildingType)) {
					return;
				}
			}
		}
	}

private boolean buildingDependenciesAreFulfilled(EBuildingType targetBuilding) {
		switch (targetBuilding) {
		case IRONMINE:
			return ratioFits(COALMINE, COAL_MINE_TO_IRONORE_MINE_RATIO, IRONMINE);
		case WEAPONSMITH:
			return ratioFits(IRONMELT, IRONMELT_TO_WEAPON_SMITH_RATIO, WEAPONSMITH);
		case IRONMELT:
			return ratioFits(COALMINE, 1.0 / COAL_MINE_TO_SMITH_RATIO, IRONMELT)
					&& ratioFits(IRONMINE, 1.0 / IRON_MINE_TO_IRONMELT_RATIO, IRONMELT);
		case BARRACK:
			return ratioFits(WEAPONSMITH, WEAPON_SMITH_TO_BARRACKS_RATIO, BARRACK);
		case MILL:
			return ratioFits(FARM, FARM_TO_MILL_RATIO, MILL);
		case BAKER:
			return ratioFits(FARM, FARM_TO_BAKER_RATIO, BAKER);
		case WATERWORKS:
			return ratioFits(FARM, FARM_TO_WATERWORKS_RATIO, WATERWORKS);
		case SLAUGHTERHOUSE:
			return ratioFits(FARM, FARM_TO_SLAUGHTER_RATIO, SLAUGHTERHOUSE);
		case PIG_FARM:
			return ratioFits(FARM, FARM_TO_PIG_FARM_RATIO, PIG_FARM);
		case TEMPLE:
			return ratioFits(WINEGROWER, WINEGROWER_TO_TEMPLE_RATIO, TEMPLE);
		case SAWMILL:
			return ratioFits(LUMBERJACK, LUMBERJACK_TO_SAWMILL_RATIO, SAWMILL);
		case FORESTER:
			return ratioFits(LUMBERJACK, LUMBERJACK_TO_FORESTER_RATIO, FORESTER);
		default:
			return true;
		}
	}

	private boolean ratioFits(EBuildingType leftBuilding, double leftToRightBuildingRatio, EBuildingType rightBuilding) {
		return aiStatistics.getTotalNumberOfBuildingTypeForPlayer(leftBuilding,
				playerId) >= (double) aiStatistics.getTotalNumberOfBuildingTypeForPlayer(rightBuilding, playerId) / leftToRightBuildingRatio;
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
		int futureNumberOfBearers = aiStatistics.getMovablePositionsByTypeForPlayer(EMovableType.BEARER, playerId).size()
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(BIG_LIVINGHOUSE, playerId) * NUMBER_OF_BIG_LIVINGHOUSE_BEDS
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(SMALL_LIVINGHOUSE, playerId) * NUMBER_OF_SMALL_LIVINGHOUSE_BEDS
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(MEDIUM_LIVINGHOUSE, playerId) * NUMBER_OF_MEDIUM_LIVINGHOUSE_BEDS;
		if (futureNumberOfBearers < MINIMUM_NUMBER_OF_BEARERS
				|| (aiStatistics.getNumberOfTotalBuildingsForPlayer(playerId) + aiStatistics.getNumberOfBuildingTypeForPlayer(WEAPONSMITH,
						playerId) * WEAPON_SMITH_FACTOR) * NUMBER_OF_BEARERSS_PER_HOUSE > futureNumberOfBearers) {
			if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STONECUTTER, playerId) < 1
					|| aiStatistics.getTotalNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) < 1) {
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
				- aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(buildingType, playerId);
	}

	private boolean construct(EBuildingType type) {
		if (failedConstructingBuildings.size() > 1 && failedConstructingBuildings.contains(type)) {
			return false;
		}
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
		failedConstructingBuildings.add(type);
		return false;
	}

	@Override
	public String toString() {
		return "Player " + playerId + " with " + economyMinister.toString() + " and " + armyGeneral.toString();
	}

}
