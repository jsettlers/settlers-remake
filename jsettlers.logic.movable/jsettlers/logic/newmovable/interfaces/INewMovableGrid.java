package jsettlers.logic.newmovable.interfaces;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;
import jsettlers.logic.newmovable.NewMovable;
import jsettlers.logic.player.Player;

/**
 * Defines all methods needed by a {@link NewMovable} to use the implementor as its grid.
 * 
 * @author Andreas Eberle
 * 
 */
public interface INewMovableGrid extends IStrategyGrid {

	/**
	 * Checks if there is a movable at the given position.
	 * 
	 * @param x
	 *            x coordinate of the position.
	 * @param y
	 *            y coordinate of the position.
	 * @return true if the given position has a movable standing on it.<br>
	 *         false if there is no movable.
	 */
	@Override
	boolean hasNoMovableAt(short x, short y);

	void leavePosition(ShortPoint2D position, NewMovable movable);

	/**
	 * Lets the given movable enter the given position.
	 * 
	 * @param position
	 *            Position to be entered.
	 * @param movable
	 *            Movable that enters the position.
	 * @param informFullArea
	 *            If true, the full soldier update area is informed if the given movable is attackable.<br>
	 *            If false, only a circle is informed if the given movable is attackable.
	 */
	void enterPosition(ShortPoint2D position, NewMovable movable, boolean informFullArea);

	Path calculatePathTo(IPathCalculateable pathRequester, ShortPoint2D targetPos);

	Path searchDijkstra(IPathCalculateable pathCalculateable, short centerX, short centerY, short radius, ESearchType searchType);

	Path searchInArea(IPathCalculateable pathCalculateable, short centerX, short centerY, short radius, ESearchType searchType);

	NewMovable getMovableAt(short x, short y);

	boolean isBlocked(short x, short y);

	boolean isProtected(short x, short y);

	boolean isBlockedOrProtected(short x, short y);

	/**
	 * Adds a map object to the grid that deletes itself after the given duration.
	 * 
	 * @param position
	 *            The position to add the map object.
	 * @param mapObjectType
	 *            The {@link EMapObjectType} of the map object that will be added.
	 * @param duration
	 *            The time (in seconds) the map object will stay on the grid.
	 * @param player
	 *            The {@link Player} of the map object.
	 */
	void addSelfDeletingMapObject(ShortPoint2D position, EMapObjectType mapObjectType, float duration, Player player);

	boolean isInBounds(short x, short y);

	boolean isValidPosition(IPathCalculateable pathRequester, ShortPoint2D position);

	ShortPoint2D calcDecentralizeVector(short x, short y);

}
