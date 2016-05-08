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

import jsettlers.ai.highlevel.AiMapInformation;
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
	private final Player player;
	private static final List<EBuildingType> MINIMAL_BUILDING_TYPES = Arrays.asList(LUMBERJACK, STONECUTTER, SAWMILL, SMALL_LIVINGHOUSE, FORESTER,
			LUMBERJACK, LUMBERJACK, STONECUTTER);

	public AdaptableEconomyMinister(AiStatistics aiStatistics, Player player) {
		this.aiStatistics = aiStatistics;
		this.player = player;
	}

	@Override
	public int getNumberOfParallelConstructionSites(AiStatistics aiStatistics, byte playerId) {
		return 5;
	}

	@Override
	public List<EBuildingType> getBuildingsToBuild(AiStatistics aiStatistics, byte playerId) {
		List<EBuildingType> buildingsToBuild = new Vector<>();
		buildingsToBuild.addAll(MINIMAL_BUILDING_TYPES);
		buildingsToBuild.addAll(determineNumberOf(LUMBERJACK));
		buildingsToBuild.addAll(determineNumberOf(STONECUTTER));
		buildingsToBuild.addAll(determineNumberOf(SAWMILL));
		buildingsToBuild.addAll(determineNumberOf(SMALL_LIVINGHOUSE));
		buildingsToBuild.addAll(determineNumberOf(FORESTER));
		buildingsToBuild.addAll(determineNumberOf(WINEGROWER));
		buildingsToBuild.addAll(determineNumberOf(FARM));
		buildingsToBuild.addAll(determineNumberOf(TEMPLE));
		buildingsToBuild.addAll(determineNumberOf(WATERWORKS));
		buildingsToBuild.addAll(determineNumberOf(MILL));
		buildingsToBuild.addAll(determineNumberOf(BAKER));
		buildingsToBuild.addAll(determineNumberOf(PIG_FARM));
		buildingsToBuild.addAll(determineNumberOf(SLAUGHTERHOUSE));
		buildingsToBuild.addAll(determineNumberOf(COALMINE));
		buildingsToBuild.addAll(determineNumberOf(IRONMINE));
		buildingsToBuild.addAll(determineNumberOf(IRONMELT));
		buildingsToBuild.addAll(determineNumberOf(WEAPONSMITH));
		buildingsToBuild.addAll(determineNumberOf(BARRACK));
		buildingsToBuild.addAll(determineNumberOf(FISHER));
		buildingsToBuild.addAll(determineNumberOf(GOLDMINE));
		buildingsToBuild.addAll(determineNumberOf(GOLDMELT));
		buildingsToBuild.addAll(determineNumberOf(BIG_TEMPLE));
		buildingsToBuild.addAll(determineNumberOf(CHARCOAL_BURNER));
		buildingsToBuild.addAll(determineNumberOf(DONKEY_FARM));
		return buildingsToBuild;
	}

	private Collection<EBuildingType> determineNumberOf(EBuildingType buildingType) {
		Collection<EBuildingType> numberOfBuildingsAsList = new Vector<EBuildingType>();
		List<Byte> enemies = aiStatistics.getEnemiesOf(player.playerId);
		byte sumOfBuildings = 0;
		for (byte playerId : enemies) {
			sumOfBuildings += aiStatistics.getTotalNumberOfBuildingTypeForPlayer(buildingType, playerId);
		}
		for (int i = 0; i < Math.max(1, sumOfBuildings / enemies.size()) - Collections.frequency(MINIMAL_BUILDING_TYPES, buildingType); i++) {
			numberOfBuildingsAsList.add(buildingType);
		}
		;
		return numberOfBuildingsAsList;
	}

	@Override
	public byte getMidGameNumberOfStoneCutters() {
		return 2;
	}

	@Override
	public boolean automaticTowersEnabled(AiStatistics aiStatistics, byte playerId) {
		return true;
	}

	@Override public boolean automaticLivingHousesEnabled(AiStatistics aiStatistics, byte playerId) {
		return true;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
