package jsettlers.common.mapobject;

import jsettlers.common.map.object.MapObject;
import jsettlers.common.movable.IMovable;

/**
 * Interface for {@link MapObject}.ATTACKABLE_TOWER objects.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IAttackableTowerMapObject {
	/**
	 * 
	 * @return The movable standing on this {@link IAttackableTowerMapObject}, or null if none needs to be drawn here.
	 */
	IMovable getMovable();
}
