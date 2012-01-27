package jsettlers.graphics;

import java.util.List;

/**
 * @author Andreas Eberle
 */
public interface INetworkScreenAdapter {

	void setListener(INetworkScreenListener networkScreen);

	/**
	 * Gets a immutable list of players.
	 * 
	 * @return The current players.
	 */
	List<INetworkPlayer> getPlayerList();

	/**
	 * Listener for events to the network screen
	 * 
	 * @author Andreas Eberle
	 */
	interface INetworkScreenListener {
		/**
		 * This method is invoked when the player list changed.
		 */
		void playerListChanged();

		/**
		 * This method is called if a chat message was received.
		 * 
		 * @param message
		 *            received message.
		 */
		void addChatMessage(String message);
	}

	/**
	 * Interface for a player of a network game offering the methods needed by
	 * the UI.
	 * 
	 * @author Andreas Eberle
	 */
	interface INetworkPlayer {

		/**
		 * @return the players name.
		 */
		String getPlayerName();

		/**
		 * @return if true, the player said it's ok for him to start the game.<br>
		 *         if false the game can't be started, because the player isn't
		 *         finished yet.
		 */
		boolean isReady();
	}

	/**
	 * @param ready
	 *            if true, the player signals he is ready to play and allows the
	 *            game to start. if false he isn't ready
	 */
	void setReady(boolean ready);

	void sendChatMessage(String message);

	/**
	 * try to start the game.
	 */
	void startGame();
}
