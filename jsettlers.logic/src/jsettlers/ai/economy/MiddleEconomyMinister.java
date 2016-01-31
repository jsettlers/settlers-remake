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

import jsettlers.common.buildings.EBuildingType;

import java.util.List;
import java.util.Vector;

import static jsettlers.common.buildings.EBuildingType.*;

/**
 * This economy minister is not well optimized. It is slowed down at all by just building 5 lumberjacks and 3 stonecutters. It one late and one
 * very late wine grower so that it gets late level upgrades. It builds 10 construction sides in parallel which makes it hard to react flexible to
 * lack of settlers which will slow down the computer player as well.
 *
 * @author codingberlin
 */
public class MiddleEconomyMinister implements EconomyMinister {

	private final List<EBuildingType> buildingsToBuild;

	public MiddleEconomyMinister() {
		buildingsToBuild = new Vector<EBuildingType>();
		initializeBuildingsToBuild();
	}

	private void initializeBuildingsToBuild() {
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(SAWMILL);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(FORESTER);
		buildingsToBuild.add(STONECUTTER);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(FORESTER);
		buildingsToBuild.add(LUMBERJACK);
		buildingsToBuild.add(SAWMILL);
		buildingsToBuild.add(FORESTER);
		buildingsToBuild.add(STONECUTTER);
		buildingsToBuild.add(STONECUTTER);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BARRACK);
		buildingsToBuild.add(WINEGROWER);
		buildingsToBuild.add(MILL);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(WATERWORKS);
		buildingsToBuild.add(PIG_FARM);
		buildingsToBuild.add(SLAUGHTERHOUSE);
		buildingsToBuild.add(TEMPLE);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(WINEGROWER);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(TEMPLE);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BIG_TEMPLE);
		buildingsToBuild.add(WATERWORKS);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BARRACK);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(FISHER);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(GOLDMINE);
		buildingsToBuild.add(GOLDMELT);
		buildingsToBuild.add(PIG_FARM);
		buildingsToBuild.add(MILL);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(WATERWORKS);
		buildingsToBuild.add(PIG_FARM);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BARRACK);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(FARM);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(COALMINE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(MILL);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(WATERWORKS);
		buildingsToBuild.add(PIG_FARM);
		buildingsToBuild.add(SLAUGHTERHOUSE);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(WATERWORKS);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(BAKER);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
		buildingsToBuild.add(IRONMELT);
		buildingsToBuild.add(WEAPONSMITH);
	}

	@Override
	public int getNumberOfParallelConstructionSides() {
		return 10;
	}

	@Override
	public List<EBuildingType> getBuildingsToBuild() {
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
