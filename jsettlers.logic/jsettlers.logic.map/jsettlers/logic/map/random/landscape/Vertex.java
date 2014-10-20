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
public class Vertex extends Point2D implements Comparable<Vertex>{

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
