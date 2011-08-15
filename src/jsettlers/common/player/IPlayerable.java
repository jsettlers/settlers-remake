package jsettlers.common.player;

public interface IPlayerable {
	/**
	 * 
	 * @return -1 if this component has no player (for example a non occupied tile)<br>
	 *         otherwise: the player number of the component
	 */
	public byte getPlayer();
}
