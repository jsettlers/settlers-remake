package jsettlers.logic.map.random.voronoi;

public interface Beach {

	/**
	 * Adds a given beach line point to the graph.
	 * 
	 * @param point
	 */
	public void add(VoronioSite point, CircleEventManager mgr);

	/**
	 * Gets the beach line part at the given position
	 * 
	 * @param y
	 *            The y-position
	 * @return The beach line part at that position.
	 */
	BeachLinePart getBeachAt(double sweepx, double y);

	/**
	 * Gets the beach line one at the bottom of a given beachline
	 * 
	 * @param current
	 *            may not be null
	 * @return The one at the bottom
	 */
	BeachLinePart getBottom(BeachLinePart current);

	/**
	 * Gets the beach line one at the top of a given beachline
	 * 
	 * @param current
	 *            may not be null
	 * @return The one at the bottom
	 */
	BeachLinePart getTop(BeachLinePart current);

}
