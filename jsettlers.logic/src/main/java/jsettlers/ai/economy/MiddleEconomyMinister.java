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

import jsettlers.ai.construction.BuildingCount;
import jsettlers.ai.highlevel.AiMapInformation;
import jsettlers.ai.highlevel.AiStatistics;
import jsettlers.common.buildings.EBuildingType;

import java.util.List;

import static jsettlers.common.buildings.EBuildingType.*;

/**
 * This economy minister is not well optimized. It is slowed down at all by just building 5 lumberjacks and 3 stonecutters. It one late and one very
 * late wine grower so that it gets late level upgrades. It builds 10 construction sides in parallel which makes it hard to react flexible to lack of
 * settlers which will slow down the computer player as well.
 *
 * @author codingberlin
 */
public class MiddleEconomyMinister extends BuildingListEconomyMinister implements EconomyMinister {


	public MiddleEconomyMinister(AiMapInformation mapInformation) {
		super();
		initializeBuildingsToBuild(mapInformation.getBuildingCounts());
	}

	private void initializeBuildingsToBuild(List<BuildingCount> buildingCounts) {
		addIfPossible(LUMBERJACK, buildingCounts);
		addIfPossible(LUMBERJACK, buildingCounts);
		addIfPossible(STONECUTTER, buildingCounts);
		addIfPossible(FORESTER, buildingCounts);
		addIfPossible(SAWMILL, buildingCounts);
		addIfPossible(LUMBERJACK, buildingCounts);
		addIfPossible(STONECUTTER, buildingCounts);
		addIfPossible(LUMBERJACK, buildingCounts);
		addIfPossible(LUMBERJACK, buildingCounts);
		addIfPossible(SAWMILL, buildingCounts);
		addIfPossible(FORESTER, buildingCounts);
		addIfPossible(FORESTER, buildingCounts);
		addIfPossible(STONECUTTER, buildingCounts);
		addIfPossible(FISHER, buildingCounts);
		addIfPossible(IRONMINE, buildingCounts);
		addIfPossible(COALMINE, buildingCounts);
		addIfPossible(IRONMELT, buildingCounts);
		addIfPossible(WEAPONSMITH, buildingCounts);
		addIfPossible(BARRACK, buildingCounts);
		addIfPossible(WINEGROWER, buildingCounts);
		addIfPossible(TEMPLE, buildingCounts);
		addBuildingMaterialIndustry(buildingCounts);
		addFoodIndustry(buildingCounts);
		addManaIndustry(buildingCounts);
		addWeaponsIndustry(buildingCounts);
	}

	private void addBuildingMaterialIndustry(List<BuildingCount> buildingCounts) {
		while (currentCountOf(LUMBERJACK) < plannedCountOf(LUMBERJACK, buildingCounts)) {
			addIfPossible(FORESTER, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(LUMBERJACK, buildingCounts);
			addIfPossible(SAWMILL, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
			addIfPossible(STONECUTTER, buildingCounts);
		}
	}

	private void addFoodIndustry(List<BuildingCount> buildingCounts) {
		while (currentCountOf(FISHER) < plannedCountOf(FISHER, buildingCounts)) {
			addIfPossible(FISHER, buildingCounts);
		}
		while (currentCountOf(FARM) < plannedCountOf(FARM, buildingCounts)) {
			addIfPossible(FARM, buildingCounts);
			addIfPossible(WATERWORKS, buildingCounts);
			addIfPossible(MILL, buildingCounts);
			addIfPossible(BAKER, buildingCounts);
			addIfPossible(FARM, buildingCounts);
			addIfPossible(PIG_FARM, buildingCounts);
			addIfPossible(SLAUGHTERHOUSE, buildingCounts);
			addIfPossible(FARM, buildingCounts);
			addIfPossible(BAKER, buildingCounts);
			addIfPossible(WATERWORKS, buildingCounts);
			addIfPossible(BAKER, buildingCounts);
		}

	}
	private void addManaIndustry(List<BuildingCount> buildingCounts) {
		while (currentCountOf(WINEGROWER) < plannedCountOf(WINEGROWER, buildingCounts)) {
			addIfPossible(WINEGROWER, buildingCounts);
			addIfPossible(TEMPLE, buildingCounts);
		}
		addIfPossible(BIG_TEMPLE, buildingCounts);
	}
	private void addWeaponsIndustry(List<BuildingCount> buildingCounts) {
		while (currentCountOf(WEAPONSMITH) < plannedCountOf(WEAPONSMITH, buildingCounts)) {
			addIfPossible(COALMINE, buildingCounts);
			addIfPossible(IRONMINE, buildingCounts);
			addIfPossible(IRONMELT, buildingCounts);
			addIfPossible(WEAPONSMITH, buildingCounts);
			addIfPossible(COALMINE, buildingCounts);
			addIfPossible(IRONMELT, buildingCounts);
			addIfPossible(WEAPONSMITH, buildingCounts);
			addIfPossible(BARRACK, buildingCounts);
			addIfPossible(GOLDMINE, buildingCounts);
			addIfPossible(GOLDMELT, buildingCounts);
		}
	}

	@Override
	public int getNumberOfParallelConstructionSides(AiStatistics aiStatistics, byte playerId) {
		return 10;
	}

	@Override
	public List<EBuildingType> getBuildingsToBuild(AiStatistics aiStatistics, byte playerId) {
		return buildingsToBuild;
	}

	@Override
	public byte getMidGameNumberOfStoneCutters() {
		return 3;
	}

	@Override
	public String toString() {
		return this.getClass().getName();
	}
}
