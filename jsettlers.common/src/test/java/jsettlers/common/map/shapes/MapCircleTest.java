/*******************************************************************************
 * Copyright (c) 2015 - 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common.map.shapes;

import jsettlers.common.position.SRectangle;
import jsettlers.common.position.ShortPoint2D;
import org.junit.Test;

import static org.junit.Assert.*;

public class MapCircleTest {

	@Test
	public void testGetBorders() {
		for (int i = 1; i < 40; i++) {
			MapCircle circle = new MapCircle(new ShortPoint2D(100, 100), i);

			SRectangle borders = circle.getBorders();
			for (ShortPoint2D curr : circle) {
				if (!borders.contains(curr)) {
					fail("position: " + curr + " is not in the border " + borders + " radius: " + i);
				}
			}
		}
	}

	@Test
	public void testStream() {
		for (int i = 0; i < 40; i++) {
			MapCircle circle = new MapCircle(new ShortPoint2D(100, 100), i);

			MapCircleIterator iterator = circle.iterator();

			circle.stream().forEach((x, y) -> {
				assertTrue(iterator.hasNext());
				ShortPoint2D expected = iterator.next();
				assertEquals(expected, new ShortPoint2D(x, y));
			});

			assertFalse(iterator.hasNext());
		}
	}

	@Test
	public void testBorderStream() {
		for (int i = 1; i < 40; i++) {
			MapCircle circle = new MapCircle(new ShortPoint2D(100, 100), i);
			MapCircle oneSmallerCircle = new MapCircle(new ShortPoint2D(100, 100), i - 1);

			circle.streamBorder().forEach((x, y) -> {
				assertTrue(circle.contains(x, y));
				assertTrue(!oneSmallerCircle.contains(x, y));
			});

			int maxCount = circle.stream().count() - oneSmallerCircle.stream().count();
			int actualCount = circle.streamBorder().count();
			assertTrue(actualCount <= maxCount);
		}
	}
}
