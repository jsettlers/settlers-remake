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
package jsettlers.common.position;

import jsettlers.common.map.shapes.HexGridArea;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ShortPoint2DTest {

	@Test
	public void testGetOnGridDist() {
		ShortPoint2D center = new ShortPoint2D(100, 100);

		for (int radius = 1; radius < 30; radius++) {
			int expectedRadius = radius;
			HexGridArea.streamBorder(center.x,center.y, radius).forEach((x,y)->{
				assertEquals(expectedRadius, center.getOnGridDistTo(x,y));
			});
		}
	}

	@Test
	public void singleGetOnGridDistTest() {
		ShortPoint2D center = new ShortPoint2D(100, 100);
		ShortPoint2D pos = new ShortPoint2D(98, 99);

		assertEquals(2, center.getOnGridDistTo(pos));
		assertEquals(2, pos.getOnGridDistTo(center));
	}
}
