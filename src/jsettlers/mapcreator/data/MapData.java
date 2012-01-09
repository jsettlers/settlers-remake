package jsettlers.mapcreator.data;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;

/**
 * This is the map data of a map that is beeing created by the editor.
 * 
 * @author michael
 */
public class MapData implements IMapData {
	private final int width;
	private final int height;

	private final ELandscapeType[][] landscapes;
	private final ObjectContainer[][] objects;

	private final LandscapeFader fader = new LandscapeFader();
	private IGraphicsBackgroundListener backgroundListener;
	private final byte[][] heights;

	public MapData(int width, int height) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(
			        "width and height must be positive");
		}

		if (width > Short.MAX_VALUE || height > Short.MAX_VALUE) {
			throw new IllegalArgumentException(
			        "width and height must be less than "
			                + (Short.MAX_VALUE + 1));
		}

		this.width = width;
		this.height = height;
		this.landscapes = new ELandscapeType[width][height];
		this.heights = new byte[width][height];
		this.objects = new ObjectContainer[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				landscapes[x][y] = ELandscapeType.GRASS;
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	/**
	 * Fills an area with the given landscape type.
	 * 
	 * @param type
	 * @param area
	 */
	public void fill(ELandscapeType type, IMapArea area) {
		boolean[][] done = new boolean[width][height];

		System.out.println("filling");
		for (ISPosition2D pos : area) {
			short x = pos.getX();
			short y = pos.getY();
			if (contains(x, y)) {
				setLandscape(x, y, type);
				done[x][y] = true;
			}
		}

		System.out.println("searching border tiles...");
		Queue<FadeTask> tasks = new ConcurrentLinkedQueue<FadeTask>();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (area.contains(new ShortPoint2D(x, y))) { // < we cannot use
					                                         // done[x][y],
					                                         // because done
					                                         // flag
					                                         // is set for other
					                                         // tiles, too.
					for (EDirection dir : EDirection.valuesCached()) {
						int tx = x + dir.getGridDeltaX();
						int ty = y + dir.getGridDeltaY();
						if (contains(tx, ty) && !done[tx][ty]) {
							tasks.add(new FadeTask(tx, ty, type));
							done[tx][ty] = true;
						}
					}
				}
			}
		}

		System.out.println("Found " + tasks.size()
		        + " tiles, starting to work on them...");
		while (!tasks.isEmpty()) {
			FadeTask task = tasks.poll();
			ELandscapeType[] fade =
			        fader.getLandscapesBetween(task.type,
			                landscapes[task.x][task.y]);

			if (fade == null || fade.length <= 2) {
				continue; // nothing to do
			}

			ELandscapeType newLandscape = fade[1];
			System.out.println("want to dim from " + landscapes[task.x][task.y]
			        + " to " + task.type + " with " + newLandscape);
			setLandscape(task.x, task.y, newLandscape);
			for (EDirection dir : EDirection.valuesCached()) {
				int nx = task.x + dir.getGridDeltaX();
				int ny = task.y + dir.getGridDeltaY();
				if (contains(nx, ny) && !done[nx][ny]) {
					tasks.add(new FadeTask(nx, ny, newLandscape));
					done[nx][ny] = true;
				}
			}
		}
	}

	private boolean contains(int tx, int ty) {
		return tx >= 0 && tx < width && ty >= 0 && ty < height;
	}

	private static class FadeTask {

		private final ELandscapeType type;
		private final int y;
		private final int x;

		/**
		 * A task to set the landscape at a given point to the landscpae close
		 * to type.
		 * 
		 * @param x
		 *            The x position where to fade
		 * @param y
		 *            The y position where to fade
		 * @param type
		 *            The type of landscape to face (almoast) to.
		 */
		public FadeTask(int x, int y, ELandscapeType type) {
			this.x = x;
			this.y = y;
			this.type = type;
		}

		@Override
		public String toString() {
			return "FadeTask[" + x + ", " + y + ", " + type + "]";
		}

	}

	private void setLandscape(int x, int y, ELandscapeType type) {
		if (isWater(type)) {
			if (objects[x][y] != null) {
				return;
			}
			setHeight(x, y, 0);
		}

		landscapes[x][y] = type;
		if (backgroundListener != null) {
			backgroundListener.backgroundChangedAt((short) x, (short) y);
		}
	}

	public void setListener(IGraphicsBackgroundListener backgroundListener) {
		this.backgroundListener = backgroundListener;
	}

	public void placeObject(MapObject object, int x, int y) {
		ObjectContainer container = null;
		if (object instanceof MapTreeObject) {
			container = TreeObjectContainer.getInstance();
		} else if (object instanceof MapStoneObject) {
			container = new StoneObjectContainer((MapStoneObject) object);
		} else if (object instanceof MovableObject) {
			container =
			        new MovableObjectContainer((MovableObject) object, x, y);
		} else if (object instanceof StackObject) {
			container = new StackContainer((StackObject) object);
		} else {
			return; // error!
		}

		boolean allowed = true;
		ISPosition2D start = new ShortPoint2D(x, y);
		for (RelativePoint p : container.getProtectedArea()) {
			ISPosition2D abs = p.calculatePoint(start);
			if (!contains(abs.getX(), abs.getY())
			        || objects[abs.getX()][abs.getY()] != null
			        || isWater(abs.getX(), abs.getY())) {
				allowed = false;
			}
		}

		if (allowed) {
			for (RelativePoint p : container.getProtectedArea()) {
				ISPosition2D abs = p.calculatePoint(start);
				objects[abs.getX()][abs.getY()] =
				        ProtectContainer.getInstance();
			}
			objects[x][y] = container;
		}
	}

	public void setHeight(int x, int y, int height) {
		if (isWater(x, y) && height != 0) {
			return;
		}

		if (height >= Byte.MAX_VALUE) {
			heights[x][y] = Byte.MAX_VALUE;
		} else if (height <= 0) {
			heights[x][y] = 0;
		} else {
			heights[x][y] = (byte) height;
		}
		if (backgroundListener != null) {
			backgroundListener.backgroundChangedAt((short) x, (short) y);
		}
	}

	private boolean isWater(int x, int y) {
		return isWater(landscapes[x][y]);
	}

	private static boolean isWater(ELandscapeType type) {
		return type == ELandscapeType.WATER;
	}

	@Override
	public ELandscapeType getLandscape(int x, int y) {
		return landscapes[x][y];
	}

	@Override
	public MapObject getMapObject(int x, int y) {
		ObjectContainer container = objects[x][y];
		if (container != null) {
			return container.getMapObject();
		} else {
			return null;
		}
	}

	@Override
	public byte getLandscapeHeight(int x, int y) {
		return heights[x][y];
	}

	@Override
	public ISPosition2D getStartPoint(int player) {
		return new ShortPoint2D(width / 2, height / 2);
	}

	public IMapObject getMapObjectContainer(int x, int y) {
		ObjectContainer container = objects[x][y];
		if (container instanceof IMapObject) {
			return (IMapObject) container;
		} else {
			return null;
		}
	}

	public IMovable getMovableContainer(int x, int y) {
		ObjectContainer container = objects[x][y];
		if (container instanceof IMovable) {
			return (IMovable) container;
		} else {
			return null;
		}
	}
}
