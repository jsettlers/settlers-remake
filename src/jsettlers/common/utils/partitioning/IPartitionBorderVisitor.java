package jsettlers.common.utils.partitioning;

import jsettlers.logic.algorithms.borders.traversing.IBorderVisitor;

/**
 * This interface extends the {@link IBorderVisitor} interface. It adds a method to give the information
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPartitionBorderVisitor extends IBorderVisitor {
	/**
	 * Called before the border traversing to specify the partition in the inner of the border.
	 * 
	 * @param partitionId
	 *            Id of the partition with the traversed border.
	 */
	void startingTraversing(short partitionId);
}
