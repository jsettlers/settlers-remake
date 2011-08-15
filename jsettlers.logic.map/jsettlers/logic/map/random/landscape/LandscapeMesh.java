package jsettlers.logic.map.random.landscape;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This is the landscape mesh. It contains of Landscape tiles and their
 * connections.
 * <p>
 * Each tile has a border.
 * 
 * @author michael
 */
public class LandscapeMesh {
	// Point2D[] borderVerteces;

	private MeshEdge[] edges;

	private MeshSite[] sites;

	private final int width;

	private final int height;

	private LandscapeMesh(int width, int height, MeshSite[] sites,
	        MeshEdge[] edges) {
		this.width = width;
		this.height = height;
		this.sites = sites;
		this.edges = edges;

		setSiteEdges();
		setVertexEdges();
	}

	private void setVertexEdges() {
		for (MeshEdge edge : edges) {
			edge.getStart().addMeshEdge(edge);
			edge.getEnd().addMeshEdge(edge);
		}
	}

	private void setSiteEdges() {
		for (MeshSite site : sites) {
			setSiteEdges(site);
		}
	}

	private void setSiteEdges(MeshSite site) {
		LinkedList<MeshEdge> found = new LinkedList<MeshEdge>();
		for (MeshEdge edge : edges) {
			if (edge.isBorderOf(site)) {
				found.add(edge);
			}
		}

		if (found.size() > 2) {
			MeshEdge[] edges = new MeshEdge[found.size()];
			edges[0] = found.poll();
			Vertex currentPoint = edges[0].getCounterclockPoint(site);

			EDGELOOP: for (int i = 1; i < edges.length; i++) {
				Iterator<MeshEdge> it = found.iterator();
				while (it.hasNext()) {
					MeshEdge next = it.next();
					if (next.getClockPoint(site).equals(currentPoint)) {
						edges[i] = next;
						it.remove();
						currentPoint = next.getCounterclockPoint(site);
						continue EDGELOOP;
					}
				}
				throw new IllegalArgumentException(
				        "The edges of the site are not continuous");
			}
			site.setMeshEdges(edges);
		} else {
			throw new IllegalArgumentException(
			        "Each site must have at least 3 edges");
		}
	}

	public static LandscapeMesh getRandomMesh(int width, int height,
	        Random random) {
		int xsites = Math.max(width / 20, 1);
		int ysites = Math.max(height / 20, 1);

		MeshSite[] sites = new MeshSite[xsites * ysites * 2];
		MeshSite[][] xySites = new MeshSite[xsites * 2][ysites];
		for (int x = 0; x < xsites * 2; x++) {
			for (int y = 0; y < ysites; y++) {
				MeshSite site = new MeshSite();
				sites[y * xsites * 2 + x] = site;
				xySites[x][y] = site;
			}
		}

		Vertex[][] borderPoints = new Vertex[(xsites + 1)][(ysites + 1)];
		for (int x = 0; x < xsites + 1; x++) {
			for (int y = 0; y < ysites + 1; y++) {
				borderPoints[x][y] =
				        new Vertex((double) width / xsites * x, (double) height
				                / ysites * y);
			}
		}

		ArrayList<MeshEdge> edges = new ArrayList<MeshEdge>();
		// horizontal borders
		for (int x = 0; x < xsites; x++) {
			// first line
			edges.add(new MeshEdge(xySites[x * 2][0], null,
			        borderPoints[x][ysites - 0],
			        borderPoints[x + 1][ysites - 0]));
			for (int y = 1; y < ysites; y++) {
				edges.add(new MeshEdge(xySites[x * 2][y],
				        xySites[x * 2 + 1][y - 1], borderPoints[x][ysites - y],
				        borderPoints[x + 1][ysites - y]));
			}
			// last line
			edges.add(new MeshEdge(null, xySites[x * 2 + 1][ysites - 1],
			        borderPoints[x][ysites - ysites],
			        borderPoints[x + 1][ysites - ysites]));
		}
		// vertical borders: / and \
		for (int y = 0; y < ysites; y++) {
			// first line
			edges.add(new MeshEdge(null, xySites[0][y], borderPoints[0][ysites
			        - y], borderPoints[0][ysites - (y + 1)]));
			for (int x = 1; x < xsites * 2; x++) {
				edges.add(new MeshEdge(xySites[x - 1][y], xySites[x][y],
				        borderPoints[(x + 1) / 2][ysites - y],
				        borderPoints[x / 2][ysites - (y + 1)]));
			}
			edges.add(new MeshEdge(xySites[xsites * 2 - 1][y], null,
			        borderPoints[xsites][ysites - y],
			        borderPoints[xsites][ysites - (y + 1)]));
		}

		LandscapeMesh mesh =
		        new LandscapeMesh(width, height, sites,
		                edges.toArray(new MeshEdge[edges.size()]));
		return mesh;
	}

