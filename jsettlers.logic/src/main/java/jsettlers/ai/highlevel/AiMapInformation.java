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

import jsettlers.ai.construction.BuildingCount;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;

import java.util.ArrayList;
import java.util.List;

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

	private static final double FISH_TO_FISHER_HUTS_RATIO = 40F / 1F;
	private static final double COAL_TO_COAL_MINES_RATIO = 120F / 1F;
	private static final double IRONORE_TO_IRON_MINES_RATIO = 50F / 1F;
	private static final double COAL_MINE_TO_IRONORE_MINE_RATIO = 2F / 1F;
	private static final double WEAPON_SMITH_TO_BARRACKS_RATIO = 3F / 1F;
	private static final double WEAPON_SMITH_TO_FISHER_HUT_RATIO = 3F / 2F;
	private static final double WEAPON_SMITH_TO_FARM_RATIO = 1F / 2F;
	private static final double FARM_TO_BAKER_RATIO = 1F / 3F;
	private static final double FARM_TO_MILL_RATIO = 1F / 6F;
	private static final double FARM_TO_WATERWORKS_RATIO = 1F / 3F;
	private static final double FARM_TO_PIG_FARM_RATIO = 1F / 3F;
	private static final double FARM_TO_SLAUGHTER_RATIO = 1F / 6F;
	private static final double WEAPON_SMITH_TO_LUMBERJACK_RATIO = 6F / 8F;
	private static final double LUMBERJACK_TO_SAWMILL_RATIO = 1F / 2F;
	private static final double LUMBERJACK_TO_FORESTER_RATIO = 1F / 2F;
	private static final double LUMBERJACK_TO_STONE_CUTTER_RATIO = 5F / 8F;
	public static final float COAL_MINE_TO_SMITH_RATIO = 9F / 5F;
	private static final int MIN_SMITHS_BEFORE_WINE_AND_GOLD_REDUCTION = 10;
	private static final int MIN_WINE_GROWER_BEFORE_GOLD_REDUCTION = 2;
	// max 10 fisher to prevent AI from building only fishermen which on the one hand looks very unnatural and on the other hand is unproductive in
	// the late game caused by over fishing.
	public static final int MAX_FISHERS = 10;
	private List<BuildingCount> buildingCounts;
	private int numberOfFisher;
	private int numberOfWeaponSmiths;
	private int numberOfGoldMelts;
	private int numberOfFarms;
	private int numberOfWineGrower;
	private int numberOfBigTemples;
	private int numberOfLumberJacks;

	public int getNumberOfFisher() {
		return numberOfFisher;
	}

	public int getNumberOfWeaponSmiths() {
		return numberOfWeaponSmiths;
	}

	public int getNumberOfGoldMelts() {
		return numberOfGoldMelts;
	}

	public int getNumberOfFarms() {
		return numberOfFarms;
	}

	public int getNumberOfWineGrower() {
		return numberOfWineGrower;
	}

	public int getNumberOfBigTemples() {
		return numberOfBigTemples;
	}

	public int getNumberOfLumberJacks() {
		return numberOfLumberJacks;
	}

	public AiMapInformation(MainGrid mainGrid) {
		LandscapeGrid landscapeGrid = mainGrid.getLandscapeGrid();

		long[] resourcesCount = new long[EResourceType.values().length];
		for (int i = 0; i < resourcesCount.length; i++) {
			resourcesCount[i] = 0;
		}
		long grasTiles = 0;
		for (int x = 0; x < mainGrid.getWidth(); x++) {
			for (int y = 0; y < mainGrid.getHeight(); y++) {
				EResourceType resourceType = landscapeGrid.getResourceTypeAt(x, y);
				if (resourceType != EResourceType.FISH || landscapeGrid.getLandscapeTypeAt(x, y) == ELandscapeType.WATER1) {
					resourcesCount[resourceType.ordinal]++;
				}
				if (landscapeGrid.getLandscapeTypeAt(x, y).isGrass()) {
					grasTiles++;
				}
			}
		}
		int numberOfPlayers = mainGrid.getPartitionsGrid().getNumberOfPlayers();
		grasTiles = grasTiles / numberOfPlayers;
		int maxFishermen = Math.min(MAX_FISHERS, (int) Math.round((resourcesCount[EResourceType.FISH.ordinal] / numberOfPlayers) / FISH_TO_FISHER_HUTS_RATIO));
		int maxCoalMines = (int) Math.round((resourcesCount[EResourceType.COAL.ordinal] / numberOfPlayers) / COAL_TO_COAL_MINES_RATIO);
		int maxIronMines = (int) Math.round((resourcesCount[EResourceType.IRONORE.ordinal] / numberOfPlayers) / IRONORE_TO_IRON_MINES_RATIO);
		int maxGoldMelts = resourcesCount[EResourceType.GOLDORE.ordinal] > 0 ? 2 : 0;

		if (maxIronMines > maxCoalMines / COAL_MINE_TO_IRONORE_MINE_RATIO + 1)
			maxIronMines = (int) Math.round(maxCoalMines / COAL_MINE_TO_IRONORE_MINE_RATIO + 1);
		if (maxCoalMines > maxIronMines * COAL_MINE_TO_IRONORE_MINE_RATIO + 1)
			maxCoalMines = (int) Math.round(maxIronMines * COAL_MINE_TO_IRONORE_MINE_RATIO + 1);
		int maxSmiths = (int) Math.floor((float) maxCoalMines * COAL_MINE_TO_SMITH_RATIO);
		calculateBuildingCounts(maxSmiths, maxFishermen, maxGoldMelts, 3, 1, grasTiles);
	}

	public List<BuildingCount> getBuildingCounts() {
		return buildingCounts;
	}

	private void calculateBuildingCounts(
			int numberOfWeaponSmiths, int maxFishermen, int maxGoldMelts, int maxWineGrower, int maxBigTemples, long grasTiles) {
		List<BuildingCount> buildingCounts = new ArrayList<BuildingCount>();
		buildingCounts.add(new BuildingCount(EBuildingType.COALMINE, numberOfWeaponSmiths));
		buildingCounts.add(new BuildingCount(EBuildingType.IRONMINE, (int) Math.round(numberOfWeaponSmiths / COAL_MINE_TO_IRONORE_MINE_RATIO + 1)));
		buildingCounts.add(new BuildingCount(EBuildingType.IRONMELT, numberOfWeaponSmiths));
		buildingCounts.add(new BuildingCount(EBuildingType.WEAPONSMITH, numberOfWeaponSmiths));
		buildingCounts.add(new BuildingCount(EBuildingType.BARRACK, (int) Math.ceil((double) numberOfWeaponSmiths / WEAPON_SMITH_TO_BARRACKS_RATIO)));
		buildingCounts.add(new BuildingCount(EBuildingType.TOOLSMITH, 1));

		int numberOfFisher = Math.min((int) (numberOfWeaponSmiths * WEAPON_SMITH_TO_FISHER_HUT_RATIO), maxFishermen);
		buildingCounts.add(new BuildingCount(EBuildingType.FISHER, numberOfFisher));
		int numberOfRemainingWeaponSmithsRest = Math.max(0, numberOfWeaponSmiths - (int) (numberOfFisher / WEAPON_SMITH_TO_FISHER_HUT_RATIO));

		int numberOfFarms = (int) (numberOfRemainingWeaponSmithsRest * WEAPON_SMITH_TO_FARM_RATIO);
		buildingCounts.add(new BuildingCount(EBuildingType.FARM, numberOfFarms));
		buildingCounts.add(new BuildingCount(EBuildingType.BAKER, (int) (numberOfFarms * FARM_TO_BAKER_RATIO)));
		buildingCounts.add(new BuildingCount(EBuildingType.MILL, (int) (numberOfFarms * FARM_TO_MILL_RATIO)));
		buildingCounts.add(new BuildingCount(EBuildingType.WATERWORKS, (int) (numberOfFarms * FARM_TO_WATERWORKS_RATIO)));
		buildingCounts.add(new BuildingCount(EBuildingType.SLAUGHTERHOUSE, (int) (numberOfFarms * FARM_TO_SLAUGHTER_RATIO)));
		buildingCounts.add(new BuildingCount(EBuildingType.PIG_FARM, (int) (numberOfFarms * FARM_TO_PIG_FARM_RATIO)));

		int numberOfLumberJacks = Math.max((int) (numberOfWeaponSmiths * WEAPON_SMITH_TO_LUMBERJACK_RATIO), 3);
		buildingCounts.add(new BuildingCount(EBuildingType.LUMBERJACK, numberOfLumberJacks));
		buildingCounts.add(new BuildingCount(EBuildingType.FORESTER, Math.max((int) (numberOfLumberJacks * LUMBERJACK_TO_FORESTER_RATIO), 1)));
		buildingCounts.add(new BuildingCount(EBuildingType.SAWMILL, Math.max((int) (numberOfLumberJacks * LUMBERJACK_TO_SAWMILL_RATIO), 1)));
		buildingCounts.add(new BuildingCount(EBuildingType.STONECUTTER, Math.max((int) (numberOfLumberJacks * LUMBERJACK_TO_STONE_CUTTER_RATIO), 1)));

		if (maxGoldMelts > 0) {
			buildingCounts.add(new BuildingCount(EBuildingType.GOLDMELT, maxGoldMelts));
			buildingCounts.add(new BuildingCount(EBuildingType.GOLDMINE, 1));
		}

		if (maxBigTemples > 0) {
			buildingCounts.add(new BuildingCount(EBuildingType.BIG_TEMPLE, maxBigTemples));
		}

		if (maxWineGrower > 0) {
			buildingCounts.add(new BuildingCount(EBuildingType.WINEGROWER, maxWineGrower));
			buildingCounts.add(new BuildingCount(EBuildingType.TEMPLE, maxWineGrower));
		}

		if (isEnoughSpace(buildingCounts, grasTiles)) {
			this.buildingCounts = buildingCounts;
			this.numberOfFisher = numberOfFisher;
			this.numberOfGoldMelts = maxGoldMelts;
			this.numberOfWeaponSmiths = numberOfWeaponSmiths;
			this.numberOfFarms = numberOfFarms;
			this.numberOfWineGrower = maxWineGrower;
			this.numberOfBigTemples = maxBigTemples;
			this.numberOfLumberJacks = numberOfLumberJacks;
		} else if (numberOfWeaponSmiths > MIN_SMITHS_BEFORE_WINE_AND_GOLD_REDUCTION) {
			calculateBuildingCounts(numberOfWeaponSmiths - 1, maxFishermen, maxGoldMelts, maxWineGrower, maxBigTemples, grasTiles);
		} else if (maxWineGrower > MIN_WINE_GROWER_BEFORE_GOLD_REDUCTION) {
			calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrower - 1, maxBigTemples, grasTiles);
		} else if (maxGoldMelts > 1) {
			calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts - 1, maxWineGrower, maxBigTemples, grasTiles);
		} else if (maxWineGrower > 1) {
			calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrower - 1, maxBigTemples, grasTiles);
		} else if (maxBigTemples == 1) {
			calculateBuildingCounts(numberOfWeaponSmiths, maxFishermen, maxGoldMelts, maxWineGrower, 0, grasTiles);
		} else {
			calculateBuildingCounts(numberOfWeaponSmiths - 1, maxFishermen, maxGoldMelts, maxWineGrower, 0, grasTiles);
		}
	}

	private boolean isEnoughSpace(List<BuildingCount> buildingCounts, long grasTiles) {
		long grasTilesWithoutBuffer = Math.round(grasTiles / 2.5F);
		for (BuildingCount buildingCount : buildingCounts) {
			grasTilesWithoutBuffer -= buildingCount.buildingType.getProtectedTiles().length * buildingCount.count;
			if (grasTilesWithoutBuffer < 0) {
				return false;
			}
		}

		return true;
	}
}
