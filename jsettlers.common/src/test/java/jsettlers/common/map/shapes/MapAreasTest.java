/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
/**
 * 
 */
package jsettlers.common.map.shapes;

import jsettlers.common.position.ShortPoint2D;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author michael
 */
public class MapAreasTest {

	private static final int TEST_WIDTH = 100;

	/**
	 * Test method for {@link jsettlers.common.map.shapes.MapCircle#contains(jsettlers.common.position.ShortPoint2D)} .
	 */
	@Test
	public void testContainsShortPoint2D() {
		short cx = 0;
		short cy = 0;
		MapCircle circle = new MapCircle(cx, cy, 1);
		ShortPoint2D[] truePoints = new ShortPoint2D[] { new ShortPoint2D(cx - 1, cy - 1), new ShortPoint2D(cx - 1, cy),
				new ShortPoint2D(cx, cy - 1), new ShortPoint2D(cx, cy), new ShortPoint2D(cx + 1, cy), new ShortPoint2D(cx, cy + 1),
				new ShortPoint2D(cx + 1, cy + 1) };

		for (ShortPoint2D point : truePoints) {
			assertTrue("Point should be inside", circle.contains(point));
		}

		ShortPoint2D[] falsePoints = new ShortPoint2D[] { new ShortPoint2D(cx - 2, cy - 1), new ShortPoint2D(cx, cy + 3),
				new ShortPoint2D(cx - 1, cy + 1), };

		for (ShortPoint2D point : falsePoints) {
			assertFalse("Point should not be inside: " + point, circle.contains(point));
		}
	}

	/**
	 * Test method for {@link jsettlers.common.map.shapes.MapCircle#iterator()}.
	 */
	@Test
	public void testIterator() {
		MapCircle circle = new MapCircle((short) (TEST_WIDTH / 2), (short) (TEST_WIDTH / 2), 31.123f);
		testShapeIterator(circle);
	}

	/**
	 * Test method for {@link jsettlers.common.map.shapes.MapRectangle#iterator()}.
	 */
	@Test
	public void testRectIterator() {
		MapRectangle rect = new MapRectangle((short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 2), (short) (TEST_WIDTH / 2));
		testShapeIterator(rect);
		MapRectangle rect2 = new MapRectangle((short) (TEST_WIDTH / 4 + 1), (short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 2),
				(short) (TEST_WIDTH / 2));
		testShapeIterator(rect2);
		MapRectangle rect3 = new MapRectangle((short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 4 + 1), (short) (TEST_WIDTH / 2),
				(short) (TEST_WIDTH / 2));
		testShapeIterator(rect3);

	}

	@Test
	public void testParallelogramIterator() {
		Parallelogram rect = new Parallelogram((short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 2), (short) (TEST_WIDTH / 2));
		testShapeIterator(rect);
		Parallelogram rect2 = new Parallelogram((short) (TEST_WIDTH / 4 + 1), (short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 2),
				(short) (TEST_WIDTH / 2));
		testShapeIterator(rect2);
		Parallelogram rect3 = new Parallelogram((short) (TEST_WIDTH / 4), (short) (TEST_WIDTH / 4 + 1), (short) (TEST_WIDTH / 2),
				(short) (TEST_WIDTH / 2));
		testShapeIterator(rect3);
	}

	private void testShapeIterator(IMapArea circle) {
		boolean[][] foundByIterator = new boolean[TEST_WIDTH][TEST_WIDTH];

		for (ShortPoint2D pos : circle) {
			foundByIterator[pos.x][pos.y] = true;
		}

		for (int x = 0; x < TEST_WIDTH; x++) {
			for (int y = 0; y < TEST_WIDTH; y++) {
				assertEquals("contains() inconsistent with iterator for " + x + "," + y, circle.contains(new ShortPoint2D(x, y)),
						foundByIterator[x][y]);
			}
		}
	}

}
