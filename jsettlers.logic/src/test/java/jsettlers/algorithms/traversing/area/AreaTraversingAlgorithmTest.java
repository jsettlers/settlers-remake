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
package jsettlers.algorithms.traversing.area;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.BitSet;
import java.util.LinkedList;

import jsettlers.algorithms.interfaces.IContainingProvider;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

public class AreaTraversingAlgorithmTest {

	private static final int WIDTH = 200;
	private static final int HEIGHT = 200;

	@Test
	public void testTraversing1() {
		final MapCircle c1 = new MapCircle(100, 100, 50);
		final MapCircle c2 = new MapCircle(120, 100, 20);
		final MapCircle c3 = new MapCircle(120, 100, 10);

		final IContainingProvider containingProvider = (x, y) -> c1.contains(x, y) && !c2.contains(x, y) || c3.contains(x, y);

		final LinkedList<ShortPoint2D> area = new LinkedList<>();
		final BitSet visited = new BitSet(WIDTH * HEIGHT);
		IAreaVisitor visitor = (x, y) -> {
			assertTrue(c1.contains(x, y) && !c2.contains(x, y)); // checks if the position is in the area
			area.add(new ShortPoint2D(x, y));
			int idx = x + y * WIDTH;
			assertFalse(visited.get(idx)); // every position is only visited once
			visited.set(idx);
			return true;
		};

		boolean result = AreaTraversingAlgorithm.traverseArea(containingProvider, visitor, c1.iterator().next(), WIDTH, HEIGHT);
		assertTrue(result);

		// check if all positions in the area have been traversed
		FreeMapArea mapArea = new FreeMapArea(area);
		for (ShortPoint2D curr : c1) {
			if (!c2.contains(curr)) {
				assertTrue(mapArea.contains(curr));
			}
		}
	}
}
