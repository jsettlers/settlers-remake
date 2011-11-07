package jsettlers.logic.map.newGrid;

import java.util.Random;

import jsettlers.common.Color;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.FreeMapArea;
import jsettlers.common.map.shapes.IMapArea;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.IGuiInputGrid;
import jsettlers.logic.algorithms.borders.BordersThread;
import jsettlers.logic.algorithms.borders.IBordersThreadGrid;
import jsettlers.logic.algorithms.construction.ConstructMarksThread;
import jsettlers.logic.algorithms.construction.IConstructionMarkableMap;
import jsettlers.logic.algorithms.landmarks.ILandmarksThreadMap;
import jsettlers.logic.algorithms.landmarks.LandmarksCorrectingThread;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.area.IInAreaFinderMap;
import jsettlers.logic.algorithms.path.area.InAreaFinder;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.logic.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.logic.buildings.Building;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.spawn.Barrack;
import jsettlers.logic.buildings.workers.WorkerBuilding;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.blocked.BlockedGrid;
import jsettlers.logic.map.newGrid.interfaces.AbstractHexMapObject;
import jsettlers.logic.map.newGrid.interfaces.IHexMovable;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
import jsettlers.logic.map.newGrid.movable.MovableGrid;
import jsettlers.logic.map.newGrid.objects.IMapObjectsManagerGrid;
import jsettlers.logic.map.newGrid.objects.MapObjectsManager;
import jsettlers.logic.map.newGrid.objects.ObjectsGrid;
import jsettlers.logic.map.newGrid.partition.IPartitionableGrid;
import jsettlers.logic.map.newGrid.partition.PartitionsGrid;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBricklayer;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableDigger;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.random.RandomMapEvaluator;
import jsettlers.logic.map.random.RandomMapFile;
import jsettlers.logic.map.random.grid.BuildingObject;
import jsettlers.logic.map.random.grid.MapGrid;
import jsettlers.logic.map.random.grid.MapObject;
import jsettlers.logic.map.random.grid.MapStoneObject;
import jsettlers.logic.map.random.grid.MapTreeObject;
import jsettlers.logic.map.random.grid.MovableObject;
import jsettlers.logic.map.random.grid.StackObject;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.stack.IRequestsStackGrid;

