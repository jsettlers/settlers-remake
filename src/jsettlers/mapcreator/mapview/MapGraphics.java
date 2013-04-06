package jsettlers.mapcreator.mapview;

import jsettlers.common.Color;
import jsettlers.common.CommonConstants;
import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.map.IGraphicsBackgroundListener;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;
import jsettlers.mapcreator.data.MapData;
import jsettlers.mapcreator.data.objects.ObjectContainer;

public class MapGraphics implements IGraphicsGrid {

	private final MapData data;
	private boolean showResources = false;

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
		if (showResources) {
			byte amount = data.getResourceAmount((short) x, (short) y);
			if (amount > 0) {
				return ResourceMapObject.get(data.getResourceType((short) x, (short) y), amount);
			} else {
				return null;
			}
		} else {
			ObjectContainer container = data.getMapObjectContainer(x, y);
			if (container instanceof IMapObject) {
				return (IMapObject) container;
			} else {
				return null;
			}
		}
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
		return data.isFailpoint(x, y) ? Color.RED.getARGB() : -1;
	}

	@Override
	public boolean isBorder(int x, int y) {
		return data.isBorder(x, y);
	}

	@Override
	public byte getPlayerIdAt(int x, int y) {
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

	public void setShowResources(boolean b) {
		showResources = b;
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
