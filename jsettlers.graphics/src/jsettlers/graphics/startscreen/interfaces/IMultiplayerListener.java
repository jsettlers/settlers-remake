package jsettlers.graphics.startscreen.interfaces;

/**
 * This is a listener to the multiplayer screen.
 * 
 * @author michael
 */
public interface IMultiplayerListener {
	/**
	 * Called once when the game is starting.
	 * 
	 * @param game
	 */
	void gameIsStarting(IStartingGame game);

	/**
	 * Called when the game was aborted and the user is forced to exit the screen.
	 */
	void gameAborted();
}
