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
		boolean correctState = isOneOf(state, expectedStates);

		if (!correctState) {
			throw new InvalidStateException("Invalid client state: " + state + ". This action can only be done in one of the following states: "
					+ Arrays.toString(expectedStates));
		}
	}

	public static boolean isOneOf(EPlayerState state, EPlayerState... allowedStates) {
		boolean correctState = false;
		for (EPlayerState curr : allowedStates) {
			if (state.equals(curr)) {
				correctState = true;
				break;
			}
		}
		return correctState;
	}
}