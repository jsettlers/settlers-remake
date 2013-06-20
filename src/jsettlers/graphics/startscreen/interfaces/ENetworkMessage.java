package jsettlers.graphics.startscreen.interfaces;

/**
 * This enum defines predefined messages that can be received with the
 * {@link IChatMessageListener}.
 * 
 * @author Andreas Eberle
 */
public enum ENetworkMessage {
	PLAYER_JOINED, PLAYER_LEFT, UNAUTHORIZED, NOT_ALL_PLAYERS_READY,

	UNKNOWN_ERROR, INVALID_STATE_ERROR;
}
