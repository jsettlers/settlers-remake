/*******************************************************************************
 * Copyright (c) 2016 - 2017
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
package jsettlers.ai.economy;

import static jsettlers.common.buildings.EBuildingType.BAKER;
import static jsettlers.common.buildings.EBuildingType.BARRACK;
import static jsettlers.common.buildings.EBuildingType.BIG_TEMPLE;
import static jsettlers.common.buildings.EBuildingType.BIG_TOWER;
import static jsettlers.common.buildings.EBuildingType.CASTLE;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IPlayer;
import jsettlers.logic.player.Player;

/**
 * This economy minister is as optimized as possible to create fast and many level 3 SOLDIERS with high combat strength. It builds a longterm economy with rush defence if needed. It starts with 8
 * lumberjacks first, then it builds mana. Then food, weapons, more lumberjacks and gold economy is build in parallel until the full amount of possible buildings of the map is reached. If the map is
 * smaller than 8 lumberjacks, it builds weapon smiths before mana. The minister is down sizable by a weapon smiths factor and a building industry factor.
 *
 * @author codingberlin
 */
public class BuildingListEconomyMinister implements EconomyMinister {

	private static final Collection<EBuildingType> RUSH_DEFENCE_BUILDINGS = EnumSet.of(LUMBERJACK, SAWMILL, STONECUTTER, IRONMELT, WEAPONSMITH, BARRACK, SMALL_LIVINGHOUSE, COALMINE, IRONMINE,
			MEDIUM_LIVINGHOUSE);
	private static final Collection<EBuildingType> BUILDING_INDUSTRY = EnumSet.of(LUMBERJACK, FORESTER, SAWMILL, STONECUTTER);

	private final AiStatistics aiStatistics;
	private final boolean limitByWeakestEnemy;
	private final float buildingIndustryFactor;
	private final float weaponSmithFactor;
	private final boolean isHighGoodsGame;
	private final boolean isMiddleGoodsGame;

	private final IPlayer player;
	private final byte playerId;

	private final List<EBuildingType> buildingsToBuild;
	private int[] mapBuildingCounts;
	private int numberOfMidGameStoneCutters = 0;

	/**
	 * @param weaponSmithFactor
	 *            influences the power of the AI. Use 1 for full power. Use < 1 for weaker AIs. The factor is used to determine the maximum amount of weapon smiths build on the map and shifts the
	 *            point of time when the weapon smiths are build.
	 * @param buildingIndustryFactor
	 *            influences the power of the AI. Use 1 for full power. Use < 1 for weaker AIs. The factor is used to determine the maximum amount of building industry buildings. If the AI e.g. builds
	 *            less lumberjacks it is slower.
	 * @param limitByWeakestEnemy
	 *            when set limits the AI in all amounts of buildings by the average building count of all alive enemies.
	 */
	public BuildingListEconomyMinister(AiStatistics aiStatistics, Player player, float weaponSmithFactor, float buildingIndustryFactor, boolean limitByWeakestEnemy) {
		this.aiStatistics = aiStatistics;
		this.buildingIndustryFactor = buildingIndustryFactor;
		this.limitByWeakestEnemy = limitByWeakestEnemy;
		this.player = player;
		this.playerId = player.playerId;
		this.buildingsToBuild = new ArrayList<>();
		this.weaponSmithFactor = weaponSmithFactor;
		this.isHighGoodsGame = isHighGoodsGame();
		this.isMiddleGoodsGame = isMiddleGoodsGame();
	}

	@Override
	public void update() {
		buildingsToBuild.clear();
		this.mapBuildingCounts = aiStatistics.getAiMapInformation().getBuildingCounts(player.getPlayerId());
		addMinimalBuildingMaterialBuildings();
		if (isVerySmallMap()) {
			addSmallWeaponProduction();
			addFoodAndBuildingMaterialAndWeaponAndGoldIndustry();
			addManaBuildings();
		} else {
			addManaBuildings();
			addFoodAndBuildingMaterialAndWeaponAndGoldIndustry();
			addSecondToolSmith();
		}
	}

	private void addSecondToolSmith() {
		if (mapBuildingCounts[WEAPONSMITH.ordinal] * weaponSmithFactor >= 16) {
			buildingsToBuild.add(80, TOOLSMITH);
		}

	}

	@Override
	public boolean isEndGame() {
		double remainingGrass = aiStatistics.getAiMapInformation().getRemainingGrassTiles(aiStatistics, playerId)
				- aiStatistics.getTreesForPlayer(playerId).size()
				- aiStatistics.getStonesForPlayer(playerId).size();
		double availableGrass = aiStatistics.getAiMapInformation().getGrassTilesOf(playerId);
		return remainingGrass / availableGrass <= 0.6F;
	}

