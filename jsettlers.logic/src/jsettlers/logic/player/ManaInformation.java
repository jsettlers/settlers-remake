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

import jsettlers.common.movable.EMovableType;

import java.io.Serializable;

import static jsettlers.common.movable.EMovableType.*;

/**
 * @author codingberlin
 */
public class ManaInformation implements Serializable {

	short mana = 0;
	short numberOfUpgradesExecuted = 0;

	EMovableType bowmenType = BOWMAN_L1;
	EMovableType swordsmenType = SWORDSMAN_L1;
	EMovableType pikemenType = PIKEMAN_L1;

	short[] necessaryManaForUpgrade = {10, 30, 60, 110, 170, 200};

	public void increaseMana() {
		mana++;
	}

	public boolean isBowmenUpgradePossible() {
		return bowmenType != BOWMAN_L3
			&& mana >= necessaryManaForUpgrade[numberOfUpgradesExecuted]
			&& (bowmenType == BOWMAN_L1
			|| swordsmenType == SWORDSMAN_L2 && pikemenType == PIKEMAN_L2);
	}

	public boolean isSwordsmenUpgradePossible() {
		return swordsmenType != SWORDSMAN_L3
				&& mana >= necessaryManaForUpgrade[numberOfUpgradesExecuted]
				&& (swordsmenType == SWORDSMAN_L1
				|| swordsmenType == BOWMAN_L2 && pikemenType == PIKEMAN_L2);
	}

	public boolean isPikemenUpgradePossible() {
		return pikemenType != PIKEMAN_L3
				&& mana >= necessaryManaForUpgrade[numberOfUpgradesExecuted]
				&& (pikemenType == PIKEMAN_L1
				|| swordsmenType == SWORDSMAN_L2 && pikemenType == BOWMAN_L2);
	}

	public void upgradeBowmen() {
		if (isBowmenUpgradePossible()) {
			numberOfUpgradesExecuted++;
			bowmenType =  bowmenType == BOWMAN_L1 ? BOWMAN_L2 : BOWMAN_L3;
		}
	}

	public void upgradeSwordsmen() {
		if (isSwordsmenUpgradePossible()) {
			numberOfUpgradesExecuted++;
			swordsmenType =  swordsmenType == SWORDSMAN_L1 ? SWORDSMAN_L2 : SWORDSMAN_L3;
		}
	}

	public void upgradePikemen() {
		if (isPikemenUpgradePossible()) {
			numberOfUpgradesExecuted++;
			pikemenType =  pikemenType == PIKEMAN_L1 ? PIKEMAN_L2 : PIKEMAN_L3;
		}
	}

	public EMovableType getBowmenType() {
		return bowmenType;
	}

	public EMovableType getSwordsmenType() {
		return swordsmenType;
	}

	public EMovableType getPikemenType() {
		return pikemenType;
	}

}
