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

	private byte[] levelOfTypes = {0, 0, 0};
	private EMovableType[] bowmenTypes = {EMovableType.BOWMAN_L1, EMovableType.BOWMAN_L2, EMovableType.BOWMAN_L3};
	private EMovableType[] swordsmenTypes = {EMovableType.SWORDSMAN_L1, EMovableType.SWORDSMAN_L2, EMovableType.SWORDSMAN_L3};
	private EMovableType[] pikemenTypes = {EMovableType.PIKEMAN_L1, EMovableType.PIKEMAN_L2, EMovableType.PIKEMAN_L3};
	private static final byte MAXIMUM_LEVEL = 2;
	private static final short[] NECESSARY_MANA_FOR_UPGRADE = {10, 30, 60, 110, 170, 200};

	public void increaseMana() {
		mana++;
	}

	public boolean isUpgradePossible(EManaType type) {
		return getLevel(type) != MAXIMUM_LEVEL
				&& isManaAvailableForUpgrade()
				&& noLevelBelow(getLevel(type));
	}

	private boolean noLevelBelow(byte level) {
		for (byte levelOfType : levelOfTypes) {
			if (levelOfType < level) {
				return false;
			}
		}
		return true;
	}

	private byte getLevel(EManaType type) {
		return levelOfTypes[type.ordinal()];
	}

	public void upgrade(EManaType type) {
		if (isUpgradePossible(type)) {
			numberOfUpgradesExecuted++;
			levelOfTypes[type.ordinal()]++;
		}
	}

	public EMovableType getMovableTypeOf(EManaType type) {
		switch (type) {
		case BOWMEN:
			return bowmenTypes[getLevel(type)];
		case SWORDSMEN:
			return swordsmenTypes[getLevel(type)];
		default:
			return pikemenTypes[getLevel(type)];
		}
	}

	private boolean isManaAvailableForUpgrade() {
		return mana >= NECESSARY_MANA_FOR_UPGRADE[numberOfUpgradesExecuted];
	}

}
