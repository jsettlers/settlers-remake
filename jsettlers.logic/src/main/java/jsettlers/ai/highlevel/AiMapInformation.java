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

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;
import jsettlers.logic.map.grid.partition.PartitionsGrid;
import jsettlers.logic.player.Player;

/**
 * This class calculates information about the map for the AI. At the moment it calculates how many buildings of a building type can be build on
 * the map by each player. In a nutshell it divides the landscape and resources of the map by the number of possible players. Then it determines
 * the maximal possible iron mines and coal mines. Depending of mines to smiths ratio it calculates the maximal number of possible smiths. Then it
 * calculates the needed amount of food building by preferring fisher huts over farms. At least it calculates how many lumberjacks and stone
 * cutters are needed to build this economy including the necessary living houses to levy soldiers. Now it checks if the calculated buildings have
 * enough space to be build on the map. If not it reduces the number of smiths unless it hits a threshold. Then it reduces the number of gold smith,
 * winegrowers and big temples to a minimum set of this buildings. If again this are too much buildings. It keeps reducing smiths.
 *
 * TODO: add information where to find the resources and how far they are to let AI use pioneers instead of towers
 *
 * @author codingberlin
 */
public class AiMapInformation {

	private static final double FISH_TO_FISHER_HUTS_RATIO = 80F / 1F;
	private static final double COAL_TO_COAL_MINES_RATIO = 80F / 1F;
	private static final double IRONORE_TO_IRON_MINES_RATIO = 50F / 1F;
	private static final double COAL_MINE_TO_IRONORE_MINE_RATIO = 2F / 1F;
	private static final double WEAPON_SMITH_TO_BARRACKS_RATIO = 3F / 1F;
	private static final double WEAPON_SMITH_TO_FISHER_HUT_RATIO = 2F / 3F;
	private static final double WEAPON_SMITH_TO_FARM_RATIO = 2F / 1F;
	private static final double FARM_TO_BAKER_RATIO = 1F / 1F;
	private static final double FARM_TO_MILL_RATIO = 3F / 1F;
	private static final double FARM_TO_WATERWORKS_RATIO = 3F / 1F;
	private static final double FARM_TO_PIG_FARM_RATIO = 3F / 1F;
	private static final double FARM_TO_SLAUGHTER_RATIO = 6F / 1F;
	private static final double WEAPON_SMITH_TO_LUMBERJACK_RATIO = 10F / 8F;
	private static final double LUMBERJACK_TO_SAWMILL_RATIO = 2F / 1F;
	private static final double LUMBERJACK_TO_FORESTER_RATIO = 2F / 1F;
	private static final double LUMBERJACK_TO_STONE_CUTTER_RATIO = 8F / 5F;
	public static final float COAL_MINE_TO_SMITH_RATIO = 1F / 1.8F;
	private static final int MIN_SMITHS_BEFORE_WINE_AND_GOLD_REDUCTION = 10;
	private static final int MIN_WINE_GROWER_BEFORE_GOLD_REDUCTION = 2;
	// max 10 fisher to prevent AI from building only fishermen which on the one hand looks very unnatural and on the other hand is unproductive in
	// the late game caused by over fishing.
	public static final int MAX_FISHERS = 10;
	private long grasTiles = 0;
	private long[][] resourceCount;

	public AiMapInformation(MainGrid mainGrid) {
		LandscapeGrid landscapeGrid = mainGrid.getLandscapeGrid();
		PartitionsGrid partitionsGrid = mainGrid.getPartitionsGrid();
		resourceCount = new long[partitionsGrid.getNumberOfPlayers() + 1][EResourceType.values().length];

		for (int i = 0; i < resourceCount.length; i++) {
			for (int ii = 0; ii < resourceCount[i].length; ii++) {
				resourceCount[i][ii] = 0;
			}
		}
		for (int x = 0; x < mainGrid.getWidth(); x++) {
			for (int y = 0; y < mainGrid.getHeight(); y++) {
				if (landscapeGrid.getResourceAmountAt(x, y) > 0) {
					EResourceType resourceType = landscapeGrid.getResourceTypeAt(x, y);
					if (resourceType != EResourceType.FISH || landscapeGrid.getLandscapeTypeAt(x, y) == ELandscapeType.WATER1) {
						int playerId;
						Player player = partitionsGrid.getPlayerAt(x, y);
						if (player != null) {
							playerId = player.playerId;
						} else {
							playerId = resourceCount.length - 1;
						}
						resourceCount[playerId][resourceType.ordinal]++;
					}
				}
				if (landscapeGrid.getLandscapeTypeAt(x, y).isGrass()) {
					grasTiles++;
				}
			}
		}
		int numberOfPlayers = resourceCount.length;
		grasTiles = grasTiles / numberOfPlayers;
	}

