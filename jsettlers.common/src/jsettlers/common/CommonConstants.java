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

public abstract class CommonConstants {
	public static final int FOG_OF_WAR_VISIBLE = 100;
	public static final int FOG_OF_WAR_EXPLORED = 50;

	public static final short TOWER_RADIUS = 40;
	public static final int MAX_PLAYERS = 16;

	/**
	 * If true, all players of a map will always be positioned on startup.
	 */
	public static boolean ACTIVATE_ALL_PLAYERS = false;

	/**
	 * If true, all System.err and System.out will be printed to the console instead of a file
	 */
	public static boolean ENABLE_CONSOLE_LOGGING = true;

	public static final boolean ENABLE_GRAPHICS_TIMES_DEBUG_OUTPUT = false;

	/**
	 * NOTE: this value has only an effect if it's changed before the MainGrid is created! IT MUSTN'T BE CHANGED AFTER A MAIN GRID HAS BEEN CREATED
	 * <br>
	 * if false, no debug coloring is possible (but saves memory) <br>
	 * if true, debug coloring is possible.
	 */
	public static boolean ENABLE_DEBUG_COLORS = true;

	public static String DEFAULT_SERVER_ADDRESS = "87.106.88.80";

	public static boolean CONTROL_ALL = false;

	public static boolean USE_SAVEGAME_COMPRESSION = false;

	public static boolean ENABLE_AI = true;

	/**
	 * Use only ai players for single player.
	 */
	public static boolean ALL_AI;

	/**
	 * If set, only this AI type is used.
	 */
	public static EWhatToDoAiType FIXED_AI_TYPE;
}
