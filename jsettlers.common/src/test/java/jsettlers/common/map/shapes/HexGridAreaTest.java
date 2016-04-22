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
package jsettlers.common.map.shapes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import jsettlers.common.map.shapes.HexGridArea.HexGridAreaIterator;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

public class HexGridAreaTest {

	@Test
	public void testSinglePoint() {
		HexGridArea area = new HexGridArea(10, 10, 0, 0);
		HexGridAreaIterator iter = area.iterator();

		assertTrue(iter.hasNext());
		assertEquals(new ShortPoint2D(10, 10), iter.next());
		assertFalse(iter.hasNext());
	}

	@Test
	public void testCircleRadius1() {
		ShortPoint2D center = new ShortPoint2D(10, 10);
		int startRadius = 1;
		int maxRadius = 1;
		int expectedCount = 6;

		assertPositions(center, startRadius, maxRadius, expectedCount);
	}

	@Test
	public void testCircleRadius1To2() {
		ShortPoint2D center = new ShortPoint2D(10, 10);
		int startRadius = 1;
		int maxRadius = 2;
		int expectedCount = 6 + 12;

		assertPositions(center, startRadius, maxRadius, expectedCount);
	}

	@Test
	public void testCircleRadius0To2() {
		ShortPoint2D center = new ShortPoint2D(10, 10);
		int startRadius = 0;
		int maxRadius = 2;
		int expectedCount = 1 + 6 + 12;

		assertPositions(center, startRadius, maxRadius, expectedCount);
	}

	@Test
	public void testCircleRadius4To6() {
		ShortPoint2D center = new ShortPoint2D(10, 10);
		int startRadius = 4;
		int maxRadius = 6;
		int expectedCount = 4 * 6 + 5 * 6 + 6 * 6;

		assertPositions(center, startRadius, maxRadius, expectedCount);
	}

	private void assertPositions(ShortPoint2D center, int startRadius, int maxRadius, int expectedCount) {
		HexGridArea area = new HexGridArea(center.x, center.y, startRadius, maxRadius);

		int count = 0;
		for (ShortPoint2D pos : area) {
			count++;

			int onGridDist = center.getOnGridDistTo(pos);
			if (!(startRadius <= onGridDist && onGridDist <= maxRadius)) {
				fail("onGridDist: " + onGridDist + "   not in the expected range of [" + startRadius + "|" + maxRadius + "]   pos: " + pos);
			}
		}

		assertEquals(expectedCount, count);
	}
}