	public int[] getBuildingCounts(byte playerId) {
		int numberOfPlayers = resourceCount.length;
		int neverland = resourceCount.length - 1;
		int maxFishermen = Math.max(1, Math.min(MAX_FISHERS, (int) Math.round(
				((resourceCount[neverland][EResourceType.FISH.ordinal] / numberOfPlayers) + resourceCount[playerId][EResourceType.FISH.ordinal])
						/ FISH_TO_FISHER_HUTS_RATIO)));
		int maxCoalMines = (int) Math.ceil((resourceCountOfAllPlayers(EResourceType.COAL) / numberOfPlayers) / COAL_TO_COAL_MINES_RATIO);
		int maxIronMines = (int) Math.ceil((resourceCountOfAllPlayers(EResourceType.IRONORE) / numberOfPlayers) /
				IRONORE_TO_IRON_MINES_RATIO);
		int maxGoldMelts = resourceCountOfAllPlayers(EResourceType.GOLDORE) > 0 ? 2 : 0;

		if (maxIronMines > maxCoalMines / COAL_MINE_TO_IRONORE_MINE_RATIO + 1)
			maxIronMines = (int) Math.ceil(maxCoalMines / COAL_MINE_TO_IRONORE_MINE_RATIO + 1);
		if (maxCoalMines > maxIronMines * COAL_MINE_TO_IRONORE_MINE_RATIO + 1)
			maxCoalMines = (int) Math.ceil(maxIronMines * COAL_MINE_TO_IRONORE_MINE_RATIO + 1);
		int maxSmiths = (int) Math.floor((float) maxCoalMines / COAL_MINE_TO_SMITH_RATIO);
		return calculateBuildingCounts(maxSmiths, maxFishermen, maxGoldMelts, 3, 1, grasTiles);
	}

	private long resourceCountOfAllPlayers(EResourceType resourceType) {
		long sum = 0;
		for (int i = 0; i < resourceCount.length; i++) {
			sum += resourceCount[i][resourceType.ordinal];
		}
		return sum;
	}

	private int[] calculateBuildingCounts(
			int numberOfWeaponSmiths, int maxFishermen, int maxGoldMelts, int maxWineGrower, int maxBigTemples, long grasTiles) {
		int[] buildingCounts = new int[EBuildingType.NUMBER_OF_BUILDINGS];
		for (int i = 0; i < buildingCounts.length; i++) {
			buildingCounts[i] = 0;
		}
		buildingCounts[EBuildingType.COALMINE.ordinal] = numberOfWeaponSmiths;

		buildingCounts[EBuildingType.IRONMINE.ordinal] = (int) Math.round(numberOfWeaponSmiths / COAL_MINE_TO_IRONORE_MINE_RATIO + 1);
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
			buildingCounts[EBuildingType.MILL.ordinal] =  (int) Math.ceil(numberOfFarms / FARM_TO_MILL_RATIO);
			buildingCounts[EBuildingType.WATERWORKS.ordinal] =  (int) Math.ceil(numberOfFarms / FARM_TO_WATERWORKS_RATIO);
			buildingCounts[EBuildingType.SLAUGHTERHOUSE.ordinal] =  (int) Math.ceil(numberOfFarms / FARM_TO_SLAUGHTER_RATIO);
			buildingCounts[EBuildingType.PIG_FARM.ordinal] =  (int) Math.ceil(numberOfFarms / FARM_TO_PIG_FARM_RATIO);

		int minLumberJacks = 8;
		if (numberOfWeaponSmiths < 4) {
			minLumberJacks = 3;
		}
		int numberOfLumberJacks = Math.max((int) (numberOfWeaponSmiths / WEAPON_SMITH_TO_LUMBERJACK_RATIO), minLumberJacks);
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

		if (maxWineGrower > 0) {
			buildingCounts[EBuildingType.WINEGROWER.ordinal] = maxWineGrower;
			buildingCounts[EBuildingType.TEMPLE.ordinal] = maxWineGrower;
		}

		if (isEnoughSpace(buildingCounts, grasTiles)) {
			return buildingCounts;
		} else if (numberOfWeaponSmiths > MIN_SMITHS_BEFORE_WINE_AND_GOLD_REDUCTION) {
			return calculateBuildingCounts(numberOfWeaponSmiths - 1, maxFishermen, maxGoldMelts, maxWineGrower, maxBigTemples, grasTiles);
		} else if (maxWineGrower > MIN_WINE_GROWER_BEFORE_GOLD_REDUCTION) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrower - 1, maxBigTemples, grasTiles);
		} else if (maxGoldMelts > 1) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts - 1, maxWineGrower, maxBigTemples, grasTiles);
		} else if (maxWineGrower > 1) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrower - 1, maxBigTemples, grasTiles);
		} else if (maxBigTemples == 1) {
			return calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrower, 0, grasTiles);
		} else {
			return calculateBuildingCounts(numberOfWeaponSmiths - 1, maxFishermen, maxGoldMelts, maxWineGrower, 0, grasTiles);
		}
	}

	private boolean isEnoughSpace(int[] buildingCounts, long grasTiles) {
		long grasTilesWithoutBuffer = Math.round(grasTiles / 1.4F);
		for (int i = 0; i < buildingCounts.length; i++) {
			EBuildingType buildingType = EBuildingType.values()[i];
			if (!buildingType.isMine()) {
				grasTilesWithoutBuffer -= EBuildingType.values()[i].getProtectedTiles().length * buildingCounts[i];
				if (grasTilesWithoutBuffer < 0) {
					return false;
				}
			}
		}

		return true;
	}
}
