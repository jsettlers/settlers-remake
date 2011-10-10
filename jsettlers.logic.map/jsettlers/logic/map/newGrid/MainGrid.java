package jsettlers.logic.map.newGrid;

import java.awt.Color;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.algorithms.path.dijkstra.IDijkstraPathMap;
import jsettlers.logic.map.hex.HexTile;
import jsettlers.logic.map.hex.interfaces.AbstractHexMapObject;
import jsettlers.logic.map.newGrid.blocked.BlockedGrid;
import jsettlers.logic.map.newGrid.interfaces.graphics.IGraphicsGrid;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
import jsettlers.logic.map.newGrid.movable.MovableGrid;
import jsettlers.logic.map.newGrid.objects.ObjectsGrid;
import jsettlers.logic.map.newGrid.partition.PartitionsGrid;
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
	private final LandscapeGrid landscapeGrid;
	private final ObjectsGrid objectsGrid;
	private final PartitionsGrid partitionsGrid;
	private final MovableGrid movableGrid;
	private final BlockedGrid blockedGrid;

	private final short width;
	private final short height;

	private final PathfinderGrid pathfinderGrid;
	private IGraphicsGrid graphicsGrid;
	private MapObjectsManager objectsManager;

	public MainGrid(short width, short height) {
		this.width = width;
		this.height = height;

		this.pathfinderGrid = new PathfinderGrid();
		this.graphicsGrid = new GraphicsGrid();
		this.objectsManager = new MapObjectsManager(new MapObjectsManagerGrid());

		this.landscapeGrid = new LandscapeGrid(width, height);
		this.objectsGrid = new ObjectsGrid(width, height);
		this.movableGrid = new MovableGrid(width, height);
		this.blockedGrid = new BlockedGrid(width, height);
		this.partitionsGrid = new PartitionsGrid(width, height, pathfinderGrid);
	}

	public IGraphicsGrid getGraphicsGrid() {
		return graphicsGrid;
	}

	/**
	 * Executes a search type action.
	 * 
	 * @param pos
	 *            The position the settler is currently at
	 * @param type
	 *            The type the settler searched for and to which the corrosponding aciton should be done.
	 * @return true if we succeeded.
	 */
	public boolean executeSearchType(ISPosition2D pos, ESearchType type) {
		return objectsManager.executeSearchType(pos, type);
	}

	public MapObjectsManager getMapObjectsManager() {
		return objectsManager;
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
			return blockedGrid.isBlocked(x, y) || (requester.needsPlayersGround() && requester.getPlayer() != partitionsGrid.getPlayer(x, y));
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
			return x >= 0 && x < width && y >= 0 && y < height;
		}

		@Override
		public boolean fitsSearchType(short x, short y, ESearchType type, IPathCalculateable requester) {
			// TODO Auto-generated method stub
			return false;
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
			return landscapeGrid.getHeight(x, y);
		}

		@Override
		public ELandscapeType getLandscapeTypeAt(short x, short y) {
			return landscapeGrid.getLandscapeType(x, y);
		}

		@Override
		public Color getDebugColorAt(short x, short y) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isBorder(short x, short y) {
			// TODO calculate border asynchronously
			return false;
		}

		@Override
		public byte getPlayerAt(short x, short y) {
			return partitionsGrid.getPlayer(x, y);
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
}
