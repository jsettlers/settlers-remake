package jsettlers.graphics.startscreen.interfaces;

/**
 * This is a list item of players that joined or can join the game.
 * @author michael
 *
 */
public interface IMultiplayerPlayer {
	/**
	 * Gets the name of the player, may return null if the channel is free.
	 * @return
	 */
	public String getName();
	
	/**
	 * If the player is ready for start.
	 * @return
	 */
	public boolean isReady();
	
	/* getTeam() */
}
