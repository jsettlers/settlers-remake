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
package jsettlers.common.map;

/**
 * Enum defining possible debug color modes.
 * 
 * @author Andreas Eberle
 * 
 */
public enum EDebugColorModes {
	NONE,
	BLOCKED_PARTITIONS,
	PARTITION_ID,
	REAL_PARTITION_ID,
	PLAYER_ID,
	TOWER_COUNT,
	DEBUG_COLOR,
	MARKS_AND_OBJECTS,
	RESOURCE_AMOUNTS;

	/**
	 * This array defines the available debug color modes. If you want to disable a mode, create a new array, that doesn't contain the mode.<br>
	 * This array also defines the order of the modes. Therefore changing the array can be used to change the order of the modes.
	 */
	private static EDebugColorModes[] AVAILABLE_COLOR_MODES = EDebugColorModes.values();

	public static EDebugColorModes getNextMode(EDebugColorModes debugColorMode) {
		for (int i = 0; i < AVAILABLE_COLOR_MODES.length; i++) {
			if (AVAILABLE_COLOR_MODES[i] == debugColorMode) {
				return AVAILABLE_COLOR_MODES[(i + 1) % AVAILABLE_COLOR_MODES.length];
			}
		}
		return EDebugColorModes.NONE;
	}
}
