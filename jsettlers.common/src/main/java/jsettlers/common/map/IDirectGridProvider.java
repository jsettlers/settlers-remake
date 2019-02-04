package jsettlers.common.map;

import java.util.BitSet;

import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.movable.IMovable;

public interface IDirectGridProvider {
	IMapObject[] getObjectArray();
	IMovable[] getMovableArray();
	BitSet getBorderArray();
	byte[][] getVisibleStatusArray();
	byte[] getHeightArray();
	boolean isFoWEnabled();
}
