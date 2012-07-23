package jsettlers.logic.map.newGrid.partition;

import java.io.Serializable;

import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;

/**
 * This class holds the metadata of a partition.
 * 
 * @author Andreas Eberle
 * 
 */
public final class Partition extends PartitionManager implements Serializable {
	private static final long serialVersionUID = -2087692347209993840L;

	private int counter = 0;
	private final byte player;

	public Partition(final byte player) {
		this.player = player;
	}

	public Partition(byte player, int size) {
		this(player);
		this.counter = size;
	}

	void decrement() {
		counter--;
	}

	public void increment() {
		counter++;
	}

	public void removePositionTo(final short x, final short y, final Partition newPartitionObject) {
		assert this != newPartitionObject : "ERROR: newManager can not be the same as this manager!!";

		this.decrement();
		newPartitionObject.increment();
		super.removePositionTo(x, y, newPartitionObject, newPartitionObject.player == this.player);

		if (isEmpty())
			super.stopManager();
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
