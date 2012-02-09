package jsettlers.mapcreator.mapview;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.mapcreator.data.MapData;

public class MapGraphics implements IGraphicsGrid {

	private final MapData data;

	public MapGraphics(MapData data) {
		this.data = data;
	}

	@Override
	public short getHeight() {
		return (short) data.getWidth();
	}

	@Override
	public short getWidth() {
		return (short) data.getHeight();
	}

	@Override
	public IMovable getMovableAt(int x, int y) {
		return data.getMovableContainer(x, y);
	}

	@Override
	public IMapObject getMapObjectsAt(int x, int y) {
		return data.getMapObjectContainer(x, y);
	}

	@Override
	public byte getHeightAt(int x, int y) {
		return data.getLandscapeHeight(x, y);
	}

	@Override
	public ELandscapeType getLandscapeTypeAt(int x, int y) {
		return data.getLandscape(x, y);
	}

	@Override
	public int getDebugColorAt(int x, int y) {
		return data.isFailpoint(x, y) ? Color.RED.getRGBA() : -1;
	}

	@Override
	public boolean isBorder(int x, int y) {
		return data.isBorder(x, y);
	}

	@Override
	public byte getPlayerAt(int x, int y) {
		return data.getPlayer(x, y);
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
		data.setListener(backgroundListener);
	}

}
