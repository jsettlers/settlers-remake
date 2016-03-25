/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.movable.interfaces;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.algorithms.path.astar.AStarOptions;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.ESearchType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.player.Player;

/**
 * Defines all methods needed by a {@link Movable} to use the implementor as its grid.
 * 
 * @author Andreas Eberle
 * 
 */
public abstract class AbstractMovableGrid extends AbstractStrategyGrid {
	private static final long serialVersionUID = -236805842467532505L;

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
	public abstract boolean hasNoMovableAt(short x, short y);

	public abstract void leavePosition(ShortPoint2D position, Movable movable);

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
	public abstract void enterPosition(ShortPoint2D position, Movable movable, boolean informFullArea);

	public Path calculatePathTo(IPathCalculatable pathRequester, ShortPoint2D targetPos) {
		return calculatePathTo(pathRequester, targetPos, new AStarOptions());
	}
	
	public abstract Path calculatePathTo(IPathCalculatable requester, ShortPoint2D targetPos, AStarOptions opts);

	public abstract Path searchDijkstra(IPathCalculatable requester, short centerX, short centerY, short radius, ESearchType searchType);

	public abstract Path searchInArea(IPathCalculatable requester, short centerX, short centerY, short radius, ESearchType searchType);

	public abstract boolean isBlocked(short x, short y);

	public abstract boolean isProtected(short x, short y);

	public abstract boolean isBlockedOrProtected(short x, short y);

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
	public abstract void addSelfDeletingMapObject(ShortPoint2D position, EMapObjectType mapObjectType, float duration, Player player);

	public abstract boolean isInBounds(short x, short y);

	public abstract ShortPoint2D calcDecentralizeVector(short x, short y);

	public abstract Player getPlayerAt(ShortPoint2D position);

	public abstract boolean isValidPosition(IPathCalculatable pathCalculatable, ShortPoint2D position);

	public abstract boolean isValidNextPathPosition(IPathCalculatable pathCalculatable, ShortPoint2D nextPos, ShortPoint2D targetPos);

}
