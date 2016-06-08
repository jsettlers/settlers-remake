/*******************************************************************************
 * Copyright (c) 2015, 2016
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
package jsettlers.algorithms.construction;

import jsettlers.algorithms.AlgorithmConstants;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.logging.StopWatch;
import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.network.client.interfaces.IPausingSupplier;

/**
 * Thread to calculate the markings for the user if he want's to construct a new building.<br>
 * This is a singleton class.
 *
 * @author Andreas Eberle
 *
 */
public final class ConstructionMarksThread implements Runnable {

	private final NewConstructionMarksAlgorithm algorithm;
	private final IPausingSupplier pausingSupplier;
	private final Thread thread;

	private boolean canceled;

	/**
	 * area of tiles to be checked.
	 */
	private MapRectangle mapArea = null;
	private EBuildingType buildingType = null;

	public ConstructionMarksThread(AbstractConstructionMarkableMap map, IPausingSupplier pausingSupplier, byte player) {
		this.algorithm = new NewConstructionMarksAlgorithm(map, player);
		this.pausingSupplier = pausingSupplier;

		thread = new Thread(this, "ConstructionMarksThread");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public void run() {
		while (!canceled) {
			try {
				synchronized (this) {
					while (buildingType == null) {
						this.wait();
					}
				}

				while (buildingType != null && !canceled) {
					if (!pausingSupplier.isPausing()) {
						StopWatch watch = new MilliStopWatch();
						watch.restart();

						EBuildingType buildingType = this.buildingType;
						if (buildingType != null && mapArea != null) { // if the task has already been canceled
							algorithm.calculateConstructMarks(mapArea, buildingType);
						}

						watch.stop("calculation of construction marks");
					}
					synchronized (this) {
						wait(AlgorithmConstants.CONSTRUCT_MARKS_MAX_REFRESH_TIME);
					}
				}
				algorithm.removeConstructionMarks();
			} catch (InterruptedException e) {
				// do nothing
			} catch (Throwable t) { // this thread must never be destroyed due to errors
				t.printStackTrace();
			}
		}
	}

	public synchronized void setScreen(MapRectangle mapArea) {
		this.mapArea = mapArea;
		this.notifyAll();
	}

	public synchronized void setBuildingType(EBuildingType buildingType) {
		this.buildingType = buildingType;
		this.notifyAll();
	}

	public void cancel() {
		canceled = true;
		thread.interrupt();
	}
}
