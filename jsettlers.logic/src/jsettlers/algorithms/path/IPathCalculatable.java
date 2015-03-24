package jsettlers.algorithms.path;

import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;

public interface IPathCalculatable extends IPlayerable, ILocatable {
	/**
	 * 
	 * @return true if this path requester can only walk on it's own players ground.<br>
	 *         false if the requester can use everybody's ground.
	 */
	boolean needsPlayersGround();

}
