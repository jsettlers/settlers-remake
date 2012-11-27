package jsettlers.logic.algorithms.traversing.area;

import java.util.BitSet;
import java.util.LinkedList;

import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.traversing.ITraversingVisitor;
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
	 *            A start position somewhere in the area.
	 * @param width
	 *            The width of the area. So the maximum x value may be width - 1;
	 * @param height
	 *            The height of the area. So the maximum y value may be height - 1;
	 * 
	 * @return true if the traversing finished<br>
	 *         false if the visitor returned false at any position and therefore caused the traversing to be canceled.
	 */
	public static boolean traverseArea(IContainingProvider containingProvider, ITraversingVisitor visitor, ShortPoint2D startPos, int width,
			int height) {

		LinkedList<ShortPoint2D> stack = new LinkedList<ShortPoint2D>();
		stack.push(startPos);
		BitSet touched = new BitSet(width * height);
		touched.set(startPos.x + startPos.y * width);

		while (!stack.isEmpty()) {
			ShortPoint2D currPos = stack.poll();
			if (!visitor.visit(currPos.x, currPos.y)) {
				return false;
			}

			for (EDirection dir : EDirection.values) {
				int nextX = dir.gridDeltaX + currPos.x;
				int nextY = dir.gridDeltaY + currPos.y;

				if (0 <= nextX && nextX <= width && 0 <= nextY && nextY <= height) {
					int nextIdx = nextX + nextY * width;
					if (!touched.get(nextIdx) && containingProvider.contains(nextX, nextY)) {
						stack.push(new ShortPoint2D(nextX, nextY));
						touched.set(nextIdx);
					}
				}
			}
		}

		return true;
	}
}
