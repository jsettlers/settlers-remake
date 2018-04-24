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
package jsettlers.common.utils.collections.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import jsettlers.testutils.TestUtils;

import org.junit.Test;

/**
 * Test for the class {@link DoubleLinkedList}.
 * 
 * @author Andreas Eberle
 * 
 */
public class DoubleLinkedListTest {
	private static final int TEST_NUMBERS = 10;

	private DoubleLinkedList<DoubleLinkedIntListItem> list = new DoubleLinkedList<>();

	@Test
	public void testPushFrontAndPopFront() {
		assertEquals(0, list.size());

		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushFront(new DoubleLinkedIntListItem(i));
			assertEquals(i + 1, list.size());
		}

		for (int i = TEST_NUMBERS - 1; i >= 0; i--) {
			assertEquals(i + 1, list.size());
			assertEquals(i, list.popFront().value);
		}

		assertEquals(0, list.size());
	}

	@Test
	public void testPushEndAndPopFront() {
		assertEquals(0, list.size());

		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushEnd(new DoubleLinkedIntListItem(i));
			assertEquals(i + 1, list.size());
		}

		for (int i = 0; i < TEST_NUMBERS; i++) {
			assertEquals(10 - i, list.size());
			assertEquals(i, list.popFront().value);
		}

		assertEquals(0, list.size());
	}

	@Test
	public void testPushFrontAndPopFrontMulti() {
		for (int i = 0; i < 5; i++) {
			testPushFrontAndPopFront();
		}
	}

	@Test
	public void testPushEndAndPopFrontMulti() {
		for (int i = 0; i < 5; i++) {
			testPushEndAndPopFront();
		}
	}

	@Test
	public void testPushFrontAndPopFrontWithClear() {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushFront(new DoubleLinkedIntListItem(i));
		}

		assertEquals(TEST_NUMBERS, list.size());
		list.clear();
		assertEquals(0, list.size());

		testPushFrontAndPopFront();
	}

	@Test
	public void testPushEndAndPopFrontWithClear() {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushEnd(new DoubleLinkedIntListItem(i));
		}

		assertEquals(TEST_NUMBERS, list.size());
		list.clear();
		assertEquals(0, list.size());

		testPushEndAndPopFront();
	}

	@Test
	public void testRemoveByHandle() {
		DoubleLinkedIntListItem handles[] = new DoubleLinkedIntListItem[TEST_NUMBERS];

		for (int i = 0; i < TEST_NUMBERS; i++) {
			handles[i] = new DoubleLinkedIntListItem(i);
			list.pushFront(handles[i]);
		}

		list.remove(handles[4]);
		list.remove(handles[7]);
		assertEquals("incorrect size after removing 2 values", TEST_NUMBERS - 2, list.size());

		assertNull("the removed item should not hold a 'next' reference", handles[4].next);
		assertNull("the removed item should not hold a 'prev' reference", handles[4].prev);
		assertNull("the removed item should not hold a 'next' reference", handles[7].next);
		assertNull("the removed item should not hold a 'prev' reference", handles[7].prev);

		for (int i = TEST_NUMBERS - 1; i >= 0; i--) {
			if (i == 4 || i == 7) { // skip the removed values
				continue;
			}
			assertEquals(i, list.popFront().value);
		}

		assertEquals("incorrect size after popFront all values", 0, list.size());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushEnd(new DoubleLinkedIntListItem(i));
		}

		DoubleLinkedList<DoubleLinkedIntListItem> readList = TestUtils.serializeAndDeserialize(list);

		assertListEquals(list, readList);
	}

	@Test
	public void testIteratorLoop() {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushEnd(new DoubleLinkedIntListItem(i));
		}

		int i = 0;
		for (DoubleLinkedIntListItem curr : list) {
			assertEquals(i, curr.value);
			i++;
		}
	}

	@Test
	public void testIteratorRemove() {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushEnd(new DoubleLinkedIntListItem(i));
		}

		int i = 0;
		Iterator<DoubleLinkedIntListItem> iter = list.iterator();
		while (iter.hasNext()) {
			assertEquals(i, iter.next().value);
			iter.remove();
			i++;
		}

		assertEquals(TEST_NUMBERS, i);
		assertEquals(0, list.size());
		assertTrue(list.isEmpty());
		assertFalse(list.iterator().hasNext());
	}

	@Test
	public void testIteratorRemoveHalf() {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushEnd(new DoubleLinkedIntListItem(i));
		}

		int i = 0;
		Iterator<DoubleLinkedIntListItem> iter = list.iterator();
		while (iter.hasNext()) {
			DoubleLinkedIntListItem curr = iter.next();
			assertEquals(i, curr.value);
			if (curr.value % 2 == 0) {
				iter.remove();
			}
			i++;
		}

		assertEquals(TEST_NUMBERS / 2, list.size());
		assertTrue(list.iterator().hasNext());

		i = 1;
		for (DoubleLinkedIntListItem curr : list) {
			assertEquals(i, curr.value);
			i += 2;
		}
	}

	@Test
	public void testMerge() {
		DoubleLinkedList<DoubleLinkedIntListItem> list2 = new DoubleLinkedList<>();
		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushEnd(new DoubleLinkedIntListItem(i));
			list2.pushEnd(new DoubleLinkedIntListItem(i));
		}

		list2.mergeInto(list);
		assertEquals(0, list2.size());
		assertEquals(TEST_NUMBERS * 2, list.size());
		assertEquals(list2.head, ((DoubleLinkedListItem<DoubleLinkedIntListItem>) list2.head).next);
		assertEquals(list2.head, ((DoubleLinkedListItem<DoubleLinkedIntListItem>) list2.head).prev);

		int i = 0;
		for (DoubleLinkedIntListItem curr : list) {
			assertEquals(i % TEST_NUMBERS, curr.value);
			i++;
		}
	}

	private void assertListEquals(DoubleLinkedList<DoubleLinkedIntListItem> list0, DoubleLinkedList<DoubleLinkedIntListItem> list1) {
		assertEquals(list0.size(), list1.size());

		int size = list0.size();
		for (int i = 0; i < size; i++) {
			assertEquals(list0.popFront().value, list1.popFront().value);
		}

		assertEquals(0, list0.size());
		assertEquals(0, list1.size());
	}

}