	public MeshSite getSiteWithCriterium(SiteCriterium criterium, Random rand) {
		ArrayList<MeshSite> possible = getPossibleSites(criterium);
		if (possible.isEmpty()) {
			return null;
		} else {
			return possible.get(rand.nextInt(possible.size()));
		}
	}

	private ArrayList<MeshSite> getPossibleSites(SiteCriterium criterium) {
		ArrayList<MeshSite> possible = new ArrayList<MeshSite>();
		for (MeshSite site : sites) {
			if (criterium.matchesCriterium(site)) {
				possible.add(site);
			}
		}
		return possible;
	}

	/**
	 * TODO: allow user to select a specific start point
	 * 
	 * @param criterium
	 * @param random
	 * @param size
	 * @return
	 */
	public MeshSite[] getSitesWithCriterium(SiteCriterium criterium,
	        Random random, int size) {
		List<MeshSite> leftOver = getPossibleSites(criterium);
		List<MeshSite> found = null;
		int foundSize = 0;

		// try some bfs
		while (!leftOver.isEmpty() && foundSize < size) {
			Queue<MeshSite> possible;
			int currentlyFoundSize;
			possible = new ConcurrentLinkedQueue<MeshSite>();
			List<MeshSite> currentlyFound = new LinkedList<MeshSite>();

			MeshSite start = leftOver.get(random.nextInt(leftOver.size()));
			start.setTemporaryFlag(true);
			currentlyFound.add(start);
			currentlyFoundSize = start.getSize();
			addUnflagedNeighboursToQueue(start, possible, leftOver);

			while (!possible.isEmpty() && currentlyFoundSize < size) {
				MeshSite current = possible.poll();
				currentlyFound.add(current);
				currentlyFoundSize += current.getSize();

				addUnflagedNeighboursToQueue(current, possible, leftOver);
			}

			for (MeshSite site : currentlyFound) {
				site.setTemporaryFlag(false);
				leftOver.remove(site);
			}
			for (MeshSite site : possible) {
				site.setTemporaryFlag(false);
			}
			if (currentlyFoundSize > foundSize) {
				found = currentlyFound;
				foundSize = currentlyFoundSize;
			}
		}

		if (found == null) {
			return new MeshSite[0];
		} else {
			return found.toArray(new MeshSite[found.size()]);
		}
	}

	/**
	 * adds all unflaged neighbours and sets their flag.
	 * 
	 * @param start
	 * @param list
	 */
	private void addUnflagedNeighboursToQueue(MeshSite start,
	        Queue<MeshSite> list, List<MeshSite> allowed) {
		for (MeshSite site : start.getNeighbours()) {
			if (allowed.contains(site) && !site.isTemporaryFlag()) {
				list.add(site);
				site.setTemporaryFlag(true);
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public MeshEdge[] getEdges() {
		return edges;
	}

	public MeshSite[] getSites() {
		return sites;
	}

}
