package jsettlers.mapcreator.mapview;

import jsettlers.common.Color;
import jsettlers.common.landscape.ELandscapeType;
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
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public IMapObject getMapObjectsAt(int x, int y) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public byte getHeightAt(int x, int y) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public ELandscapeType getLandscapeTypeAt(int x, int y) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public Color getDebugColorAt(int x, int y) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public boolean isBorder(int x, int y) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public byte getPlayerAt(int x, int y) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public byte getVisibleStatus(int x, int y) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public boolean isFogOfWarVisible(int x, int y) {
	    // TODO Auto-generated method stub
	    return false;
    }
	
}
