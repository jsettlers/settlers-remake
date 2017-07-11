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
package jsettlers.ai.highlevel;

import static jsettlers.ai.highlevel.AiBuildingConstants.COAL_MINE_TO_IRON_MINE_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.COAL_MINE_TO_SMITH_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_BAKER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_MILL_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_PIG_FARM_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_SLAUGHTER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.FARM_TO_WATERWORKS_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.LUMBERJACK_TO_FORESTER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.LUMBERJACK_TO_SAWMILL_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.LUMBERJACK_TO_STONE_CUTTER_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.WEAPON_SMITH_TO_BARRACKS_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.WEAPON_SMITH_TO_FARM_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.WEAPON_SMITH_TO_FISHER_HUT_RATIO;
import static jsettlers.ai.highlevel.AiBuildingConstants.WEAPON_SMITH_TO_LUMBERJACK_RATIO;
import static jsettlers.common.buildings.EBuildingType.FISHER;

import java.util.BitSet;

import jsettlers.algorithms.distances.DistancesCalculationAlgorithm;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;

/**
 * This class calculates information about the map for the AI. At the moment it calculates how many buildings of a building type can be build on the map by each player. In a nutshell it divides the
 * landscape and resources of the map by the number of possible players. Then it determines the maximal possible iron mines and coal mines. Depending of mines to smiths ratio it calculates the maximal
 * number of possible smiths. Then it calculates the needed amount of food building by preferring fisher huts over farms. At least it calculates how many lumberjacks and stone cutters are needed to
 * build this economy including the necessary living houses to levy SOLDIERS. Now it checks if the calculated buildings have enough space to be build on the map. If not it reduces the number of smiths
 * unless it hits a threshold. Then it reduces the number of gold smith, winegrowers and big temples to a minimum set of this buildings. If again this are too much buildings. It keeps reducing smiths.
 *
 * @author codingberlin
 */
public class AiMapInformation {

	public static final int GRASS_INDEX = EResourceType.VALUES.length;
	private static final double FISH_TO_FISHER_HUTS_RATIO = 80F / 1F;
	private static final double COAL_TO_COAL_MINES_RATIO = 100F / 1F;
	private static final double IRONORE_TO_IRON_MINES_RATIO = 100F / 1F;
	private static final float GRASS_TO_LUMBERJACK_RATIO = 1360F;
	private static final int MIN_SMITHS_BEFORE_WINE_AND_GOLD_REDUCTION = 10;
	private static final int MIN_WINE_GROWER_BEFORE_GOLD_REDUCTION = 2;
	private static final int MIN_LUMBERJACK_COUNT = 3;
	private static final int MAX_FISHERS = 10;
	// max 10 fisher to prevent AI from building only fishermen which on the one hand looks very unnatural and on the other hand is unproductive in
	// the late game caused by over fishing.
	public long[][] resourceAndGrassCount;
	public final BitSet wasFishNearByAtGameStart;

	public AiMapInformation(PartitionsGrid partitionsGrid, LandscapeGrid landscapeGrid) {
		resourceAndGrassCount = new long[partitionsGrid.getNumberOfPlayers() + 1][EResourceType.VALUES.length + 1];
		wasFishNearByAtGameStart = calculateIsFishNearBy(partitionsGrid, landscapeGrid);
	}

	private BitSet calculateIsFishNearBy(PartitionsGrid partitionsGrid, LandscapeGrid landscapeGrid) {
		return DistancesCalculationAlgorithm.calculatePositionsInDistance(partitionsGrid.getWidth(), partitionsGrid.getHeight(),
				(x, y) -> landscapeGrid.getResourceTypeAt(x, y) == EResourceType.FISH && landscapeGrid.getResourceAmountAt(x, y) > 0,
				FISHER.getWorkRadius());
	}

	public void clear() {
		for (int i = 0; i < resourceAndGrassCount.length; i++) {
			for (int ii = 0; ii < resourceAndGrassCount[i].length; ii++) {
				resourceAndGrassCount[i][ii] = 0;
			}
		}
	}

