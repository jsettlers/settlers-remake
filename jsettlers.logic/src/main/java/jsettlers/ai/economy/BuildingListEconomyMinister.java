/*******************************************************************************
 * Copyright (c) 2015
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.ai.economy;

import jsettlers.ai.construction.BuildingCount;
import jsettlers.ai.highlevel.AiMapInformation;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.player.Player;

import java.util.ArrayList;
import java.util.List;

import static jsettlers.common.buildings.EBuildingType.*;
import static jsettlers.common.buildings.EBuildingType.IRONMELT;
import static jsettlers.common.buildings.EBuildingType.WEAPONSMITH;

/**
 * This economy minister is as optimized as possible to create fast and many level 3 soldiers with high combat strength. It builds a longterm economy
 * with 8 lumberjacks first, then mana, food, weapons and gold economy.
 *
 * @author codingberlin
 */
public class BuildingListEconomyMinister implements EconomyMinister {

	private final AiMapInformation aiMapInformation;
	protected final List<EBuildingType> buildingsToBuild;

	/**
	 *
	 * @param aiStatistics
	 * @param aiMapInformation
	 * @param player
	 * @param weaponSmithFactor influences the power of the AI. Use 1 for full power. Use < 1 for weaker AIs. The factor is used to determine the
	 *                             maximum amount of weapon smiths build on the map and shifts the point of time when the weapon smiths are build.
	 */
	public BuildingListEconomyMinister(AiStatistics aiStatistics, AiMapInformation aiMapInformation, Player player, float weaponSmithFactor) {
		this.buildingsToBuild = new ArrayList<>();
		this.aiMapInformation = aiMapInformation;
		initializeBuildingsToBuild(aiStatistics, player, weaponSmithFactor);
	};

	protected int plannedCountOf(EBuildingType buildingType, List<BuildingCount> buildingCounts) {
		for (BuildingCount count : buildingCounts) {
			if (count.buildingType == buildingType) {
				return (int) count.count;
			}
		}
		return 0;
	}

	protected int currentCountOf(EBuildingType targetBuildingType) {
		int result = 0;
		for (EBuildingType buildingType : buildingsToBuild) {
			if (buildingType == targetBuildingType) {
				result++;
			}
		}
		return result;
	}

	protected void addIfPossible(EBuildingType buildingType, List<BuildingCount> buildingCounts) {
		if (currentCountOf(buildingType) < plannedCountOf(buildingType, buildingCounts)) {
			buildingsToBuild.add(buildingType);
		}
	}

