package jsettlers.common.utils.collections.list;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import jsettlers.TestUtils;
import jsettlers.logic.algorithms.path.astar.queues.bucket.DoubleLinkedIntListItem;

import org.junit.Test;

/**
 * Test for the class {@link DoubleLinkedIntList}.
 * 
 * @author Andreas Eberle
 * 
 */
public class DoubleLinkedListTest {
	private static final int TEST_NUMBERS = 10;

	private DoubleLinkedList<DoubleLinkedIntListItem> list = new DoubleLinkedList<DoubleLinkedIntListItem>();

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
		assertEquals(TEST_NUMBERS - 2, list.size());

		for (int i = TEST_NUMBERS - 1; i >= 0; i--) {
			if (i == 4 || i == 7) { // skip the removed values
				continue;
			}
			assertEquals(i, list.popFront().value);
		}

		assertEquals(0, list.size());
	}

	@Test
	public void testSerialization() throws IOException, ClassNotFoundException {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushEnd(new DoubleLinkedIntListItem(i));
		}

		DoubleLinkedList<DoubleLinkedIntListItem> readList = TestUtils.serializeAndDeserialize(list);

		assertListEquals(list, readList);
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
