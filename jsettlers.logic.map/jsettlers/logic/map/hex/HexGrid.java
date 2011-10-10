package jsettlers.logic.map.hex;

import java.util.Random;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IHexMap;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapCircle;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.construction.IConstructionMarkableMap;
import jsettlers.logic.algorithms.landmarks.ILandmarksThreadMap;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.wrapper.IPathRequester;
import jsettlers.logic.algorithms.path.wrapper.IPathfinderWrapperMap;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingableGrid;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.management.GameManager;
import jsettlers.logic.management.MaterialJobPart;
import jsettlers.logic.map.hex.interfaces.IHexMovable;
import jsettlers.logic.map.hex.interfaces.IHexStack;
import jsettlers.logic.map.random.RandomMapEvaluator;
import jsettlers.logic.map.random.RandomMapFile;
import jsettlers.logic.map.random.grid.BuildingObject;
import jsettlers.logic.map.random.grid.MapGrid;
import jsettlers.logic.map.random.grid.MapObject;
import jsettlers.logic.map.random.grid.MapStoneObject;
import jsettlers.logic.map.random.grid.MapTreeObject;
import jsettlers.logic.map.random.grid.MovableObject;
import jsettlers.logic.map.random.grid.StackObject;
import jsettlers.logic.materials.stack.single.EStackType;
import jsettlers.logic.materials.stack.single.SingleMaterialStack;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.objects.IMapObjectsManagerGrid;
import jsettlers.logic.objects.MapObjectsManager;
import random.RandomSingleton;

