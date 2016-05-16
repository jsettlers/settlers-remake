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
package jsettlers.ai.economy;

import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.logic.player.Player;

import java.util.*;

import static jsettlers.common.buildings.EBuildingType.*;

/**
 * This economy minister checks how many buildings of each type its enemies did build and build one building less than the player with the fewest
 * number of that building. But it builds minimal 1 building for each building type. This leads to an adaptive AI which can be beaten by the weakest
 * player in the game.
 * 
 * @author codingberlin
 */
public class AdaptableEconomyMinister implements EconomyMinister {

	private final AiStatistics aiStatistics;
	private final byte playerId;
	private static final List<EBuildingType> MINIMAL_BUILDING_TYPES = Arrays.asList(LUMBERJACK, STONECUTTER, SAWMILL, SMALL_LIVINGHOUSE, FORESTER,
			LUMBERJACK, LUMBERJACK, STONECUTTER);

	public AdaptableEconomyMinister(AiStatistics aiStatistics, Player player) {
		this.aiStatistics = aiStatistics;
		this.playerId = player.playerId;
	}

	@Override
	public int getNumberOfParallelConstructionSites() {
		return 5;
	}

	@Override
	public List<EBuildingType> getBuildingsToBuild() {
		List<EBuildingType> buildingsToBuild = new Vector<>();
		buildingsToBuild.addAll(MINIMAL_BUILDING_TYPES);
		buildingsToBuild.addAll(buildListOf(LUMBERJACK));
		buildingsToBuild.addAll(buildListOf(STONECUTTER));
		buildingsToBuild.addAll(buildListOf(SAWMILL));
		buildingsToBuild.addAll(buildListOf(SMALL_LIVINGHOUSE));
		buildingsToBuild.addAll(buildListOf(FORESTER));
		buildingsToBuild.addAll(buildListOf(WINEGROWER));
		buildingsToBuild.addAll(buildListOf(FARM));
		buildingsToBuild.addAll(buildListOf(TEMPLE));
		buildingsToBuild.addAll(buildListOf(WATERWORKS));
		buildingsToBuild.addAll(buildListOf(MILL));
		buildingsToBuild.addAll(buildListOf(BAKER));
		buildingsToBuild.addAll(buildListOf(PIG_FARM));
		buildingsToBuild.addAll(buildListOf(SLAUGHTERHOUSE));
		buildingsToBuild.addAll(buildListOf(COALMINE));
		buildingsToBuild.addAll(buildListOf(IRONMINE));
		buildingsToBuild.addAll(buildListOf(IRONMELT));
		buildingsToBuild.addAll(buildListOf(WEAPONSMITH));
		buildingsToBuild.addAll(buildListOf(BARRACK));
		buildingsToBuild.addAll(buildListOf(FISHER));
		buildingsToBuild.addAll(buildListOf(GOLDMINE));
		buildingsToBuild.addAll(buildListOf(GOLDMELT));
		buildingsToBuild.addAll(buildListOf(BIG_TEMPLE));
		buildingsToBuild.addAll(buildListOf(CHARCOAL_BURNER));
		buildingsToBuild.addAll(buildListOf(DONKEY_FARM));
		return buildingsToBuild;
	}

	private Collection<EBuildingType> buildListOf(EBuildingType buildingType) {
		Collection<EBuildingType> numberOfBuildingsAsList = new Vector<EBuildingType>();;
		int averageSumOfBuildings = determineNumberOf(buildingType);
		for (int i = 0; i < averageSumOfBuildings - Collections.frequency(MINIMAL_BUILDING_TYPES, buildingType); i++) {
			numberOfBuildingsAsList.add(buildingType);
		}
		return numberOfBuildingsAsList;
	}

	private int determineNumberOf(EBuildingType buildingType) {
		List<Byte> enemies = aiStatistics.getEnemiesOf(playerId);
		float sumOfBuildings = 0;
		for (byte playerId : enemies) {
			sumOfBuildings += aiStatistics.getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId);
		}
		return Math.max(1, (int) (sumOfBuildings / enemies.size()));
	}

	@Override
	public int getMidGameNumberOfStoneCutters() {
		return 2;
	}

	@Override
	public boolean automaticTowersEnabled() {
		return true;
	}

	@Override public boolean automaticLivingHousesEnabled() {
		return true;
	}

	@Override public void update() {
		// nothing to update
	}

	@Override
	public boolean isEndGame() {
		return false;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
