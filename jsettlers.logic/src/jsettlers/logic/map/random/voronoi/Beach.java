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
package jsettlers.logic.map.random.voronoi;

public interface Beach {

	/**
	 * Adds a given beach line point to the graph.
	 * 
	 * @param point
	 */
	public void add(VoronioSite point, CircleEventManager mgr);

	/**
	 * Gets the beach line part at the given position
	 * 
	 * @param y
	 *            The y-position
	 * @return The beach line part at that position.
	 */
	BeachLinePart getBeachAt(double sweepx, double y);

	/**
	 * Gets the beach line one at the bottom of a given beachline
	 * 
	 * @param current
	 *            may not be null
	 * @return The one at the bottom
	 */
	BeachLinePart getBottom(BeachLinePart current);

	/**
	 * Gets the beach line one at the top of a given beachline
	 * 
	 * @param current
	 *            may not be null
	 * @return The one at the bottom
	 */
	BeachLinePart getTop(BeachLinePart current);

}
