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
package jsettlers.logic.player;

/**
 * @author codingberlin
 */
public final class CombatStrengthCalculator {

	public static final float COMBAT_STRENGTH_WITHIN_OWN_LAND = 1;
	public static final int BORDER_50_PERCENT = 600;
	public static final int BORDER_85_PERCENT = 1160;

	private static final float[] COMBAT_STRENGTH_INCREASE = { 613, 626, 639, 652, 665, 679, 693, 707, 721, 735, 750, 765, 780, 795, 810, 826,
			842, 858, 874, 890, 907, 924, 941, 958, 975, 993, 1011, 1029, 1047, 1065, 1084, 1103, 1122, 1141 };
	private static final int[] START_AMOUNT = { 652, 600, 504, 444, 396, 348, 312, 300, 276, 252, 228, 216, 204, 192, 180, 168, 163, 156, 149,
			144 };

	private CombatStrengthCalculator() { // no objects of this class possible.
	}

	public static float getCombatStrength(boolean ownGround, byte numberOfPlayers, int amountOfGold) {
		float goldCombatStrength = CombatStrengthCalculator.getGoldCombatStrength(numberOfPlayers, amountOfGold);

		if (ownGround) {
			return Math.max(goldCombatStrength, COMBAT_STRENGTH_WITHIN_OWN_LAND);
		} else {
			return goldCombatStrength;
		}
	}

	private static float getGoldCombatStrength(byte numberOfPlayers, int amountOfGold) {
		amountOfGold += START_AMOUNT[numberOfPlayers - 1];

		if (amountOfGold <= BORDER_50_PERCENT) {
			return (amountOfGold / 12f) / 100f;

		} else if (amountOfGold <= BORDER_85_PERCENT) {
			for (int i = COMBAT_STRENGTH_INCREASE.length; i >= 0; i--) {
				if (amountOfGold >= COMBAT_STRENGTH_INCREASE[i]) {
					return (51f + i) / 100f;
				}
			}
			return 0.5f;

		} else {
			return ((1700f + amountOfGold - BORDER_85_PERCENT) / 20f) / 100f;
		}
	}
}
