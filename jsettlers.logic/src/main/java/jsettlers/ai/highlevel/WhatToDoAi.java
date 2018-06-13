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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java8.util.stream.Collectors;
import jsettlers.ai.army.ArmyGeneral;
import jsettlers.ai.construction.BestConstructionPositionFinderFactory;
import jsettlers.ai.economy.EconomyMinister;
import jsettlers.ai.highlevel.pioneers.PioneerAi;
import jsettlers.ai.highlevel.pioneers.PioneerGroup;
import jsettlers.ai.highlevel.pioneers.target.SameBlockedPartitionLikePlayerFilter;
import jsettlers.ai.highlevel.pioneers.target.SurroundedByResourcesFilter;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.tasks.ConstructBuildingTask;
import jsettlers.input.tasks.ConvertGuiTask;
import jsettlers.input.tasks.SimpleBuildingGuiTask;
import jsettlers.input.tasks.EGuiAction;
import jsettlers.input.tasks.MoveToGuiTask;
import jsettlers.input.tasks.WorkAreaGuiTask;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.military.occupying.OccupyingBuilding;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.movable.MovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.network.client.interfaces.ITaskScheduler;

import static java8.util.stream.StreamSupport.stream;
import static jsettlers.ai.highlevel.AiBuildingConstants.COAL_MINE_TO_IRON_MINE_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.COAL_MINE_TO_SMITH_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_BAKER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_MILL_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_PIG_FARM_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_SLAUGHTER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_WATERWORKS_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.IRONMELT_TO_WEAPON_SMITH_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.IRON_MINE_TO_IRONMELT_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.LUMBERJACK_TO_FORESTER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.LUMBERJACK_TO_SAWMILL_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.WEAPON_SMITH_TO_BARRACKS_RATIO;
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
import static jsettlers.logic.constants.Constants.TOWER_ATTACKABLE_SEARCH_RADIUS;
import static jsettlers.logic.constants.Constants.TOWER_SEARCH_SOLDIERS_RADIUS;

/**
 * This WhatToDoAi is a high level KI. It delegates the decision which building is build next to its economy minister. However this WhatToDoAi takes care against lack of settlers and it builds a
 * toolsmith when needed but as late as possible. Furthermore it builds towers continously to spread the land. It destroys not needed living houses and stonecutters to get back building materials.
 * Which SOLDIERS to levy and to command the SOLDIERS is delegated to its armay general.
 *
 * @author codingberling
 */
class WhatToDoAi implements IWhatToDoAi {

	private static final int NUMBER_OF_SMALL_LIVING_HOUSE_BEDS = 10;
	private static final int NUMBER_OF_MEDIUM_LIVING_HOUSE_BEDS = 30;
	private static final int NUMBER_OF_BIG_LIVING_HOUSE_BEDS = 100;

	private static final int MINIMUM_NUMBER_OF_BEARERS = 10;
	private static final int MINIMUM_NUMBER_OF_JOBLESS_BEARERS = 10;
	private static final float MINIMUM_NUMBER_OF_JOBLESS_BEARERS_PER_BUILDING = 1.2f;

	private static final int NUMBER_OF_BEARERS_PER_HOUSE = 3;
	private static final int MAXIMUM_STONECUTTER_WORK_RADIUS_FACTOR = 2;
	private static final float WEAPON_SMITH_FACTOR = 7F;
	private static final int RESOURCE_PIONEER_GROUP_COUNT = 20;
	private static final int BROADEN_PIONEER_GROUP_COUNT = 40;

	private final MainGrid mainGrid;
	private final MovableGrid movableGrid;
	private final byte playerId;
	private final ITaskScheduler taskScheduler;
	private final AiStatistics aiStatistics;
	private final ArmyGeneral armyGeneral;
	private final BestConstructionPositionFinderFactory bestConstructionPositionFinderFactory;
	private final EconomyMinister economyMinister;
	private final PioneerAi pioneerAi;
	private boolean isEndGame = false;
	private ArrayList<Object> failedConstructingBuildings;
	private PioneerGroup resourcePioneers;
	private PioneerGroup broadenerPioneers;
	private AiPositions.AiPositionFilter[] geologistFilters = new AiPositions.AiPositionFilter[EResourceType.values().length];

