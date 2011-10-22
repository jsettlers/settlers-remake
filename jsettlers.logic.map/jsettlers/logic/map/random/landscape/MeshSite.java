package jsettlers.logic.map.random.landscape;

import java.util.ArrayList;
import java.util.HashSet;

import jsettlers.logic.map.random.geometry.Point2D;

public class MeshSite {

	private MeshEdge[] edges = new MeshEdge[0];

	private MeshLandscapeType landscape = MeshLandscapeType.UNSPECIFIED;

	private byte height = 0;

	private boolean fixed = false;
	
	private HillPolicy hillPolicy = HillPolicy.FLAT;

	private boolean temporaryFlag = false;

	public MeshSite() {
	}

	/**
	 * Asserts that the edges form a circle
	 * 
	 * @param edges
	 */
	protected void setMeshEdges(MeshEdge[] edges) {
		this.edges = edges;
	}

	public Point2D getCenter() {
		if (edges.length == 0) {
			return new Point2D(0,0);
		} else {
			double x = 0;
			double y = 0;
			for (MeshEdge edge : edges) {
				Vertex point = edge.getClockPoint(this);
				x += point.getX();
				y += point.getY();
			}
			return new Point2D(x / edges.length, y / edges.length);
		}
	}

	public void setLandscape(MeshLandscapeType landscape, HillPolicy hillPolicy) {
		this.landscape = landscape;
		this.hillPolicy = hillPolicy;
	}

	public MeshLandscapeType getLandscape() {
		return landscape;
	}
	
	public HillPolicy getHillPolicy() {
	    return hillPolicy;
    }

	public void setHeight(byte height) {
		this.height = height;
	}

	public byte getHeight() {
		return height;
	}

	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}

	public boolean isFixed() {
		return fixed;
	}

	/**
	 * Set a temporary flag, for search ,...
	 * <p>
	 * It has to be unset after the operation!
	 * 
	 * @param temporaryFlag
	 *            The flag
	 */
	public void setTemporaryFlag(boolean temporaryFlag) {
		this.temporaryFlag = temporaryFlag;
	}

	public boolean isTemporaryFlag() {
		return temporaryFlag;
	}

	public MeshSite[] getNeighbours() {
		ArrayList<MeshSite> found = new ArrayList<MeshSite>();
		for (MeshEdge edge : edges) {
			MeshSite opposite = edge.getOppositeSite(this);
			if (opposite != null) {
				found.add(opposite);
			}
		}
		return found.toArray(new MeshSite[found.size()]);
	}

	public int getSize() {
		// TODO: compute
		return 200;
	}

	/**
	 * Gets all neighbours, also the ones that only share a common vertex.
	 * 
	 * @return The neighbours
	 */
	public HashSet<MeshSite> getAllNeighbours() {
		HashSet<MeshSite> sites = new HashSet<MeshSite>();
		for (MeshEdge e : edges) {
			Vertex v = e.getClockPoint(this);
			for (MeshSite site : v.getNeighbourSites()) {
				sites.add(site);
			}
		}
		return sites;
	}

	public MeshEdge[] getEdges() {
	    return edges;
    }

}
