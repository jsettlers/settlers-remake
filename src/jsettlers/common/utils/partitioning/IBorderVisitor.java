package jsettlers.common.utils.partitioning;

/**
 * Interface defining the methods to be able to traverse the borders of partitions with the {@link PartitionCalculator}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IBorderVisitor {
	/**
	 * Called if the given coordinates are lying around the border.
	 * 
	 * @param x
	 *            X coordinate.
	 * @param y
	 *            Y coordinate.
	 */
	void visit(int x, int y);

	/**
	 * Called after the complete border has been visited.
	 */
	void traversingFinished();
}
