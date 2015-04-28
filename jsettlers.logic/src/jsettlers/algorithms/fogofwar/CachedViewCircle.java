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
package jsettlers.algorithms.fogofwar;

import jsettlers.common.CommonConstants;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapCircleIterator;

/**
 * Caches a {@link MapCircle} and the calculated view distances of the circles positions.
 * 
 * @author Andreas Eberle
 * 
 */
public final class CachedViewCircle {

	final short[] x;
	final short[] y;
	final byte[] sight;
	final int size;

	public CachedViewCircle(int radius) {
		radius -= FogOfWar.PADDING / 2;
		MapCircle circle = new MapCircle(0, 0, radius + FogOfWar.PADDING);

		size = countElements(circle);

		x = new short[size];
		y = new short[size];
		sight = new byte[size];

		MapCircleIterator iter = circle.iterator();
		final float squaredViewDistance = radius * radius;
		int i = 0;

		while (iter.hasNext()) {
			int y = iter.nextY();
			int x = iter.nextX();
			this.x[i] = (short) x;
			this.y[i] = (short) y;

			double squaredDistance = MapCircle.getSquaredDistance(x, y);
			byte newSight;
			if (squaredDistance < squaredViewDistance) {
				newSight = CommonConstants.FOG_OF_WAR_VISIBLE;
			} else {
				newSight = (byte) (CommonConstants.FOG_OF_WAR_VISIBLE - (Math.sqrt(squaredDistance) - radius) / FogOfWar.PADDING
						* CommonConstants.FOG_OF_WAR_VISIBLE);
			}
			sight[i] = newSight;

			i++;
		}
	}

	private int countElements(MapCircle circle) {
		int counter = 0;
		MapCircleIterator iter = circle.iterator();
		while (iter.hasNext()) { // count the elements in the circle to create array
			iter.nextX();
			counter++;
		}

		return counter;
	}

	public CachedViewCircleIterator iterator(int xOffset, int yOffset) {
		return new CachedViewCircleIterator(xOffset, yOffset);
	}

	public final class CachedViewCircleIterator {
		private final int xOffset;
		private final int yOffset;

		private int idx;

		public CachedViewCircleIterator(int xOffset, int yOffset) {
			this.xOffset = xOffset;
			this.yOffset = yOffset;
		}

		public boolean hasNext() {
			return ++idx < size;
		}

		public int getCurrX() {
			return x[idx] + xOffset;
		}

		public int getCurrY() {
			return y[idx] + yOffset;
		}

		public byte getCurrSight() {
			return sight[idx];
		}
	}
}
