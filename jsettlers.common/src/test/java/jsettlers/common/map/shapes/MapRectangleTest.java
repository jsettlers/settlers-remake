/*******************************************************************************
 * Copyright (c) 2018
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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MapRectangleTest {
	@Test
	public void containsBorders() {
		MapRectangle mapRectangle = new MapRectangle(0, 0, 10, 10);

		assertTrue(mapRectangle.contains(0, 0));
		assertTrue(mapRectangle.contains(9, 0));
		assertTrue(mapRectangle.contains(4, 9));
		assertTrue(mapRectangle.contains(13, 9));
	}

	@Test
	public void containsNotOutsideBorders() {
		MapRectangle mapRectangle = new MapRectangle(0, 0, 10, 10);

		assertFalse(mapRectangle.contains(0 - 1, 0));
		assertFalse(mapRectangle.contains(0, -1));
		assertFalse(mapRectangle.contains(-1, -1));

		assertFalse(mapRectangle.contains(10, 0));
		assertFalse(mapRectangle.contains(9, -1));
		assertFalse(mapRectangle.contains(10, -1));

		assertFalse(mapRectangle.contains(4 - 1, 9));
		assertFalse(mapRectangle.contains(4, 9 + 1));
		assertFalse(mapRectangle.contains(4 - 1, 9 + 1));

		assertFalse(mapRectangle.contains(13 + 1, 9));
		assertFalse(mapRectangle.contains(13, 9 + 1));
		assertFalse(mapRectangle.contains(13 + 1, 9 + 1));
	}
}
