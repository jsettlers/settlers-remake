package jsettlers.mapcreator.data;

import jsettlers.common.landscape.ELandscapeType;

/**
 * This is a map data delta, that can be applyed from a map data to an other.
 * 
 * @author michael
 */
public class MapDataDelta {
	private HeightChange heightChanges = null;
	private LandscapeChange landscapeChanges = null;
	
	public MapDataDelta() {
	}
	
	public synchronized void addHeightChange(int x, int y, byte height) {
		HeightChange c = new HeightChange();
		c.x = (short) x;
		c.y = (short) y;
		c.height = height;
		c.next = heightChanges;
		heightChanges = c;
	}
	
	public HeightChange getHeightChanges() {
	    return heightChanges;
    }

	public synchronized void addLandscapeChange(int x, int y, ELandscapeType l) {
		LandscapeChange c = new LandscapeChange();
		c.x = (short) x;
		c.y = (short) y;
		c.landscape = l;
		c.next = landscapeChanges;
		landscapeChanges = c;
	}
	
	public static class HeightChange {
		HeightChange next = null;
		short x;
		short y;
		byte height;
	}

	public LandscapeChange getLandscapeChanges() {
	    return landscapeChanges;
    }
	
	public static class LandscapeChange {
		LandscapeChange next = null;
		short x;
		short y;
		ELandscapeType landscape;
	}
}
