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
