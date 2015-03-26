package jsettlers.network.server.match;

import java.util.Arrays;

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

	public static void assertState(EPlayerState state, EPlayerState... expectedStates) throws IllegalStateException {
		boolean correctState = isOneOf(state, expectedStates);

		if (!correctState) {
			throw new IllegalStateException("Invalid client state: " + state + ". This action can only be done in one of the following states: "
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