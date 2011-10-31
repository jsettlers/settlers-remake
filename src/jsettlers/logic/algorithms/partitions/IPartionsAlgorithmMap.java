package jsettlers.logic.algorithms.partitions;

import jsettlers.common.position.ISPosition2D;

/**
 * Interface defining methods needed by the PartitionsManager.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPartionsAlgorithmMap {

	/**
	 * 
	 * @param position
	 *            position the partition is requested for
	 * @return index of the partition of this position
	 */
	short getPartition(final ISPosition2D position);

	short getPartition(short x, short y);

	/**
	 * 
	 * @param position
	 *            position the team is requested for
	 * @return team occupying this position
	 */
	byte getPlayerAt(final ISPosition2D position);

	/**
	 * Sets the given partition to the given position.
	 * 
	 * @param x
	 *            x coordinate
	 * @param y
	 *            y coordinate
	 * @param partition
	 *            partition to be set to the position
	 */
	void setPartition(final short x, final short y, final short partition);

	/**
	 * Merges two partitions specified by the given positions.
	 * 
	 * @param x1
	 *            x coordinate of first position
	 * @param y1
	 *            y coordinate of first position
	 * @param x2
	 *            x coordinate of second position
	 * @param y2
	 *            y coordinate of second position
	 * 
	 * @return returns the partition of the result of the merge
	 */
	short mergePartitions(final short x1, final short y1, final short x2, final short y2);

	/**
	 * Creates a new partition for the given position and the given team.
	 * 
	 * @param x
	 *            x coordinate of position
	 * @param y
	 *            y coordinate of position
	 * @param player
	 *            player that owns the partition
	 */
	void createPartition(final short x, final short y, final byte player);

	/**
	 * Disconnects the partition of the two positions.
	 * 
	 * @param x
	 *            x coordinate of position
	 * @param y
	 *            y coordinate of position
	 * @param firstPos
	 *            position of first partition
	 * @param secondPos
	 *            position of second partition
	 */
	void dividePartition(final short x, final short y, final ISPosition2D firstPos, final ISPosition2D secondPos);

}
