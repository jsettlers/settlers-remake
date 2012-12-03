package jsettlers.logic.map.newGrid.partition;

import java.io.Serializable;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;

/**
 * This class holds the metadata of a partition.
 * 
 * @author Andreas Eberle
 * 
 */
public final class Partition extends PartitionManager implements Serializable {
	private static final long serialVersionUID = -2087692347209993840L;

	final byte playerId;
	private int counter = 0;
	private int xSum = 0;
	private int ySum = 0;

	public Partition(final byte player) {
		this.playerId = player;
	}

	public Partition(byte player, int size) {
		this(player);
		this.counter = size;
	}

	void decrement(int x, int y) {
		counter--;
		xSum -= x;
		ySum -= y;
	}

	public void increment(int x, int y) {
		counter++;
		xSum += x;
		ySum += y;
	}

	public void mergeInto(Partition newPartition) {
		super.mergeInto(newPartition);
		newPartition.counter += this.counter;
		newPartition.xSum += xSum;
		newPartition.ySum += ySum;

		counter = 0;
		xSum = 0;
		ySum = 0;
	}

	public void removePositionTo(final int x, final int y, final Partition newPartitionObject) {
		assert this != newPartitionObject : "ERROR: newManager can not be the same as this manager!!";

		this.decrement(x, y);
		newPartitionObject.increment(x, y);
		super.removePositionTo(x, y, newPartitionObject, newPartitionObject.playerId == this.playerId);

		if (isEmpty())
			super.stopManager();
	}

	public boolean isEmpty() {
		return counter <= 0;
	}

	public byte getPlayerId() {
		return playerId;
	}

	public int getNumberOfElements() {
		return counter;
	}

	/**
	 * 
	 * @param pos1
	 * @param pos2
	 * @return The position with the bigger distance to the gravity center of this partition.
	 */
	public ShortPoint2D getPositionCloserToGravityCenter(ShortPoint2D pos1, ShortPoint2D pos2) {
		int gravityY;
		int gravityX;
		if (counter > 0) {
			gravityX = xSum / counter;
			gravityY = ySum / counter;
		} else {
			gravityX = 0;
			gravityY = 0;
		}

		int dist1 = ShortPoint2D.getOnGridDist(gravityX - pos1.x, gravityY - pos1.y);
		int dist2 = ShortPoint2D.getOnGridDist(gravityX - pos2.x, gravityY - pos2.y);

		return dist1 >= dist2 ? pos1 : pos2;
	}
}
