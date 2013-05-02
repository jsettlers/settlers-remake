package networklib.server.game;

import java.util.Arrays;

import networklib.client.exceptions.InvalidStateException;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public enum EPlayerState {
	CHANNEL_CONNECTED,
	LOGGED_IN,
	IN_MATCH,
	IN_RUNNING_MATCH,
	DISCONNECTED, ;

	public static void assertState(EPlayerState state, EPlayerState... expectedStates) throws InvalidStateException {
		boolean correctState = false;
		for (EPlayerState curr : expectedStates) {
			if (state.equals(curr)) {
				correctState = true;
				break;
			}
		}

		if (!correctState) {
			throw new InvalidStateException("Invalid client state: " + state + ". This action can only be done in one of the following states: "
					+ Arrays.toString(expectedStates));
		}
	}
}