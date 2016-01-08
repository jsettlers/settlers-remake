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

import jsettlers.common.player.ICombatStrengthInformation;
import jsettlers.logic.constants.Constants;

/**
 * @author codingberlin
 */

public class CombatStrengthInformation implements ICombatStrengthInformation {
	private static final float[] START_AMOUNT = { 29, 22, 14, 10.25f, 8, 6, 5, 4.7f, 4.2f, 3.8f, 3.3f, 3.1f, 2.9f, 2.7f, 2.6f, 2.4f, 2.3f, 2.2f, 2.1f,
			2, 1.9f, 1.8f, 1.7f, 1.6f, 1.5f, 1.4f, 1.3f, 1.2f, 1.1f };
	private static final float goldDivisor = (float) (Math.log(2) * 9);

	private float combatStrength;
	private float combatStrengthOwnGround;

	@Override
	public float getCombatStrength(boolean ownGround) {
		return ownGround ? combatStrengthOwnGround : combatStrength;
	}

	@Override
	public String toString() {
		return "CombatStrength: ownGround: " + combatStrengthOwnGround + "    external: " + combatStrength;
	}

	public void updateGoldCombatStrength(byte numberOfPlayers, int amountOfGold) {
		float totalGoldAmount = amountOfGold + START_AMOUNT[Math.min(START_AMOUNT.length, numberOfPlayers) - 1];
		float goldCombatStrength = ((float) Math.log(totalGoldAmount)) / goldDivisor;

		this.combatStrength = goldCombatStrength;
		this.combatStrengthOwnGround = Math.max(goldCombatStrength, Constants.COMBAT_STRENGTH_OWN_GROUND);
	}
}
