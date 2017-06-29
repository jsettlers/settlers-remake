/*******************************************************************************
 * Copyright (c) 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.algorithms.distances;

import static jsettlers.common.buildings.EBuildingType.FISHER;
import static org.junit.Assert.assertTrue;

import java.util.BitSet;

import org.junit.BeforeClass;
import org.junit.Test;

import jsettlers.common.logging.MilliStopWatch;
import jsettlers.logic.map.loading.data.IMapData;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.testutils.DebugImagesHelper;
import jsettlers.common.utils.coordinates.ICoordinatePredicate;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.main.swing.resources.SwingResourceLoader;

/**
 * Created by Andreas Eberle on 06.01.2017.
 */

public class DistanceCalculationAlgorithmTest {

	@BeforeClass
	public static void setupTest() {
		// DebugImagesHelper.DEBUG_IMAGES_ENABLED = true;

		SwingResourceLoader.setupMapListFactory("../maps", null);
		DebugImagesHelper.setupDebugging();
	}

	@Test
	public void testDistanceCalculation() throws MapLoadException {
		MapLoader mapLoader = MapList.getDefaultList().getMapByName("mountain lake");

		IMapData mapData = mapLoader.getMapData();
		int width = mapData.getWidth();
		int height = mapData.getHeight();
		int testDistance = FISHER.getWorkRadius();

		MilliStopWatch stopWatch = new MilliStopWatch();
		BitSet actual = DistancesCalculationAlgorithm.calculatePositionsInDistance(width, height, (x, y) -> mapData.getLandscape(x, y).isWater,
				testDistance);
		stopWatch.stop("calculatePositionsInDistance");

		stopWatch.restart();
		BitSet expected = calculatePositionsInDistanceTrivial(width, height, (x, y) -> mapData.getLandscape(x, y).isWater, testDistance);
		stopWatch.stop("calculatePositionsInDistance");

		DebugImagesHelper.writeDebugImageBoolean("actual", width, height, (x, y) -> actual.get(x + y * width));
		DebugImagesHelper.writeDebugImageBoolean("expected", width, height, (x, y) -> expected.get(x + y * width));

		expected.xor(actual);
		DebugImagesHelper.writeDebugImageBoolean("difference", width, height, (x, y) -> expected.get(x + y * width));

		assertTrue("there exists a difference between actual and expected", expected.isEmpty());
	}

	private BitSet calculatePositionsInDistanceTrivial(int width, int height, ICoordinatePredicate provider, int maxDistance) {
		BitSet inDistance = new BitSet();

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (provider.test(x, y)) {
					HexGridArea.stream(x, y, 0, maxDistance)
							.filterBounds(width, height)
							.forEach((currX, currY) -> inDistance.set(currY * width + currX));
				}
			}
		}

		return inDistance;
	}
}
