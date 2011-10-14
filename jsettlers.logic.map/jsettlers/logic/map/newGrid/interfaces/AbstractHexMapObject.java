package jsettlers.logic.map.newGrid.interfaces;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.mapobject.IMapObject;
import jsettlers.common.position.RelativePoint;

/**
 * extension to IMapObject to get functions needed by the hex grid.
 * 
 * @author Andreas Eberle
 */
public abstract class AbstractHexMapObject implements IMapObject {
	private static final RelativePoint[] NO_BLOCKING = new RelativePoint[] {};
	private static final RelativePoint[] SELF_BLOCKING = new RelativePoint[] { new RelativePoint(0, 0) };
	/**
	 * next map object to build a list of map objects
	 */
	private AbstractHexMapObject next;

	public abstract boolean cutOff();

	/**
	 * is this map object blocking the position it has?
	 * 
	 * @return true if this map object is blocking it's position
	 */
	@Deprecated
	protected boolean isBlocking() {
		return false;
	}

	public RelativePoint[] getBlockedTiles() {
		if (isBlocking()) {
			return SELF_BLOCKING;
		} else {
			return NO_BLOCKING;
		}
	}

	@Override
	public final AbstractHexMapObject getNextObject() {
		return this.next;
	}

	/**
	 * checks if any map object on this list is blocking.
	 * 
	 * @return true if any of the map objects on this list is blocking it's position (returns true when {@link #isBlocking()} is called).
	 */
	@Deprecated
	public final boolean hasAnyBlocking() {
		return isBlocking() || this.next != null && this.next.hasAnyBlocking();
	}

	/**
	 * appends the given object to this list.
	 * 
	 * @param mapObject
	 *            map object to be appended
	 */
	public final void addMapObject(AbstractHexMapObject mapObject) {
		if (this.next == null)
			this.next = mapObject;
		else
			this.next.addMapObject(mapObject);
	}

	/**
	 * The given mapObject is checked by ==, not by equals<br>
	 * NOTE: the first element can't be removed by this method.
	 * 
	 * @param mapObject
	 *            mapObject to be removed
	 * @return true if the mapObject had been found and removed<br>
	 *         false if it wasn't on the list
	 */
	public final boolean removeMapObject(AbstractHexMapObject mapObject) {
		if (this.next != null) {
			if (this.next == mapObject) {
				this.next = this.next.next;
				return true;
			} else {
				return this.next.removeMapObject(mapObject);
			}
		} else {
			return false;
		}
	}

	public final AbstractHexMapObject removeMapObjectType(EMapObjectType mapObjectType) {
		AbstractHexMapObject removed = null;
		if (this.next != null) {
			if (this.next.getObjectType() == mapObjectType) {
				removed = this.next;
				this.next = this.next.next;
			} else {
				removed = this.next.removeMapObjectType(mapObjectType);
			}
		}
		return removed;
	}

	/**
	 * @return true if this map object can be cut.
	 */
	public abstract boolean canBeCut();

	/**
	 * @param mapObjectType
	 *            {@link EMapObjectType} to be checked
	 * @return true if any of the objects in this list is of the given mapObjectType and {@link #canBeCut()} returns true
	 */
	public boolean hasCuttableObject(EMapObjectType mapObjectType) {
		return this.getObjectType() == mapObjectType && this.canBeCut() || this.next != null && this.next.hasCuttableObject(mapObjectType);
	}

	/**
	 * @param mapObjectType
	 *            type to be looked for
	 * @return true if at least one of the map objects fits the given EMapObjectType
	 */
	public boolean hasMapObjectType(EMapObjectType mapObjectType) {
		return this.getObjectType() == mapObjectType || this.next != null && this.next.hasMapObjectType(mapObjectType);
	}

	public AbstractHexMapObject getMapObject(EMapObjectType type) {
		if (this.getObjectType() == type) {
			return this;
		} else {
			return this.next != null ? this.next.getMapObject(type) : null;
		}
	}
}
