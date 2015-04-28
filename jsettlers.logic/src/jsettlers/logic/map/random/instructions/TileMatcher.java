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
package jsettlers.logic.map.random.instructions;

import java.util.Random;

import jsettlers.common.map.IMapData;
import jsettlers.common.position.ShortPoint2D;

public abstract class TileMatcher implements Iterable<ShortPoint2D> {
	protected final IMapData grid;
	protected final int startx;
	protected final int starty;
	protected final int distance;
	protected final Random random;
	private final LandFilter filter;

	public TileMatcher(IMapData grid, int startx, int starty, int distance,
			LandFilter filter, Random random) {
		this.grid = grid;
		this.startx = startx;
		this.starty = starty;
		this.distance = distance;
		this.filter = filter;
		this.random = random;

	}

	protected boolean isPlaceable(ShortPoint2D point) {
		return filter.isPlaceable(point);
	}

}
