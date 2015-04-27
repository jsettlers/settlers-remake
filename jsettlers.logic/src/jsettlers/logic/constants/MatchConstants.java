package jsettlers.logic.constants;

import jsettlers.network.client.interfaces.IGameClock;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public final class MatchConstants {
	private MatchConstants() {
	}

	public static IGameClock clock;

	/**
	 * if true, the user will be able to see other players people and buildings
	 */
	public static boolean ENABLE_ALL_PLAYER_FOG_OF_WAR = false;

	/**
	 * if true, the user will be able to select other player's people and buildings.
	 */
	public static boolean ENABLE_ALL_PLAYER_SELECTION = false;

	public static boolean ENABLE_FOG_OF_WAR_DISABLING = false;
}
