package jsettlers.algorithms.path.astar.lists;

import static org.junit.Assert.assertEquals;
import jsettlers.logic.algorithms.path.astar.lists.DoubleLinkedIntList;
import jsettlers.logic.algorithms.path.astar.lists.DoubleLinkedIntListItem;

import org.junit.Test;

/**
 * Test for the class {@link DoubleLinkedIntList}.
 * 
 * @author Andreas Eberle
 * 
 */
public class DoubleLinkedListTest {
	private static final int TEST_NUMBERS = 10;

	private DoubleLinkedIntList list = new DoubleLinkedIntList();

	@Test
	public void testPushAndPop() {
		assertEquals(0, list.size());

		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushFront(i);
			assertEquals(i + 1, list.size());
		}

		for (int i = TEST_NUMBERS - 1; i >= 0; i--) {
			assertEquals(i + 1, list.size());
			assertEquals(i, list.popFront().value);
		}

		assertEquals(0, list.size());
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
			list.pushFront(i);
		}

		assertEquals(TEST_NUMBERS, list.size());
		list.clear();
		assertEquals(0, list.size());

		testPushAndPop();
	}

	@Test
	public void testRemoveByHandle() {
		DoubleLinkedIntListItem handles[] = new DoubleLinkedIntListItem[TEST_NUMBERS];

		for (int i = 0; i < TEST_NUMBERS; i++) {
			handles[i] = list.pushFront(i);
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
	public void testDeepCopy() {
		for (int i = 0; i < TEST_NUMBERS; i++) {
			list.pushFront(i);
		}

		assertEquals(TEST_NUMBERS, list.size());

		DoubleLinkedIntList copy = list.deepCopy();
		copy.assertCorrect();

		assertListsEqual(copy);
	}

	private void assertListsEqual(DoubleLinkedIntList copy) {
		assertEquals(list.size(), copy.size());
		while (!list.isEmpty()) {
			assertEquals(list.popFront().value, copy.popFront().value);
		}
	}

}
