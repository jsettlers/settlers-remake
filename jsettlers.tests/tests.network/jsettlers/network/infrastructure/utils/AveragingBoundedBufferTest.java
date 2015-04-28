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
package jsettlers.network.infrastructure.utils;

import static org.junit.Assert.assertEquals;
import jsettlers.network.infrastructure.utils.AveragingBoundedBuffer;

import org.junit.Test;

/**
 * Tests the {@link AveragingBoundedBuffer}.
 * 
 * @author Andreas Eberle
 * 
 */
public class AveragingBoundedBufferTest {
	private static final int BUFFER_LENGTH = 11;

	private AveragingBoundedBuffer buffer = new AveragingBoundedBuffer(BUFFER_LENGTH);

	@Test
	public void testAverageNoOverlap() {
		for (int i = 1; i <= BUFFER_LENGTH; i++) {
			buffer.insert(i);
		}

		int expectedAvg = ((int) (BUFFER_LENGTH * 0.5f * (BUFFER_LENGTH + 1))) / BUFFER_LENGTH;
		assertEquals(expectedAvg, buffer.getAverage());
	}

	@Test
	public void testAverageNoOverlapDouble() {
		testAverageNoOverlap();
		testAverageNoOverlap();
	}

	@Test
	public void testAverageOverlapUneven() {
		for (int i = 1; i <= 3 * BUFFER_LENGTH; i++) {
			buffer.insert(i);

			int base = (int) ((i - BUFFER_LENGTH) > 0 ? ((i - BUFFER_LENGTH) * 0.5f * (i - BUFFER_LENGTH + 1)) : 0);
			int expectedSum = (int) (i * 0.5f * (i + 1) - base);
			int expectedAvg = expectedSum / BUFFER_LENGTH;
			assertEquals("index: " + i, expectedAvg, buffer.getAverage());
		}
	}
}
