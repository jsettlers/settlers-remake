package jsettlers.logic.map.random.geometry;

import jsettlers.logic.map.random.voronoi.VoronoiVertex;

/**
 * This is an edge in the map landscape graph, and also one in it's voronoi graph
 * 
 * @author michael
 */
public class MapMeshEdge {
	private VoronoiVertex v1;
	private VoronoiVertex v2;

	private final MapAreaPoint point1;
	private final MapAreaPoint point2;

	/**
	 * Creates a new mesh edge between the two mgiven points with an undefined voronoi start and end.
	 * 
	 * @param point1
	 *            The first point.
	 * @param point2
	 *            The second one.
	 */
	public MapMeshEdge(MapAreaPoint point1, MapAreaPoint point2) {
		this.point1 = point1;
		this.point2 = point2;

	}

	/**
	 * Adds the given vertex as an end of the edge, if there are undefined ends.
	 * 
	 * @param v
	 *            the new vertex
	 * @throws IllegalStateException
	 *             if a third vertex was added.
	 */
	public void addVoronoiVertex(VoronoiVertex v) {
		if (v1 == null) {
			v1 = v;
		} else if (v2 == null) {
			v2 = v;
		} else {
			throw new IllegalStateException("There are already two verteces added");
		}
	}

	public MapAreaPoint getPoint1() {
	    return point1;
    }

	public MapAreaPoint getPoint2() {
	    return point2;
    }
}
