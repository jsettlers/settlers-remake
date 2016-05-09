/*******************************************************************************
 * Copyright (c) 2015, 2016
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
package jsettlers.mapcreator.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.algorithms.previewimage.IPreviewImageDataSupplier;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.logging.MilliStopWatch;
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
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.loading.newmap.FreshMapSerializer;
import jsettlers.logic.map.loading.newmap.FreshMapSerializer.IMapDataReceiver;
import jsettlers.mapcreator.data.MapDataDelta.HeightChange;
import jsettlers.mapcreator.data.MapDataDelta.LandscapeChange;
import jsettlers.mapcreator.data.MapDataDelta.ObjectAdder;
import jsettlers.mapcreator.data.MapDataDelta.ObjectRemover;
import jsettlers.mapcreator.data.MapDataDelta.ResourceChanger;
import jsettlers.mapcreator.data.MapDataDelta.StartPointSetter;
import jsettlers.mapcreator.data.objects.BuildingContainer;
import jsettlers.mapcreator.data.objects.MapObjectContainer;
import jsettlers.mapcreator.data.objects.MovableObjectContainer;
import jsettlers.mapcreator.data.objects.ObjectContainer;
import jsettlers.mapcreator.data.objects.ProtectContainer;
import jsettlers.mapcreator.data.objects.StackContainer;
import jsettlers.mapcreator.data.objects.StoneObjectContainer;
import jsettlers.mapcreator.data.objects.TreeObjectContainer;
import jsettlers.mapcreator.mapvalidator.tasks.error.ValidatePlayerStartPosition;

/**
 * This is the map data of a map that is beeing created by the editor.
 * 
 * @author michael
 */
public class MapData implements IMapData {
	private final int width;
	private final int height;

	private final ELandscapeType[][] landscapes;
	private final byte[][] heights;
	private final ObjectContainer[][] objects;

	private final EResourceType[][] resources;
	private final byte[][] resourceAmount;
	private final short[][] blockedPartitions;

	private MapDataDelta undoDelta;
	private int playercount;

	/**
	 * Start position of all player, will be converted to a border in ValidatePlayerStartPosition
	 * 
	 * @see ValidatePlayerStartPosition
	 */
	private ShortPoint2D[] playerStarts;

	private byte[][] lastPlayers;
	private boolean[][] lastBorders;
	private final boolean[][] doneBuffer;
	private boolean[][] failpoints;

	private final LandscapeFader fader = new LandscapeFader();
	private IGraphicsBackgroundListener backgroundListener;

