package jsettlers.graphics.startscreen.interfaces;

/**
 * This enum defines the possible states of the multiplayer connector
 * {@link IMultiplayerConnector}.
 * 
 * @author Andreas Eberle
 */
public enum EMultiplayerConnectorState {
	CONNECTING_TO_SERVER, CONNECTED_TO_SERVER,

	JOINING_GAME, OPENING_NEW_GAME, JOINED_GAME,

	STARTING_GAME, IN_RUNNING_GAME,

	FAILED_CONNECTING, FAILED_SERVER_NOT_FOUND,

}
