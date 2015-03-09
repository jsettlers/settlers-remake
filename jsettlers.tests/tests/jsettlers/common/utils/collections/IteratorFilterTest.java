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
		list = new LinkedList<Integer>();
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
		LinkedList<Integer> expected = new LinkedList<Integer>();
		for (Integer curr : list) {
			if (curr % 2 == 1)
				expected.add(curr);
		}

		assertEqualality(expected, new IteratorFilter<Integer>(list, new IPredicate<Integer>() {
			@Override
			public boolean evaluate(Integer object) {
				return object % 2 == 1;
			}
		}));
	}

	@Test
	public void testFilterEven() {
		LinkedList<Integer> expected = new LinkedList<Integer>();
		for (Integer curr : list) {
			if (curr % 2 == 0)
				expected.add(curr);
		}

		assertEqualality(expected, new IteratorFilter<Integer>(list, new IPredicate<Integer>() {
			@Override
			public boolean evaluate(Integer object) {
				return object % 2 == 0;
			}
		}));
	}

	@Test
	public void testUseFilterTwoTimes() {
		IteratorFilter<Integer> filter = new IteratorFilter<Integer>(list, new IPredicate<Integer>() {
			@Override
			public boolean evaluate(Integer object) {
				return true;
			}
		});

		assertEqualality(list, filter);
		assertEqualality(list, filter);
	}

	private void assertEqualality(LinkedList<Integer> expectedList, IteratorFilter<Integer> iteratorFilter) {
		Iterator<Integer> expectedIter = expectedList.iterator();

		Iterator<Integer> filterIterator = iteratorFilter.iterator();

		while (expectedIter.hasNext() && filterIterator.hasNext()) {
			Integer expected = expectedIter.next();
			Integer filtered = filterIterator.next();

			assertEquals(expected, filtered);
		}

		assertEquals(expectedIter.hasNext(), filterIterator.hasNext());
	}
}