	WhatToDoAi(byte playerId, AiStatistics aiStatistics, EconomyMinister economyMinister, ArmyGeneral armyGeneral, MainGrid mainGrid, ITaskScheduler taskScheduler) {
		this.playerId = playerId;
		this.mainGrid = mainGrid;
		this.movableGrid = mainGrid.getMovableGrid();
		this.taskScheduler = taskScheduler;
		this.aiStatistics = aiStatistics;
		this.armyGeneral = armyGeneral;
		this.economyMinister = economyMinister;
		this.pioneerAi = new PioneerAi(aiStatistics, playerId);
		bestConstructionPositionFinderFactory = new BestConstructionPositionFinderFactory();
		resourcePioneers = new PioneerGroup(RESOURCE_PIONEER_GROUP_COUNT);
		broadenerPioneers = new PioneerGroup(BROADEN_PIONEER_GROUP_COUNT);
		List<Integer> allPioneers = stream(aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.PIONEER))
				.map(pos -> movableGrid.getMovableAt(pos.x, pos.y))
				.map(ILogicMovable::getID)
				.collect(Collectors.toList());

		int resourcePioneersNumber = Math.min(allPioneers.size(), RESOURCE_PIONEER_GROUP_COUNT);
		resourcePioneers.addAll(allPioneers.subList(0, resourcePioneersNumber));
		broadenerPioneers.addAll(allPioneers.subList(resourcePioneersNumber, allPioneers.size()));

