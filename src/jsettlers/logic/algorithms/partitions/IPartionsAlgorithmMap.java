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
	 * @param position
	 *            the position the new partition value should be set
	 * @param partition
	 *            partition to be set to the position
	 */
	void setPartition(final ISPosition2D position, final short partition);

	/**
	 * Merges two partitions specified by the given positions.
	 * 
	 * @param firstPos
	 *            a position of first partition
	 * @param secondPos
	 *            a position of the second partition
	 * @return returns the partition of the result of the merge
	 */
	short mergePartitions(final ISPosition2D firstPos, final ISPosition2D secondPos);

	/**
	 * Creates a new partition for the given position and the given team.
	 * 
	 * @param position
	 *            position that needs a new partition
	 * @param player
	 *            player that owns the partition
	 */
	void createPartition(final ISPosition2D position, final byte player);

	/**
	 * Disconnects the partition of the two positions.
	 * 
	 * @param changedPosition
	 *            position that has been changed
	 * @param firstPos
	 *            position of first partition
	 * @param secondPos
	 *            position of second partition
	 */
	void dividePartition(final ISPosition2D changedPosition, final ISPosition2D firstPos, final ISPosition2D secondPos);

}
