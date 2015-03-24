package jsettlers.algorithms.fogofwar;

import jsettlers.common.player.IPlayerable;
import jsettlers.common.position.ILocatable;

public interface IViewDistancable extends ILocatable, IPlayerable {
	short getViewDistance();

}
