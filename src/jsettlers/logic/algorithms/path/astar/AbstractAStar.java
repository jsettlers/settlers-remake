package jsettlers.logic.algorithms.path.astar;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculatable;
import jsettlers.logic.algorithms.path.Path;

public abstract class AbstractAStar {
	public abstract Path findPath(IPathCalculatable requester, final short sx, final short sy, final short tx, final short ty);

	public abstract Path findPath(IPathCalculatable aStarPathable, ShortPoint2D targetPos);
}
