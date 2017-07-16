/*******************************************************************************
 * Copyright (c) 2016
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
package jsettlers.ai.highlevel.pioneers.target;

import jsettlers.ai.highlevel.AiPositions;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.position.RelativePoint;
import jsettlers.logic.map.grid.MainGrid;
import jsettlers.logic.map.grid.landscape.LandscapeGrid;

/**
 * @author codingberlin
 */
public class SurroundedByResourcesFilter implements AiPositions.AiPositionFilter {
	private final LandscapeGrid landscapeGrid;
	private final MainGrid mainGrid;
	private final EResourceType resourceType;
	private final static RelativePoint[] someNeighbours = {
			new RelativePoint(1, 1), new RelativePoint(-1, -1), new RelativePoint(1, -1), new RelativePoint(-1, 1) };

	public SurroundedByResourcesFilter(final MainGrid mainGrid, final LandscapeGrid landscapeGrid, final EResourceType resourceType) {
		this.resourceType = resourceType;
		this.landscapeGrid = landscapeGrid;
		this.mainGrid = mainGrid;
	}

	@Override
	public boolean contains(int x, int y) {
		for (RelativePoint relativeNeighbour : someNeighbours) {
			int neighbourX = relativeNeighbour.calculateX(x);
			int neighbourY = relativeNeighbour.calculateY(y);
			if (!mainGrid.isInBounds(neighbourX, neighbourY) || landscapeGrid.getResourceTypeAt(neighbourX, neighbourY) != resourceType
					|| landscapeGrid.getResourceAmountAt(neighbourX, neighbourY) <= 0) {
				return false;
			}
		}
		return true;
	}
}
