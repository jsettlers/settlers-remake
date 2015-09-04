/**
 * ****************************************************************************
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
 * *****************************************************************************
 */
package jsettlers.ai.construction;

import static jsettlers.common.buildings.EBuildingType.FARM;
import static jsettlers.common.buildings.EBuildingType.GOLDMELT;
import static jsettlers.common.buildings.EBuildingType.GOLDMINE;
import static jsettlers.common.buildings.EBuildingType.IRONMELT;
import static jsettlers.common.buildings.EBuildingType.IRONMINE;
import static jsettlers.common.buildings.EBuildingType.LUMBERJACK;
import static jsettlers.common.buildings.EBuildingType.MILL;
import static jsettlers.common.buildings.EBuildingType.PIG_FARM;
import static jsettlers.common.buildings.EBuildingType.WEAPONSMITH;
import static jsettlers.common.buildings.EBuildingType.WINEGROWER;
import static jsettlers.common.landscape.EResourceType.COAL;
import static jsettlers.common.landscape.EResourceType.GOLD;
import static jsettlers.common.landscape.EResourceType.IRON;
import jsettlers.common.buildings.EBuildingType;

public class BestConstructionPositionFinderFactory {

	public final IBestConstructionPositionFinder getBestConstructionPositionFinderFor(EBuildingType type) {
		switch (type) {
		case STONECUTTER:
			return new BestStoneCutterConstructionPositionFinder(type);
		case LUMBERJACK:
			return new BestLumberJackConstructionPositionFinder(type);
		case FORESTER:
			return new BestForesterConstructionPositionFinder(type);
		case SAWMILL:
			return new NearNeededBuildingConstructionPositionFinder(type, LUMBERJACK);
		case TOWER:
		case BIG_TOWER:
		case CASTLE:
			return new BestMilitaryConstructionPositionFinder(type);
		case FARM:
		case WINEGROWER:
			return new BestFarmAndWineGrowerConstructionPositionFinder(type);
		case COALMINE:
			return new BestMineConstructionPositionFinder(type, COAL);
		case IRONMINE:
			return new BestMineConstructionPositionFinder(type, IRON);
		case GOLDMINE:
			return new BestMineConstructionPositionFinder(type, GOLD);
		case WATERWORKS:
			return new BestWaterWorksConstructionPositionFinder(type);
		case IRONMELT:
			return new NearNeededBuildingConstructionPositionFinder(type, IRONMINE);
		case WEAPONSMITH:
			return new NearNeededBuildingConstructionPositionFinder(type, IRONMELT);
		case TOOLSMITH:
			return new NearNeededBuildingConstructionPositionFinder(type, IRONMELT);
		case BARRACK:
			return new NearNeededBuildingConstructionPositionFinder(type, WEAPONSMITH);
		case MILL:
			return new NearNeededBuildingConstructionPositionFinder(type, FARM);
		case BAKER:
			return new NearNeededBuildingConstructionPositionFinder(type, MILL);
		case PIG_FARM:
			return new NearNeededBuildingConstructionPositionFinder(type, FARM);
		case SLAUGHTERHOUSE:
			return new NearNeededBuildingConstructionPositionFinder(type, PIG_FARM);
		case TEMPLE:
			return new NearNeededBuildingConstructionPositionFinder(type, WINEGROWER);
		case GOLDMELT:
			return new NearNeededBuildingConstructionPositionFinder(type, GOLDMINE);
		case FISHER:
			return new BestFisherConstructionPositionFinder(type);
		case STOCK:
			return new NearNeededBuildingConstructionPositionFinder(type, GOLDMELT);
		default:
			return new NearDiggersConstructionPositionFinder(type);
		}
	}

}
