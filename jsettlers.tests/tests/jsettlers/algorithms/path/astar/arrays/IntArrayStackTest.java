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
package jsettlers.algorithms.path.astar.arrays;

import static org.junit.Assert.assertEquals;
import jsettlers.algorithms.path.arrays.IntArrayStack;

import org.junit.Test;

/**
 * Test for the class {@link IntArrayStack}.
 * 
 * @author Andreas Eberle
 * 
 */
public class IntArrayStackTest {
	private static final int TEST_NUMBERS = 10;

	private IntArrayStack stack = new IntArrayStack(20);

	@Test
	public void testPushAndPop() {
		assertEquals(0, stack.size());

		for (int i = 0; i < TEST_NUMBERS; i++) {
			stack.pushFront(i);
			assertEquals(i + 1, stack.size());
		}

		for (int i = TEST_NUMBERS - 1; i >= 0; i--) {
			assertEquals(i + 1, stack.size());
			assertEquals(i, stack.popFront());
		}

		assertEquals(0, stack.size());
	}

	@Test
	public void testPushAndPopMulti() {
		for (int i = 0; i < 5; i++) {
			testPushAndPop();
		}
	}

	@Test
	public void testPushAndPopWithClear() {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			stack.pushFront(i);
		}

		assertEquals(TEST_NUMBERS, stack.size());
		stack.clear();
		assertEquals(0, stack.size());

		testPushAndPop();
	}

	@Test
	public void testRemoveByHandle() {
		int handles[] = new int[TEST_NUMBERS];

		for (int i = 0; i < TEST_NUMBERS; i++) {
			handles[i] = stack.pushFront(i);
		}

		stack.remove(handles[4]);
		stack.remove(handles[7]);
		assertEquals(TEST_NUMBERS - 2, stack.size());

		for (int i = TEST_NUMBERS - 1; i >= 0; i--) {
			if (i == 4 || i == 7) { // skip the removed values
				continue;
			}
			assertEquals(i, stack.popFront());
		}

		assertEquals(0, stack.size());
	}

	@Test
	public void testDeepCopy() {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			stack.pushFront(i);
		}

		assertEquals(TEST_NUMBERS, stack.size());

		IntArrayStack copy = stack.deepCopy();

		assertListsEqual(copy);
	}

	private void assertListsEqual(IntArrayStack copy) {
		assertEquals(stack.size(), copy.size());
		while (!stack.isEmpty()) {
			assertEquals(stack.popFront(), copy.popFront());
		}
	}

}
