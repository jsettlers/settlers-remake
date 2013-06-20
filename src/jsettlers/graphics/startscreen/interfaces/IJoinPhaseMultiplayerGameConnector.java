package jsettlers.graphics.startscreen.interfaces;

/**
 * This is the screen that is displayed when a multiplayer game is in the join
 * phase.
 * 
 * @author michael
 * @author Andreas Eberle
 */
public interface IJoinPhaseMultiplayerGameConnector {
	/**
	 * Sets the multiplayer listener that listens to game state changes. If the
	 * game has already started,
	 * {@link IMultiplayerListener#gameIsStarting(IStartingGame)} is called
	 * immediately by this method.
	 * 
	 * @param listener
	 */
	public void setMultiplayerListener(IMultiplayerListener listener);

	public void setChatListener(IChatMessageListener chatMessageListener);

	public void sendChatMessage(String chatMessage);

	/* TODO: Add a method to access information about the base map */

	/**
	 * The list of players that join this game.
	 * 
	 * @return
	 */
	public IChangingList<IMultiplayerPlayer> getPlayers();

	/**
	 * Sets the ready state of the current user.
	 * 
	 * @param ready
	 */
	void setReady(boolean ready);

	/**
	 * Starts the game. Calls the game listener as soon as it is starting.
	 */
	void startGame();

	/**
	 * Called when the user exits the screen, to abort the multiplayer game.
	 * This method does not need to call the abort() method of the
	 * {@link IMultiplayerListener}.
	 */
	public void abort();

}
