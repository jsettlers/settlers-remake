package jsettlers.common.ai;

/**
 * @author codingberlin
 */
public enum WhatToDoAiType {
	ROMAN_VERY_EASY,
	ROMAN_EASY,
	ROMAN_HARD,
	ROMAN_VERY_HARD;


	public static final WhatToDoAiType[] values = WhatToDoAiType.values();
	public static final int NUMBER_OF_AI_TYPES = values.length;

	public static WhatToDoAiType getTypeByIndex(int index) {
		return values[index % NUMBER_OF_AI_TYPES];
	}
}
