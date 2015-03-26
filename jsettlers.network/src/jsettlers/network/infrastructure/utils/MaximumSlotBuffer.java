package jsettlers.network.infrastructure.utils;

/**
 * Buffer calculating the maximum of {@code n} slots.
 * 
 * @author Andreas Eberle
 * 
 */
public class MaximumSlotBuffer {
	private final int[] buffer;
	private int max = 0;

	public MaximumSlotBuffer(int length) {
		this.buffer = new int[length];
	}

	public void insert(int index, int value) {
		buffer[index] = value;

		int max = buffer[0];
		for (int i = 1; i < buffer.length; i++) {
			max = Math.max(max, buffer[i]);
		}
		this.max = max;
	}

	public int getMax() {
		return max;
	}

	public int getLength() {
		return buffer.length;
	}
}