	private void initializeBuildingsToBuild(AiStatistics aiStatistics, Player player, float weaponSmithFactor) {
		List<BuildingCount> buildingCounts = aiMapInformation.getBuildingCounts();
		addMinimalBuildingMaterialBuildings(buildingCounts, aiStatistics, player);
		if (isVerySmallMap()) {
			buildingsToBuild.add(FISHER);
			buildingsToBuild.add(COALMINE);
			buildingsToBuild.add(IRONMINE);
			buildingsToBuild.add(IRONMELT);
			buildingsToBuild.add(WEAPONSMITH);
			buildingsToBuild.add(BARRACK);
		} else {
			addManaBuildings(buildingCounts);
		}
		List<EBuildingType> weaponsBuildings = new ArrayList<>();
		for (int i = 0; i < (aiMapInformation.getNumberOfWeaponSmiths()*weaponSmithFactor); i++) {
			weaponsBuildings.add(COALMINE);
			if (i % 2 == 0) {
				weaponsBuildings.add(IRONMINE);
			}
			weaponsBuildings.add(IRONMELT);
			if (i == 0 && currentCountOf(TOOLSMITH) < 1) {
				weaponsBuildings.add(TOOLSMITH);
			}
			weaponsBuildings.add(WEAPONSMITH);
			if (i % 3 == 0) {
				weaponsBuildings.add(BARRACK);
			}
			if (i == 3) {
				addGoldBuildings(aiMapInformation, weaponsBuildings);
			}
		}
		if (aiMapInformation.getNumberOfWeaponSmiths() < 4) {
			addGoldBuildings(aiMapInformation, weaponsBuildings);
		}

		List<EBuildingType> foodBuildings = new ArrayList<>();
		for (int i = 0; i < aiMapInformation.getNumberOfFisher(); i++) {
			foodBuildings.add(FISHER);
		}
		for (int i = 0; i < aiMapInformation.getNumberOfFarms(); i++) {
			foodBuildings.add(FARM);
			if (i % 2 == 0) {
				foodBuildings.add(WATERWORKS);
			}
			if (i % 3 == 0) {
				foodBuildings.add(MILL);
			}
			if (i % 3 == 0) {
				foodBuildings.add(BAKER);
				foodBuildings.add(BAKER);
			}
			if (i % 3 == 1) {
				foodBuildings.add(BAKER);
			}
			if (i % 6 == 1 || i % 6 == 2 || i % 6 == 5) {
				foodBuildings.add(PIG_FARM);
			}
			if (i % 6 == 1) {
				foodBuildings.add(SLAUGHTERHOUSE);
			}
		}

		List<EBuildingType> buildingMaterialBuildings = new ArrayList<>();
		for (int i = 0; i < aiMapInformation.getNumberOfLumberJacks() - 8; i++) {
			buildingMaterialBuildings.add(LUMBERJACK);
			if (i % 3 == 1) {
				buildingMaterialBuildings.add(FORESTER);
			}
			if (i % 2 == 1) {
				buildingMaterialBuildings.add(SAWMILL);
			}
			if (i % 2 == 1) {
				buildingMaterialBuildings.add(STONECUTTER);
			}
		}

		float allBuildingsCount = foodBuildings.size() + buildingMaterialBuildings.size() + weaponsBuildings.size();
		float weaponsBuildingsRatio = ((float) weaponsBuildings.size()) / allBuildingsCount;
		float foodBuildingsRatio = ((float) foodBuildings.size()) / (allBuildingsCount * weaponSmithFactor);
		float buildingMaterialBuildingRatio = ((float) buildingMaterialBuildings.size()) / (allBuildingsCount * weaponSmithFactor);
		int maxSize = Math.max(foodBuildings.size(), Math.max(buildingMaterialBuildings.size(), weaponsBuildings.size()));
		for (int i = 0; i < maxSize; i++) {
			if (weaponsBuildingsRatio > foodBuildingsRatio && weaponsBuildingsRatio > buildingMaterialBuildingRatio) {
				mergeNextItems(weaponsBuildings,
						foodBuildings, foodBuildingsRatio,
						buildingMaterialBuildings, buildingMaterialBuildingRatio,
						buildingCounts);
			} else if (buildingMaterialBuildingRatio > foodBuildingsRatio && buildingMaterialBuildingRatio > weaponsBuildingsRatio) {
				mergeNextItems(buildingMaterialBuildings,
						weaponsBuildings, weaponsBuildingsRatio,
						foodBuildings, foodBuildingsRatio,
						buildingCounts);

			} else {
				mergeNextItems(foodBuildings,
						weaponsBuildings, weaponsBuildingsRatio,
						buildingMaterialBuildings, buildingMaterialBuildingRatio,
						buildingCounts);
			}
		}

		if (isVerySmallMap()) {
			addManaBuildings(buildingCounts);
		}
	}

	private boolean isVerySmallMap() {
		return aiMapInformation.getNumberOfLumberJacks() < 8;
	}

	private void addManaBuildings(List<BuildingCount> buildingCounts) {
		for (int i = 0; i < aiMapInformation.getNumberOfWineGrower(); i++) {
			addIfPossible(WINEGROWER, buildingCounts);
		}
		for (int i = 0; i < aiMapInformation.getNumberOfWineGrower(); i++) {
			addIfPossible(TEMPLE, buildingCounts);
		}
		if (aiMapInformation.getNumberOfBigTemples() > 0) {
			addIfPossible(BIG_TEMPLE, buildingCounts);
		}
	}

	private void mergeNextItems(
			List<EBuildingType>  dominantBuildingList,
			List<EBuildingType> slaveBuildingListA, float targetSlaveRatioA,
			List<EBuildingType> slaveBuildingListB, float targetSlaveRatioB,
			List<BuildingCount> buildingCounts) {
		addFirstItemToBuildingList(dominantBuildingList, buildingCounts);
		float allBuildingsCount = dominantBuildingList.size() + slaveBuildingListA.size() + slaveBuildingListB.size();
		if (allBuildingsCount == 0)
			return;

		float currentSlaveRatioA = ((float) slaveBuildingListA.size()) / allBuildingsCount;
		float currentSlaveRatioB = ((float) slaveBuildingListB.size()) / allBuildingsCount;
		if (currentSlaveRatioA > targetSlaveRatioA) {
			addFirstItemToBuildingList(slaveBuildingListA, buildingCounts);
		}
		if (currentSlaveRatioB > targetSlaveRatioB) {
			addFirstItemToBuildingList(slaveBuildingListB, buildingCounts);
		}
	}

	private void addFirstItemToBuildingList(List<EBuildingType> buildingList, List<BuildingCount> buildingCounts) {
		if (buildingList.size() > 0) {
			addIfPossible(buildingList.remove(0), buildingCounts);
		}
	}

