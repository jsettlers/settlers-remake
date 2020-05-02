/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Set;

import jsettlers.algorithms.partitions.IBlockingProvider;
import jsettlers.algorithms.partitions.PartitionCalculatorAlgorithm;
import jsettlers.algorithms.previewimage.IPreviewImageDataSupplier;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.landscape.EResourceType;
import jsettlers.common.logging.MilliStopWatch;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.Parallelogram;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.RelativePoint;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.loading.data.IMapData;
import jsettlers.logic.map.loading.data.objects.BuildingMapDataObject;
import jsettlers.logic.map.loading.data.objects.DecorationMapDataObject;
import jsettlers.logic.map.loading.data.objects.MapDataObject;
import jsettlers.logic.map.loading.data.objects.MapTreeObject;
import jsettlers.logic.map.loading.data.objects.MovableObject;
import jsettlers.logic.map.loading.data.objects.StackMapDataObject;
import jsettlers.logic.map.loading.data.objects.StoneMapDataObject;
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
import jsettlers.mapcreator.mapvalidator.tasks.error.ValidateLandscape;
import jsettlers.mapcreator.mapvalidator.tasks.error.ValidatePlayerStartPosition;

/**
 * This is the map data of a map that is being created by the editor.
 *
 * @author michael
 */
public class MapData implements IMapData {
	private final int width;
	private final int height;

	private final ELandscapeType[][]  landscapes;
	private final byte[][]            heights;
	private final ObjectContainer[][] objects;

	private final EResourceType[][] resources;
	private final byte[][]          resourceAmount;
	private final short[][]         blockedPartitions;

	private MapDataDelta undoDelta;
	private int          playerCount;

	/**
	 * Start position of all player, will be converted to a border in ValidatePlayerStartPosition
	 *
	 * @see ValidatePlayerStartPosition
	 */
	private ShortPoint2D[] playerStarts;

	private       byte[][]    lastPlayers;
	private       boolean[][] lastBorders;
	private       boolean[][] failpoints;

	private       IGraphicsBackgroundListener backgroundListener;

