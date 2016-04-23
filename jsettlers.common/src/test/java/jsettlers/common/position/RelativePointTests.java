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
package jsettlers.common.position;

import static org.junit.Assert.assertEquals;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

public class RelativePointTests {

	@Test
	public void testCalculatePoint() {
		ShortPoint2D p1 = new ShortPoint2D((short) 1, (short) 2);
		ShortPoint2D p2 = new ShortPoint2D((short) 5, (short) 6);

		RelativePoint expected = new RelativePoint((short) 4, (short) 4);
		assertEquals(expected, RelativePoint.getRelativePoint(p1, p2));

		assertEquals(p2, expected.calculatePoint(p1));
	}

}
