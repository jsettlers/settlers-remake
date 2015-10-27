package jsettlers.common.ai;

/**
 * @author codingberlin
 */
public enum EWhatToDoAiType {
	ROMAN_VERY_EASY,
	ROMAN_EASY,
	ROMAN_HARD,
	ROMAN_VERY_HARD;


	public static final EWhatToDoAiType[] values = EWhatToDoAiType.values();
	public static final int NUMBER_OF_AI_TYPES = values.length;

	public static EWhatToDoAiType getTypeByIndex(int index) {
		return values[index % NUMBER_OF_AI_TYPES];
	}
}
