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

import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.utils.coordinates.ICoordinatePredicate;

import java.util.BitSet;

/**
 * Created by Andreas Eberle on 06.01.2017.
 */
public class DistancesCalculationAlgorithm {

	public static BitSet calculatePositionsInDistance(int width, int height, ICoordinatePredicate provider, int maxDistance) {
		int area = width * height;
		BitSet inDistance = new BitSet(area);
		BitSet next = new BitSet(area);

		setupInitial(width, height, provider, inDistance, next);
		// DebugImagesHelper.writeDebugImageBoolean("inDistance-" + 0, width, height, (x, y) -> inDistance.get(x + y * width));

		for (int distance = 1; distance <= maxDistance; distance++) {
			next.andNot(inDistance);
			BitSet current = next;
			if (current.isEmpty()) {
				break;
			}

			BitSet neighbors = new BitSet(area);
			for (int index = current.nextSetBit(0); index >= 0; index = current.nextSetBit(index + 1)) {
				int x = index % width;
				int y = index / width;

				MapNeighboursArea.stream(x, y).filterBounds(width, height).forEach((neighborX, neighborY) -> { // set neighbors for next run
					neighbors.set(width * neighborY + neighborX);
				});
			}

			next = neighbors;
			inDistance.or(current);

			// DebugImagesHelper.writeDebugImageBoolean("inDistance-" + distance, width, height, (x, y) -> inDistance.get(x + y * width));
		}
		return inDistance;
	}

	private static void setupInitial(int width, int height, ICoordinatePredicate provider, BitSet done, BitSet next) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (provider.test(x, y)) {
					done.set(width * y + x); // set as done

					MapNeighboursArea.stream(x, y).filterBounds(width, height).forEach((neighborX, neighborY) -> { // set neighbors for next run
						next.set(width * neighborY + neighborX);
					});
				}
			}
		}
	}
}
