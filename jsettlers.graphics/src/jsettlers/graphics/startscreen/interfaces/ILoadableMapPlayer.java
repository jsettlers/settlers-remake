package jsettlers.graphics.startscreen.interfaces;

import jsettlers.common.Color;

/**
 * This is a player that was active on a loadable map.
 * 
 * @author michael
 *
 */
public interface ILoadableMapPlayer {
	/**
	 * Gets the name of the player
	 * 
	 * @return
	 */
	public String getName();

	public Color getColor();

	public boolean isDead();
}
