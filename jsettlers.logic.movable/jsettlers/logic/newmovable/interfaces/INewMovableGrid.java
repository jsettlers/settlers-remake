package jsettlers.logic.newmovable.interfaces;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.newmovable.NewMovable;

/**
 * Defines all methods needed by a {@link NewMovable} to use the implementor as its grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INewMovableGrid extends IStrategyGrid {

	boolean hasNoMovableAt(short x, short y);

	void leavePosition(ShortPoint2D position, NewMovable movable);

	void enterPosition(ShortPoint2D position, NewMovable movable);

	Path calculatePathTo(IPathCalculateable pathRequester, ShortPoint2D targetPos);

	Path searchDijkstra(IPathCalculateable pathCalculateable, short centerX, short centerY, short radius, ESearchType searchType);

	Path searchInArea(IPathCalculateable pathCalculateable, short centerX, short centerY, short radius, ESearchType searchType);

	NewMovable getMovableAt(short x, short y);

	/**
	 * 
	 * @param x
	 * @param y
	 * @return player currently occupying the given position.
	 */
	byte getPlayerAt(short x, short y);

	boolean isBlocked(short x, short y);

	boolean isProtected(short x, short y);

	boolean isBlockedOrProtected(short x, short y);

	void addSelfDeletingMapObject(ShortPoint2D position, EMapObjectType mapObjectType, int duration, byte player);

	boolean isInBounds(short x, short y);

	boolean isValidPosition(IPathCalculateable pathRequester, ShortPoint2D position);

}
