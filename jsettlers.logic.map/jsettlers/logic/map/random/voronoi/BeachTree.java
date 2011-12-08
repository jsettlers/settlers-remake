package jsettlers.logic.map.random.voronoi;

/**
 * This is a tree that orders the items by y-coordinate.
 * 
 * @author michael
 */
public class BeachTree implements Beach {
	private BeachTreeItem root;

	public boolean isEmpty() {
		return root == null;
	}

	@Override
	public void add(VoronioSite point, CircleEventManager mgr) {
		if (isEmpty()) {
			root = new BeachLinePart(point);
		} else {
			// double y = point.getY();
			// TODO
		}
	}

	@Override
	public BeachLinePart getBeachAt(double sweepx, double y) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the part of the beach line below the given item
	 * 
	 * @param brokenArc
	 *            The current arc
	 * @return The arc below the current
	 */
	@Override
	public BeachLinePart getBottom(BeachLinePart current) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the part of the beach line above the given item
	 * 
	 * @param brokenArc
	 *            The current arc
	 * @return The arc above the current
	 */
	@Override
	public BeachLinePart getTop(BeachLinePart current) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the separation point above the given point
	 * 
	 * @param middle
	 *            The middle point
	 * @return The found separator, or <code>null</code> if it was the topmost line.
	 */
	public BeachSeparator findSeparatorAbove(@SuppressWarnings("unused") BeachLinePart middle) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the separation point below the given point
	 * 
	 * @param middle
	 *            The middle point
	 * @return The found separator, or <code>null</code> if it was the topmost line.
	 */
	public BeachSeparator findSeparatorBelow(@SuppressWarnings("unused") BeachLinePart middle) {
		// TODO Auto-generated method stub
		return null;
	}
}
