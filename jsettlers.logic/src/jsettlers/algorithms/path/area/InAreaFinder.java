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
package jsettlers.algorithms.path.area;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.MatchConstants;

/**
 * 
 * 
 * NOTE: This class uses random.Random! Therefore it needs to run synchronous to avoid inconsistencies between multiple players!
 * 
 * @author Andreas Eberle
 * 
 */
public final class InAreaFinder {
	private final IInAreaFinderMap map;
	private final short width;
	private final short height;

	public InAreaFinder(IInAreaFinderMap map, short width, short height) {
		this.map = map;
		this.width = width;
		this.height = height;
	}

	/**
	 * 
	 * @param requester
	 * @param centerX
	 * @param centerY
	 * @param searchRadius
	 * @param searched
	 * @return an SPoint2D object if the searched thing has been found<br>
	 *         null if it hasn't been found.
	 */
	public final ShortPoint2D find(IPathCalculatable requester, short centerX, short centerY, short searchRadius, ESearchType searched) {

		for (int i = 0; i < 100; i++) {
			double angle = MatchConstants.random().nextDouble() * 2 * Math.PI; // get an angle in the interval [0, 2PI]
			double radius = Math.pow(MatchConstants.random().nextDouble(), 3.9) * searchRadius; // get a radius in the interval [0, pixelRadius]

			short tileX = (short) (Math.cos(angle) * radius + centerX);
			short tileY = (short) (Math.sin(angle) * radius + centerY);

			if (isInBounds(tileX, tileY) && !map.isBlocked(requester, tileX, tileY) && map.fitsSearchType(tileX, tileY, searched, requester)) {
				return new ShortPoint2D(tileX, tileY);
			}
		}
		return null;
	}

	private final boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}
}