	public int[] getBuildingCounts(byte playerId) {
		int numberOfPlayers = resourceAndGrassCount.length - 1;
		int neverland = resourceAndGrassCount.length - 1;
		long playersAndNeverlandFish = Math.round(resourceAndGrassCount[neverland][EResourceType.FISH.ordinal] / numberOfPlayers) + resourceAndGrassCount[playerId][EResourceType.FISH.ordinal];
		long playersAndNeverlandCoal = Math.round(resourceAndGrassCount[neverland][EResourceType.COAL.ordinal] / numberOfPlayers) + resourceAndGrassCount[playerId][EResourceType.COAL.ordinal];
		long playersAndNeverlandIronOre = Math.round(resourceAndGrassCount[neverland][EResourceType.IRONORE.ordinal] / numberOfPlayers)
				+ resourceAndGrassCount[playerId][EResourceType.IRONORE.ordinal];
		long playersAndNeverlandGold = Math.round(resourceAndGrassCount[neverland][EResourceType.GOLDORE.ordinal] / numberOfPlayers) + resourceAndGrassCount[playerId][EResourceType.GOLDORE.ordinal];
		long playersAndNeverlandGrass = Math.round(resourceAndGrassCount[neverland][GRASS_INDEX] / numberOfPlayers) + resourceAndGrassCount[playerId][GRASS_INDEX];

		int maxFishermen = Math.max(1, (int) Math.min(MAX_FISHERS, Math.ceil(playersAndNeverlandFish / FISH_TO_FISHER_HUTS_RATIO)));
		int maxCoalMines = (int) Math.ceil(playersAndNeverlandCoal / COAL_TO_COAL_MINES_RATIO);
		int maxIronMines = (int) Math.ceil(playersAndNeverlandIronOre / IRONORE_TO_IRON_MINES_RATIO);
		int maxGoldMelts = playersAndNeverlandGold > 0 ? 2 : 0;

		if (maxIronMines > maxCoalMines / COAL_MINE_TO_IRON_MINE_RATIO + 1)
			maxIronMines = (int) Math.ceil(maxCoalMines / COAL_MINE_TO_IRON_MINE_RATIO + 1);
		if (maxCoalMines > maxIronMines * COAL_MINE_TO_IRON_MINE_RATIO + 1)
			maxCoalMines = (int) Math.ceil(maxIronMines * COAL_MINE_TO_IRON_MINE_RATIO + 1);
		int maxSmiths = (int) Math.floor((float) maxCoalMines / COAL_MINE_TO_SMITH_RATIO);
		return calculateBuildingCounts(maxSmiths, maxFishermen, maxGoldMelts, 3, 1, playersAndNeverlandGrass);
	}

