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
package jsettlers.common.movable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import jsettlers.common.position.ShortPoint2D;

import org.junit.Test;

/**
 * Unit Tests for {@link EDirection}
 * 
 * @author Andreas Eberle
 * 
 */
public class EDirectionTest {

	@Test
	public void testGetDirection() {
		short startX = 100;
		short startY = 100;

		for (EDirection currDir : EDirection.VALUES) {
			ShortPoint2D target = currDir.getNextHexPoint(new ShortPoint2D(startX, startY));

			EDirection calculatedDir = EDirection.getDirectionOfMultipleSteps(target.x - startX, target.y - startY);
			assertNotNull(calculatedDir);
			assertEquals(currDir, calculatedDir);
		}
	}

	@Test
	public void testGetDirectionOfMultipleSteps() {
		short startX = 100;
		short startY = 100;

		for (EDirection currDir : EDirection.VALUES) {
			for (int i = 1; i < 30; i++) {
				ShortPoint2D target = currDir.getNextHexPoint(new ShortPoint2D(startX, startY), i);

				EDirection calculatedDir = EDirection.getDirectionOfMultipleSteps(target.x - startX, target.y - startY);
				assertNotNull(calculatedDir);
				assertEquals(currDir, calculatedDir);
			}
		}
	}
}
