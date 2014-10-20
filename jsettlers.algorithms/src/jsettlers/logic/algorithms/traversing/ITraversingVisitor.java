package jsettlers.logic.algorithms.traversing;

import jsettlers.logic.algorithms.traversing.area.AreaTraversingAlgorithm;
import jsettlers.logic.algorithms.traversing.borders.BorderTraversingAlgorithm;

/**
 * Interface defining the methods to be able to traverse borders or the areas with the {@link BorderTraversingAlgorithm} or the
 * {@link AreaTraversingAlgorithm}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface ITraversingVisitor {
	/**
	 * Called when the given coordinate is visited..
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
