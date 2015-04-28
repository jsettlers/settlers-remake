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

import java.util.ArrayList;

import jsettlers.logic.map.random.geometry.Parabola;

/**
 * A simple array of beach line parts
 * 
 * @author michael
 *
 */
public class BeachArray implements Beach {
	/**
	 * The parts ordered by y coordijnate, that means from bottom to top.
	 */
	private ArrayList<BeachLinePart> parts = new ArrayList<BeachLinePart>();

	@Override
	public void add(VoronioSite point, CircleEventManager mgr) {
		double directrixx = point.getX();
		int brokenIndex = getBeachInexAt(directrixx, point.getY());
		if (brokenIndex == -1) {
			parts.add(new BeachLinePart(point));
		} else {
			BeachLinePart broken = getSafe(brokenIndex);
			BeachLinePart top = getSafe(brokenIndex + 1);
			BeachLinePart bottom = getSafe(brokenIndex - 1);

			BeachLinePart arc = new BeachLinePart(point);
			if (top != null && bottom != null) {
				CircleEvent falseCircle = new CircleEvent(bottom, broken, top);
				mgr.remove(falseCircle);
			}

			// top part of broken arc
			BeachLinePart brokenCopy = broken.copy();

			parts.add(brokenIndex + 1, arc);
			parts.add(brokenIndex + 2, brokenCopy);

			if (bottom != null) {
				mgr.add(new CircleEvent(bottom, broken, arc));
			}
			if (top != null) {
				mgr.add(new CircleEvent(arc, brokenCopy, top));
			}
		}
	}

	/**
	 * gets the point or returns null
	 * 
	 * @param brokenIndex
	 * @return
	 */
	private BeachLinePart getSafe(int index) {
		if (index >= 0 && index < parts.size()) {
			return parts.get(index);
		} else {
			return null;
		}
	}

	private int getBeachInexAt(double sweepx, double y) {
		for (int i = 0; i < parts.size() - 1; i++) {
			if (Parabola.getCutY(parts.get(i).getX(), parts.get(i).getY(), parts.get(i + 1).getX(), parts.get(i + 1).getY(), sweepx) > y) {
				return i;
			}
		}
		return parts.size() - 1;
	}

	@Override
	public BeachLinePart getBeachAt(double sweepx, double y) {
		return parts.get(getBeachInexAt(sweepx, y));
	}

	@Override
	public BeachLinePart getBottom(BeachLinePart current) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BeachLinePart getTop(BeachLinePart current) {
		// TODO Auto-generated method stub
		return null;
	}

}
