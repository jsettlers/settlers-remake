package jsettlers.common.player;

/**
 * Interface defining an object that may belong to a player.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPlayerable {
	/**
	 * Gives the id of the player of this object.
	 * 
	 * @return -1 if this component has no player (for example a non occupied tile)<br>
	 *         otherwise: the id of the player of the component.
	 */
	public byte getPlayerId();
}
