package jsettlers.logic.algorithms.path.astar.normal;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;
import jsettlers.logic.algorithms.path.Path;

public interface IAStar {

	Path findPath(IPathCalculateable requester, final short sx, final short sy, final short tx, final short ty);

	Path findPath(IPathCalculateable aStarPathable, ShortPoint2D targetPos);
}
