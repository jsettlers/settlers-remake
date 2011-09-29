package jsettlers.logic.map.random.grid;

import java.awt.Point;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.random.geometry.LineDrawer;
import jsettlers.logic.map.random.geometry.Point2D;
import jsettlers.logic.map.random.landscape.LandscapeMesh;
import jsettlers.logic.map.random.landscape.MeshEdge;
import jsettlers.logic.map.random.landscape.MeshSite;
import jsettlers.logic.map.random.noise.NoiseGenerator;

/**
 * This class holds the map grid
 * 
 * @author michael
 */
public class MapGrid {
	private static final int MINDISTANCE = 2;
	private final ELandscapeType[][] types;
	private final MapObject[][] objects;
	private final int height;
	private final int width;
	
	private final NoiseGenerator heightGenerator = new NoiseGenerator();

	private Hashtable<MeshEdge, List<Point>> noisyEdges =
	        new Hashtable<MeshEdge, List<Point>>();

	private MapGrid(LandscapeMesh mesh, Random random) {
		this.width = mesh.getWidth();
		this.height = mesh.getHeight();
		types = new ELandscapeType[width][height];
		objects = new MapObject[width][height];

		loadLandscapeFromMesh(mesh, random);
	}

	private void loadLandscapeFromMesh(LandscapeMesh mesh, Random random) {
		createNoisyEdges(mesh, random);

		MeshEdge[][] edgeMap = new MeshEdge[width][height];

		for (MeshEdge edge : mesh.getEdges()) {
			LineDrawer drawer = new LineDrawer(noisyEdges.get(edge));
			for (ISPosition2D position : drawer) {
				short x = clampX(position.getX());
				short y = clampY(position.getY());
				edgeMap[x][y] = edge;
			}
		}
		System.out.println("mapped edges");

		for (int x = 0; x < width; x++) {
			if (edgeMap[x][0] == null) {
				edgeMap[x][0] = mesh.getEdges()[0];
			}
			if (edgeMap[x][height - 1] == null) {
				edgeMap[x][height - 1] = mesh.getEdges()[0];
			}
		}

		for (int y = 0; y < height; y++) {
			if (edgeMap[0][y] == null) {
				edgeMap[0][y] = mesh.getEdges()[0];
			}
			if (edgeMap[width - 1][y] == null) {
				edgeMap[width - 1][y] = mesh.getEdges()[0];
			}
		}
		System.out.println("fixed borders");

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (edgeMap[x][y] != null) {
					types[x][y] = GridLandscapeType.forEdge(edgeMap[x][y]);
				}
			}
		}
		System.out.println("added site borders");

		for (MeshSite site : mesh.getSites()) {
			Point center = site.getCenter().getIntPoint();
			Queue<ISPosition2D> sitePoints =
			        new ConcurrentLinkedQueue<ISPosition2D>();
			sitePoints.offer(new ShortPoint2D(center.x, center.y));
			ELandscapeType landscape =
			        GridLandscapeType.convert(site.getLandscape());

			while (!sitePoints.isEmpty()) {
				ISPosition2D point = sitePoints.poll();
				short x = point.getX();
				short y = point.getY();
				if (types[x][y] == null) {
					types[x][y] = landscape;
					for (ISPosition2D toAdd : new MapNeighboursArea(point)) {
						sitePoints.offer(toAdd);
					}
				}
			}
		}
		System.out.println("filled sites");

		for (int x = 0; x < width; x++) {
			// line 0 is guaranteed to not to be null
			for (int y = 1; y < height; y++) {
				if (types[x][y] == null) {
					types[x][y] = types[x][y - 1];
				}
			}
		}
		System.out.println("ceanup finished");
	}

	private short clampY(short y) {
		if (y <= 0) {
			return 0;
		} else if (y >= height) {
			return (short) (height - 1);
		} else {
			return y;
		}
	}

	private short clampX(short x) {
		if (x <= 0) {
			return 0;
		} else if (x >= width) {
			return (short) (width - 1);
		} else {
			return x;
		}
	}

	private void createNoisyEdges(LandscapeMesh mesh, Random random) {
		for (MeshEdge edge : mesh.getEdges()) {
			if (!noisyEdges.contains(edge)) {
				noisyEdges.put(edge, createNoisyEdge(edge, random));
			}
		}
	}

	private List<Point> createNoisyEdge(MeshEdge edge, Random random) {
		LinkedList<Point> points = new LinkedList<Point>();

		Point2D startPoint = edge.getStart();
		Point2D endPoint = edge.getEnd();
		if (edge.getLeft() == null || edge.getRight() == null) {
			points.add(startPoint.getIntPoint());
			points.add(endPoint.getIntPoint());
			return points;
		} else {
			points.add(startPoint.getIntPoint());
			addSubdivided(points, startPoint, endPoint, edge.getLeft()
			        .getCenter(), edge.getRight().getCenter(), random);
			points.add(endPoint.getIntPoint());
			return points;
		}

	}

	private void addSubdivided(LinkedList<Point> points, Point2D start,
	        Point2D end, Point2D left, Point2D right, Random random) {
		if (start.distanceSquared(end) < MINDISTANCE * MINDISTANCE) {
			return;
		}
		double rand1 = random.nextDouble() * .6 + .2;
		double rand2 = random.nextDouble() * .6 + .2;

		Point2D left1 = start.interpolate(left, rand1);
		Point2D left2 = left.interpolate(end, rand2);
		Point2D right1 = start.interpolate(right, rand2);
		Point2D right2 = right.interpolate(end, rand1);

		Point2D middle = left1.interpolate(right2, rand2);

		addSubdivided(points, start, middle, left1, right1, random);
		points.add(middle.getIntPoint());
		addSubdivided(points, middle, end, left2, right2, random);
	}

	public static MapGrid createFromLandscapeMesh(LandscapeMesh mesh,
	        Random random) {
		return new MapGrid(mesh, random);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ELandscapeType getLandscape(int x, int y) {
		return types[x][y];
	}

	public MapObject getMapObject(int x, int y) {
		MapObject object = objects[x][y];
		if (objects[x][y] instanceof PlaceholderObject) {
			return null;
		} else {
			return object;
		}
	}

	public void setMapObject(int x, int y, MapObject object) {
		objects[x][y] = object;
	}

	public boolean isReserved(int x, int y) {
		return objects[x][y] != null;
	}

	public void reserveArea(int x, int y, int radius) {
		for (ISPosition2D pos : new MapShapeFilter(new MapCircle((short) x,
		        (short) y, radius), width, height)) {
			if (objects[pos.getX()][pos.getY()] == null) {
				objects[pos.getX()][pos.getY()] =
				        PlaceholderObject.getInstance();
			}
		}
	}

	public boolean isObjectPlaceable(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height && !isReserved(x, y);
	}

	public byte getLandscapeHeight(short x, short y) {
		return (byte) Math.max(0, 10 + heightGenerator.getNoise(x, y) * 25);
    }
}
