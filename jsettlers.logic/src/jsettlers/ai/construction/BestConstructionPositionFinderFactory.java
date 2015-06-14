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
package jsettlers.ai.construction;

import static jsettlers.common.buildings.EBuildingType.BIG_TOWER;
import static jsettlers.common.buildings.EBuildingType.CASTLE;
import static jsettlers.common.buildings.EBuildingType.FORESTER;
import static jsettlers.common.buildings.EBuildingType.LUMBERJACK;
import static jsettlers.common.buildings.EBuildingType.SAWMILL;
import static jsettlers.common.buildings.EBuildingType.STONECUTTER;
import static jsettlers.common.buildings.EBuildingType.TOWER;
import jsettlers.common.buildings.EBuildingType;

public class BestConstructionPositionFinderFactory {

	public final IBestConstructionPositionFinder getBestConstructionPositionFinderFor(EBuildingType type) {
		if (type == STONECUTTER) {
			return new BestStoneCutterConstructionPositionFinder(type);
		}
		if (type == LUMBERJACK) {
			return new BestLumberJackConstructionPositionFinder(type);
		}
		if (type == FORESTER) {
			return new BestForesterConstructionPositionFinder(type);
		}
		if (type == SAWMILL) {
			return new BestSawMillConstructionPositionFinder(type);
		}
		if (type == TOWER || type == BIG_TOWER || type == CASTLE) {
			return new BestMilitaryConstructionPositionFinder(type);
		}

		return new NearDiggersConstructionPositionFinder(type);
	}

}
