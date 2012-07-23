package jsettlers.algorithms.supergrid.wnd;

import java.util.BitSet;
import java.util.LinkedList;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.astar.supergrid.ISuperGridAStarGrid;

class TestMapGrid implements ISuperGridAStarGrid, IGraphicsGrid {

	private final short width;
	private final short height;
	private final BitSet blocked;
	private final Color[][] colors;
	private IGraphicsBackgroundListener backgroundListener;
	private IBlockedChangedListener listener;

	public TestMapGrid(short width, short height) {
		this.width = width;
		this.height = height;

		this.blocked = new BitSet(width * height);
		this.colors = new Color[width][height];

		for (int x = width / 3; x < 2 * width / 3; x++) {
			for (int y = height / 3; y < 2 * height / 3; y++) {
				blocked.set(y * width + x);
			}
		}
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
	public IMovable getMovableAt(int x, int y) {
		return null;
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
		return isBlocked(x, y) ? ELandscapeType.WATER1 : ELandscapeType.GRASS;
	}

	@Override
	public int getDebugColorAt(int x, int y) {
		return colors[x][y] == null ? Color.TRANSPARENT.getARGB() : colors[x][y].getARGB();
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
		this.backgroundListener = backgroundListener;
	}

	@Override
	public void setDebugColor(int x, int y, Color color) {
		colors[x][y] = color;
	}

	@Override
	public boolean isBlocked(int x, int y) {
		return blocked.get(y * width + x);
	}

	public void invertBlocked(int x, int y) {
		blocked.flip(y * width + x);
		backgroundListener.backgroundChangedAt((short) x, (short) y);
		this.listener.blockedChanged(x, y);
	}

	public void invertBlocked(LinkedList<ShortPoint2D> positions) {
		for (ShortPoint2D curr : positions) {
			blocked.flip(curr.getX() + curr.getY() * width);
			backgroundListener.backgroundChangedAt(curr.getX(), curr.getY());
		}

		this.listener.blockedChanged(positions);
	}

	@Override
	public void setBlockedChangedListener(IBlockedChangedListener listener) {
		this.listener = listener;
	}

	@Override
	public int nextDrawableX(int x, int y) {
		return x + 1;
	}
}
