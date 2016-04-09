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
package jsettlers.common.position;

import java.util.List;

/**
 * Interface offering methods to get the {@link ILocatable} of a list of {@link ILocatable}s that's closest to a given target.<br>
 * It also specifies a heuristic for the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ILocatable {
	ShortPoint2D getPos();

	static class Methods {
		/**
		 * returns the index of the {@link ILocatable} in the list that's closest to the target.
		 * 
		 * @param toBeCompared
		 *            list of {@link ILocatable} to be checked.
		 * @param target
		 *            target position
		 * @return index of the closest {@link ILocatable} on the list.
		 */
		public static int getNearest(List<? extends ILocatable> toBeCompared, ShortPoint2D target) {
			int closestIdx = 0;
			float closestDist = Short.MAX_VALUE;

			short tx = target.x;
			short ty = target.y;

			int idx = 0;
			for (ILocatable curr : toBeCompared) {
				ShortPoint2D currPos = curr.getPos();
				float currHeu = getHeuristic(currPos, tx, ty);
				if (currHeu < closestDist) {
					closestDist = currHeu;
					closestIdx = idx;
				}
				idx++;
			}

			return closestIdx;
		}

		public static float getHeuristic(ShortPoint2D pos, short tx, short ty) {
			return (float) Math.hypot(pos.x - tx, pos.y - ty);
		}
	}
}