	public MapData(int width, int height, int playerCount, ELandscapeType ground) {
		if (width <= 0 || height <= 0) {
			throw new IllegalArgumentException("width and height must be positive");
		}

		if (width > Short.MAX_VALUE || height > Short.MAX_VALUE) {
			throw new IllegalArgumentException("width and height must be less than " + (Short.MAX_VALUE + 1));
		}

		if (playerCount <= 0 || playerCount > CommonConstants.MAX_PLAYERS) {
			throw new IllegalArgumentException("Player count must be 1.." + CommonConstants.MAX_PLAYERS);
		}

		this.playerCount = playerCount;
		this.playerStarts = new ShortPoint2D[playerCount];
		for (int i = 0; i < playerCount; i++) {
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

		for (int x = 0; x < width; x++) {
			Arrays.fill(landscapes[x], ELandscapeType.WATER8);
			Arrays.fill(resources[x], EResourceType.FISH);
		}
		resetUndoDelta();
		fill(ground, new Parallelogram((short)0, (short)0, (short)width, (short)height));
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
		List<ShortPoint2D> points = area.stream().filterBounds(width, height).toList();
		fill(type, null, points);

		if(backgroundListener != null) {
			int size = points.size();
			for(int i = 0; i != size; i++) {
				ShortPoint2D pt = points.get(i);
				backgroundListener.backgroundShapeChangedAt(pt.x, pt.y);
			}
		}
	}

	private void fill(ELandscapeType type, ELandscapeType ignore, List<ShortPoint2D> points) {
		boolean is_root = true;
		int size = points.size();
		for(int i = 0; i != size; i++) {
			ShortPoint2D pt = points.get(i);
			if(!type.isRoot(landscapes[pt.x][pt.y])) {
				is_root = false;
				break;
			}
		}

		if(!is_root) {
			fill(type.getDirectRoot(), type, points);
			replaceDirect(type.getDirectRoot(), type, points);
		}

		replaceDown(type, type, ignore, points);
	}

	private void replaceDown(ELandscapeType from, ELandscapeType to, ELandscapeType ignore, List<ShortPoint2D> points) {
		from.getDirectChildren().stream().filter(type -> ignore==null||type!=ignore).forEach(childType -> replaceDown(childType, from, null, points));
		replaceDirect(from, to, points);
	}

	private void replaceDirect(ELandscapeType from, ELandscapeType to, List<ShortPoint2D> points) {
		int size = points.size();
		for(int i = 0; i != size; i++) {
			ShortPoint2D pt = points.get(i);
			if(landscapes[pt.x][pt.y] == from) setLandscape(pt.x, pt.y, to);
		}
	}

	public boolean contains(int tx, int ty) {
		return tx >= 0 && tx < width && ty >= 0 && ty < height;
	}

	private boolean setLandscape(int x, int y, ELandscapeType type) {
		if(type == landscapes[x][y]) return true;

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

		/* Only water is allowed on the map border.
		To fix falsely set non water tiles (e.g. in the original map editor) one must be allowed
		to reduce the current layer to its root layer (GRASS to SAND and then SAND to WATER1).*/
		if(type != ELandscapeType.WATER8 && !type.isRoot(landscapes[x][y]) && (x <= 1 || y <= 1 || x >= width - 2 || y >= height - 2)) return false;

		boolean has_hight_diff = false;
		for(EDirection neighbor : EDirection.VALUES) {
			int nx = neighbor.gridDeltaX + x;
			int ny = neighbor.gridDeltaY + y;

			if(contains(nx, ny)) {
				if(!type.isAllowedNeighbor(landscapes[nx][ny])) return false;
				if(heights[x][y] != heights[nx][ny]) has_hight_diff = true;
			}
		}

		if(type.isFlat() && has_hight_diff) return false;

		if(resourceAmount[x][y] > 0 && !type.canHoldResource(resources[x][y])) decreaseResourceTo(x, y, (byte) 0);

		undoDelta.addLandscapeChange(x, y, landscapes[x][y]);
		landscapes[x][y] = type;
		return true;
	}

	public void setListener(IGraphicsBackgroundListener backgroundListener) {
		this.backgroundListener = backgroundListener;
	}

	public void placeObject(MapDataObject object, int x, int y) {
		ObjectContainer container;
		boolean isShip = false;
		ProtectContainer protector = ProtectContainer.getInstance();
		Set<ELandscapeType> landscapes = null;
		if (object instanceof MapTreeObject) {
			container = TreeObjectContainer.getInstance();
		} else if (object instanceof StoneMapDataObject) {
			container = new StoneObjectContainer((StoneMapDataObject) object);
		} else if (object instanceof MovableObject) {
			MovableObject movableObject = (MovableObject) object;
			container = new MovableObjectContainer(movableObject, x, y);
			isShip = movableObject.getType().isShip();
		} else if (object instanceof StackMapDataObject) {
			container = new StackContainer((StackMapDataObject) object);
		} else if (object instanceof BuildingMapDataObject) {
			container = new BuildingContainer((BuildingMapDataObject) object, new ShortPoint2D(x, y));
			landscapes = ((BuildingMapDataObject) object).getType().getGroundTypes();
			protector = new ProtectLandscapeConstraint(((BuildingMapDataObject) object).getType());
		} else if (object instanceof DecorationMapDataObject) {
			container = new MapObjectContainer((DecorationMapDataObject) object);
		} else {
			return; // error!
		}

		ShortPoint2D start = new ShortPoint2D(x, y);
		for (RelativePoint p : container.getProtectedArea()) {
			ShortPoint2D abs = p.calculatePoint(start);
			if (!contains(abs.x, abs.y)
				|| objects[abs.x][abs.y] != null
				|| (!isShip && !landscapeAllowsObjects(getLandscape(abs.x, abs.y)))
				|| (landscapes != null && !landscapes.contains(getLandscape(abs.x, abs.y)))) {

				return;
			}
		}

		for (RelativePoint p : container.getProtectedArea()) {
			ShortPoint2D abs = p.calculatePoint(start);
			objects[abs.x][abs.y] = protector;
			undoDelta.removeObject(abs.x, abs.y);
		}
		objects[x][y] = container;
		undoDelta.removeObject(x, y);
	}

	public void setHeight(int x, int y, int height) {
		boolean is_flat = false;
		int minNeighborH = height;
		int maxNeighborH = height;
		for (EDirection dir : EDirection.VALUES) {
			int nx = dir.gridDeltaX + x;
			int ny = dir.gridDeltaY + y;

			if(!contains(nx, ny)) continue;

			int neighborH = getLandscapeHeight(nx, ny);

			minNeighborH = Math.min(minNeighborH, neighborH);
			maxNeighborH = Math.max(maxNeighborH, neighborH);
			if(landscapes[nx][ny].isFlat()) is_flat = true;
		}

		if(is_flat) {
			if(minNeighborH != maxNeighborH) return;
			height = minNeighborH;

		} else {
			if (height > heights[x][y]) {
				height = Math.max(maxNeighborH - ValidateLandscape.MAX_HEIGHT_DIFF, height);
				height = Math.min(minNeighborH + ValidateLandscape.MAX_HEIGHT_DIFF, height);
			} else {
				height = Math.min(minNeighborH + ValidateLandscape.MAX_HEIGHT_DIFF, height);
				height = Math.max(maxNeighborH - ValidateLandscape.MAX_HEIGHT_DIFF, height);
			}
		}

		byte safeHeight;
		if (height >= Byte.MAX_VALUE) {
			safeHeight = Byte.MAX_VALUE;
		} else if (height <= 0) {
			safeHeight = 0;
		} else {
			safeHeight = (byte) height;
		}

		undoDelta.addHeightChange(x, y, heights[x][y]);
		heights[x][y] = safeHeight;

		if (backgroundListener != null) {
			backgroundListener.backgroundShapeChangedAt((short) x, (short) y);
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
	public MapDataObject getMapObject(int x, int y) {
		ObjectContainer container = objects[x][y];
		if (container != null) {
			return container.getMapObject();
		} else {
			return null;
		}
	}

	@Override
	public byte getLandscapeHeight(int x, int y) {
		if(!contains(x, y)) return 0;
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
			setHeight(c.x, c.y, c.height);
			backgroundListener.backgroundShapeChangedAt(c.x, c.y);
			c = c.next;
		}

		// landscape
		LandscapeChange cl = delta.getLandscapeChanges();
		while (cl != null) {
			inverse.addLandscapeChange(cl.x, cl.y, landscapes[cl.x][cl.y]);
			landscapes[cl.x][cl.y] = cl.landscape;
			backgroundListener.backgroundShapeChangedAt(cl.x, cl.y);
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
		return playerCount;
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
		public void setMapObject(int x, int y, MapDataObject object) {
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
		public void setDimension(int width, int height, int playerCount) {
			data = new MapData(width, height, playerCount, ELandscapeType.GRASS);
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
			newPlayerStarts[i] = i < playerCount ? playerStarts[i] : new ShortPoint2D(width / 2, height / 2);
		}
		this.playerCount = maxPlayer;
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
		if(!landscapes[x][y].canHoldResource(type)) return;

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
			IBlockingProvider.DEFAULT_IMPLEMENTATION
		);
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
