package jsettlers.graphics.messages;

public enum EMessageType {
	/**
	 * A chat message.
	 */
	CHAT,
	/**
	 * The user was attacked by an other user.
	 */
	ATTACKED,
	/**
	 * Minerals have been found.
	 */
	MINERALS,

	/**
	 * The worker of a building was not able to find a place to execute his action in his work are for some time.
	 */
	NOTHING_FOUND_IN_SEARCH_AREA
}
