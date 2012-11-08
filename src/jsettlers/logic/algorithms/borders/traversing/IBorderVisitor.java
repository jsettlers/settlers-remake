package jsettlers.logic.algorithms.borders.traversing;

/**
 * Interface defining the methods to be able to traverse the borders of partitions with the {@link BorderTraversingAlgorithm}.
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
	 * 
	 * @return True if the traversing shall be continued.<br>
	 *         False if it shall be stopped.
	 */
	boolean visit(int x, int y);

}
