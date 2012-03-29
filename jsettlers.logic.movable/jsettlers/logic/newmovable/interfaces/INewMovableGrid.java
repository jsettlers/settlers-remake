package jsettlers.logic.newmovable.interfaces;

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

	boolean isFreeForMovable(short x, short y);

	void leavePosition(ShortPoint2D position, NewMovable movable);

	void enterPosition(ShortPoint2D position, NewMovable movable);

	Path calculatePathTo(IPathCalculateable pathRequester, ShortPoint2D targetPos);

}