	private void addFoodAndBuildingMaterialAndWeaponAndGoldIndustry() {
		List<EBuildingType> weaponsBuildings = determineWeaponAndGoldBuildings();
		List<EBuildingType> foodBuildings = determineFoodBuildings();
		List<EBuildingType> buildingMaterialBuildings = determineBuildingMaterialBuildings();

		float allBuildingsCount = foodBuildings.size() + buildingMaterialBuildings.size() + weaponsBuildings.size();
		float weaponsBuildingsRatio = ((float) weaponsBuildings.size()) / allBuildingsCount;
		float foodBuildingsRatio = ((float) foodBuildings.size()) / (allBuildingsCount * weaponSmithFactor);
		float buildingMaterialBuildingRatio = ((float) buildingMaterialBuildings.size()) / (allBuildingsCount * weaponSmithFactor);
		int maxSize = Math.max(foodBuildings.size(), Math.max(buildingMaterialBuildings.size(), weaponsBuildings.size()));
		for (int i = 0; i < maxSize; i++) {
			if (weaponsBuildingsRatio > foodBuildingsRatio && weaponsBuildingsRatio > buildingMaterialBuildingRatio) {
				mergeAndAddNextItems(weaponsBuildings, foodBuildings, foodBuildingsRatio, buildingMaterialBuildings, buildingMaterialBuildingRatio);
			} else if (buildingMaterialBuildingRatio > foodBuildingsRatio && buildingMaterialBuildingRatio > weaponsBuildingsRatio) {
				mergeAndAddNextItems(buildingMaterialBuildings, weaponsBuildings, weaponsBuildingsRatio, foodBuildings, foodBuildingsRatio);
			} else {
				mergeAndAddNextItems(foodBuildings, weaponsBuildings, weaponsBuildingsRatio, buildingMaterialBuildings, buildingMaterialBuildingRatio);
			}
		}

		numberOfMidGameStoneCutters = (currentCountOf(STONECUTTER) / 2);
	}

	private List<EBuildingType> determineBuildingMaterialBuildings() {
		List<EBuildingType> buildingMaterialBuildings = new ArrayList<>();
		for (int i = 0; i < Math.ceil(mapBuildingCounts[LUMBERJACK.ordinal] * buildingIndustryFactor) - 8; i++) {
			buildingMaterialBuildings.add(LUMBERJACK);
			if (i % 3 == 1)
				buildingMaterialBuildings.add(FORESTER);
			if (i % 2 == 1)
				buildingMaterialBuildings.add(SAWMILL);
			if (i % 2 == 1)
				buildingMaterialBuildings.add(STONECUTTER);
		}
		return buildingMaterialBuildings;
	}

	private List<EBuildingType> determineFoodBuildings() {
		List<EBuildingType> foodBuildings = new ArrayList<>();
		for (int i = 0; i < mapBuildingCounts[FISHER.ordinal]; i++) {
			foodBuildings.add(FISHER);
		}
		for (int i = 0; i < mapBuildingCounts[FARM.ordinal]; i++) {
			foodBuildings.add(FARM);
			if (i % 2 == 0)
				foodBuildings.add(WATERWORKS);
			if (i % 3 == 0)
				foodBuildings.add(MILL);
			if (i % 3 == 0)
				foodBuildings.add(BAKER);
			if (i % 3 == 0)
				foodBuildings.add(BAKER);
			if (i % 3 == 1)
				foodBuildings.add(BAKER);
			if (i % 6 == 1 || i % 6 == 2 || i % 6 == 5)
				foodBuildings.add(PIG_FARM);
			if (i % 6 == 1)
				foodBuildings.add(SLAUGHTERHOUSE);
		}
		return foodBuildings;
	}

	private List<EBuildingType> determineWeaponAndGoldBuildings() {
		List<EBuildingType> weaponsBuildings = new ArrayList<>();
		for (int i = 0; i < (mapBuildingCounts[WEAPONSMITH.ordinal] * weaponSmithFactor); i++) {
			weaponsBuildings.add(COALMINE);
			if (i % 2 == 0)
				weaponsBuildings.add(IRONMINE);
			weaponsBuildings.add(IRONMELT);
			if (i == 0 && currentCountOf(TOOLSMITH) < 1)
				weaponsBuildings.add(TOOLSMITH);
			weaponsBuildings.add(WEAPONSMITH);
			if (i % 3 == 0)
				weaponsBuildings.add(BARRACK);
			if (i == 2)
				weaponsBuildings.add(TOWER);
			if (i == 6)
				weaponsBuildings.add(BIG_TOWER);
			if (i == 16)
				weaponsBuildings.add(CASTLE);
			if (i == 3)
				addGoldBuildings(weaponsBuildings);
		}
		if (mapBuildingCounts[WEAPONSMITH.ordinal] < 4)
			addGoldBuildings(weaponsBuildings);
		return weaponsBuildings;
	}

