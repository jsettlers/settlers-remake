package jsettlers.algorithms.interfaces;

import jsettlers.algorithms.traversing.borders.BorderTraversingAlgorithm;

/**
 * This interface defines a method a contains(x,y) method needed by several algorithms (e.g. {@link BorderTraversingAlgorithm} ).
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