		for (EResourceType resourceType : EResourceType.VALUES) {
			geologistFilters[resourceType.ordinal] = new AiPositions.CombinedAiPositionFilter(
					new SurroundedByResourcesFilter(mainGrid, mainGrid.getLandscapeGrid(), resourceType),
					new SameBlockedPartitionLikePlayerFilter(aiStatistics, playerId));
		}
	}

	@Override
	public void applyRules() {
		if (aiStatistics.isAlive(playerId)) {
			economyMinister.update();
			isEndGame = economyMinister.isEndGame();
			failedConstructingBuildings = new ArrayList<>();
			destroyBuildings();
			commandPioneers();
			buildBuildings();
			Set<Integer> soldiersWithOrders = occupyMilitaryBuildings();
			armyGeneral.levyUnits();
			armyGeneral.commandTroops(soldiersWithOrders);
			sendGeologists();
		}
	}

	private void sendGeologists() {
		int geologistsCount = aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.GEOLOGIST).size();
		List<ShortPoint2D> bearersPositions = aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.BEARER);
		int bearersCount = bearersPositions.size();
		int stoneCutterCount = aiStatistics.getNumberOfBuildingTypeForPlayer(STONECUTTER, playerId);
		if (geologistsCount == 0 && stoneCutterCount >= 1 && bearersCount - 3 > MINIMUM_NUMBER_OF_BEARERS) {
			ILogicMovable coalGeologist = getBearerAt(bearersPositions.get(0));
			ILogicMovable ironGeologist = getBearerAt(bearersPositions.get(1));
			ILogicMovable goldGeologist = getBearerAt(bearersPositions.get(2));

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

	private void sendGeologistToNearest(ILogicMovable geologist, EResourceType resourceType) {
		ShortPoint2D resourcePoint = aiStatistics.getNearestResourcePointForPlayer(aiStatistics.getPositionOfPartition(playerId), resourceType, playerId, Integer.MAX_VALUE,
				geologistFilters[resourceType.ordinal]);
		if (resourcePoint == null) {
			resourcePoint = aiStatistics.getNearestResourcePointInDefaultPartitionFor(
					aiStatistics.getPositionOfPartition(playerId), resourceType, Integer.MAX_VALUE, geologistFilters[resourceType.ordinal]);
		}
		if (resourcePoint != null) {
			sendMovableTo(geologist, resourcePoint);
		}
	}

	private ILogicMovable getBearerAt(ShortPoint2D point) {
		return mainGrid.getMovableGrid().getMovableAt(point.x, point.y);
	}

	private Set<Integer> occupyMilitaryBuildings() {
		Set<Integer> soldiersWithOrders = new HashSet<>();

		for (ShortPoint2D militaryBuildingPosition : aiStatistics.getBuildingPositionsOfTypesForPlayer(EBuildingType.MILITARY_BUILDINGS, playerId)) {
			OccupyingBuilding militaryBuilding = (OccupyingBuilding) aiStatistics.getBuildingAt(militaryBuildingPosition);
			if (!militaryBuilding.isOccupied()) {
				ShortPoint2D door = militaryBuilding.getDoor();
				IMovable soldier = aiStatistics.getNearestSwordsmanOf(door, playerId);
				if (soldier != null && militaryBuilding.getPosition().getOnGridDistTo(soldier.getPosition()) > TOWER_SEARCH_SOLDIERS_RADIUS) {
					soldiersWithOrders.add(soldier.getID());
					sendMovableTo(soldier, door);
				}
			}
		}

		return soldiersWithOrders;
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

				ShortPoint2D nearestStone = aiStatistics.getStonesForPlayer(playerId)
						.getNearestPoint(stoneCutterPosition, STONECUTTER.getWorkRadius() * MAXIMUM_STONECUTTER_WORK_RADIUS_FACTOR, null);
				if (nearestStone != null && numberOfStoneCutters < economyMinister.getMidGameNumberOfStoneCutters()) {
					taskScheduler.scheduleTask(new WorkAreaGuiTask(EGuiAction.SET_WORK_AREA, playerId, nearestStone, stoneCutterPosition));
				} else {
					taskScheduler.scheduleTask(new SimpleBuildingGuiTask(EGuiAction.DESTROY_BUILDING, playerId, stoneCutterPosition));
					break; // destroy only one stone cutter
				}
			}
		}

		// destroy livinghouses
		if (economyMinister.automaticLivingHousesEnabled()) {
			int numberOfFreeBeds = aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.SMALL_LIVINGHOUSE, playerId)
					* NUMBER_OF_SMALL_LIVING_HOUSE_BEDS
					+ aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.MEDIUM_LIVINGHOUSE, playerId) * NUMBER_OF_MEDIUM_LIVING_HOUSE_BEDS
					+ aiStatistics.getNumberOfBuildingTypeForPlayer(EBuildingType.BIG_LIVINGHOUSE, playerId) * NUMBER_OF_BIG_LIVING_HOUSE_BEDS
					- aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.BEARER).size();
			if (numberOfFreeBeds >= NUMBER_OF_SMALL_LIVING_HOUSE_BEDS + 1 && !destroyLivingHouse(SMALL_LIVINGHOUSE)) {
				if (numberOfFreeBeds >= NUMBER_OF_MEDIUM_LIVING_HOUSE_BEDS + 1 && !destroyLivingHouse(MEDIUM_LIVINGHOUSE)) {
					if (numberOfFreeBeds >= NUMBER_OF_BIG_LIVING_HOUSE_BEDS + 1) {
						destroyLivingHouse(BIG_LIVINGHOUSE);
					}
				}
			}
		}

		// destroy not necessary buildings to get enough space for livinghouses in end-game
		if (isEndGame && isWoodJam()) {
			List<ShortPoint2D> foresters = aiStatistics.getBuildingPositionsOfTypeForPlayer(FORESTER, playerId);
			if (foresters.size() > 1) {
				for (int i = 1; i < foresters.size(); i++) {
					taskScheduler.scheduleTask(new SimpleBuildingGuiTask(EGuiAction.DESTROY_BUILDING, playerId, foresters.get(i)));
				}
			}

			stream(aiStatistics.getBuildingPositionsOfTypeForPlayer(LUMBERJACK, playerId))
					.filter(lumberJackPosition -> aiStatistics.getBuildingAt(lumberJackPosition).cannotWork())
					.forEach(lumberJackPosition -> taskScheduler.scheduleTask(new SimpleBuildingGuiTask(EGuiAction.DESTROY_BUILDING, playerId, lumberJackPosition)));

			if ((aiStatistics.getNumberOfBuildingTypeForPlayer(SAWMILL, playerId) * 3 - 2) > aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId)) {
				taskScheduler.scheduleTask(new SimpleBuildingGuiTask(EGuiAction.DESTROY_BUILDING, playerId, aiStatistics.getBuildingPositionsOfTypeForPlayer(SAWMILL, playerId).get(0)));
			}

			for (ShortPoint2D bigTemple : aiStatistics.getBuildingPositionsOfTypeForPlayer(BIG_TEMPLE, playerId)) {
				taskScheduler.scheduleTask(new SimpleBuildingGuiTask(EGuiAction.DESTROY_BUILDING, playerId, bigTemple));
			}
		}
	}

	private boolean destroyLivingHouse(EBuildingType livingHouseType) {
		for (ShortPoint2D livingHousePosition : aiStatistics.getBuildingPositionsOfTypeForPlayer(livingHouseType, playerId)) {
			if (aiStatistics.getBuildingAt(livingHousePosition).cannotWork()) {
				taskScheduler.scheduleTask(new SimpleBuildingGuiTask(EGuiAction.DESTROY_BUILDING, playerId, livingHousePosition));
				return true;
			}
		}
		return false;
	}

	private boolean isWoodJam() {
		return aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.TRUNK, playerId) > aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) * 2;
	}

	private void buildBuildings() {
		if (aiStatistics.getNumberOfNotFinishedBuildingsForPlayer(playerId) < economyMinister.getNumberOfParallelConstructionSites()) {
			if (economyMinister.automaticLivingHousesEnabled() && buildLivingHouse())
				return;
			if (isEndGame) {
				return;
			}
			if (isLackOfSettlers()) {
				return;
			}
			if (buildTower()) {
				return;
			}
			if (buildStock())
				return;
			buildEconomy();
		}
	}

	private boolean buildTower() {
		for (ShortPoint2D towerPosition : aiStatistics.getBuildingPositionsOfTypeForPlayer(TOWER, playerId)) {
			Building tower = aiStatistics.getBuildingAt(towerPosition);
			if (!tower.isConstructionFinished() || !tower.isOccupied()) {
				return false;
			}
		}

		List<ShortPoint2D> threatenedBorder = aiStatistics.threatenedBorderOf(playerId);
		if (threatenedBorder.size() == 0) {
			return false;
		}

		ShortPoint2D position = bestConstructionPositionFinderFactory
				.getBorderDefenceConstructionPosition(threatenedBorder)
				.findBestConstructionPosition(aiStatistics, mainGrid.getConstructionMarksGrid(), playerId);
		if (position != null) {
			taskScheduler.scheduleTask(new ConstructBuildingTask(EGuiAction.BUILD, playerId, position, TOWER));
			sendSwordsmenToTower(position);
			return true;
		}
		return false;
	}

	private void sendSwordsmenToTower(ShortPoint2D position) {
		IMovable soldier = aiStatistics.getNearestSwordsmanOf(position, playerId);
		if (soldier != null) {
			sendMovableTo(soldier, position);
		}
	}

	private boolean buildStock() {
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(GOLDMELT, playerId) < 1) {
			return false;
		}
		int stockCount = aiStatistics.getTotalNumberOfBuildingTypeForPlayer(STOCK, playerId);
		int goldCount = aiStatistics.getNumberOfMaterialTypeForPlayer(GOLD, playerId);

		return stockCount * 6 * 8 - 32 < goldCount && construct(STOCK);
	}

	private void buildEconomy() {
		Map<EBuildingType, Integer> playerBuildingPlan = new HashMap<>();
		for (EBuildingType currentBuildingType : economyMinister.getBuildingsToBuild()) {
			addBuildingCountToBuildingPlan(currentBuildingType, playerBuildingPlan);
			if (buildingNeedsToBeBuild(playerBuildingPlan, currentBuildingType)
					&& buildingDependenciesAreFulfilled(currentBuildingType)
					&& construct(currentBuildingType)) {
				return;
			}
		}
	}

	private boolean buildingDependenciesAreFulfilled(EBuildingType targetBuilding) {
		switch (targetBuilding) {
		case IRONMINE:
			return ratioFits(COALMINE, COAL_MINE_TO_IRON_MINE_RATIO, IRONMINE);
		case WEAPONSMITH:
			return ratioFits(IRONMELT, IRONMELT_TO_WEAPON_SMITH_RATIO, WEAPONSMITH);
		case IRONMELT:
			return ratioFits(COALMINE, COAL_MINE_TO_SMITH_RATIO, IRONMELT)
					&& ratioFits(IRONMINE, IRON_MINE_TO_IRONMELT_RATIO, IRONMELT);
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
				playerId) >= (double) aiStatistics.getTotalNumberOfBuildingTypeForPlayer(rightBuilding, playerId) * leftToRightBuildingRatio;
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

	private boolean isLackOfSettlers() {
		return aiStatistics.getPositionsOfJoblessBearersForPlayer(playerId).size() == 0;
	}

	private void commandPioneers() {
		if (isLackOfSettlers()) {
			releasePioneers(10);
		} else if (aiStatistics.getBorderIngestibleByPioneersOf(playerId).isEmpty() || !aiStatistics.getEnemiesInTownOf(playerId).isEmpty()) {
			releasePioneers(Integer.MAX_VALUE);
		} else if (aiStatistics.getNumberOfTotalBuildingsForPlayer(playerId) >= 4) {
			sendOutPioneers();
		}
	}

	private void releasePioneers(int numberOfPioneers) {
		broadenerPioneers.clear();
		resourcePioneers.clear();
		List<ShortPoint2D> pioneers = aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.PIONEER);
		if (!pioneers.isEmpty()) {
			List<Integer> pioneerIds = stream(pioneers)
					.limit(numberOfPioneers)
					.map(pioneerPosition -> mainGrid.getMovableGrid().getMovableAt(pioneerPosition.x, pioneerPosition.y).getID())
					.collect(Collectors.toList());
			taskScheduler.scheduleTask(new ConvertGuiTask(playerId, pioneerIds, EMovableType.BEARER));
			if (numberOfPioneers == Integer.MAX_VALUE) {
				// pioneers which can not be converted shall walk into player's land to be converted the next tic
				taskScheduler.scheduleTask(new MoveToGuiTask(playerId, aiStatistics.getPositionOfPartition(playerId), pioneerIds));
			}
		}
	}

	private void sendOutPioneers() {
		resourcePioneers.removeDeadPioneers();
		broadenerPioneers.removeDeadPioneers();

		if (!resourcePioneers.isFull()) {
			fill(resourcePioneers);
		} else if (!broadenerPioneers.isFull()) {
			fill(broadenerPioneers);
		}
		setNewTargetForResourcePioneers();
		setNewTargetForBroadenerPioneers();
	}

	private void setNewTargetForBroadenerPioneers() {
		if (broadenerPioneers.isNotEmpty()) {
			PioneerGroup pioneersWithNoAction = broadenerPioneers.getPioneersWithNoAction();
			ShortPoint2D broadenTarget = pioneerAi.findBroadenTarget();
			if (broadenTarget != null) {
				taskScheduler.scheduleTask(new MoveToGuiTask(playerId, broadenTarget, pioneersWithNoAction.getPioneerIds()));
			}
		}
	}

	private void setNewTargetForResourcePioneers() {
		if (resourcePioneers.isNotEmpty()) {
			ShortPoint2D resourceTarget = pioneerAi.findResourceTarget();
			if (resourceTarget != null) {
				taskScheduler.scheduleTask(new MoveToGuiTask(playerId, resourceTarget, resourcePioneers.getPioneerIds()));
			}
		}
	}

	private void fill(PioneerGroup pioneerGroup) {
		int numberOfBearers = aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.BEARER).size();
		int numberOfJoblessBearers = aiStatistics.getPositionsOfJoblessBearersForPlayer(playerId).size();

		int minRequiredJoblessBearers = Math.max(MINIMUM_NUMBER_OF_JOBLESS_BEARERS, (int) (MINIMUM_NUMBER_OF_JOBLESS_BEARERS_PER_BUILDING * aiStatistics.getNumberOfTotalBuildingsForPlayer(playerId)));
		int maxNewPioneersCount = Math.min(numberOfBearers - MINIMUM_NUMBER_OF_BEARERS, numberOfJoblessBearers - minRequiredJoblessBearers);

		if (maxNewPioneersCount > 0) {
			pioneerGroup.fill(taskScheduler, aiStatistics, playerId, maxNewPioneersCount);
		}
	}

	private boolean buildLivingHouse() {
		int futureNumberOfBearers = aiStatistics.getPositionsOfMovablesWithTypeForPlayer(playerId, EMovableType.BEARER).size()
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(BIG_LIVINGHOUSE, playerId) * NUMBER_OF_BIG_LIVING_HOUSE_BEDS
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(SMALL_LIVINGHOUSE, playerId) * NUMBER_OF_SMALL_LIVING_HOUSE_BEDS
				+ aiStatistics.getNumberOfNotFinishedBuildingTypesForPlayer(MEDIUM_LIVINGHOUSE, playerId) * NUMBER_OF_MEDIUM_LIVING_HOUSE_BEDS;
		if (futureNumberOfBearers < MINIMUM_NUMBER_OF_BEARERS
				|| (aiStatistics.getNumberOfTotalBuildingsForPlayer(playerId) + aiStatistics.getNumberOfBuildingTypeForPlayer(WEAPONSMITH, playerId) * WEAPON_SMITH_FACTOR)
						* NUMBER_OF_BEARERS_PER_HOUSE > futureNumberOfBearers) {
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
				sendSwordsmenToTower(position);
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
