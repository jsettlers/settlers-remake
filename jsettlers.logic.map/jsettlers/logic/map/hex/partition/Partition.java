package jsettlers.logic.map.hex.partition;

/**
 * This class holds the metadata of a partition.
 * 
 * @author Andreas Eberle
 * 
 */
public class Partition {

	private int counter = 0;
	private final byte player;

	public Partition(final byte player) {
		this.player = player;
	}

	public void decrement() {
		counter--;
	}

	public void increment() {
		counter++;
	}

	public boolean isEmpty() {
		return counter <= 0;
	}

	public byte getPlayer() {
		return player;
	}

	public int getNumberOfElements() {
		return counter;
	}

}