	private void addSmallWeaponProduction() {
		buildingsToBuild.add(FISHER);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BARRACK);
	}

	private int currentCountOf(EBuildingType targetBuildingType) {
		int result = 0;
		for (EBuildingType buildingType : buildingsToBuild) {
			if (buildingType == targetBuildingType) {
				result++;
			}
		}
		return result;
	}

	private void addIfPossible(EBuildingType buildingType) {
		if (buildingType.isMilitaryBuilding()) {
			buildingsToBuild.add(buildingType);
		} else {
			float factor = 1F;
			if (BUILDING_INDUSTRY.contains(buildingType)) {
				factor = buildingIndustryFactor;
			}
			double currentCount = currentCountOf(buildingType);
			if (currentCount < Math.ceil(mapBuildingCounts[buildingType.ordinal] * factor)
					&& currentCount < maximumAllowedCount(buildingType)) {
				buildingsToBuild.add(buildingType);
			}
		}
	}

	private int maximumAllowedCount(EBuildingType buildingType) {
		if (!limitByWeakestEnemy) {
			return Integer.MAX_VALUE;
		}
		List<IPlayer> enemies = aiStatistics.getAliveEnemiesOf(player);
		float sumOfBuildings = 0;
		for (IPlayer enemy : enemies) {
			sumOfBuildings += aiStatistics.getTotalNumberOfBuildingTypeForPlayer(buildingType, enemy.getPlayerId());
		}
		return Math.max(1, (int) (sumOfBuildings / enemies.size()));
	}

	private boolean isVerySmallMap() {
		return mapBuildingCounts[LUMBERJACK.ordinal] < 8;
	}

	private void addManaBuildings() {
		for (int i = 0; i < mapBuildingCounts[WINEGROWER.ordinal]; i++) {
			addIfPossible(WINEGROWER);
		}
		for (int i = 0; i < mapBuildingCounts[WINEGROWER.ordinal]; i++) {
			addIfPossible(TEMPLE);
		}
		if (mapBuildingCounts[BIG_TEMPLE.ordinal] > 0) {
			addIfPossible(BIG_TEMPLE);
		}
	}

	private void mergeAndAddNextItems(
			List<EBuildingType> dominantBuildingList,
			List<EBuildingType> slaveBuildingListA, float targetSlaveRatioA,
			List<EBuildingType> slaveBuildingListB, float targetSlaveRatioB) {
		addFirstItemToBuildingList(dominantBuildingList);
		float allBuildingsCount = dominantBuildingList.size() + slaveBuildingListA.size() + slaveBuildingListB.size();
		if (allBuildingsCount == 0)
			return;

		float currentSlaveRatioA = ((float) slaveBuildingListA.size()) / allBuildingsCount;
		float currentSlaveRatioB = ((float) slaveBuildingListB.size()) / allBuildingsCount;
		if (currentSlaveRatioA > targetSlaveRatioA) {
			addFirstItemToBuildingList(slaveBuildingListA);
		}
		if (currentSlaveRatioB > targetSlaveRatioB) {
			addFirstItemToBuildingList(slaveBuildingListB);
		}
	}

	private void addFirstItemToBuildingList(List<EBuildingType> buildingList) {
		if (buildingList.size() > 0) {
			addIfPossible(buildingList.remove(0));
		}
	}

	private void addMinimalBuildingMaterialBuildings() {
		buildingsToBuild.add(TOWER); // Start Tower
		if (isHighGoodsGame) {
			addIfPossible(LUMBERJACK);
			addIfPossible(SAWMILL);
			addIfPossible(LUMBERJACK);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			addIfPossible(STONECUTTER);
			addIfPossible(LUMBERJACK);
			addIfPossible(SAWMILL);
			addIfPossible(LUMBERJACK);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			addIfPossible(FORESTER);
			addIfPossible(LUMBERJACK);
			addIfPossible(SAWMILL);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			addIfPossible(STONECUTTER);
			addIfPossible(STONECUTTER);
			addIfPossible(STONECUTTER);
			addIfPossible(STONECUTTER);
		} else if (isMiddleGoodsGame) {
			addIfPossible(LUMBERJACK);
			addIfPossible(SAWMILL);
			addIfPossible(LUMBERJACK);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			addIfPossible(STONECUTTER);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			addIfPossible(LUMBERJACK);
			addIfPossible(SAWMILL);
			addIfPossible(LUMBERJACK);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			addIfPossible(STONECUTTER);
			addIfPossible(IRONMELT);
			addIfPossible(TOOLSMITH);
			addIfPossible(FORESTER);
			addIfPossible(LUMBERJACK);
			addIfPossible(SAWMILL);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			addIfPossible(STONECUTTER);
			addIfPossible(STONECUTTER);
			addIfPossible(STONECUTTER);
		} else {
			addIfPossible(LUMBERJACK);
			addIfPossible(SAWMILL);
			addIfPossible(LUMBERJACK);
			buildingsToBuild.add(SMALL_LIVINGHOUSE);
			addIfPossible(STONECUTTER);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			addIfPossible(IRONMELT);
			addIfPossible(TOOLSMITH);
			addIfPossible(COALMINE);
			addIfPossible(IRONMINE);
			addIfPossible(FISHER);
			addIfPossible(FARM);
			addIfPossible(WATERWORKS);
			addIfPossible(MILL);
			addIfPossible(BAKER);
			addIfPossible(COALMINE);
			addIfPossible(SAWMILL);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			addIfPossible(STONECUTTER);
			addIfPossible(LUMBERJACK);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			addIfPossible(LUMBERJACK);
			addIfPossible(SAWMILL);
			addIfPossible(LUMBERJACK);
			addIfPossible(FORESTER);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			addIfPossible(STONECUTTER);
			addIfPossible(STONECUTTER);
			addIfPossible(STONECUTTER);
		}
	}

	private boolean isHighGoodsGame() {
		return aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.AXE, playerId) >= 8 &&
				aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.SAW, playerId) >= 3 &&
				aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.PICK, playerId) >= 5;
	}

	private boolean isMiddleGoodsGame() {
		return aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.AXE, playerId) >= 6 &&
				aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.SAW, playerId) >= 2 &&
				aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.PICK, playerId) >= 4;
	}

	private void addGoldBuildings(List<EBuildingType> weaponsBuildings) {
		if (mapBuildingCounts[GOLDMELT.ordinal] > 0) {
			weaponsBuildings.add(GOLDMINE);
			for (int ii = 0; ii < mapBuildingCounts[GOLDMELT.ordinal]; ii++) {
				weaponsBuildings.add(GOLDMELT);
				weaponsBuildings.add(STOCK);
			}
		}
	}

	@Override
	public int getNumberOfParallelConstructionSites() {
		if (aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.PLANK, playerId) > 1
				&& aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.STONE, playerId) > 1) {
			// If plank and stone is still offered, we can build the next building.
			// If the next building will consume all remaining offers we won't return 100 in the next tick
			return 100;
		}
		return Math.max((int) Math.ceil((float) aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) / 2F), 2);
	}

	@Override
	public List<EBuildingType> getBuildingsToBuild() {
		if (isDanger()) {
			return prefixBuildingsToBuildWithRushDefence();
		} else {
			return buildingsToBuild;
		}
	}

	private boolean isDanger() {
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(BARRACK, playerId) < 1) {
			int numberOfSwordsmen = aiStatistics.getCountOfMovablesOfPlayer(player, EMovableType.SWORDSMEN);
			if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(TOWER, playerId) >= numberOfSwordsmen) {
				return true;
			}
			for (IPlayer enemy : aiStatistics.getAliveEnemiesOf(player)) {
				if (aiStatistics.getNumberOfBuildingTypeForPlayer(WEAPONSMITH, enemy.getPlayerId()) > 0) {
					return true;
				}
			}
		}
		return false;
	}

	private List<EBuildingType> prefixBuildingsToBuildWithRushDefence() {
		List<EBuildingType> allBuildingsToBuild = new ArrayList<>(RUSH_DEFENCE_BUILDINGS.size() + buildingsToBuild.size());
		allBuildingsToBuild.addAll(RUSH_DEFENCE_BUILDINGS);
		allBuildingsToBuild.addAll(buildingsToBuild);
		return allBuildingsToBuild;
	}

	@Override
	public int getMidGameNumberOfStoneCutters() {
		return numberOfMidGameStoneCutters;
	}

	@Override
	public boolean automaticLivingHousesEnabled() {
		return aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) >= 8
				|| aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) >= mapBuildingCounts[LUMBERJACK.ordinal] * buildingIndustryFactor
				|| aiStatistics.getNumberOfBuildingTypeForPlayer(WEAPONSMITH, playerId) >= 1;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