/**
 * This is the main grid offering an interface for interacting with the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public class MainGrid {
	protected final short width;
	protected final short height;

	protected final LandscapeGrid landscapeGrid;
	protected final ObjectsGrid objectsGrid;
	protected final PartitionsGrid partitionsGrid;
	protected final MovableGrid movableGrid;
	protected final BlockedGrid blockedGrid;
	protected final Color[][] debugColors;

	protected final MovablePathfinderGrid movablePathfinderGrid;
	private final IGraphicsGrid graphicsGrid;
	protected final MapObjectsManager mapObjectsManager;
	private final LandmarksCorrectingThread landmarksCorrectionThread;
	private final ConstructMarksThread constructionMarksCalculator;
	private final BordersThread bordersThread;
	private final BuildingsGrid buildingsGrid;
	private final IGuiInputGrid guiInputGrid;

	public MainGrid(short width, short height, LandscapeGrid landscapeGrid, BlockedGrid blockedGrid, ObjectsGrid objectsGrid) {
		this.width = width;
		this.height = height;
		this.landscapeGrid = landscapeGrid;
		this.blockedGrid = blockedGrid;
		this.objectsGrid = objectsGrid;

		this.movablePathfinderGrid = new MovablePathfinderGrid();
		this.graphicsGrid = new GraphicsGrid();
		this.mapObjectsManager = new MapObjectsManager(new MapObjectsManagerGrid());

		this.movableGrid = new MovableGrid(width, height);
		this.partitionsGrid = new PartitionsGrid(width, height, new PartitionableGrid(), movablePathfinderGrid);

		this.landmarksCorrectionThread = new LandmarksCorrectingThread(new LandmarksGrid());
		this.bordersThread = new BordersThread(new BordersThreadGrid());
		this.constructionMarksCalculator = new ConstructMarksThread(new ConstructionMarksGrid(), (byte) 0); // TODO player needs to be set
		// dynamically
		this.buildingsGrid = new BuildingsGrid();
		this.guiInputGrid = new GUIInputGrid();

		this.debugColors = new Color[width][height];
	}

	public MainGrid(short width, short height) {
		this.width = width;
		this.height = height;

		this.movablePathfinderGrid = new MovablePathfinderGrid();
		this.graphicsGrid = new GraphicsGrid();
		this.mapObjectsManager = new MapObjectsManager(new MapObjectsManagerGrid());

		this.landscapeGrid = new LandscapeGrid(width, height);
		this.objectsGrid = new ObjectsGrid(width, height);
		this.movableGrid = new MovableGrid(width, height);
		this.blockedGrid = new BlockedGrid(width, height);
		this.partitionsGrid = new PartitionsGrid(width, height, new PartitionableGrid(), movablePathfinderGrid);

		this.landmarksCorrectionThread = new LandmarksCorrectingThread(new LandmarksGrid());
		this.bordersThread = new BordersThread(new BordersThreadGrid());
		this.constructionMarksCalculator = new ConstructMarksThread(new ConstructionMarksGrid(), (byte) 0); // TODO player needs to be set
		// dynamically
		this.buildingsGrid = new BuildingsGrid();
		this.guiInputGrid = new GUIInputGrid();

		this.debugColors = new Color[width][height];
	}

	private MainGrid(MapGrid mapGrid) {
		this((short) mapGrid.getWidth(), (short) mapGrid.getHeight());

		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				landscapeGrid.setLandscapeTypeAt(x, y, mapGrid.getLandscape(x, y));
				landscapeGrid.setHeightAt(x, y, mapGrid.getLandscapeHeight(x, y));
			}
		}

		// tow passes, we might need the base grid tiles to add blocking, ... status
		for (short y = 0; y < height; y++) {
			for (short x = 0; x < width; x++) {
				MapObject object = mapGrid.getMapObject(x, y);
				if (object != null) {
					addMapObject(x, y, object);
				}
			}
		}

		System.out.println("grid filled");
	}

	public static MainGrid createForDebugging() {
		MainGrid grid = new MainGrid((short) 200, (short) 100);

		for (short x = 0; x < grid.width; x++) {
			for (short y = 0; y < grid.height; y++) {
				grid.landscapeGrid.setLandscapeTypeAt(x, y, ELandscapeType.GRASS);
				grid.landscapeGrid.setHeightAt(x, y, (byte) 0);
			}
		}

		Building tower = Building.getBuilding(EBuildingType.TOWER, (byte) 0);
		tower.appearAt(grid.buildingsGrid, new ShortPoint2D(55, 50));

		tower = Building.getBuilding(EBuildingType.TOWER, (byte) 0);
		tower.appearAt(grid.buildingsGrid, new ShortPoint2D(145, 50));

		grid.placeStack(new ShortPoint2D(30, 50), EMaterialType.PLANK, 8);
		grid.placeStack(new ShortPoint2D(32, 50), EMaterialType.PLANK, 8);
		grid.placeStack(new ShortPoint2D(34, 50), EMaterialType.PLANK, 8);
		grid.placeStack(new ShortPoint2D(36, 50), EMaterialType.PLANK, 8);
		grid.placeStack(new ShortPoint2D(30, 40), EMaterialType.STONE, 8);
		grid.placeStack(new ShortPoint2D(32, 40), EMaterialType.STONE, 8);
		grid.placeStack(new ShortPoint2D(34, 40), EMaterialType.STONE, 8);
		grid.placeStack(new ShortPoint2D(36, 40), EMaterialType.STONE, 8);
		grid.placeStack(new ShortPoint2D(34, 30), EMaterialType.HAMMER, 1);
		grid.placeStack(new ShortPoint2D(36, 30), EMaterialType.BLADE, 1);

		grid.placeStack(new ShortPoint2D(38, 30), EMaterialType.AXE, 1);
		grid.placeStack(new ShortPoint2D(40, 30), EMaterialType.SAW, 1);

		for (int i = 0; i < 10; i++) {
			grid.createNewMovableAt(new ShortPoint2D(60 + 2 * i, 50), EMovableType.BEARER, (byte) 0);
		}
		grid.createNewMovableAt(new ShortPoint2D(50, 50), EMovableType.PIONEER, (byte) 0);

		grid.createNewMovableAt(new ShortPoint2D(60, 60), EMovableType.SWORDSMAN_L3, (byte) 0);

		return grid;
	}

	public static MainGrid create(String filename, byte players, Random random) {
		RandomMapFile file = RandomMapFile.getByName(filename);
		RandomMapEvaluator evaluator = new RandomMapEvaluator(file.getInstructions(), players);
		evaluator.createMap(random);
		MapGrid mapGrid = evaluator.getGrid();

		System.out.println("Generated random map");

		return new MainGrid(mapGrid);
	}

	private void addMapObject(short x, short y, MapObject object) {
		ISPosition2D pos = new ShortPoint2D(x, y);

		if (object instanceof MapTreeObject) {
			if (isInBounds(x, y) && movablePathfinderGrid.isTreePlantable(x, y)) {
				mapObjectsManager.executeSearchType(new ShortPoint2D(x, y - 1), ESearchType.PLANTABLE_TREE);
			}
		} else if (object instanceof MapStoneObject) {
			mapObjectsManager.addStone(pos, ((MapStoneObject) object).getCapacity());
		} else if (object instanceof StackObject) {
			placeStack(pos, ((StackObject) object).getType(), ((StackObject) object).getCount());
		} else if (object instanceof BuildingObject) {
			Building building = Building.getBuilding(((BuildingObject) object).getType(), ((BuildingObject) object).getPlayer());
			building.appearAt(buildingsGrid, pos);
		} else if (object instanceof MovableObject) {
			createNewMovableAt(pos, ((MovableObject) object).getType(), ((MovableObject) object).getPlayer());
		}
	}

	private void placeStack(ISPosition2D pos, EMaterialType materialType, int count) {
		for (int i = 0; i < count; i++) {
			movablePathfinderGrid.pushMaterial(pos, materialType, true);
		}
	}

	public IGraphicsGrid getGraphicsGrid() {
		return graphicsGrid;
	}

	public IGuiInputGrid getGuiInputGrid() {
		return guiInputGrid;
	}

	protected final boolean isInBounds(short x, short y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private void changePlayerAt(ISPosition2D position, byte player) {
		partitionsGrid.changePlayerAt(position.getX(), position.getY(), player);
		bordersThread.checkPosition(position);
		landmarksCorrectionThread.addLandmarkedPosition(position);
	}

	public void createNewMovableAt(ISPosition2D pos, EMovableType type, byte player) {
		buildingsGrid.placeNewMovable(pos, new Movable(movablePathfinderGrid, pos, type, player));
	}

	protected boolean isLandscapeBlocking(short x, short y) {
		ELandscapeType landscapeType = landscapeGrid.getLandscapeTypeAt(x, y);
		return landscapeType == ELandscapeType.WATER || landscapeType == ELandscapeType.SNOW;
	}

	class PathfinderGrid implements IAStarPathMap, IDijkstraPathMap, IInAreaFinderMap {
		@Override
		public short getHeight() {
			return height;
		}

		@Override
		public short getWidth() {
			return width;
		}

		@Override
		public boolean isBlocked(IPathCalculateable requester, short x, short y) {
			return blockedGrid.isBlocked(x, y) || isLandscapeBlocking(x, y)
					|| (requester.needsPlayersGround() && requester.getPlayer() != partitionsGrid.getPlayerAt(x, y));
		}

		@Override
		public float getHeuristicCost(short sx, short sy, short tx, short ty) {
			float dx = (short) Math.abs(sx - tx);
			float dy = (short) Math.abs(sy - ty);

			return (dx + dy) * Constants.TILE_HEURISTIC_DIST;
		}

		@Override
		public float getCost(short sx, short sy, short tx, short ty) {
			return Constants.TILE_PATHFINDER_COST * (blockedGrid.isProtected(sx, sy) ? 1.7f : 1);
		}

		@Override
		public void markAsOpen(short x, short y) {
			debugColors[x][y] = Color.BLUE;
		}

		@Override
		public void markAsClosed(short x, short y) {
			debugColors[x][y] = Color.RED;
		}

		@Override
		public void setDijkstraSearched(short x, short y) {
			markAsOpen(x, y);
		}

		@Override
		public boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public boolean fitsSearchType(short x, short y, ESearchType searchType, IPathCalculateable pathCalculable) {
			switch (searchType) {

			case FOREIGN_GROUND:
				return !blockedGrid.isBlocked(x, y) && !hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

			case CUTTABLE_TREE:
				return isInBounds((short) (x - 1), (short) (y - 1))
						&& objectsGrid.hasCuttableObject((short) (x - 1), (short) (y - 1), EMapObjectType.TREE_ADULT)
						&& hasSamePlayer((short) (x - 1), (short) (y - 1), pathCalculable) && !isMarked(x, y);

			case PLANTABLE_TREE:
				return y < height - 1 && isTreePlantable(x, (short) (y + 1)) && !hasProtectedNeighbor(x, (short) (y + 1))
						&& hasSamePlayer(x, (short) (y + 1), pathCalculable) && !isMarked(x, y);

			case PLANTABLE_CORN:
				return isCornPlantable(x, y) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y) && !blockedGrid.isProtected(x, y);

			case CUTTABLE_CORN:
				return isCornCuttable(x, y) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

			case CUTTABLE_STONE:
				return y < height - 1 && x < width - 2 && objectsGrid.hasCuttableObject((short) (x - 2), (short) (y - 1), EMapObjectType.STONE)
						&& hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

			case ENEMY:
				IMovable movable = movableGrid.getMovableAt(x, y);
				return movable != null && movable.getPlayer() != pathCalculable.getPlayer();

			case RIVER:
				return isRiver(x, y) && hasSamePlayer(x, y, pathCalculable) && !isMarked(x, y);

			case FISHABLE:
				return hasSamePlayer(x, y, pathCalculable) && hasNeighbourLandscape(x, y, ELandscapeType.WATER);

			case NON_BLOCKED_OR_PROTECTED:
				return !(blockedGrid.isProtected(x, y) || blockedGrid.isBlocked(x, y)) && !isLandscapeBlocking(x, y)
						&& hasSamePlayer(x, y, pathCalculable);

			default:
				System.err.println("can't handle search type in fitsSearchType(): " + searchType);
				return false;
			}
		}

		private boolean isMarked(short x, short y) {
			return blockedGrid.isMarked(x, y);
		}

		private boolean hasProtectedNeighbor(short x, short y) {
			for (EDirection currDir : EDirection.values()) {
				if (blockedGrid.isProtected(currDir.getNextTileX(x), currDir.getNextTileY(y)))
					return true;
			}
			return false;
		}

		private boolean hasNeighbourLandscape(short x, short y, ELandscapeType landscape) {
			for (ISPosition2D pos : new MapNeighboursArea(new ShortPoint2D(x, y))) {
				short currX = pos.getX();
				short currY = pos.getY();
				if (isInBounds(currX, currY) && landscapeGrid.getLandscapeTypeAt(currX, currY) == landscape) {
					return true;
				}
			}
			return false;
		}

		private boolean hasSamePlayer(short x, short y, IPathCalculateable requester) {
			return partitionsGrid.getPlayerAt(x, y) == requester.getPlayer();
		}

		private boolean isRiver(short x, short y) {
			ELandscapeType type = landscapeGrid.getLandscapeTypeAt(x, y);
			return type == ELandscapeType.RIVER1 || type == ELandscapeType.RIVER2 || type == ELandscapeType.RIVER3 || type == ELandscapeType.RIVER4;
		}

		boolean isTreePlantable(short x, short y) {
			return landscapeGrid.getLandscapeTypeAt(x, y) == ELandscapeType.GRASS && !blockedGrid.isBlocked(x, y) && !hasBlockedNeighbor(x, y);
		}

		private boolean hasBlockedNeighbor(short x, short y) {
			for (EDirection currDir : EDirection.values()) {
				short currX = currDir.getNextTileX(x);
				short currY = currDir.getNextTileY(y);
				if (!isInBounds(currX, currY) || blockedGrid.isBlocked(currX, currY)) {
					return true;
				}
			}

			return false;
		}

		private boolean isCornPlantable(short x, short y) {
			ELandscapeType landscapeType = landscapeGrid.getLandscapeTypeAt(x, y);
			return (landscapeType == ELandscapeType.GRASS || landscapeType == ELandscapeType.EARTH) && !blockedGrid.isProtected(x, y)
					&& !hasProtectedNeighbor(x, y) && !objectsGrid.hasMapObjectType(x, y, EMapObjectType.CORN_GROWING)
					&& !objectsGrid.hasMapObjectType(x, y, EMapObjectType.CORN_ADULT)
					&& !objectsGrid.hasNeighborObjectType(x, y, EMapObjectType.CORN_ADULT)
					&& !objectsGrid.hasNeighborObjectType(x, y, EMapObjectType.CORN_GROWING);
		}

		private boolean isCornCuttable(short x, short y) {
			return objectsGrid.hasCuttableObject(x, y, EMapObjectType.CORN_ADULT);
		}

	}

	class GraphicsGrid implements IGraphicsGrid {

		@Override
		public short getHeight() {
			return height;
		}

		@Override
		public short getWidth() {
			return width;
		}

		@Override
		public IMovable getMovableAt(int x, int y) {
			return movableGrid.getMovableAt((short) x, (short) y);
		}

		@Override
		public IMapObject getMapObjectsAt(int x, int y) {
			return objectsGrid.getObjectsAt((short) x, (short) y);
		}

		@Override
		public byte getHeightAt(int x, int y) {
			return landscapeGrid.getHeightAt((short) x, (short) y);
		}

		@Override
		public ELandscapeType getLandscapeTypeAt(int x, int y) {
			return landscapeGrid.getLandscapeTypeAt((short) x, (short) y);
		}

		@Override
		public Color getDebugColorAt(int x, int y) {
			// short value = (short) (partitionsGrid.getPartitionAt((short) x, (short) y) + 1);
			// return new Color((value % 3) * 0.33f, ((value / 3) % 3) * 0.33f, ((value / 9) % 3) * 0.33f, 1);

			// return debugColors[x][y];

			return blockedGrid.isBlocked((short) x, (short) y) ? new Color(0, 0, 0, 1) : (blockedGrid.isProtected((short) x, (short) y) ? new Color(
					0, 0, 1, 1) : null);
		}

		@Override
		public boolean isBorder(int x, int y) {
			return partitionsGrid.isBorderAt((short) x, (short) y);
		}

		@Override
		public byte getPlayerAt(int x, int y) {
			return partitionsGrid.getPlayerAt((short) x, (short) y);
		}
	}

	private class MapObjectsManagerGrid implements IMapObjectsManagerGrid {

		@Override
		public void setLandscape(short x, short y, ELandscapeType landscapeType) {
			landscapeGrid.setLandscapeTypeAt(x, y, landscapeType);
		}

		@Override
		public void setBlocked(short x, short y, boolean blocked) {
			blockedGrid.setBlocked(x, y, blocked);
		}

		@Override
		public AbstractHexMapObject removeMapObjectType(short x, short y, EMapObjectType mapObjectType) {
			return objectsGrid.removeMapObjectType(x, y, mapObjectType);
		}

		@Override
		public boolean removeMapObject(short x, short y, AbstractHexMapObject mapObject) {
			return objectsGrid.removeMapObject(x, y, mapObject);
		}

		@Override
		public boolean isBlocked(short x, short y) {
			return blockedGrid.isBlocked(x, y);
		}

		@Override
		public AbstractHexMapObject getMapObject(short x, short y, EMapObjectType mapObjectType) {
			return objectsGrid.getMapObjectAt(x, y, mapObjectType);
		}

		@Override
		public void addMapObject(short x, short y, AbstractHexMapObject mapObject) {
			objectsGrid.addMapObjectAt(x, y, mapObject);
		}

		@Override
		public short getWidth() {
			return width;
		}

		@Override
		public short getHeight() {
			return height;
		}

		@Override
		public boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public void setProtected(short x, short y, boolean protect) {
			blockedGrid.setProtected(x, y, protect);
		}

	}

	private class LandmarksGrid implements ILandmarksThreadMap {
		@Override
		public boolean isBlocked(short x, short y) {
			return blockedGrid.isBlocked(x, y);
		}

		@Override
		public boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}

		@Override
		public short getPartitionAt(short x, short y) {
			return partitionsGrid.getPartitionAt(x, y);
		}

		@Override
		public void setPartitionAndPlayerAt(short x, short y, short partition) {
			partitionsGrid.setPartitionAndPlayerAt(x, y, partition);
			bordersThread.checkPosition(new ShortPoint2D(x, y));
		}
	}

	private class ConstructionMarksGrid implements IConstructionMarkableMap {
		@Override
		public void setConstructMarking(ISPosition2D pos, byte value) {
			mapObjectsManager.setConstructionMarking(pos, value);
		}

		@Override
		public boolean isBuildingPlaceable(ISPosition2D position, byte player) {
			short x = position.getX(), y = position.getY();
			return MainGrid.this.isInBounds(x, y) && !blockedGrid.isProtected(x, y) && !blockedGrid.isBlocked(x, y)
					&& partitionsGrid.getPlayerAt(x, y) == player;
		}

		@Override
		public short getWidth() {
			return width;
		}

		@Override
		public short getHeight() {
			return height;
		}

		@Override
		public byte getHeightAt(ISPosition2D pos) {
			return landscapeGrid.getHeightAt(pos.getX(), pos.getY());
		}

		@Override
		public ELandscapeType getLandscapeTypeAt(ISPosition2D pos) {
			return landscapeGrid.getLandscapeTypeAt(pos.getX(), pos.getY());
		}
	}

	private class MovablePathfinderGrid extends PathfinderGrid implements IMovableGrid {
		private final HexAStar aStar;
		private final DijkstraAlgorithm dijkstra;
		private final InAreaFinder inAreaFinder;

		public MovablePathfinderGrid() {
			aStar = new HexAStar(this);
			dijkstra = new DijkstraAlgorithm(this, aStar);
			inAreaFinder = new InAreaFinder(this);
		}

		@Override
		public void movableLeft(ISPosition2D position, IHexMovable movable) {
			movableGrid.movableLeft(position, movable);
		}

		@Override
		public void movableEntered(ISPosition2D position, IHexMovable movable) {
			movableGrid.movableEntered(position, movable);
		}

		@Override
		public MapObjectsManager getMapObjectsManager() {
			return mapObjectsManager;
		}

		@Override
		public IHexMovable getMovable(ISPosition2D position) {
			return movableGrid.getMovableAt(position.getX(), position.getY());
		}

		@Override
		public boolean isBlocked(short x, short y) {
			return blockedGrid.isBlocked(x, y) || isLandscapeBlocking(x, y);
		}

		@Override
		public boolean canPush(ISPosition2D position, EMaterialType material) {
			return mapObjectsManager.canPush(position, material);
		}

		@Override
		public boolean pushMaterial(ISPosition2D position, EMaterialType materialType, boolean offer) {
			if (mapObjectsManager.pushMaterial(position, materialType)) {
				if (offer) {
					partitionsGrid.pushMaterial(position, materialType);
				}
				return true;
			} else
				return false;
		}

		@Override
		public boolean canPop(ISPosition2D position, EMaterialType material) {
			return mapObjectsManager.canPop(position, material);
		}

		@Override
		public boolean popMaterial(ISPosition2D position, EMaterialType materialType) {
			if (mapObjectsManager.popMaterial(position, materialType)) {
				return true;
			} else
				return false;
		}

		@Override
		public ELandscapeType getLandscapeTypeAt(ISPosition2D position) {
			return landscapeGrid.getLandscapeTypeAt(position.getX(), position.getY());
		}

		@Override
		public byte getHeightAt(ISPosition2D position) {
			return landscapeGrid.getHeightAt(position.getX(), position.getY());
		}

		@Override
		public void changeHeightAt(ISPosition2D position, byte delta) {
			landscapeGrid.changeHeightAt(position.getX(), position.getY(), delta);
		}

		@Override
		public void setMarked(ISPosition2D position, boolean marked) {
			blockedGrid.setMarked(position.getX(), position.getY(), marked);
		}

		@Override
		public boolean isMarked(ISPosition2D position) {
			return blockedGrid.isMarked(position.getX(), position.getY());
		}

		@Override
		public boolean isInBounds(ISPosition2D position) {
			return isInBounds(position.getX(), position.getY());
		}

		@Override
		public byte getPlayerAt(ISPosition2D position) {
			return partitionsGrid.getPlayerAt(position);
		}

		@Override
		public void changePlayerAt(ISPosition2D position, byte player) {
			MainGrid.this.changePlayerAt(position, player);
		}

		@Override
		public boolean executeSearchType(ISPosition2D position, ESearchType searchType) {
			return mapObjectsManager.executeSearchType(position, searchType);
		}

		@Override
		public HexAStar getAStar() {
			return aStar;
		}

		@Override
		public DijkstraAlgorithm getDijkstra() {
			return dijkstra;
		}

		@Override
		public InAreaFinder getInAreaFinder() {
			return inAreaFinder;
		}

		@Override
		public boolean fitsSearchType(ISPosition2D position, ESearchType searchType, IPathCalculateable pathCalculateable) {
			return super.fitsSearchType(position.getX(), position.getY(), searchType, pathCalculateable);
		}

		@Override
		public void addJobless(IManageableBearer manageable) {
			partitionsGrid.addJobless(manageable);
		}

		@Override
		public void addJobless(IManageableWorker worker) {
			partitionsGrid.addJobless(worker);

		}

		@Override
		public void addJobless(IManageableBricklayer bricklayer) {
			partitionsGrid.addJobless(bricklayer);
		}

		@Override
		public void addJobless(IManageableDigger digger) {
			partitionsGrid.addJobless(digger);
		}

		@Override
		public void changeLandscapeAt(ISPosition2D pos, ELandscapeType type) {
			landscapeGrid.setLandscapeTypeAt(pos.getX(), pos.getY(), type);
		}

		@Override
		public void placeSmoke(ISPosition2D pos, boolean place) {
			if (place) {
				mapObjectsManager.addSimpleMapObject(pos, EMapObjectType.SMOKE, false, (byte) 0);
			} else {
				mapObjectsManager.removeMapObjectType(pos, EMapObjectType.SMOKE);
			}
		}

		@Override
		public boolean isProtected(short x, short y) {
			return blockedGrid.isProtected(x, y);
		}

		@Override
		public void placePig(ISPosition2D pos, boolean place) {
			mapObjectsManager.placePig(pos, place);
		}

		@Override
		public boolean isPigThere(ISPosition2D pos) {
			return mapObjectsManager.pigIsThere(pos);
		}

		@Override
		public boolean isPigAdult(ISPosition2D pos) {
			return mapObjectsManager.pigIsAdult(pos);
		}

	}

	private class BordersThreadGrid implements IBordersThreadGrid {
		@Override
		public byte getPlayer(short x, short y) {
			return partitionsGrid.getPlayerAt(x, y);
		}

		@Override
		public void setBorder(short x, short y, boolean isBorder) {
			partitionsGrid.setBorderAt(x, y, isBorder);
		}

		@Override
		public final boolean isInBounds(short x, short y) {
			return MainGrid.this.isInBounds(x, y);
		}
	}

	private class BuildingsGrid implements IBuildingsGrid {
		private final RequestStackGrid requestStackGrid = new RequestStackGrid();

		@Override
		public byte getHeightAt(ISPosition2D position) {
			return landscapeGrid.getHeightAt(position.getX(), position.getY());
		}

		@Override
		public boolean setBuilding(ISPosition2D position, Building newBuilding) {
			if (MainGrid.this.isInBounds(position.getX(), position.getY())) {
				FreeMapArea area = new FreeMapArea(position, newBuilding.getBuildingType().getProtectedTiles());

				if (canConstructAt(area)) {
					setProtectedState(area, true);
					mapObjectsManager.addBuildingTo(position, newBuilding);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}

		private void setProtectedState(FreeMapArea area, boolean setProtected) {
			for (ISPosition2D curr : area) {
				if (MainGrid.this.isInBounds(curr.getX(), curr.getY()))
					blockedGrid.setProtected(curr.getX(), curr.getY(), setProtected);
			}
		}

		private boolean canConstructAt(FreeMapArea area) {
			boolean isFree = true;

			for (ISPosition2D curr : area) {
				short x = curr.getX();
				short y = curr.getY();
				if (!isInBounds(x, y) || blockedGrid.isProtected(x, y) || blockedGrid.isBlocked(x, y)) {
					isFree = false;
				}
			}
			return isFree;
		}

		@Override
		public void setBlocked(FreeMapArea area, boolean blocked) {
			for (ISPosition2D curr : area) {
				if (MainGrid.this.isInBounds(curr.getX(), curr.getY()))
					blockedGrid.setBlocked(curr.getX(), curr.getY(), blocked);
			}
		}

		@Override
		public void setPlayerAt(ISPosition2D position, byte player) {
			changePlayerAt(position, player);
		}

		@Override
		public short getWidth() {
			return width;
		}

		@Override
		public short getHeight() {
			return height;
		}

		@Override
		public IHexMovable getMovable(ISPosition2D position) {
			return movableGrid.getMovableAt(position.getX(), position.getY());
		}

		@Override
		public void placeNewMovable(ISPosition2D position, IHexMovable movable) {
			movableGrid.movableEntered(position, movable);
		}

		@Override
		public MapObjectsManager getMapObjectsManager() {
			return mapObjectsManager;
		}

		@Override
		public IMovableGrid getMovableGrid() {
			return movablePathfinderGrid;
		}

		@Override
		public void requestDiggers(FreeMapArea buildingArea, byte heightAvg, byte amount) {
			partitionsGrid.requestDiggers(buildingArea, heightAvg, amount);
		}

		@Override
		public void requestBricklayer(Building building, ShortPoint2D bricklayerTargetPos, EDirection direction) {
			partitionsGrid.requestBricklayer(building, bricklayerTargetPos, direction);
		}

		@Override
		public IRequestsStackGrid getRequestStackGrid() {
			return requestStackGrid;
		}

		@Override
		public void requestBuildingWorker(EMovableType workerType, WorkerBuilding workerBuilding) {
			partitionsGrid.requestBuildingWorker(workerType, workerBuilding);
		}

		@Override
		public void requestSoilderable(ISPosition2D position, Barrack barrack) {
			partitionsGrid.requestSoilderable(position, barrack);
		}

		private class RequestStackGrid implements IRequestsStackGrid {
			@Override
			public void request(ISPosition2D position, EMaterialType materialType, byte priority) {
				partitionsGrid.request(position, materialType, priority);
			}

			@Override
			public boolean hasMaterial(ISPosition2D position, EMaterialType materialType) {
				return mapObjectsManager.canPop(position, materialType);
			}

			@Override
			public void popMaterial(ISPosition2D position, EMaterialType materialType) {
				mapObjectsManager.popMaterial(position, materialType);
			}

			@Override
			public int getStackSize(ISPosition2D position, EMaterialType materialType) {
				return mapObjectsManager.getStackSize(position, materialType);
			}
		}

	}

	private class GUIInputGrid implements IGuiInputGrid {
		@Override
		public IHexMovable getMovable(short x, short y) {
			return movableGrid.getMovableAt(x, y);
		}

		@Override
		public short getWidth() {
			return width;
		}

		@Override
		public short getHeight() {
			return height;
		}

		@Override
		public IBuilding getBuildingAt(short x, short y) {
			return (IBuilding) objectsGrid.getMapObjectAt(x, y, EMapObjectType.BUILDING);
		}

		@Override
		public boolean isInBounds(ISPosition2D position) {
			return MainGrid.this.isInBounds(position.getX(), position.getY());
		}

		@Override
		public IBuildingsGrid getBuildingsGrid() {
			return buildingsGrid;
		}

		@Override
		public byte getPlayerAt(ISPosition2D position) {
			return partitionsGrid.getPlayerAt(position);
		}

		@Override
		public void setBuildingType(EBuildingType buildingType) {
			constructionMarksCalculator.setBuildingType(buildingType);
		}

		@Override
		public void setScreen(IMapArea screenArea) {
			constructionMarksCalculator.setScreen(screenArea);
		}

		@Override
		public void resetDebugColors() {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					debugColors[x][y] = null;
				}
			}
		}

		@Override
		public boolean canConstructAt(ISPosition2D pos, EBuildingType type) {
			return objectsGrid.hasMapObjectType(pos.getX(), pos.getY(), EMapObjectType.CONSTRUCTION_MARK);
		}
	}

	class PartitionableGrid implements IPartitionableGrid {
		@Override
		public boolean isBlocked(short x, short y) {
			return blockedGrid.isBlocked(x, y) || isLandscapeBlocking(x, y);
		}

		@Override
		public void changedPartitionAt(short x, short y) {
			landmarksCorrectionThread.addLandmarkedPosition(new ShortPoint2D(x, y));
		}

		@Override
		public void setDebugColor(final short x, final short y, Color color) {
			debugColors[x][y] = color;
		}
	}

}
