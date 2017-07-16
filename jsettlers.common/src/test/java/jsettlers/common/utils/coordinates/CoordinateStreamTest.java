/*******************************************************************************
 * Copyright (c) 2017
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
package jsettlers.common.utils.coordinates;

import org.junit.Test;

import jsettlers.common.utils.mutables.MutableInt;

import static org.junit.Assert.assertEquals;

/**
 * Created by Andreas Eberle on 14.01.2017.
 */
public class CoordinateStreamTest {

	@Test
	public void testSkipFirst() {
		final int elements = 20;
		CoordinateStream stream = streamForArray(increasingArray(elements));

		for (int skips = 0; skips < elements; skips++) {
			MutableInt counter = new MutableInt(skips);

			stream.skipFirst(skips).forEach((x, y) -> {
				assertEquals(counter.value, x);
				assertEquals(counter.value, y);
				counter.value++;
			});

			assertEquals(elements, counter.value);
		}
	}

	@Test
	public void testGetEvery() {
		final int elements = 5;
		CoordinateStream stream = streamForArray(increasingArray(elements));

		for (int every = 1; every < elements; every++) {
			MutableInt counter = new MutableInt(0);
			int fixedEvery = every;

			stream.getEvery(every).forEach((x, y) -> {
				assertEquals(fixedEvery * counter.value, x);
				assertEquals(fixedEvery * counter.value, y);
				counter.value++;
			});

			int expectedCounter = (int) Math.ceil(elements / (float) fixedEvery);
			assertEquals(expectedCounter, counter.value);
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetEveryInvalidParameterZero() {
		CoordinateStream stream = streamForArray(increasingArray(40));
		stream.getEvery(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetEveryInvalidParameterNegative() {
		CoordinateStream stream = streamForArray(increasingArray(40));
		stream.getEvery(-5);
	}

	private int[] increasingArray(int length) {
		int[] coordinatesFlat = new int[2 * length];
		for (int i = 0; i < length; i++) {
			coordinatesFlat[2 * i] = i;
			coordinatesFlat[2 * i + 1] = i;
		}
		return coordinatesFlat;
	}

	private CoordinateStream streamForArray(final int[] coordinatesFlat) {
		return new CoordinateStream() {
			@Override
			public boolean iterate(IBooleanCoordinateFunction function) {
				for (int i = 0; i < coordinatesFlat.length / 2; i++) {
					int x = coordinatesFlat[2 * i];
					int y = coordinatesFlat[2 * i + 1];
					boolean result = function.apply(x, y);
					if (!result) {
						return result;
					}
				}
				return true;
			}
		};
	}
}
