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
package jsettlers.logic.map.random.landscape;

import jsettlers.logic.map.random.geometry.Point2D;

public class SiteDistanceCriterium implements SiteCriterium {
	private final int minDistanceSquared;
	private final int maxDistanceSquared;
	private final Point2D center;

	public SiteDistanceCriterium(Point2D point, String distanceRange) {
		this.center = point;
		String[] rangeParts = distanceRange.split("-");
		int mindistance = Integer.parseInt(rangeParts[0]);
		int maxdistance;
		if (rangeParts.length < 2) {
			maxdistance = mindistance;
		} else {
			maxdistance = Integer.parseInt(rangeParts[1]);
		}
		minDistanceSquared = mindistance * mindistance;
		maxDistanceSquared = maxdistance * maxdistance;
	}

	@Override
	public boolean matchesCriterium(MeshSite site) {
		if (site.isFixed()) {
			return false;
		}
		double distanceSquared = site.getCenter().distanceSquared(center);
		return minDistanceSquared < distanceSquared && distanceSquared < maxDistanceSquared;
	}

}
