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

	private short mana = 0;
	private short numberOfUpgradesExecuted = 0;

	private EMovableType bowmenType = BOWMAN_L1;
	private EMovableType swordsmenType = SWORDSMAN_L1;
	private EMovableType pikemenType = PIKEMAN_L1;

	private static final short[] NECESSARY_MANA_FOR_UPGRADE = {10, 30, 60, 110, 170, 200};

	public void increaseMana() {
		mana++;
	}

	public boolean isBowmenUpgradePossible() {
		return bowmenType != BOWMAN_L3
			&& isManaForUpgradeAvailable()
			&& (bowmenType == BOWMAN_L1 || isLevel3Available());
	}

	public boolean isSwordsmenUpgradePossible() {
		return swordsmenType != SWORDSMAN_L3
				&& isManaForUpgradeAvailable()
				&& (swordsmenType == SWORDSMAN_L1 || isLevel3Available());
	}

	public boolean isPikemenUpgradePossible() {
		return pikemenType != PIKEMAN_L3
				&& isManaForUpgradeAvailable()
				&& (pikemenType == PIKEMAN_L1 || isLevel3Available());
	}

	public void upgradeBowmen() {
		if (isBowmenUpgradePossible()) {
			numberOfUpgradesExecuted++;
			bowmenType = bowmenType == BOWMAN_L1 ? BOWMAN_L2 : BOWMAN_L3;
		}
	}

	public void upgradeSwordsmen() {
		if (isSwordsmenUpgradePossible()) {
			numberOfUpgradesExecuted++;
			swordsmenType = swordsmenType == SWORDSMAN_L1 ? SWORDSMAN_L2 : SWORDSMAN_L3;
		}
	}

	public void upgradePikemen() {
		if (isPikemenUpgradePossible()) {
			numberOfUpgradesExecuted++;
			pikemenType = pikemenType == PIKEMAN_L1 ? PIKEMAN_L2 : PIKEMAN_L3;
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

	private boolean isManaForUpgradeAvailable() {
		return mana >= NECESSARY_MANA_FOR_UPGRADE[numberOfUpgradesExecuted];
	}

	private boolean isLevel3Available() {
		return numberOfUpgradesExecuted >= 3;
	}

}
