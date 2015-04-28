/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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
