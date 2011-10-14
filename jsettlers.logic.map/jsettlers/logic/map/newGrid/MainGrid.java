package jsettlers.logic.map.newGrid;

import java.awt.Color;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.shapes.MapNeighboursArea;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.borders.BordersThread;
import jsettlers.logic.algorithms.borders.IBordersThreadGrid;
import jsettlers.logic.algorithms.construction.ConstructMarksCalculator;
import jsettlers.logic.algorithms.construction.IConstructionMarkableMap;
import jsettlers.logic.algorithms.landmarks.ILandmarksThreadMap;
import jsettlers.logic.algorithms.landmarks.LandmarksCorrectingThread;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.area.InAreaFinder;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.algorithms.path.dijkstra.DijkstraAlgorithm;
import jsettlers.logic.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.map.hex.HexTile;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;
import jsettlers.logic.map.hex.interfaces.IHexMovable;
import jsettlers.logic.map.hex.interfaces.IHexStack;
import jsettlers.logic.map.newGrid.blocked.BlockedGrid;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
import jsettlers.logic.map.newGrid.movable.MovableGrid;
import jsettlers.logic.map.newGrid.objects.ObjectsGrid;
import jsettlers.logic.map.newGrid.partition.PartitionsGrid;
import jsettlers.logic.movable.IMovableGrid;
import jsettlers.logic.objects.IMapObjectsManagerGrid;
import jsettlers.logic.objects.IMapObjectsManagerTile;
import jsettlers.logic.objects.MapObjectsManager;