public class HexGrid implements IPathfinderWrapperMap, IHexMap, ILandmarksThreadMap, IConstructionMarkableMap, IBuildingableGrid,
		IMapObjectsManagerGrid {
	private static HexGrid uniIns = null;

	private static final int TREE_COUNT = 1200;

	private static final int STONE_COUNT = 800;

	private final HexTile[][] map;

	private final short width;
	private final short height;

	private EBuildingType previewBuilding;

	private final MapObjectsManager mapObjectsManager;

	public static HexGrid get() {
		return uniIns;
	}

	/**
	 * creates a new HexGrid if there has not already been created one.
	 */
	@Deprecated
	public synchronized static void create(short width, short height) {
		if (uniIns == null) {
			uniIns = new HexGrid(width, height);
		}
	}

	/**
	 * creates a new HexGrid if there has not already been created one.
	 */
	public synchronized static void createRandom(String filename, int players, Random random) {
		if (uniIns == null) {
			RandomMapFile file = RandomMapFile.getByName(filename);
			RandomMapEvaluator evaluator = new RandomMapEvaluator(file.getInstructions(), players);
			evaluator.createMap(random);
			uniIns = new HexGrid(evaluator.getGrid());
		}
	}

	private HexGrid(short width, short height) {
		this.mapObjectsManager = new MapObjectsManager(this);
		this.width = width;
		this.height = height;
		map = new HexTile[height][width];

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				map[y][x] = new HexTile(x, y);
				map[y][x].setHeight((byte) RandomSingleton.getInt(0, 3));
			}
		}

		for (int i = 0; i < TREE_COUNT; i++) {
			short x, y;
			do {
				x = (short) (RandomSingleton.nextD() * width);
				y = (short) (RandomSingleton.nextD() * height);
			} while (map[y][x].hasMapObject());

			mapObjectsManager.executeSearchType(map[y][x], ESearchType.PLANTABLE_TREE);
		}

		for (int i = 0; i < STONE_COUNT; i++) {
			short x, y;
			do {
				x = (short) (RandomSingleton.nextD() * (width - 20) + 10);
				y = (short) (RandomSingleton.nextD() * (height - 20) + 10);
			} while (!isStonePlantable(new ShortPoint2D(x, y)));

			mapObjectsManager.addStone(map[y][x], RandomSingleton.getInt(4, 12));
		}

		createTestPlayerArea();

		createTestSea();

		int x = 50;
		for (EMaterialType type : EMaterialType.values()) {
			placeFullStack(type, (short) x, (short) (height - 10));
			x += 5;
		}

		placeFullStack(EMaterialType.PLANK, (short) 50, (short) (height - 20));
		placeFullStack(EMaterialType.PLANK, (short) 50, (short) (height - 30));
		placeFullStack(EMaterialType.PLANK, (short) 55, (short) (height - 25));
		placeFullStack(EMaterialType.STONE, (short) 60, (short) (height - 20));
		placeFullStack(EMaterialType.STONE, (short) 60, (short) (height - 30));

		for (x = 20; x < 70; x += 2) {
			setMovable(x, 5, EMovableType.BEARER, 1);
		}

		for (short y = 60; y < 8; y++) {
			for (x = 60; x < 80; x++) {
				setMovable(x, y, EMovableType.PIONEER, 1);
			}
		}
		// setMovable(20, 5, EMovableType.BEARER, 1);

		for (int i = 0; i < 2; i++)
			GameManager.requestMaterial(new MaterialJobPart(EMaterialType.PLANK, new ShortPoint2D(20, height - 40), (byte) 1));

		mapObjectsManager.executeSearchType(new ShortPoint2D(30, height - 30), ESearchType.PLANTABLE_CORN);

	}

	private boolean isStonePlantable(ShortPoint2D pos) {
		IMapArea circle = new MapCircle(pos, 5);
		for (ISPosition2D curr : circle) {
			HexTile tile = getTile(curr);
			if (tile == null || tile.isBlocked()) {
				return false;
			}
		}
		return true;
	}

	private void setMovable(int x, int y, EMovableType type, int player) {
		ShortPoint2D pos = new ShortPoint2D((short) x, (short) (height - y));
		if (!isBlocked(pos.getX(), pos.getY()))
			placeNewMovable(pos, new Movable(pos, type, (byte) player));
	}

	private HexGrid(MapGrid grid) {
		this.mapObjectsManager = new MapObjectsManager(this);
		this.width = (short) grid.getWidth();
		this.height = (short) grid.getHeight();
		map = new HexTile[height][width];
		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				map[y][x] = new HexTile(x, y);
				map[y][x].setLandscape(grid.getLandscape(x, y));
				map[y][x].setHeight(grid.getLandscapeHeight(x, y));
			}
		}
		// tow passes, we might need the base grid tiles to add blocking, ...
		// status
		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				MapObject object = grid.getMapObject(x, y);
				if (object != null) {
					addMapObject(map[y][x], object);
				}
			}
		}
	}

	private void addMapObject(HexTile tile, MapObject object) {
		if (object instanceof MapTreeObject) {
			mapObjectsManager.executeSearchType(tile, ESearchType.PLANTABLE_TREE);
		} else if (object instanceof MapStoneObject) {
			mapObjectsManager.addStone(tile, ((MapStoneObject) object).getCapacity());
		} else if (object instanceof StackObject) {
			EMaterialType type = ((StackObject) object).getType();
			IHexStack stack = new SingleMaterialStack(type, tile, EStackType.OFFER, tile.getPlayer());
			tile.setStack(stack);
			for (int i = 0; i < ((StackObject) object).getCount(); i++) {
				stack.push(type);
			}
		} else if (object instanceof BuildingObject) {
			Building building = Building.getBuilding(((BuildingObject) object).getType(), ((BuildingObject) object).getPlayer());
			building.appearAt(this, tile);
		} else if (object instanceof MovableObject) {
			this.placeNewMovable(tile, new Movable(tile, ((MovableObject) object).getType(), ((MovableObject) object).getPlayer()));
		}
	}

	private void createTestSea() {
		for (ISPosition2D pos : new MapCircle((short) 70, (short) (height - 70), 20)) {
			map[pos.getY()][pos.getX()].setLandscape(ELandscapeType.SAND);
		}
		for (ISPosition2D pos : new MapCircle((short) 70, (short) (height - 70), 16)) {
			map[pos.getY()][pos.getX()].setLandscape(ELandscapeType.WATER);
		}
	}

	private void createTestPlayerArea() {
		for (short y = (short) (height - 1); y >= height - 80; y--) {
			for (short x = 0; x < 300; x++) {
				setPlayerAt(x, y, (byte) 1);
			}
		}

	}

	private void placeFullStack(EMaterialType type, short x, short y) {
		IHexStack stack = new SingleMaterialStack(type, new ShortPoint2D(x, y), EStackType.OFFER, getTile(x, y).getPlayer());
		map[y][x].setStack(stack);
		for (int i = 0; i < Constants.STACK_SIZE; i++) {
			stack.push(type);
		}
	}

	@Override
	public synchronized HexTile getTile(short x, short y) {
		if (isInBounds(x, y)) {
			return map[y][x];
		} else {
			return null;
		}
	}

	@Override
	public HexTile getTile(ISPosition2D pos) {
		return getTile(pos.getX(), pos.getY());
	}

	@Override
	public final short getHeight() {
		return height;
	}

	@Override
	public final short getWidth() {
		return width;
	}

	public boolean isValidPosition(IPathRequester requester, short x, short y) {
		return !isBlocked(requester, x, y);
	}

	@Override
	public boolean isBlocked(IPathCalculateable requester, short x, short y) {
		return isBlocked(x, y) || requester.needsPlayersGround() && map[y][x].getPlayer() != requester.getPlayer();
	}

	@Override
	public boolean isBlocked(short x, short y) {
		HexTile tile = getTile(x, y);
		return !isInBounds(x, y) || tile.isBlocked() || tile.getLandscapeType() == ELandscapeType.WATER;
	}

	@Override
	public short[][] getNeighbors(short x, short y, short[][] neighbors) {
		EDirection[] directions = EDirection.values();
		if (neighbors == null || neighbors.length != directions.length) {
			neighbors = new short[directions.length][2];
		}

		for (int i = 0; i < directions.length; i++) {
			neighbors[i][0] = directions[i].getNextTileX(x);
			neighbors[i][1] = directions[i].getNextTileY(y);
		}

		return neighbors;
	}

	@Override
	public float getHeuristicCost(short sx, short sy, short tx, short ty) {
		return getHeuristic(sx, sy, tx, ty);
	}

	public static float getHeuristic(short sx, short sy, short tx, short ty) {
		float dx = (short) Math.abs(sx - tx);
		float dy = (short) Math.abs(sy - ty);

		return (dx + dy) * HexTile.TILE_HEURISTIC_DIST;
	}

	@Override
	public float getCost(short sx, short sy, short tx, short ty) {
		return HexTile.TILE_PATHFINDER_COST;
	}

	@Override
	public void markAsOpen(short x, short y) {
		map[y][x].markAsOpen();
	}

	@Override
	public void markAsClosed(short x, short y) {
		map[y][x].markAsClosed();
	}

	public boolean fitsSearchType(ISPosition2D pos, ESearchType option, IPathCalculateable requester) {
		return fitsSearchType(pos.getX(), pos.getY(), option, requester);
	}

	@Override
	public boolean fitsSearchType(short x, short y, ESearchType type, IPathCalculateable requester) {

		switch (type) {

		case FOREIGN_GROUND:
			return !isBlocked(x, y) && !hasSamePlayer(x, y, requester) && !isMarked(x, y);

		case CUTTABLE_TREE:
			HexTile tile = getTile((short) (x - 1), (short) (y - 1));
			return tile != null && tile.hasCuttableObject(EMapObjectType.TREE_ADULT) && hasSamePlayer(x - 1, y - 1, requester) && !isMarked(x, y);

		case PLANTABLE_TREE:
			return y < height - 1 && isTreePlantable(x, (short) (y + 1)) && !hasProtectedNeighbor(new ShortPoint2D(x, (short) (y + 1)))
					&& hasSamePlayer(x, y + 1, requester) && !isMarked(x, y);

		case PLANTABLE_CORN:
			return isCornPlantable(x, y) && hasSamePlayer(x, y, requester) && !isMarked(x, y);

		case CUTTABLE_CORN:
			return isCornCuttable(x, y) && hasSamePlayer(x, y, requester) && !isMarked(x, y);

		case CUTTABLE_STONE:
			return y < height - 1 && x < width - 2 && getTile((short) (x - 2), (short) (y - 1)).hasCuttableObject(EMapObjectType.STONE)
					&& hasSamePlayer(x, y, requester) && !isMarked(x, y);

		case ENEMY:
			IHexMovable movable = map[y][x].getMovable();
			return movable != null && movable.getPlayer() != requester.getPlayer();

		case RIVER:
			return isRiver(x, y) && hasSamePlayer(x, y, requester) && !isMarked(x, y);

		case FISHABLE:
			return hasNeighbourLandscape(x, y, ELandscapeType.WATER);

		default:
			System.err.println("can't handle search type in fitsSearchType(): " + type);
			return false;
		}
	}

	private boolean hasNeighbourLandscape(short x, short y, ELandscapeType landscape) {
		for (ISPosition2D pos : new MapNeighboursArea(new ShortPoint2D(x, y))) {
			HexTile tile = getTile(pos);
			if (tile != null && tile.getLandscapeType() == landscape) {
				return true;
			}
		}
		return false;
	}

	/**
	 * NOTE: NO CHECK if coordinates are out of the map!
	 * 
	 * @param x
	 * @param y
	 * @param requester
	 * @return
	 */
	private boolean hasSamePlayer(int x, int y, IPathCalculateable requester) {
		return getTile((short) x, (short) y).getPlayer() == requester.getPlayer();
	}

	private boolean isRiver(short x, short y) {
		ELandscapeType type = getTile(x, y).getLandscapeType();
		return type == ELandscapeType.RIVER1 || type == ELandscapeType.RIVER2 || type == ELandscapeType.RIVER3 || type == ELandscapeType.RIVER4;
	}

	private boolean isTreePlantable(short x, short y) {
		return getTile(x, y).getLandscapeType() == ELandscapeType.GRASS && !isBlocked(x, y) && !hasBlockedNeighbor(new ShortPoint2D(x, y));
	}

	private boolean isCornPlantable(short x, short y) {
		HexTile tile = getTile(x, y);
		return (tile.getLandscapeType() == ELandscapeType.GRASS || tile.getLandscapeType() == ELandscapeType.EARTH) && !tile.isProtected()
				&& !hasProtectedNeighbor(tile) && !tile.hasMapObjectType(EMapObjectType.CORN_GROWING)
				&& !tile.hasMapObjectType(EMapObjectType.CORN_ADULT) && !isNeighbor(tile, EMapObjectType.CORN_ADULT)
				&& !isNeighbor(tile, EMapObjectType.CORN_GROWING);
	}

	private boolean isCornCuttable(short x, short y) {
		return getTile(x, y).hasCuttableObject(EMapObjectType.CORN_ADULT);
	}

	/**
	 * Executes a search type action. TODO: does this really belong here?
	 * 
	 * @param pos
	 *            The position the settler is currently at
	 * @param type
	 *            The type the settler searched for and to which the corrosponding aciton should be done.
	 * @return true if we succeeded.
	 */
	public boolean executeSearchType(ISPosition2D pos, ESearchType type) {
		return mapObjectsManager.executeSearchType(pos, type);
	}

	private boolean hasBlockedNeighbor(ISPosition2D pos) {
		short[][] neighbors = this.getNeighbors(pos.getX(), pos.getY(), null);

		for (short[] curr : neighbors) {
			short x = curr[0], y = curr[1];
			if (!isInBounds(x, y) || map[y][x].isBlocked()) {
				return true;
			}
		}

		return false;
	}

	private boolean hasProtectedNeighbor(ISPosition2D pos) {
		for (ISPosition2D neighbour : new MapNeighboursArea(pos)) {
			if (!isInBounds(neighbour) || getTile(neighbour).isProtected()) {
				return true;
			}
		}
		return false;
	}

	private boolean isNeighbor(ISPosition2D pos, EMapObjectType mapObjectType) {
		short[][] neighbors = this.getNeighbors(pos.getX(), pos.getY(), null);

		for (short[] curr : neighbors) {
			short x = curr[0], y = curr[1];
			if (getTile(x, y).hasMapObjectType(mapObjectType)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @param pos
	 *            position to be checked
	 * @return true if the given position is in the grid
	 */
	private boolean isInBounds(ISPosition2D pos) {
		return isInBounds(pos.getX(), pos.getY());
	}

	@Override
	public boolean isInBounds(short x, short y) {
		return 0 <= x && x < width && 0 <= y && y < height;
	}

	@Override
	public EBuildingType getConstructionPreviewBuilding() {
		return previewBuilding;
	}

	public void setPreviewBuilding(EBuildingType buildingType) {
		previewBuilding = buildingType;
	}

	@Override
	public byte getPlayer(ISPosition2D pos) {
		return getTile(pos).getPlayer();
	}

	@Override
	public boolean isBuildingPlaceable(ISPosition2D pos, byte player) {
		HexTile tile = getTile(pos);
		return tile != null && !tile.isProtected() && tile.getPlayer() == player;
	}

	@Override
	public void placeNewMovable(ISPosition2D pos, IHexMovable movable) {
		if (isInBounds(pos)) {
			getTile(pos).moveableEntered(movable);
		}
	}

	public void movableLeft(ISPosition2D pos, IHexMovable movable) {
		getTile(pos).moveableLeft(movable);
	}

	public void movableEntered(ISPosition2D pos, IHexMovable movable) {
		getTile(pos).moveableEntered(movable);
	}

	/**
	 * Removes a given material from the stack at the given position.
	 * 
	 * @param pos
	 *            The position to remove the material from
	 * @param materialType
	 *            The material type.
	 * @return true if the material was removed.
	 */
	public boolean popMaterial(ISPosition2D pos, EMaterialType materialType) {
		if (isInBounds(pos)) {
			IHexStack stack = getTile(pos).getStack();
			// TODO support multistack here
			if (stack != null && stack.getMaterial() == materialType) {
				stack.pop(materialType);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean pushMaterial(ISPosition2D pos, EMaterialType materialType) {
		if (isInBounds(pos)) {
			IHexStack stack = map[pos.getY()][pos.getX()].getStack();
			if (stack == null) {
				stack = new SingleMaterialStack(materialType, pos, EStackType.OFFER, getTile(pos).getPlayer());
				getTile(pos).setStack(stack);
			}
			return stack.push(materialType);
		} else {
			return false;
		}
	}

	@Override
	public boolean setBuilding(ISPosition2D pos, IBuilding building) {
		if (isInBounds(pos)) {
			FreeMapArea area = new FreeMapArea(pos, building.getBuildingType().getProtectedTiles());
			boolean protectWorked = setProtectedState(area, true);

			if (protectWorked) {
				getTile(pos).setBuilding(building);
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean setProtectedState(FreeMapArea area, boolean protectState) {
		boolean isFree = true;
		for (ISPosition2D protect : area) {
			if (!isInBounds(protect) || (getTile(protect).isProtected() == protectState)) {
				isFree = false;
			}
		}
		if (!isFree) {
			return false;
		} else {
			for (ISPosition2D protect : area) {
				getTile(protect).setProtected(protectState);
			}
			return true;
		}
	}

	public IBuilding getBuilding(ISPosition2D pos) {
		if (isInBounds(pos))
			return getTile(pos).getBuilding();
		else
			return null;
	}

	public boolean canPop(ISPosition2D pos, EMaterialType material) {
		IHexStack stack = getTile(pos).getStack();
		return stack != null && !stack.isEmpty();
	}

	public boolean canPush(ISPosition2D pos, EMaterialType material) {
		IHexStack stack = getTile(pos).getStack();
		return stack == null || !stack.isFull();
	}

	@Override
	public void placeStack(ISPosition2D stackPos, IHexStack stack) {
		getTile(stackPos).setStack(stack);
	}

	@Override
	public void removeStack(ISPosition2D stackPos) {
		getTile(stackPos).removeStack();
	}

	public void releaseStack(ISPosition2D stackPos) {
		getTile(stackPos).getStack().releaseRequests();
	}

	@Override
	public void setConstructMarking(ISPosition2D pos, byte constructionValue) {
		getTile(pos).setConstructMarking(constructionValue);
	}

	@Override
	public IHexMovable getMovable(ISPosition2D pos) {
		HexTile tile = getTile(pos);
		if (tile != null)
			return tile.getMovable();
		else
			return null;
	}

	public boolean isFreeToEnter(ISPosition2D nextTile) {
		return isInBounds(nextTile) && getTile(nextTile).getMovable() == null;
	}

	@Override
	public byte getHeightAt(ISPosition2D pos) {
		return getTile(pos).getHeight();
	}

	public void changeHeightAt(ISPosition2D pos, byte signum) {
		HexTile tile = getTile(pos);
		tile.setHeight((byte) (tile.getHeight() + signum));
		tile.setLandscape(ELandscapeType.FLATTENED);
	}

	@Override
	public void setPlayerAt(ISPosition2D pos, byte player) {
		setPlayerAt(pos.getX(), pos.getY(), player);
	}

	@Override
	public void setPlayerAt(short x, short y, byte player) {

		getTile(x, y).setPlayer(player);
	}

	public void setMarked(ISPosition2D pos, boolean setMarker) {
		getTile(pos).setMarked(setMarker);
	}

	public boolean isMarked(ISPosition2D pos) {
		return isMarked(pos.getX(), pos.getY());
	}

	public boolean isMarked(short x, short y) {
		return getTile(x, y).isMarked();
	}

	@Override
	public MapObjectsManager getMapObjectsManager() {
		return mapObjectsManager;
	}
}
