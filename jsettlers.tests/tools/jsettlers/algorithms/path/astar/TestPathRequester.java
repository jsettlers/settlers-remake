package jsettlers.algorithms.path.astar;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculatable;

public final class TestPathRequester implements IPathCalculatable {
	@Override
	public byte getPlayerId() {
		return 0;
	}

	@Override
	public ShortPoint2D getPos() {
		return null;
	}

	@Override
	public boolean needsPlayersGround() {
		return false;
	}
}