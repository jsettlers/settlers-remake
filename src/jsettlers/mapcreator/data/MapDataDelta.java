package jsettlers.mapcreator.data;

import jsettlers.common.landscape.ELandscapeType;
import jsettlers.common.position.ISPosition2D;

/**
 * This is a map data delta, that can be applyed from a map data to an other.
 * 
 * @author michael
 */
public class MapDataDelta {
	private HeightChange heightChanges = null;
	private LandscapeChange landscapeChanges = null;
	private ObjectAdder addObjects = null;
	private ObjectRemover removeObjects = null;

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

	public static class ObjectAdder {
		ObjectAdder next = null;
		short x;
		short y;
		ObjectContainer obj;
	}

	public void addObject(int x, int y, ObjectContainer obj) {
		if (obj != null) {
			ObjectAdder adder = new ObjectAdder();
			adder.x = (short) x;
			adder.y = (short) y;
			adder.obj = obj;
			adder.next = addObjects;
			addObjects = adder;
		}
	}
	
	public ObjectAdder getAddObjects() {
	    return addObjects;
    }

	public static class ObjectRemover {
		ObjectRemover next = null;
		short x;
		short y;
	}

	public void removeObject(int x, int y) {
		ObjectRemover remover = new ObjectRemover();
		remover.x = (short) x;
		remover.y = (short) y;
		remover.next = removeObjects;
		removeObjects = remover;
	}
	
	public ObjectRemover getRemoveObjects() {
	    return removeObjects;
    }
	
	//ignore start item!
	private StartPointSetter startPoints = new StartPointSetter();

	public static class StartPointSetter {
		StartPointSetter next = null;
		byte player;
		ISPosition2D pos;
	}

	public StartPointSetter getStartPoints() {
	    return startPoints.next;
    }
	
	public void setStartPoint(byte player, ISPosition2D pos) {
	    StartPointSetter cur = startPoints;
	    while (cur.next != null) {
	    	if (cur.next.player == player) {
	    		cur.next = cur.next.next;
	    	}
	    }
	    StartPointSetter item = new StartPointSetter();
	    item.player = player;
	    item.pos = pos;
	    item.next = startPoints.next;
	    startPoints.next = item;
    }

}
