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
package jsettlers.algorithms.partitions;

import org.junit.Test;

import java.util.BitSet;

import jsettlers.logic.map.grid.MainGridDataAccessor;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.logic.map.loading.MapLoadException;
import jsettlers.logic.constants.MatchConstants;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;
import jsettlers.network.synchronic.timer.NetworkTimer;
import jsettlers.testutils.map.MapUtils;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionCalculatorAlgorithmSpeedTest {

	@Test
	public void testSpeed() throws MapLoadException, InterruptedException {
		MatchConstants.init(new NetworkTimer(true), 0);

		MainGrid grid = MapUtils.getBigMap().loadMainGrid(null).getMainGrid();
		MainGridDataAccessor gridAccessor = new MainGridDataAccessor(grid);

		short width = gridAccessor.getWidth();
		short height = gridAccessor.getHeight();
		BitSet notBlockingSet = new BitSet(width * height);
		LandscapeGrid landscapeGrid = gridAccessor.getLandscapeGrid();

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				notBlockingSet.set(x + y * width, !landscapeGrid.getLandscapeTypeAt(x, y).isBlocking);
			}
		}

		Thread.sleep(500L);

		MilliStopWatch watch = new MilliStopWatch();

		PartitionCalculatorAlgorithm partitioner = new PartitionCalculatorAlgorithm(0, 0, width, height, notBlockingSet,
				IBlockingProvider.DEFAULT_IMPLEMENTATION);
		partitioner.calculatePartitions();
		System.out.println("\n\n\n\nnumber of partitions: " + partitioner.getNumberOfPartitions());

		watch.stop("partitioning test needed:");
	}
}
