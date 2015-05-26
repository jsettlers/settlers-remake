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
package jsettlers.logic.map.newGrid.partition;

import jsettlers.common.logging.MilliStopWatch;

/**
 * This class implements a Thread that periodically checks if there are to much merged partitions on the grid. Merged partitions aren't needed an so,
 * this thread normalizes the grid by replacing the real partitions with their representatives.
 * 
 * @author Andreas Eberle
 * 
 */
final class PartitionsGridNormalizerThread extends Thread {
	private static final int MERGED_PARTITIONS_THRESHOLD = 700;
	private static final int CHECK_DELAY_MS = 5000;

	private final PartitionsGrid grid;
	private boolean running = true;

	/**
	 * This constructor creates a new {@link PartitionsGridNormalizerThread} that's working on the given PartitionsGrid and uses the given object as
	 * synchronization lock when writing the partitions or the partition objects.
	 * 
	 * @param grid
	 */
	PartitionsGridNormalizerThread(PartitionsGrid grid) {
		super("PartitionsGridNormalizer");
		this.grid = grid;

		super.setDaemon(true);
	}

	public void cancel() {
		running = false;
		this.interrupt();
	}

	@Override
	public void run() {
		MilliStopWatch milliWatch = new MilliStopWatch();

		while (running) {
			try {
				Thread.sleep(CHECK_DELAY_MS);
			} catch (InterruptedException e) {
			}

			milliWatch.restart();

			int normlizedPartitions = grid.checkNormalizePartitions(MERGED_PARTITIONS_THRESHOLD);

			System.out.println("PartitionsGridNormalizerThread: NORMALIZED " + normlizedPartitions + " partitions in " + milliWatch.getDiff()
					+ "ms!-----------------------------------");
		}
	}

}
