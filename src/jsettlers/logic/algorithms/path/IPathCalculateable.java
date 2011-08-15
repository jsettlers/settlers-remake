package jsettlers.logic.algorithms.path;

import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;

public interface IPathCalculateable extends IPlayerable, ILocatable {
	/**
	 * 
	 * @return if this method returns true, that means that this requester can only work on ground that belongs to his own player.
	 */
	boolean needsPlayersGround();

}
