/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.map.loading.original.data;

/**
 * @author Thomas Zeugner
 * @author codingberlin
 */
public enum EOriginalMapFilePartType {
	EOF(0, ""), // End of File and Padding
	MAP_INFO(1, "Map Info"),
	PLAYER_INFO(2, "Player Info"),
	TEAM_INFO(3, "Team Info"),
	PREVIEW(4, "Preview"),
	UNKNOWN_5(5, "UNKNOWN_5"),
	AREA(6, "Area"),
	SETTLERS(7, "Settlers"),
	BUILDINGS(8, "Buildings"),
	STACKS(9, "Stacks"),
	UNKNOWN_10(10, "UNKNOWN_10"), // - maybe the winning conditions
	QUEST_TEXT(11, "QuestText"),
	QUEST_TIP(12, "QuestTip");

	private static final EOriginalMapFilePartType[] VALUES = EOriginalMapFilePartType.values();
	public final int value;
	private final String typeText;

	EOriginalMapFilePartType(int typeValue, String typeText) {
		this.value = typeValue;
		this.typeText = typeText;
	}

	@Override
	public String toString() {
		return typeText;
	}

	public static EOriginalMapFilePartType getTypeByInt(int intType) {
		int val = intType & 0x0000FFFF;
		if (val <= 0 || val >= VALUES.length) {
			return EOF;
		} else {
			return VALUES[val];
		}
	}

}