package jsettlers.mapcreator.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IMapData;
import jsettlers.common.map.object.BuildingObject;
import jsettlers.common.map.object.MapDecorationObject;
import jsettlers.common.map.object.MapObject;
import jsettlers.common.map.object.MapStoneObject;
import jsettlers.common.map.object.MapTreeObject;
import jsettlers.common.map.object.MovableObject;
import jsettlers.common.map.object.StackObject;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.save.MapDataSerializer;
import jsettlers.logic.map.save.MapDataSerializer.IMapDataReceiver;
import jsettlers.mapcreator.data.MapDataDelta.HeightChange;
import jsettlers.mapcreator.data.MapDataDelta.LandscapeChange;
import jsettlers.mapcreator.data.MapDataDelta.ObjectAdder;
import jsettlers.mapcreator.data.MapDataDelta.ObjectRemover;
import jsettlers.mapcreator.data.MapDataDelta.StartPointSetter;
import jsettlers.mapcreator.data.objects.BuildingContainer;
import jsettlers.mapcreator.data.objects.MapObjectContainer;
import jsettlers.mapcreator.data.objects.MovableObjectContainer;
import jsettlers.mapcreator.data.objects.ObjectContainer;
import jsettlers.mapcreator.data.objects.ProtectContainer;
import jsettlers.mapcreator.data.objects.StackContainer;
import jsettlers.mapcreator.data.objects.StoneObjectContainer;
import jsettlers.mapcreator.data.objects.TreeObjectContainer;

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
	private MapDataDelta undoDelta;
	private final int playercount;
	private ISPosition2D[] playerStarts;

	private byte[][] lastPlayers;
	private boolean[][] lastBorders;
	private boolean[][] doneBuffer;
	private boolean[][] failpoints;

	public MapData(int width, int height, int playercount, ELandscapeType ground) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException(
			        "width and height must be positive");
		}

		if (width > Short.MAX_VALUE || height > Short.MAX_VALUE) {
			throw new IllegalArgumentException(
			        "width and height must be less than "
			                + (Short.MAX_VALUE + 1));
		}

		if (playercount <= 0 || playercount >= CommonConstants.MAX_PLAYERS) {
			throw new IllegalArgumentException("Player count must be 1..32");
		}

		this.playercount = playercount;
		this.playerStarts = new ShortPoint2D[playercount];
		for (int i = 0; i < playercount; i++) {
			playerStarts[i] = new ShortPoint2D(width / 2, height / 2);
		}

		this.width = width;
		this.height = height;
		this.landscapes = new ELandscapeType[width][height];
		this.heights = new byte[width][height];
		this.objects = new ObjectContainer[width][height];
		this.doneBuffer = new boolean[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				landscapes[x][y] = ground;
			}
		}
		resetUndoDelta();
	}

	public MapData(IMapData data) {
		this(data.getWidth(), data.getHeight(), data.getPlayerCount(),
		        ELandscapeType.GRASS);

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				landscapes[x][y] = data.getLandscape(x, y);
				heights[x][y] = data.getLandscapeHeight(x, y);
			}
		}
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (data.getMapObject(x, y) != null) {
					placeObject(data.getMapObject(x, y), x, y);
				}
			}
		}
		for (int i = 0; i < data.getPlayerCount(); i++) {
			playerStarts[i] = data.getStartPoint(i);
		}
		resetUndoDelta();
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	/**
	 * Fills an area with the given landscape type.
	 * 
	 * @param type
	 * @param area
	 * @param usedmaxy
	 * @param usedmaxx
	 * @param usedminy
	 * @param usedminx
	 */
	public void fill(ELandscapeType type, IMapArea area) {

		assert (isAllFalse(this.doneBuffer));

		System.out.println("filling");
		int ymin = Integer.MAX_VALUE;
		int ymax = Integer.MIN_VALUE;
		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		for (ISPosition2D pos : area) {
			short x = pos.getX();
			short y = pos.getY();
			if (contains(x, y)) {
				if (setLandscape(x, y, type)) {
					doneBuffer[x][y] = true;
					if (x < xmin) {
						xmin = x;
					}
					if (x > xmax) {
						xmax = x;
					}

					if (y < ymin) {
						ymin = y;
					}
					if (y > ymax) {
						ymax = y;
					}
				}

			}
		}
		if (ymin == Integer.MAX_VALUE) {
			return; // nothing done
		}

		if (xmin > 0) {
			xmin -= 1;
		}
		if (xmax < width - 1) {
			xmax += 1;
		}
		if (ymin > 0) {
			ymin -= 1;
		}
		if (ymax < width - 1) {
			ymax += 1;
		}

		System.out.println("searching border tiles...");
		Queue<FadeTask> tasks = new ConcurrentLinkedQueue<FadeTask>();
		for (int y = ymin; y < ymax; y++) {
			for (int x = xmin; x < xmax; x++) {
				if (area.contains(new ShortPoint2D(x, y))) { // < we cannot use
					                                         // done[x][y],
					                                         // because done
					                                         // flag
					                                         // is set for other
					                                         // tiles, too.
					for (EDirection dir : EDirection.values) {
						int tx = x + dir.getGridDeltaX();
						int ty = y + dir.getGridDeltaY();
						if (contains(tx, ty) && !doneBuffer[tx][ty]) {
							tasks.add(new FadeTask(tx, ty, type));
							doneBuffer[tx][ty] = true;

							if (tx < xmin) {
								xmin = tx;
							} else if (tx > xmax) {
								xmax = tx;
							}

							if (ty < ymin) {
								ymin = ty;
							} else if (ty > ymax) {
								ymax = ty;
							}
						}
					}
				}
			}
		}

		System.out.println("Found " + tasks.size()
		        + " tiles, starting to work on them...");
		while (!tasks.isEmpty()) {
			FadeTask task = tasks.poll();
			assert contains(task.x, task.y);

			ELandscapeType[] fade =
			        fader.getLandscapesBetween(task.type,
			                landscapes[task.x][task.y]);

			if (fade == null || fade.length <= 2) {
				continue; // nothing to do
			}

			ELandscapeType newLandscape = fade[1];
			setLandscape(task.x, task.y, newLandscape);
			for (EDirection dir : EDirection.values) {
				int nx = task.x + dir.getGridDeltaX();
				int ny = task.y + dir.getGridDeltaY();
				if (contains(nx, ny) && !doneBuffer[nx][ny]) {
					tasks.add(new FadeTask(nx, ny, newLandscape));
					doneBuffer[nx][ny] = true;

					if (nx < xmin) {
						xmin = nx;
					} else if (nx > xmax) {
						xmax = nx;
					}

					if (ny < ymin) {
						ymin = ny;
					} else if (ny > ymax) {
						ymax = ny;
					}
				}

			}
		}

		// reset done buffer
		for (int y = ymin; y <= ymax; y++) {
			for (int x = xmin; x <= xmax; x++) {
				doneBuffer[x][y] = false;
			}
		}
		assert (isAllFalse(this.doneBuffer));
	}

	private static boolean isAllFalse(boolean[][] doneBuffer2) {
		for (boolean[] arr : doneBuffer2) {
			for (boolean b : arr) {
				if (b) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean contains(int tx, int ty) {
		return tx >= 0 && tx < width && ty >= 0 && ty < height;
	}

	private static final class MapDataReceiver implements IMapDataReceiver {
		MapData data = null;

		@Override
		public void setPlayerStart(byte player, int x, int y) {
			data.playerStarts[player] = new ShortPoint2D(x, y);
		}

		@Override
		public void setMapObject(int x, int y, MapObject object) {
			data.placeObject(object, x, y);
		}

		@Override
		public void setLandscape(int x, int y, ELandscapeType type) {
			data.landscapes[x][y] = type;
		}

		@Override
		public void setHeight(int x, int y, byte height) {
			data.heights[x][y] = height;
		}

		@Override
		public void setDimension(int width, int height, int playercount) {
			data =
			        new MapData(width, height, playercount,
			                ELandscapeType.GRASS);
		}
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

	private boolean setLandscape(int x, int y, ELandscapeType type) {
		if (objects[x][y] != null) {
			if (!landscapeAllowsObjects(type)) {
				return false;
			}
			if (objects[x][y] instanceof LandscapeConstraint) {
				LandscapeConstraint constraint =
				        (LandscapeConstraint) objects[x][y];
				if (!allowsLandscape(type, constraint)) {
					return false;
				}
			}
		}

		undoDelta.addLandscapeChange(x, y, landscapes[x][y]);
		landscapes[x][y] = type;
		if (backgroundListener != null) {
			backgroundListener.backgroundChangedAt((short) x, (short) y);
		}
		return true;
	}

	private static boolean allowsLandscape(ELandscapeType type,
	        LandscapeConstraint constraint) {
		boolean allowed = false;
		for (ELandscapeType t : constraint.getAllowedLandscapes()) {
			if (t == type) {
				allowed = true;
			}
		}
		return allowed;
	}

	public void setListener(IGraphicsBackgroundListener backgroundListener) {
		this.backgroundListener = backgroundListener;
	}

	public void placeObject(MapObject object, int x, int y) {
		ObjectContainer container = null;
		ProtectContainer protector = ProtectContainer.getInstance();
		ELandscapeType[] landscapes = null;
		if (object instanceof MapTreeObject) {
			container = TreeObjectContainer.getInstance();
		} else if (object instanceof MapStoneObject) {
			container = new StoneObjectContainer((MapStoneObject) object);
		} else if (object instanceof MovableObject) {
			container =
			        new MovableObjectContainer((MovableObject) object, x, y);
		} else if (object instanceof StackObject) {
			container = new StackContainer((StackObject) object);
		} else if (object instanceof BuildingObject) {
			container =
			        new BuildingContainer((BuildingObject) object,
			                new ShortPoint2D(x, y));
			landscapes = ((BuildingObject) object).getType().getGroundtypes();
			protector =
			        new ProtectLandscapeConstraint(((BuildingObject) object)
			                .getType().getGroundtypes());
		} else if (object instanceof MapDecorationObject) {
			container = new MapObjectContainer((MapDecorationObject) object);
		} else {
			return; // error!
		}

		boolean allowed = true;
		ISPosition2D start = new ShortPoint2D(x, y);
		for (RelativePoint p : container.getProtectedArea()) {
			ISPosition2D abs = p.calculatePoint(start);
			if (!contains(abs.getX(), abs.getY())
			        || objects[abs.getX()][abs.getY()] != null
			        || !landscapeAllowsObjects(getLandscape(abs.getX(),
			                abs.getY()))
			        || !listAllowsLandscape(landscapes,
			                getLandscape(abs.getX(), abs.getY()))) {
				allowed = false;
			}
		}

		if (allowed) {
			for (RelativePoint p : container.getProtectedArea()) {
				ISPosition2D abs = p.calculatePoint(start);
				objects[abs.getX()][abs.getY()] = protector;
				undoDelta.removeObject(abs.getX(), abs.getY());
			}
			objects[x][y] = container;
			undoDelta.removeObject(x, y);
		}
	}

	public static boolean listAllowsLandscape(ELandscapeType[] landscapes2,
	        ELandscapeType landscape) {
		if (landscapes2 == null) {
			return true;
		} else {
			for (ELandscapeType type : landscapes2) {
				if (type == landscape) {
					return true;
				}
			}
			return false;
		}
	}

	public void setHeight(int x, int y, int height) {
		// if (objects[x][y] instanceof LandscapeConstraint &&
		// !((LandscapeConstraint) objects[x][y]).allowHeightChange()) {
		// return;
		// }

		byte safeheight;
		if (height >= Byte.MAX_VALUE) {
			safeheight = Byte.MAX_VALUE;
		} else if (height <= 0) {
			safeheight = 0;
		} else {
			safeheight = (byte) height;
		}
		undoDelta.addHeightChange(x, y, heights[x][y]);
		heights[x][y] = safeheight;
		// if (objects[x][y] instanceof BuildingContainer) {
		// ShortPoint2D center = new ShortPoint2D(x, y);
		// for (RelativePoint r : ((BuildingContainer)
		// objects[x][y]).getMapObject().getType().getBlockedTiles()) {
		// ISPosition2D pos = r.calculatePoint(center);
		// undoDelta.addHeightChange(pos.getX(), pos.getY(),
		// heights[pos.getX()][pos.getY()]);
		// heights[pos.getX()][pos.getY()] = safeheight;
		// }
		// }

		if (backgroundListener != null) {
			backgroundListener.backgroundChangedAt((short) x, (short) y);
		}
	}

	private static boolean landscapeAllowsObjects(ELandscapeType type) {
		return !type.isWater() && type != ELandscapeType.SNOW
		        && type != ELandscapeType.RIVER1
		        && type != ELandscapeType.RIVER2
		        && type != ELandscapeType.RIVER3
		        && type != ELandscapeType.RIVER4 && type != ELandscapeType.MOOR;
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
		return playerStarts[player];
	}

	public ObjectContainer getMapObjectContainer(int x, int y) {
		return objects[x][y];
	}

	public IMovable getMovableContainer(int x, int y) {
		ObjectContainer container = objects[x][y];
		if (container instanceof IMovable) {
			return (IMovable) container;
		} else {
			return null;
		}
	}

	public void resetUndoDelta() {
		undoDelta = new MapDataDelta();
	}

	public MapDataDelta getUndoDelta() {
		return undoDelta;
	}

	/**
	 * Applys a map delta. Does not do checking, so use with care!
	 * 
	 * @param delta
	 */
	public MapDataDelta apply(MapDataDelta delta) {
		MapDataDelta inverse = new MapDataDelta();

		// heights
		HeightChange c = delta.getHeightChanges();
		while (c != null) {
			inverse.addHeightChange(c.x, c.y, heights[c.x][c.y]);
			heights[c.x][c.y] = c.height;
			backgroundListener.backgroundChangedAt(c.x, c.y);
			c = c.next;
		}

		// landscape
		LandscapeChange cl = delta.getLandscapeChanges();
		while (cl != null) {
			inverse.addLandscapeChange(cl.x, cl.y, landscapes[cl.x][cl.y]);
			landscapes[cl.x][cl.y] = cl.landscape;
			backgroundListener.backgroundChangedAt(cl.x, cl.y);
			cl = cl.next;
		}

		// objects
		ObjectRemover remove = delta.getRemoveObjects();
		while (remove != null) {
			inverse.addObject(remove.x, remove.y, objects[remove.x][remove.y]);
			objects[remove.x][remove.y] = null;
			remove = remove.next;
		}
		
		ObjectAdder adder = delta.getAddObjects();
		while (adder != null) {
			inverse.removeObject(adder.x, adder.y);
			objects[adder.x][adder.y] = adder.obj;
			adder = adder.next;
		}

		// start points
		StartPointSetter start = delta.getStartPoints();
		while (start != null) {
			inverse.setStartPoint(start.player, playerStarts[start.player]);
			playerStarts[start.player] = start.pos;
		}
		return inverse;
	}

	@Override
	public int getPlayerCount() {
		return playercount;
	}

	public static MapData deserialize(InputStream in) throws IOException {
		MapDataReceiver receiver = new MapDataReceiver();
		MapDataSerializer.deserialize(receiver, in);
		return receiver.data;
	}

	public void deleteObject(int x, int y) {
		ObjectContainer obj = objects[x][y];
		if (obj instanceof ProtectContainer) {
			return;
		} else if (obj != null) {
			undoDelta.addObject(x, y, obj);
			objects[x][y] = null;
			ShortPoint2D start = new ShortPoint2D(x, y);
			RelativePoint[] area = obj.getProtectedArea();
			for (RelativePoint point : area) {
				ISPosition2D pos = point.calculatePoint(start);

				if (contains(pos.getX(), pos.getY())) {
					undoDelta.addObject(pos.getX(), pos.getY(),
					        objects[pos.getX()][pos.getY()]);
					objects[pos.getX()][pos.getY()] = null;
				}
			}
		}
	}

	public boolean isBorder(int x, int y) {
		return lastBorders != null && lastBorders[x][y];
	}

	public byte getPlayer(int x, int y) {
		return lastPlayers != null ? lastPlayers[x][y] : (byte) -1;
	}

	public void setPlayers(byte[][] lastPlayers) {
		this.lastPlayers = lastPlayers;
	}

	public void setBorders(boolean[][] lastBorders) {
		this.lastBorders = lastBorders;
	}

	public void setStartPoint(byte activePlayer, ISPosition2D pos) {
		this.undoDelta.setStartPoint(activePlayer, playerStarts[activePlayer]);
		this.playerStarts[activePlayer] = pos;
	}

	public void setFailpoints(boolean[][] failpoints) {
		this.failpoints = failpoints;
	}

	public boolean isFailpoint(int x, int y) {
		return failpoints != null && failpoints[x][y];
	}
}
