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
 * This class calculates information about the map for the AI
 * 
 * @author codingberlin
 */
public class AiMapInformation {

	private static final short FISHERMAN_DIVISOR = 27;
	private static final short COAL_MINE_DIVISOR = 120;
	private static final short IRON_MINE_DIVISOR = 50;
	private static final int COAL_TO_IRON_FACTOR = 2;
	private static final double WEAPON_SMITH_BARRACKS_RATIO = 3;
	private static final double WEAPON_SMITH_FISHER_RATIO = 3F / 2F;
	private static final double WEAPON_SMITH_FARM_RATIO = 1F / 2F;
	private static final double FARM_BAKER_RATIO = 1F / 3F;
	private static final double FARM_MILL_RATIO = 1F / 6F;
	private static final double FARM_WATERWORKS_RATIO = 1F / 3F;
	private static final double FARM_PIG_FARM_RATIO = 1F / 3F;
	private static final double FARM_SLAUGHTER_RATIO = 1F / 6F;
	private static final int MIN_SMITHS_BEFORE_WINE_AND_GOLD_REDUCTION = 10;
	private static final int MIN_WINE_GROWER_BEFORE_GOLD_REDUCTION = 2;
	private static final double WEAPON_SMITH_LUMBERJACK_RATIO = 6F / 8F;
	private static final double LUMBERJACK_SAWMILL_RATIO = 1F / 2F;
	private static final double LUMBERJACK_FORESTER_RATIO = 1F / 2F;
	private static final double LUMBERJACK_STONE_CUTTER_RATIO = 5F / 8F;
	public static final float COAL_MINE_TO_SMITH_RATIO = 1.8F;
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
		// maximal 10 Fisher to prevent AI from building only Fishermen which looks very unnatural
		int maxFishermen = Math.min(10, (int) (resourcesCount[EResourceType.FISH.ordinal] / numberOfPlayers) / FISHERMAN_DIVISOR);
		int maxCoalMines = (int) (resourcesCount[EResourceType.COAL.ordinal] / numberOfPlayers) / COAL_MINE_DIVISOR;
		int maxIronMines = (int) (resourcesCount[EResourceType.IRONORE.ordinal] / numberOfPlayers) / IRON_MINE_DIVISOR;
		int maxGoldMelts = resourcesCount[EResourceType.GOLDORE.ordinal] > 0 ? 2 : 0;

		if (maxIronMines > maxCoalMines / COAL_TO_IRON_FACTOR + 1)
			maxIronMines = maxCoalMines / COAL_TO_IRON_FACTOR + 1;
		if (maxCoalMines > maxIronMines * COAL_TO_IRON_FACTOR + 1)
			maxCoalMines = maxIronMines * COAL_TO_IRON_FACTOR + 1;
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
		buildingCounts.add(new BuildingCount(EBuildingType.IRONMINE, numberOfWeaponSmiths / COAL_TO_IRON_FACTOR + 1));
		buildingCounts.add(new BuildingCount(EBuildingType.IRONMELT, numberOfWeaponSmiths));
		buildingCounts.add(new BuildingCount(EBuildingType.WEAPONSMITH, numberOfWeaponSmiths));
		buildingCounts.add(new BuildingCount(EBuildingType.BARRACK, (int) Math.ceil((double) numberOfWeaponSmiths / WEAPON_SMITH_BARRACKS_RATIO)));
		buildingCounts.add(new BuildingCount(EBuildingType.TOOLSMITH, 1));

		int numberOfFisher = Math.min((int) (numberOfWeaponSmiths * WEAPON_SMITH_FISHER_RATIO), maxFishermen);
		buildingCounts.add(new BuildingCount(EBuildingType.FISHER, numberOfFisher));
		int numberOfRemainingWeaponSmithsRest = Math.max(0, numberOfWeaponSmiths - (int) (numberOfFisher / WEAPON_SMITH_FISHER_RATIO));

		int numberOfFarms = (int) (numberOfRemainingWeaponSmithsRest * WEAPON_SMITH_FARM_RATIO);
		int minimumFood = 0;
		if (numberOfFarms == 0 && numberOfWeaponSmiths >= 1) {
			numberOfFarms++;
			minimumFood = 1;
		}
		buildingCounts.add(new BuildingCount(EBuildingType.FARM, numberOfFarms));
		buildingCounts.add(new BuildingCount(EBuildingType.BAKER, Math.max(minimumFood, (int) (numberOfFarms * FARM_BAKER_RATIO))));
		buildingCounts.add(new BuildingCount(EBuildingType.MILL, Math.max(minimumFood, (int) (numberOfFarms * FARM_MILL_RATIO))));
		buildingCounts.add(new BuildingCount(EBuildingType.WATERWORKS, Math.max(minimumFood, (int) (numberOfFarms * FARM_WATERWORKS_RATIO))));
		buildingCounts.add(new BuildingCount(EBuildingType.SLAUGHTERHOUSE, Math.max(minimumFood, (int) (numberOfFarms * FARM_SLAUGHTER_RATIO))));
		buildingCounts.add(new BuildingCount(EBuildingType.PIG_FARM, Math.max(minimumFood, (int) (numberOfFarms * FARM_PIG_FARM_RATIO))));

		int numberOfLumberJacks = Math.max((int) (numberOfWeaponSmiths * WEAPON_SMITH_LUMBERJACK_RATIO), 1);
		buildingCounts.add(new BuildingCount(EBuildingType.LUMBERJACK, numberOfLumberJacks));
		buildingCounts.add(new BuildingCount(EBuildingType.FORESTER, Math.max((int) (numberOfLumberJacks * LUMBERJACK_FORESTER_RATIO), 1)));
		buildingCounts.add(new BuildingCount(EBuildingType.SAWMILL, Math.max((int) (numberOfLumberJacks * LUMBERJACK_SAWMILL_RATIO), 1)));
		buildingCounts.add(new BuildingCount(EBuildingType.STONECUTTER, Math.max((int) (numberOfLumberJacks * LUMBERJACK_STONE_CUTTER_RATIO), 1)));

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
		long grasTilesWithoutBuffer = grasTiles / 6;
		for (BuildingCount buildingCount : buildingCounts) {
			grasTilesWithoutBuffer -= buildingCount.buildingType.getProtectedTiles().length * buildingCount.count;
			if (grasTilesWithoutBuffer < 0) {
				return false;
			}
		}

		return true;
	}

	public int getNumberOfLumberJacks() {
		return numberOfLumberJacks;
	}
}