	private void addMinimalBuildingMaterialBuildings(List<BuildingCount> buildingCounts, AiStatistics aiStatistics, Player player) {
		buildingsToBuild.add(TOWER); // Start Tower
		if (isHighGoodsGame(aiStatistics, player)) {
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			addIfPossible(STONECUTTER, buildingCounts);
			buildingsToBuild.add(TOWER);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
		} else if (isMiddleGoodsGame(aiStatistics, player)) {
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			addIfPossible(STONECUTTER, buildingCounts);
			buildingsToBuild.add(TOWER);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(IRONMELT, buildingCounts);
			addIfPossible(TOOLSMITH, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
		} else {
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			buildingsToBuild.add(SMALL_LIVINGHOUSE);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			buildingsToBuild.add(TOWER);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			addIfPossible(IRONMELT, buildingCounts);
			addIfPossible(TOOLSMITH, buildingCounts);
			addIfPossible(COALMINE, buildingCounts);
			addIfPossible(IRONMINE, buildingCounts);
			addIfPossible(FISHER, buildingCounts);
			addIfPossible(FARM, buildingCounts);
			addIfPossible(WATERWORKS, buildingCounts);
			addIfPossible(MILL, buildingCounts);
			addIfPossible(BAKER, buildingCounts);
			addIfPossible(COALMINE, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(FORESTER, buildingCounts);
			buildingsToBuild.add(MEDIUM_LIVINGHOUSE);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
		}
	}

	private boolean isHighGoodsGame(AiStatistics aiStatistics, Player player) {
		byte playerId = player.playerId;
		return aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.AXE, playerId) >= 8 &&
				aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.SAW, playerId) >= 3 &&
				aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.PICK, playerId) >= 5;
	}

	private boolean isMiddleGoodsGame(AiStatistics aiStatistics, Player player) {
		byte playerId = player.playerId;
		return aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.AXE, playerId) >= 6 &&
				aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.SAW, playerId) >= 2 &&
				aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.PICK, playerId) >= 4;
	}

	private void addGoldBuildings(AiMapInformation aiMapInformation, List<EBuildingType> weaponsBuildings) {
		if (aiMapInformation.getNumberOfGoldMelts() > 0) {
			weaponsBuildings.add(GOLDMINE);
			for (int ii = 0; ii < aiMapInformation.getNumberOfGoldMelts(); ii++) {
				weaponsBuildings.add(GOLDMELT);
				weaponsBuildings.add(STOCK);
			}
		}
	}

	@Override
	public int getNumberOfParallelConstructionSides(AiStatistics aiStatistics, byte playerId) {
		if (aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.PLANK, playerId) > 1
				&& aiStatistics.getNumberOfMaterialTypeForPlayer(EMaterialType.STONE, playerId) > 1) {
			// If plank and stone is still offered, we can build the next building.
			// If the next building will consume all remaining offers we won't return 100 in the next tick
			return 100;
		}
		return Math.max((int) Math.ceil((float) aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) / 2F), 2);
	}

	@Override
	public List<EBuildingType> getBuildingsToBuild(AiStatistics aiStatistics, byte playerId) {
		List<EBuildingType> allBuildingsToBuild = getEmergencyBuildings(aiStatistics, playerId);
		allBuildingsToBuild.addAll(buildingsToBuild);
		return allBuildingsToBuild;
	}

	private List<EBuildingType> getEmergencyBuildings(AiStatistics aiStatistics, byte playerId) {
		List<EBuildingType> emergencyBuildings = new ArrayList<>();
		if (aiStatistics.getTotalNumberOfBuildingTypeForPlayer(IRONMINE, playerId) < 1) {
			for (byte enemy : aiStatistics.getEnemiesOf(playerId)) {
				if (aiStatistics.getNumberOfBuildingTypeForPlayer(WEAPONSMITH, enemy) > 0) {
					emergencyBuildings.add(LUMBERJACK);
					emergencyBuildings.add(SAWMILL);
					emergencyBuildings.add(STONECUTTER);
					emergencyBuildings.add(IRONMELT);
					emergencyBuildings.add(WEAPONSMITH);
					emergencyBuildings.add(BARRACK);
					emergencyBuildings.add(SMALL_LIVINGHOUSE);
					emergencyBuildings.add(COALMINE);
					emergencyBuildings.add(IRONMINE);
					emergencyBuildings.add(MEDIUM_LIVINGHOUSE);
					return emergencyBuildings;
				}
			}
		}
		return emergencyBuildings;
	}

	@Override
	public byte getMidGameNumberOfStoneCutters() {
		return 5;
	}

	@Override
	public boolean automaticTowersEnabled(AiStatistics aiStatistics, byte playerId) {
		return aiStatistics.getNumberOfBuildingTypeForPlayer(TOWER, playerId) >= 2;
	}

	@Override
	public boolean automaticLivingHousesEnabled(AiStatistics aiStatistics, byte playerId) {
		return aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) >= 8
				|| aiStatistics.getNumberOfBuildingTypeForPlayer(LUMBERJACK, playerId) >= aiMapInformation.getNumberOfLumberJacks();
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