	public MapData(int width, int height, int playercount, ELandscapeType ground) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("width and height must be positive");
		}

		if (width > Short.MAX_VALUE || height > Short.MAX_VALUE) {
			throw new IllegalArgumentException("width and height must be less than " + (Short.MAX_VALUE + 1));
		}

		if (playercount <= 0 || playercount > CommonConstants.MAX_PLAYERS) {
			throw new IllegalArgumentException("Player count must be 1.." + CommonConstants.MAX_PLAYERS);
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
		this.resourceAmount = new byte[width][height];
		this.resources = new EResourceType[width][height];
		this.objects = new ObjectContainer[width][height];
		this.blockedPartitions = new short[width][height];
		this.doneBuffer = new boolean[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				landscapes[x][y] = ground;
				resources[x][y] = EResourceType.FISH;
			}
		}
		resetUndoDelta();
	}

	public MapData(IMapData data) {
		this(data.getWidth(), data.getHeight(), data.getPlayerCount(), ELandscapeType.GRASS);

		for (short x = 0; x < width; x++) {
			for (short y = 0; y < height; y++) {
				landscapes[x][y] = data.getLandscape(x, y);
				heights[x][y] = data.getLandscapeHeight(x, y);
				resourceAmount[x][y] = data.getResourceAmount(x, y);
				resources[x][y] = data.getResourceType(x, y);
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
	 */
	public void fill(ELandscapeType type, IMapArea area) {

		assert (isAllFalse(this.doneBuffer));

		System.out.println("filling");
		int ymin = Integer.MAX_VALUE;
		int ymax = Integer.MIN_VALUE;
		int xmin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE;
		for (ShortPoint2D pos : area) {
			short x = pos.x;
			short y = pos.y;
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
				// we cannot use done[x][y], because done flag is set for other tiles, too.
				if (area.contains(new ShortPoint2D(x, y))) {
					for (EDirection dir : EDirection.VALUES) {
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

		System.out.println("Found " + tasks.size() + " tiles, starting to work on them...");
		while (!tasks.isEmpty()) {
			FadeTask task = tasks.poll();
			assert contains(task.x, task.y);

			ELandscapeType[] fade = fader.getLandscapesBetween(task.type, landscapes[task.x][task.y]);

			if (fade == null || fade.length <= 2) {
				continue; // nothing to do
			}

			ELandscapeType newLandscape = fade[1];
			setLandscape(task.x, task.y, newLandscape);
			for (EDirection dir : EDirection.VALUES) {
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

	private static class FadeTask {

		private final ELandscapeType type;
		private final int y;
		private final int x;

		/**
		 * A task to set the landscape at a given point to the landscpae close to type.
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
				LandscapeConstraint constraint = (LandscapeConstraint) objects[x][y];
				if (!constraint.getAllowedLandscapes().contains(type)) {
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

	private static boolean allowsLandscape(ELandscapeType type, LandscapeConstraint constraint) {
		for (ELandscapeType t : constraint.getAllowedLandscapes()) {
			if (t == type) {
				return true;
			}
		}
		return false;
	}

	public void setListener(IGraphicsBackgroundListener backgroundListener) {
		this.backgroundListener = backgroundListener;
	}

	public void placeObject(MapObject object, int x, int y) {
		ObjectContainer container = null;
		ProtectContainer protector = ProtectContainer.getInstance();
		Set<ELandscapeType> landscapes = null;
		if (object instanceof MapTreeObject) {
			container = TreeObjectContainer.getInstance();
		} else if (object instanceof MapStoneObject) {
			container = new StoneObjectContainer((MapStoneObject) object);
		} else if (object instanceof MovableObject) {
			container = new MovableObjectContainer((MovableObject) object, x, y);
		} else if (object instanceof StackObject) {
			container = new StackContainer((StackObject) object);
		} else if (object instanceof BuildingObject) {
			container = new BuildingContainer((BuildingObject) object, new ShortPoint2D(x, y));
			landscapes = ((BuildingObject) object).getType().getGroundTypes();
			protector = new ProtectLandscapeConstraint(((BuildingObject) object).getType());
		} else if (object instanceof MapDecorationObject) {
			container = new MapObjectContainer((MapDecorationObject) object);
		} else {
			return; // error!
		}

		boolean allowed = true;
		ShortPoint2D start = new ShortPoint2D(x, y);
		for (RelativePoint p : container.getProtectedArea()) {
			ShortPoint2D abs = p.calculatePoint(start);
			if (!contains(abs.x, abs.y) || objects[abs.x][abs.y] != null || !landscapeAllowsObjects(getLandscape(abs.x, abs.y))
					|| !landscapes.contains(getLandscape(abs.x, abs.y))) {
				allowed = false;
			}
		}

		if (allowed) {
			for (RelativePoint p : container.getProtectedArea()) {
				ShortPoint2D abs = p.calculatePoint(start);
				objects[abs.x][abs.y] = protector;
				undoDelta.removeObject(abs.x, abs.y);
			}
			objects[x][y] = container;
			undoDelta.removeObject(x, y);
		}
	}

	public void setHeight(int x, int y, int height) {
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

		if (backgroundListener != null) {
			backgroundListener.backgroundChangedAt((short) x, (short) y);
		}
	}

	private static boolean landscapeAllowsObjects(ELandscapeType type) {
		return !type.isWater() && type != ELandscapeType.SNOW && type != ELandscapeType.RIVER1 && type != ELandscapeType.RIVER2
				&& type != ELandscapeType.RIVER3 && type != ELandscapeType.RIVER4 && type != ELandscapeType.MOOR;
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
	public ShortPoint2D getStartPoint(int player) {
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

		ResourceChanger res = delta.getChangeResources();
		while (res != null) {
			inverse.changeResource(res.x, res.y, resources[res.x][res.y], resourceAmount[res.x][res.y]);
			resources[res.x][res.y] = res.type;
			resourceAmount[res.x][res.y] = res.amount;
			res = res.next;
		}

		// start points
		StartPointSetter start = delta.getStartPoints();
		while (start != null) {
			inverse.setStartPoint(start.player, playerStarts[start.player]);
			playerStarts[start.player] = start.pos;
			start = start.next;
		}
		return inverse;
	}

	@Override
	public int getPlayerCount() {
		return playercount;
	}

	/**
	 * Class to read serialized data
	 */
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
			data = new MapData(width, height, playercount, ELandscapeType.GRASS);
		}

		@Override
		public void setResources(int x, int y, EResourceType type, byte amount) {
			data.resources[x][y] = type;
			data.resourceAmount[x][y] = amount;
		}

		@Override
		public void setBlockedPartition(int x, int y, short blockedPartition) {
			data.blockedPartitions[x][y] = blockedPartition;
		}
	}

	/**
	 * Read serialized file
	 * 
	 * @param in
	 *            input stream
	 * @return MapData
	 * @throws IOException
	 */
	public static MapData deserialize(InputStream in) throws IOException {
		MapDataReceiver receiver = new MapDataReceiver();
		FreshMapSerializer.deserialize(receiver, in);
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
				ShortPoint2D pos = point.calculatePoint(start);

				if (contains(pos.x, pos.y)) {
					undoDelta.addObject(pos.x, pos.y, objects[pos.x][pos.y]);
					objects[pos.x][pos.y] = null;
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

	/**
	 * Start position of a player, will be converted to a border in ValidatePlayerStartPosition
	 * 
	 * @see ValidatePlayerStartPosition
	 * 
	 * @param activePlayer
	 *            Player
	 * @param pos
	 *            Position
	 */
	public void setStartPoint(int activePlayer, ShortPoint2D pos) {
		this.undoDelta.setStartPoint(activePlayer, playerStarts[activePlayer]);
		this.playerStarts[activePlayer] = pos;
	}

	public void setFailpoints(boolean[][] failpoints) {
		this.failpoints = failpoints;
	}

	public boolean isFailpoint(int x, int y) {
		return failpoints != null && failpoints[x][y];
	}

	/**
	 * Set the maximum player count
	 * 
	 * @param maxPlayer
	 *            Min: 1, Max: CommonConstants.MAX_PLAYERS
	 */
	public void setMaxPlayers(short maxPlayer) {
		if (maxPlayer <= 0 || maxPlayer > CommonConstants.MAX_PLAYERS) {
			throw new IllegalArgumentException("Player count must be 1.." + CommonConstants.MAX_PLAYERS);
		}

		ShortPoint2D[] newPlayerStarts = new ShortPoint2D[maxPlayer];
		for (int i = 0; i < maxPlayer; i++) {
			newPlayerStarts[i] = i < playercount ? playerStarts[i] : new ShortPoint2D(width / 2, height / 2);
		}
		this.playercount = maxPlayer;
		this.playerStarts = newPlayerStarts;

	}

	@Override
	public EResourceType getResourceType(short x, short y) {
		return resources[x][y];
	}

	@Override
	public byte getResourceAmount(short x, short y) {
		return resourceAmount[x][y];
	}

	public void addResource(int x, int y, EResourceType type, byte amount) {
		if (resourceAmount[x][y] <= amount) {
			this.undoDelta.changeResource(x, y, resources[x][y], resourceAmount[x][y]);
			resourceAmount[x][y] = amount;
			resources[x][y] = type;
		}
	}

	public void decreaseResourceTo(int x, int y, byte amount) {
		if (resourceAmount[x][y] > amount) {
			this.undoDelta.changeResource(x, y, resources[x][y], resourceAmount[x][y]);
			resourceAmount[x][y] = amount;
		}
	}

	@Override
	public short getBlockedPartition(short x, short y) {
		return blockedPartitions[x][y];
	}

	/**
	 * This method must be called before the data is saved to execute actions that need to be done before that.
	 */
	public void doPreSaveActions() {
		calculateBlockedPartitions();
	}

	private void calculateBlockedPartitions() {
		MilliStopWatch watch = new MilliStopWatch();

		BitSet notBlockedSet = new BitSet(width * height);
		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				notBlockedSet.set(x + width * y, !landscapes[x][y].isBlocking);
			}
		}

		PartitionCalculatorAlgorithm partitionCalculator = new PartitionCalculatorAlgorithm(0, 0, width, height, notBlockedSet,
				IBlockingProvider.DEFAULT_IMPLEMENTATION);
		partitionCalculator.calculatePartitions();

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				blockedPartitions[x][y] = partitionCalculator.getPartitionAt(x, y);
			}
		}

		watch.stop("Calculating partitions needed");
		System.out.println("found " + partitionCalculator.getNumberOfPartitions() + " partitions.");
	}

	public IPreviewImageDataSupplier getPreviewImageDataSupplier() {
		return new IPreviewImageDataSupplier() {
			@Override
			public byte getLandscapeHeight(short x, short y) {
				return heights[x][y];
			}

			@Override
			public ELandscapeType getLandscape(short x, short y) {
				return landscapes[x][y];
			}
		};
	}
}
