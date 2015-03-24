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
