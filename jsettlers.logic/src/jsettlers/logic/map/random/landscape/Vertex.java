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
package jsettlers.logic.map.random.landscape;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jsettlers.logic.map.random.geometry.Point2D;

/**
 * this is a vertex of the border mesh.
 * 
 * @author michael
 */
public class Vertex extends Point2D implements Comparable<Vertex> {

	private LinkedList<MeshEdge> edges = new LinkedList<MeshEdge>();

	private float height = Float.POSITIVE_INFINITY;

	public Vertex(double x, double y) {
		super(x, y);
	}

	protected void addMeshEdge(MeshEdge edge) {
		synchronized (edges) {
			edges.add(edge);
		}
	}

	public LinkedList<MeshEdge> getEdges() {
		return edges;
	}

	public List<MeshSite> getNeighbourSites() {
		synchronized (edges) {
			ArrayList<MeshSite> sites = new ArrayList<MeshSite>();
			for (MeshEdge edge : edges) {
				MeshSite site = edge.getClockSite(this);
				if (site != null) {
					sites.add(site);
				}
			}
			return sites;
		}
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	@Override
	public int compareTo(Vertex o) {
		return Float.compare(height, o.height);
	}
}
