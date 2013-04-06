package jsettlers;

import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;

public class GraphicsGridAdapter implements IGraphicsGrid {

	private short width;
	private short height;

	public GraphicsGridAdapter(int width, int height) {
		this.width = (short) width;
		this.height = (short) height;
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
	public byte getPlayerIdAt(int x, int y) {
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

	@Override
	public int nextDrawableX(int x, int y, int maxX) {
		return x + 1;
	}

	@Override
	public IPartitionSettings getPartitionSettings(int x, int y) {
		return null;
	}

}
