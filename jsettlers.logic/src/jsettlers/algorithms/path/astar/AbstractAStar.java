package jsettlers.algorithms.path.astar;

import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.common.position.ShortPoint2D;

public abstract class AbstractAStar {
	public abstract Path findPath(IPathCalculatable requester, final short sx, final short sy, final short tx, final short ty);

	public abstract Path findPath(IPathCalculatable aStarPathable, ShortPoint2D targetPos);
}
