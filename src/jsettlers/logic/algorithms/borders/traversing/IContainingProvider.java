package jsettlers.logic.algorithms.borders.traversing;

/**
 * This interface defines a method needed by the {@link BorderTraversingAlgorithm}. The implementor of this interface defines the area the
 * {@link BorderTraversingAlgorithm} is walking around.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IContainingProvider {

	/**
	 * This method defines the area the {@link BorderTraversingAlgorithm} is walking around.
	 * 
	 * @param x
	 *            X coordinate of the position.
	 * @param y
	 *            Y coordinate of the position.
	 * @return true if the given position is in the area that shall be surrounded by the border.<br>
	 *         false if the position is on the outside.
	 */
	boolean contains(int x, int y);

}
