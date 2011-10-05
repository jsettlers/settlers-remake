package jsettlers.logic.map.newGrid;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.IHexMap;
import jsettlers.common.map.IHexTile;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ISPosition2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.map.hex.HexTile;
import jsettlers.logic.map.newGrid.blocked.BlockedGrid;
import jsettlers.logic.map.newGrid.landscape.LandscapeGrid;
import jsettlers.logic.map.newGrid.movable.MovableGrid;
import jsettlers.logic.map.newGrid.objects.ObjectsGrid;
import jsettlers.logic.map.newGrid.partition.PartitionsGrid;

/**
 * This is the main grid offering an interface for interacting with the grid.
 * 
 * @author Andreas Eberle
 * 
 */
public class MainGrid implements IHexMap {
	private final LandscapeGrid landscapeGrid;
	private final ObjectsGrid objectsGrid;
	private final PartitionsGrid partitionsGrid;
	private final MovableGrid movableGrid;
	private final BlockedGrid blockedGrid;

	private final short width;
	private final short height;

	private final PathfinderMap pathfinderMap;

	public MainGrid(short width, short height) {
		this.width = width;
		this.height = height;

		this.pathfinderMap = new PathfinderMap();

		this.landscapeGrid = new LandscapeGrid(width, height);
		this.objectsGrid = new ObjectsGrid(width, height);
		this.movableGrid = new MovableGrid(width, height);
		this.blockedGrid = new BlockedGrid(width, height);
		this.partitionsGrid = new PartitionsGrid(width, height, pathfinderMap);
	}

	@Override
	public EBuildingType getConstructionPreviewBuilding() {
		// TODO Auto-generated method stub
		return null;
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
	public IHexTile getTile(ISPosition2D pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IHexTile getTile(short x, short y) {
		// TODO Auto-generated method stub
		return null;
	}

	private class PathfinderMap implements IAStarPathMap {

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

	}
}