	private int[] calculateBuildingCounts(int numberOfWeaponSmiths, int maxFishermen, int maxGoldMelts, int maxWineGrowers, int maxBigTemples, long grassTiles) {
		int[] buildingCounts = new int[EBuildingType.NUMBER_OF_BUILDINGS];
		for (int i = 0; i < buildingCounts.length; i++) {
			buildingCounts[i] = 0;
		}
		buildingCounts[EBuildingType.COALMINE.ordinal] = numberOfWeaponSmiths;

		buildingCounts[EBuildingType.IRONMINE.ordinal] = Math.round(numberOfWeaponSmiths / COAL_MINE_TO_IRON_MINE_RATIO + 1);
		buildingCounts[EBuildingType.IRONMELT.ordinal] = numberOfWeaponSmiths;
		buildingCounts[EBuildingType.WEAPONSMITH.ordinal] = numberOfWeaponSmiths;
		buildingCounts[EBuildingType.BARRACK.ordinal] = (int) Math.ceil((double) numberOfWeaponSmiths / WEAPON_SMITH_TO_BARRACKS_RATIO);
		buildingCounts[EBuildingType.TOOLSMITH.ordinal] = 1;

		int numberOfFisher = Math.min((int) (numberOfWeaponSmiths / WEAPON_SMITH_TO_FISHER_HUT_RATIO), maxFishermen);
		buildingCounts[EBuildingType.FISHER.ordinal] = numberOfFisher;
		int numberOfRemainingWeaponSmiths = Math.max(0, numberOfWeaponSmiths - (int) (numberOfFisher * WEAPON_SMITH_TO_FISHER_HUT_RATIO));

		int numberOfFarms = (int) Math.ceil(numberOfRemainingWeaponSmiths / WEAPON_SMITH_TO_FARM_RATIO);
		buildingCounts[EBuildingType.FARM.ordinal] = numberOfFarms;
		buildingCounts[EBuildingType.BAKER.ordinal] = (int) Math.ceil(numberOfFarms / FARM_TO_BAKER_RATIO);
		buildingCounts[EBuildingType.MILL.ordinal] = (int) Math.ceil(numberOfFarms / FARM_TO_MILL_RATIO);
		buildingCounts[EBuildingType.WATERWORKS.ordinal] = (int) Math.ceil(numberOfFarms / FARM_TO_WATERWORKS_RATIO);
		buildingCounts[EBuildingType.SLAUGHTERHOUSE.ordinal] = (int) Math.ceil(numberOfFarms / FARM_TO_SLAUGHTER_RATIO);
		buildingCounts[EBuildingType.PIG_FARM.ordinal] = (int) Math.ceil(numberOfFarms / FARM_TO_PIG_FARM_RATIO);

		int lumberJacksForWeaponSmiths = Math.max(8, (int) (numberOfWeaponSmiths / WEAPON_SMITH_TO_LUMBERJACK_RATIO));
		int maxLumberJacksForMap = Math.round((float) grassTiles / GRASS_TO_LUMBERJACK_RATIO);
		int numberOfLumberJacks = Math.max(MIN_LUMBERJACK_COUNT, Math.min(maxLumberJacksForMap, lumberJacksForWeaponSmiths));
		buildingCounts[EBuildingType.LUMBERJACK.ordinal] = numberOfLumberJacks;
		buildingCounts[EBuildingType.FORESTER.ordinal] = Math.max((int) (numberOfLumberJacks / LUMBERJACK_TO_FORESTER_RATIO), 1);
		buildingCounts[EBuildingType.SAWMILL.ordinal] = Math.max((int) (numberOfLumberJacks / LUMBERJACK_TO_SAWMILL_RATIO), 1);
		buildingCounts[EBuildingType.STONECUTTER.ordinal] = Math.max((int) (numberOfLumberJacks / LUMBERJACK_TO_STONE_CUTTER_RATIO), 1);

		if (maxGoldMelts > 0) {
			buildingCounts[EBuildingType.GOLDMELT.ordinal] = maxGoldMelts;
			buildingCounts[EBuildingType.GOLDMINE.ordinal] = 1;
		}

		if (maxBigTemples > 0) {
			buildingCounts[EBuildingType.BIG_TEMPLE.ordinal] = maxBigTemples;
		}

		if (maxWineGrowers > 0) {
			buildingCounts[EBuildingType.WINEGROWER.ordinal] = maxWineGrowers;
			buildingCounts[EBuildingType.TEMPLE.ordinal] = maxWineGrowers;
		}

		if (isEnoughSpace(buildingCounts, grassTiles)) {
			return buildingCounts;
		} else if (numberOfWeaponSmiths > MIN_SMITHS_BEFORE_WINE_AND_GOLD_REDUCTION) {
			return calculateBuildingCounts(numberOfWeaponSmiths - 1, maxFishermen, maxGoldMelts, maxWineGrowers, maxBigTemples, grassTiles);
		} else if (maxWineGrowers > MIN_WINE_GROWER_BEFORE_GOLD_REDUCTION) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrowers - 1, maxBigTemples, grassTiles);
		} else if (maxGoldMelts > 1) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts - 1, maxWineGrowers, maxBigTemples, grassTiles);
		} else if (maxWineGrowers > 1) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrowers - 1, maxBigTemples, grassTiles);
		} else if (maxBigTemples > 1) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrowers, 0, grassTiles);
		} else if (maxWineGrowers > 0) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrowers - 1, 0, grassTiles);
		} else if (maxFishermen > 0) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen - 1, maxGoldMelts, maxWineGrowers, 0, grassTiles);
		} else if (numberOfWeaponSmiths > 0) {
			return calculateBuildingCounts(numberOfWeaponSmiths - 1, maxFishermen, maxGoldMelts, maxWineGrowers, 0, grassTiles);
		} else {
			return new int[EBuildingType.NUMBER_OF_BUILDINGS];
		}
	}

	private boolean isEnoughSpace(int[] buildingCounts, long grassTiles) {
		long grassTilesWithoutBuffer = Math.round(grassTiles / 3F);
		for (int i = 0; i < buildingCounts.length; i++) {
			EBuildingType buildingType = EBuildingType.VALUES[i];
			if (!buildingType.isMine()) {
				grassTilesWithoutBuffer -= EBuildingType.VALUES[i].getProtectedTiles().length * buildingCounts[i];
				if (grassTilesWithoutBuffer < 0) {
					return false;
				}
			}
		}

		return true;
	}

	public long getRemainingGrassTiles(AiStatistics aiStatistics, byte playerId) {
		long remainingGrass = resourceAndGrassCount[playerId][GRASS_INDEX];
		for (EBuildingType buildingType : EBuildingType.VALUES) {
			if (!buildingType.isMine()) {
				remainingGrass -= buildingType.getProtectedTiles().length * aiStatistics.getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId);
			}
		}
		return remainingGrass;
	}

	public long getGrassTilesOf(byte playerId) {
		return resourceAndGrassCount[playerId][GRASS_INDEX];
	}
}
