package jsettlers.logic.algorithms.path.arrays;

public class IntArrayStack {
	private static final int DELETED_VALUE = Integer.MIN_VALUE;

	private static final float GROWTH_FACTOR = 3f;

	private int[] values;
	private int insertPosition = 0;
	private int removed = 0;
	private int length;

	public IntArrayStack(int startSize) {
		values = new int[startSize];
		this.length = startSize;
	}

	/**
	 * Pushes the given element to the stack and returns a handle that can be used to remove the element from the stack.
	 * 
	 * @param elementId
	 * @return
	 */
	public int pushFront(int elementId) {
		if (insertPosition >= length) { // do we need to grow the array?
			final int newLength = (int) (length * GROWTH_FACTOR);
			int[] newValuesArray = new int[newLength];
			System.arraycopy(values, 0, newValuesArray, 0, values.length);
			values = newValuesArray;
			length = newLength;
			System.out.println("grew stack to " + newLength);
		}

		values[insertPosition] = elementId;
		return insertPosition++;
	}

	public void remove(int handle) {
		values[handle] = DELETED_VALUE;
		removed++;
	}

	public void clear() {
		insertPosition = 0;
		removed = 0;
	}

	public boolean isEmpty() {
		return insertPosition - removed <= 0;
	}

	public int popFront() {
		int resultValue = values[--insertPosition];

		while (resultValue == DELETED_VALUE) {
			resultValue = values[--insertPosition];
			removed--;
		}

		return resultValue;
	}

	public int size() {
		return insertPosition - removed;
	}

	public IntArrayStack deepCopy() {
		IntArrayStack copy = new IntArrayStack(length);

		System.arraycopy(values, 0, copy.values, 0, values.length);
		copy.insertPosition = insertPosition;
		copy.removed = removed;

		return copy;
	}
}
