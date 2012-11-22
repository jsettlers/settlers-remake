package jsettlers.logic.algorithms.traversing.area;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.traversing.ITraversingVisitor;
import jsettlers.logic.algorithms.traversing.borders.BorderTraversingAlgorithm;
import jsettlers.logic.algorithms.traversing.borders.IContainingProvider;

/**
 * This algorithm offers a method to traverse a connected area with an {@link ITraversingVisitor}.
 * 
 * @author Andreas Eberle
 * 
 */
public final class AreaTraversingAlgorithm {

	/**
	 * No instances of this class shall be created.
	 */
	private AreaTraversingAlgorithm() {
	}

	/**
	 * Traverses a connected area and calls the given visitor for every position.
	 * 
	 * @param containingProvider
	 *            {@link IContainingProvider} defining what's part of the area and what isn't.
	 * @param visitor
	 *            The visitor that will be called on every position in the connected area reachable from the given start position.
	 * @param startPos
	 *            A start position lying on the border of the expected area.
	 * @param maxHeight
	 *            The maximum y value that can be reached.
	 * @return true if the traversing finished<br>
	 *         false if the visitor returned false at any position and therefore caused the traversing to be canceled.
	 */
	public static boolean traverseArea(IContainingProvider containingProvider, ITraversingVisitor visitor, ShortPoint2D startPos, int maxHeight) {
		BordersInformationVisitor bordersInfoVisitor = new BordersInformationVisitor(startPos, maxHeight);

		// traverse the border to get the borders of the area.
		BorderTraversingAlgorithm.traverseBorder(containingProvider, startPos, bordersInfoVisitor, false);

		int minY = bordersInfoVisitor.minY;
		int maxY = bordersInfoVisitor.maxY;
		int[] xMin = bordersInfoVisitor.xMin;
		int[] xMax = bordersInfoVisitor.xMax;

		for (int y = minY; y <= maxY; y++) {
			if (!traverseLine(containingProvider, visitor, xMin[y], xMax[y], y)) {
				return false;
			}
		}

		return true;
	}

	private static boolean traverseLine(IContainingProvider containingProvider, ITraversingVisitor visitor, int xMin, int xMax, int y) {
		for (int x = xMin; x <= xMax; x++) {
			if (containingProvider.contains(x, y)) {
				if (!visitor.visit(x, y)) {
					return false;
				}
			}
		}

		return true;
	}
}
