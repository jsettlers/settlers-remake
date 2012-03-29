package jsettlers.logic.movable.testmap;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.algorithms.path.astar.HexAStar;
import jsettlers.logic.algorithms.path.astar.IAStarPathMap;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableBearer;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.newmovable.interfaces.INewMovableGrid;

public class MovableTestsMap implements IGraphicsGrid, IAStarPathMap {

	private final short width;
	private final short height;

	private final IMovable movableMap[][];
	private final EMaterialType materialTypeMap[][];
	private final byte materialAmmountMap[][];
	private final HexAStar aStar;

	public MovableTestsMap(int width, int height) {
		this.width = (short) width;
		this.height = (short) height;

		this.movableMap = new IMovable[width][height];
		this.materialTypeMap = new EMaterialType[width][height];
		this.materialAmmountMap = new byte[width][height];

		aStar = new HexAStar(this, this.width, this.height);
	}

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
		return movableMap[x][y];
	}

	@Override
	public IMapObject getMapObjectsAt(int x, int y) {
		return null;
	}

	@Override
	public byte getHeightAt(int x, int y) {
		return 0;
	}

	@Override
	public ELandscapeType getLandscapeTypeAt(int x, int y) {
		return ELandscapeType.GRASS;
	}

	@Override
	public int getDebugColorAt(int x, int y) {
		return -1;
	}

	@Override
	public boolean isBorder(int x, int y) {
		return false;
	}

	@Override
	public byte getPlayerAt(int x, int y) {
		return 0;
	}

	@Override
	public byte getVisibleStatus(int x, int y) {
		return CommonConstants.FOG_OF_WAR_VISIBLE;
	}

	@Override
	public boolean isFogOfWarVisible(int x, int y) {
		return true;
	}

	@Override
	public void setBackgroundListener(IGraphicsBackgroundListener backgroundListener) {
	}

	private final INewMovableGrid movableGrid = new INewMovableGrid() {
		@Override
		public void leavePosition(ShortPoint2D position, NewMovable movable) {
			if (movableMap[position.getX()][position.getY()] == movable) {
				movableMap[position.getX()][position.getY()] = null;
			}
		}

		@Override
		public boolean isFreeForMovable(short x, short y) {
			return movableMap[x][y] == null;
		}

		@Override
		public void enterPosition(ShortPoint2D position, NewMovable movable) {
			movableMap[position.getX()][position.getY()] = movable;
		}

		@Override
		public Path calculatePathTo(IPathCalculateable pathRequester, ShortPoint2D targetPos) {
			return aStar.findPath(pathRequester, targetPos);
		}

		@Override
		public void addJoblessBearer(IManageableBearer bearer) {
		}

		@Override
		public boolean takeMaterial(ShortPoint2D pos, EMaterialType materialType) {
			if (materialTypeMap[pos.getX()][pos.getY()] == materialType && materialAmmountMap[pos.getX()][pos.getY()] > 0) {
				materialAmmountMap[pos.getX()][pos.getY()]--;
				return true;
			} else {
				return false;
			}
		}
	};

	public INewMovableGrid getMovableGrid() {
		return movableGrid;
	}

	// ==================== IAStarPathMap ==============================================================

	@Override
	public boolean isBlocked(IPathCalculateable requester, short x, short y) {
		return false;
	}

	@Override
	public float getCost(short sx, short sy, short tx, short ty) {
		return 1;
	}

	@Override
	public void markAsOpen(short x, short y) {
	}

	@Override
	public void markAsClosed(short x, short y) {
	}

	@Override
	public void setDebugColor(short x, short y, Color color) {
	}
}
