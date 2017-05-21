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
package jsettlers.common.utils.collections;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

public class IteratorFilterTest {

	private LinkedList<Integer> list;

	@Before
	public void setUp() throws Exception {
		list = new LinkedList<>();
		list.add(1);
		list.add(6);
		list.add(12);
		list.add(34);
		list.add(7);
		list.add(9);
		list.add(12);
		list.add(13);
	}

	@Test
	public void testFilterUneven() {
		LinkedList<Integer> expected = new LinkedList<>();
		for (Integer curr : list) {
			if (curr % 2 != 0)
				expected.add(curr);
		}

		assertEqualality(expected, new IteratorFilter<>(list, object -> object % 2 != 0));
	}

	@Test
	public void testFilterEven() {
		LinkedList<Integer> expected = new LinkedList<>();
		for (Integer curr : list) {
			if (curr % 2 == 0)
				expected.add(curr);
		}

		assertEqualality(expected, new IteratorFilter<>(list, object -> object % 2 == 0));
	}

	@Test
	public void testUseFilterTwoTimes() {
		IteratorFilter<Integer> filter = new IteratorFilter<>(list, object -> true);

		assertEqualality(list, filter);
		assertEqualality(list, filter);
	}

	private void assertEqualality(LinkedList<Integer> expectedList, IteratorFilter<Integer> iteratorFilter) {
		Iterator<Integer> expectedIterator = expectedList.iterator();

		Iterator<Integer> filterIterator = iteratorFilter.iterator();

		while (expectedIterator.hasNext() && filterIterator.hasNext()) {
			Integer expected = expectedIterator.next();
			Integer filtered = filterIterator.next();

			assertEquals(expected, filtered);
		}

		assertEquals(expectedIterator.hasNext(), filterIterator.hasNext());
	}
}