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
	 */
	void visit(int x, int y);

}
