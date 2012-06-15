package jsettlers.common;

public abstract class CommonConstants {
	public static final int FOG_OF_WAR_VISIBLE = 100;
	public static final int FOG_OF_WAR_EXPLORED = 50;

	/**
	 * if true, the user will be able to see other players people and buildings
	 */
	public static final boolean ENABLE_ALL_PLAYER_FOG_OF_WAR = false;
	public static final int TOWERRADIUS = 40;
	public static final int MAX_PLAYERS = 16;

	/**
	 * if true, the user will be able to select other player's people and buildings.
	 */
	public static boolean ENABLE_ALL_PLAYER_SELECTION = true;

	public static final boolean ENABLE_GRAPHICS_TIMES_DEBUG_OUTPUT = false;

	/**
	 * NOTE: this value has only an effect if it's changed before the MainGrid is created! IT MUSTN'T BE CHANGED AFTER A MAIN GRID HAS BEEN CREATED<br>
	 * if false, no debug coloring is possible (but saves memory) <br>
	 * if true, debug coloring is possible.
	 */
	public static boolean ENABLE_DEBUG_COLORS = false;
}
