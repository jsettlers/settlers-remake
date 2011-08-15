package jsettlers.logic.map.random.landscape;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jsettlers.logic.map.random.geometry.Point2D;

/**
 * this is a vertex of the border mesh.
 * 
 * @author michael
 */
public class Vertex extends Point2D {

	private HashSet<MeshEdge> edges = new HashSet<MeshEdge>();

	public Vertex(double x, double y) {
		super(x, y);
	}

	protected void addMeshEdge(MeshEdge edge) {
		synchronized (edges) {
			edges.add(edge);
		}
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
}