/**
 * This is the main grid offering an interface for interacting with the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public class MainGrid {
	protected final LandscapeGrid landscapeGrid;
	protected final ObjectsGrid objectsGrid;
	protected final PartitionsGrid partitionsGrid;
	protected final MovableGrid movableGrid;
	protected final BlockedGrid blockedGrid;

	protected final short width;
	protected final short height;

	protected final MovablePathfinderGrid movablePathfinderGrid;
	private final IGraphicsGrid graphicsGrid;
	protected final MapObjectsManager mapObjectsManager;
	private final LandmarksCorrectingThread landmarksCorrectionThread;
	private final ConstructMarksCalculator constructionMarksCalculator;
	private final BordersThread bordersThread;
	private final BuildingsGrid buildingsGrid;

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
		this.partitionsGrid = new PartitionsGrid(width, height, movablePathfinderGrid);

		this.landmarksCorrectionThread = new LandmarksCorrectingThread(new LandmarksGrid());
		this.bordersThread = new BordersThread(new BordersThreadGrid());
		this.constructionMarksCalculator = new ConstructMarksCalculator(new ConstructionMarksGrid(), (byte) 0); // TODO player needs to be set
		// dynamically
		this.buildingsGrid = new BuildingsGrid();

	}

	public IGraphicsGrid getGraphicsGrid() {
		return graphicsGrid;
	}

	protected final boolean isInBounds(short x, short y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	private void changePlayerAt(ISPosition2D position, byte player) {
		partitionsGrid.changePlayerAt(position, player);
		bordersThread.checkPosition(position);
	}

	private class PathfinderGrid implements IAStarPathMap, IDijkstraPathMap {

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
			return blockedGrid.isBlocked(x, y) || (requester.needsPlayersGround() && requester.getPlayer() != partitionsGrid.getPlayerAt(x, y));
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

		}

		@Override
		public void markAsClosed(short x, short y) {

		}

		@Override
		public boolean isInBounds(short x, short y) {
			return this.isInBounds(x, y);
		}

		@Override
		public boolean fitsSearchType(short x, short y, ESearchType searchType, IPathCalculateable pathCalculateable) {
			switch (searchType) {

			case FOREIGN_GROUND:
				return !blockedGrid.isBlocked(x, y) && !hasSamePlayer(x, y, pathCalculateable) && !isMarked(x, y);

			case CUTTABLE_TREE:
				return isInBounds(x, y) && objectsGrid.hasCuttableObject(x, y, EMapObjectType.TREE_ADULT)
						&& hasSamePlayer((short) (x - 1), (short) (y - 1), pathCalculateable) && !isMarked(x, y);

			case PLANTABLE_TREE:
				return y < height - 1 && isTreePlantable(x, (short) (y + 1)) && !hasProtectedNeighbor(x, (short) (y + 1))
						&& hasSamePlayer(x, (short) (y + 1), pathCalculateable) && !isMarked(x, y);

			case PLANTABLE_CORN:
				return isCornPlantable(x, y) && hasSamePlayer(x, y, pathCalculateable) && !isMarked(x, y);

			case CUTTABLE_CORN:
				return isCornCuttable(x, y) && hasSamePlayer(x, y, pathCalculateable) && !isMarked(x, y);

			case CUTTABLE_STONE:
				return y < height - 1 && x < width - 2 && objectsGrid.hasCuttableObject((short) (x - 2), (short) (y - 1), EMapObjectType.STONE)
						&& hasSamePlayer(x, y, pathCalculateable) && !isMarked(x, y);

			case ENEMY:
				IMovable movable = movableGrid.getMovableAt(x, y);
				return movable != null && movable.getPlayer() != pathCalculateable.getPlayer();

			case RIVER:
				return isRiver(x, y) && hasSamePlayer(x, y, pathCalculateable) && !isMarked(x, y);

			case FISHABLE:
				return hasNeighbourLandscape(x, y, ELandscapeType.WATER);

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
				if (blockedGrid.isPortected(currDir.getNextTileX(x), currDir.getNextTileY(y)))
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

		private boolean isTreePlantable(short x, short y) {
			return landscapeGrid.getLandscapeTypeAt(x, y) == ELandscapeType.GRASS && !blockedGrid.isBlocked(x, y) && !hasBlockedNeighbor(x, y);
		}

		private boolean hasBlockedNeighbor(short x, short y) {
			for (EDirection currDir : EDirection.values()) {
				if (blockedGrid.isBlocked(currDir.getNextTileX(x), currDir.getNextTileY(y)))
					return true;
			}

			return false;
		}

		private boolean isCornPlantable(short x, short y) {
			ELandscapeType landscapeType = landscapeGrid.getLandscapeTypeAt(x, y);
			return (landscapeType == ELandscapeType.GRASS || landscapeType == ELandscapeType.EARTH) && !blockedGrid.isPortected(x, y)
					&& !hasProtectedNeighbor(x, y) && !objectsGrid.hasMapObjectType(x, y, EMapObjectType.CORN_GROWING)
					&& !objectsGrid.hasMapObjectType(x, y, EMapObjectType.CORN_ADULT)
					&& !objectsGrid.hasNeighborObjectType(x, y, EMapObjectType.CORN_ADULT)
					&& !objectsGrid.hasNeighborObjectType(x, y, EMapObjectType.CORN_GROWING);
		}

		private boolean isCornCuttable(short x, short y) {
			return objectsGrid.hasCuttableObject(x, y, EMapObjectType.CORN_ADULT);
		}

	}

	private class GraphicsGrid implements IGraphicsGrid {

		@Override
		public short getHeight() {
			return height;
		}

		@Override
		public short getWidth() {
			return width;
		}

		@Override
		public IMovable getMovableAt(short x, short y) {
			return movableGrid.getMovableAt(x, y);
		}

		@Override
		public IMapObject getMapObjectsAt(short x, short y) {
			return objectsGrid.getObjectsAt(x, y);
		}

		@Override
		public byte getHeightAt(short x, short y) {
			return landscapeGrid.getHeightAt(x, y);
		}

		@Override
		public ELandscapeType getLandscapeTypeAt(short x, short y) {
			return landscapeGrid.getLandscapeTypeAt(x, y);
		}

		@Override
		public Color getDebugColorAt(short x, short y) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isBorder(short x, short y) {
			return partitionsGrid.isBorderAt(x, y);
		}

		@Override
		public byte getPlayerAt(short x, short y) {
			return partitionsGrid.getPlayerAt(x, y);
		}

	}

	private class MapObjectsManagerGrid implements IMapObjectsManagerGrid {
		@Override
		public IMapObjectsManagerTile getTile(final short x, final short y) {
			return new IMapObjectsManagerTile() { // TODO remove the tile and replace it with single methods

				@Override
				public void setLandscape(ELandscapeType landscapeType) {
					landscapeGrid.setLandscapeType(x, y, landscapeType);
				}

				@Override
				public void setBlocked(boolean blocked) {
					blockedGrid.setBlocked(x, y, blocked);
				}

				@Override
				public AbstractHexMapObject removeMapObjectType(EMapObjectType mapObjectType) {
					return objectsGrid.removeMapObjectType(x, y, mapObjectType);
				}

				@Override
				public boolean removeMapObject(AbstractHexMapObject mapObject) {
					return objectsGrid.removeMapObjectType(x, y, mapObject);
				}

				@Override
				public boolean isBlocked() {
					return blockedGrid.isBlocked(x, y);
				}

				@Override
				public AbstractHexMapObject getMapObject(EMapObjectType mapObjectType) {
					return objectsGrid.getMapObjectAt(x, y, mapObjectType);
				}

				@Override
				public void addMapObject(AbstractHexMapObject mapObject) {
					objectsGrid.addMapObjectAt(x, y, mapObject);
				}
			};
		}

		@Override
		public short getWidth() {
			return width;
		}

		@Override
		public short getHeight() {
			return height;
		}

	}

	private class LandmarksGrid implements ILandmarksThreadMap {
		@Override
		public boolean isBlocked(short x, short y) {
			return blockedGrid.isBlocked(x, y);
		}

		@Override
		public byte getPlayerAt(ISPosition2D position) {
			return partitionsGrid.getPlayerAt(position);
		}

		@Override
		public void setPlayerAt(short x, short y, byte newPlayer) {
			changePlayerAt(new ShortPoint2D(x, y), newPlayer); // TODO check if creation of ShortPoint2D can be avoided
		}
	}

	private class ConstructionMarksGrid implements IConstructionMarkableMap {
		@Override
		public void setConstructMarking(ISPosition2D pos, byte value) {
			mapObjectsManager.setConstructionMarking(pos, value);
		}

		@Override
		public boolean isBuildingPlaceable(ISPosition2D position, byte player) {
			return !blockedGrid.isBlocked(position.getX(), position.getY()) && partitionsGrid.getPlayerAt(position) == player;
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
	}

	private class MovablePathfinderGrid extends PathfinderGrid implements IMovableGrid {
		private final HexAStar aStar;
		private final DijkstraAlgorithm dijkstra;
		private final InAreaFinder inAreaFinder;

		public MovablePathfinderGrid() {
			aStar = new HexAStar(this);
			dijkstra = new DijkstraAlgorithm(this);
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
			return blockedGrid.isBlocked(x, y);
		}

		@Override
		public boolean isBlocked(ISPosition2D position) {
			return isBlocked(position.getX(), position.getY());
		}

		@Override
		public boolean canPush(ISPosition2D position, EMaterialType material) {
			return mapObjectsManager.canPush(position, material);
		}

		@Override
		public boolean pushMaterial(ISPosition2D position, EMaterialType materialType) {
			if (mapObjectsManager.pushMaterial(position, materialType)) {
				partitionsGrid.pushMaterial(position, materialType);
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
		public void changeHeightAt(ISPosition2D position, byte newHeight) {
			landscapeGrid.setHeight(position.getX(), position.getY(), newHeight);
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
		public void setPlayerAt(ISPosition2D position, byte player) {
			changePlayerAt(position, player);
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
	}

	private class BuildingsGrid implements IBuildingsGrid {
		@Override
		public byte getHeightAt(ISPosition2D position) {
			return landscapeGrid.getHeightAt(position.getX(), position.getY());
		}

		@Override
		public boolean setBuilding(ISPosition2D position, IBuilding newBuilding) {
			// FIXME make building to map objects
			return false;
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
		public void placeStack(ISPosition2D position, IHexStack stack) {
			// FIXME implement requests of material
		}

		@Override
		public void removeStack(ISPosition2D position) {
			// TODO remove this method, it's not needed any more
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

	}
}
