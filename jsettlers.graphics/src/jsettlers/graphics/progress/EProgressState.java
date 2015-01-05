package jsettlers.graphics.progress;

/**
 * A progress state that can be shown on the progress screen.
 * 
 * @author michael
 */
public enum EProgressState {
	LOADING,
	LOADING_IMAGES,
	LOADING_MAP,
	WAITING_FOR_OTHER_PLAYERS,

	/**
	 * Start a network server.
	 */
	STARTING_SERVER,
	WAIT_FOR_SERVER_RESPONSE,
	JOINING_GAME,

	/**
	 * Update application data
	 */
	UPDATE
}
