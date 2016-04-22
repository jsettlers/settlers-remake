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

import static org.junit.Assert.fail;
import jsettlers.common.position.SRectangle;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

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
}
