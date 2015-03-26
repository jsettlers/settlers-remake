package jsettlers.network.infrastructure.utils;

/**
 * Cyclic buffer calculating the average of the last {@code n} given numbers, where {@code n} is a configurable number.
 * 
 * @author Andreas Eberle
 * 
 */
public class AveragingBoundedBuffer {
	private final int length;
	private final int[] buffer;
	private int index = 0;
	private int sum = 0;

	public AveragingBoundedBuffer(int length) {
		this.length = length;
		this.buffer = new int[length];
	}

	public void insert(int value) {
		sum = sum - buffer[index] + value;
		buffer[index] = value;

		index = (index + 1) % length;
	}

	public int getAverage() {
		return sum / length;
	}

	public int getLength() {
		return length;
	}
}