/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.common;

import jsettlers.common.ai.EWhatToDoAiType;

/**
 * Common constants that influence the game. With this class you can also influence the debugging output of the game.
 * 
 * @author Andreas Eberle
 * @author Michael Zangl
 */
public abstract class CommonConstants {
	/**
	 * A byte value indicating that the given position is visible.
	 */
	public static final int FOG_OF_WAR_VISIBLE = 100;
	/**
	 * A byte value indicating explored for FOW values.
	 */
	public static final int FOG_OF_WAR_EXPLORED = 50;

	/**
	 * Radius of the area occupied by towers.
	 */
	public static final short TOWER_RADIUS = 40;
	/**
	 * Maximum number of players allowed to play. Should be 127 or less.
	 */
	public static final int MAX_PLAYERS = 16;

	/**
	 * If true, all players of a map will always be positioned on startup.
	 */
	public static boolean ACTIVATE_ALL_PLAYERS = false;

	/**
	 * If true, all System.err and System.out will be printed to the console instead of a file.
	 */
	public static boolean ENABLE_CONSOLE_LOGGING = true;

	/**
	 * Makes the graphics print timing information to the console.
	 */
	public static final boolean ENABLE_GRAPHICS_TIMES_DEBUG_OUTPUT = false;

	/**
	 * NOTE: this value has only an effect if it's changed before the MainGrid is created! IT MUSTN'T BE CHANGED AFTER A MAIN GRID HAS BEEN CREATED <br>
	 * if false, no debug coloring is possible (but saves memory) <br>
	 * if true, debug coloring is possible.
	 */
	public static boolean ENABLE_DEBUG_COLORS = true;

	/**
	 * This is the default address the network game connects to.
	 */
	public static String DEFAULT_SERVER_ADDRESS = "87.106.88.80";

	/**
	 * If this is set to <code>true</code> the UI allows you to control all players.
	 */
	public static boolean CONTROL_ALL = false;

	/**
	 * If set to <code>true</code>, save games are compressed.
	 */
	public static boolean USE_SAVEGAME_COMPRESSION = false;

	/**
	 * Enables the AI submodule.
	 */
	public static boolean ENABLE_AI = true;

	/**
	 * If set to <code>true</code>, all players - including the player controlled by the user - are controlled by the AI.
	 */
	public static boolean ALL_AI = false;

	/**
	 * If set, only this AI type is used.
	 */
	public static EWhatToDoAiType FIXED_AI_TYPE = null;

	/**
	 * Option to disable the loading of original maps.
	 */
	public static boolean DISABLE_ORIGINAL_MAPS = false;

	/**
	 * Disables the checksum test for original maps.
	 */
	public static final boolean DISABLE_ORIGINAL_MAPS_CHECKSUM = false;
}
