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
package jsettlers.logic.map.random.grid;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.map.shapes.MapShapeFilter;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.random.geometry.LineDrawer;
import jsettlers.logic.map.random.geometry.Point;
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
public class MapGrid implements IMapData {
	private static final int MINDISTANCE = 2;

	private final int height;
	private final int width;

	private final ELandscapeType[][] types;
	private final MapObject[][] objects;

	private final NoiseGenerator heightGenerator = new NoiseGenerator();

	private Hashtable<MeshEdge, List<Point>> noisyEdges = new Hashtable<MeshEdge, List<Point>>();
	private final ShortPoint2D[] playerstarts;

	private MapGrid(LandscapeMesh mesh, Random random, ShortPoint2D[] playerstarts) {
		this.playerstarts = playerstarts;
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
			for (ShortPoint2D position : drawer) {
				short x = clampX(position.x);
				short y = clampY(position.y);
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

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (edgeMap[x][y] != null) {
					types[x][y] = GridLandscapeType.forEdge(edgeMap[x][y]);
				}
			}
		}

		for (MeshSite site : mesh.getSites()) {
			Point center = site.getCenter().getIntPoint();
			Queue<ShortPoint2D> sitePoints = new ConcurrentLinkedQueue<ShortPoint2D>();
			sitePoints.offer(new ShortPoint2D(center.getX(), center.getY()));
			ELandscapeType landscape = GridLandscapeType.convert(site.getLandscape());

			while (!sitePoints.isEmpty()) {
				ShortPoint2D point = sitePoints.poll();
				short x = point.x;
				short y = point.y;
				if (types[x][y] == null) {
					types[x][y] = landscape;
					for (ShortPoint2D toAdd : new MapNeighboursArea(point)) {
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
			addSubdivided(points, startPoint, endPoint, edge.getLeft().getCenter(), edge.getRight().getCenter(), random);
			points.add(endPoint.getIntPoint());
			return points;
		}

	}

	private void addSubdivided(LinkedList<Point> points, Point2D start, Point2D end, Point2D left, Point2D right, Random random) {
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

	public static MapGrid createFromLandscapeMesh(LandscapeMesh mesh, Random random, ShortPoint2D[] playerstarts) {
		return new MapGrid(mesh, random, playerstarts);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.logic.map.random.grid.IMapData#getWidth()
	 */
	@Override
	public int getWidth() {
		return width;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.logic.map.random.grid.IMapData#getHeight()
	 */
	@Override
	public int getHeight() {
		return height;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.logic.map.random.grid.IMapData#getLandscape(int, int)
	 */
	@Override
	public ELandscapeType getLandscape(int x, int y) {
		return types[x][y];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.logic.map.random.grid.IMapData#getMapObject(int, int)
	 */
	@Override
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
		for (ShortPoint2D pos : new MapShapeFilter(new MapCircle((short) x, (short) y, radius), width, height)) {
			if (objects[pos.x][pos.y] == null) {
				objects[pos.x][pos.y] = PlaceholderObject.getInstance();
			}
		}
	}

	public boolean isObjectPlaceable(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height && !isReserved(x, y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.logic.map.random.grid.IMapData#getLandscapeHeight(short, short)
	 */
	@Override
	public byte getLandscapeHeight(int x, int y) {
		return (byte) Math.max(0, 10 + heightGenerator.getNoise(x, y) * 25);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jsettlers.logic.map.random.grid.IMapData#getStartPoint(int)
	 */
	@Override
	public ShortPoint2D getStartPoint(int player) {
		if (player < 0 || player >= playerstarts.length) {
			return new ShortPoint2D(getWidth() / 2, getHeight() / 2);
		} else {
			return playerstarts[player];
		}
	}

	@Override
	public int getPlayerCount() {
		return playerstarts.length;
	}

	public static EResourceType getResourceType(ELandscapeType landscape, Random rand) {
		if (landscape == ELandscapeType.MOUNTAIN) {
			return EResourceType.values[rand.nextInt(3)];
		} else {
			return EResourceType.FISH;
		}
	}

	public static byte getResourceAmount(ELandscapeType landscape, Random rand) {
		if (landscape == ELandscapeType.MOUNTAIN || landscape.isWater()) {
			return (byte) rand.nextInt(Byte.MAX_VALUE + 1);
		} else {
			return 0;
		}
	}

	@Override
	public EResourceType getResourceType(short x, short y) {
		return EResourceType.FISH;
	}

	@Override
	public byte getResourceAmount(short x, short y) {
		return 0;
	}

	@Override
	public short getBlockedPartition(short x, short y) {
		return 0; // TODO @Michael Zangl: let the blocked partitions be calculated.
	}

}
