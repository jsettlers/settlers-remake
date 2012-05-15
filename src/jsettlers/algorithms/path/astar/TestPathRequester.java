package jsettlers.algorithms.path.astar;

import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.algorithms.path.IPathCalculateable;

public final class TestPathRequester implements IPathCalculateable {
	@Override
	public byte getPlayer() {
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