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
package jsettlers.logic.buildings;

import jsettlers.common.buildings.IMaterialProduction;
import jsettlers.common.material.EMaterialType;
import jsettlers.network.synchronic.random.RandomSingleton;

import java.io.Serializable;

/**
 * @author codingberlin
 */
public class MaterialProduction implements IMaterialProduction, Serializable {
	private static final long serialVersionUID = 5315922528738308895L;

	private static EMaterialType[] WEAPONS = {EMaterialType.SWORD, EMaterialType.SPEAR, EMaterialType.BOW};
	private final float[] ratios = new float[EMaterialType.NUMBER_OF_MATERIALS];
	private final float[] numberOfFutureProducedMaterials = new float[EMaterialType.NUMBER_OF_MATERIALS];

	public MaterialProduction() {
		for (EMaterialType type : EMaterialType.values) {
			ratios[type.ordinal] = 0;
			numberOfFutureProducedMaterials[type.ordinal] = 0;
		}
		ratios[EMaterialType.SWORD.ordinal] = 1f;
		ratios[EMaterialType.SPEAR.ordinal] = 0.3f;
		ratios[EMaterialType.BOW.ordinal] = 0.7f;
	}

	@Override
	public float ratioOfMaterial(EMaterialType type) {
		return ratios[type.ordinal];
	}

	@Override
	public int numberOfFutureProducedMaterial(EMaterialType type) {
		return (int) numberOfFutureProducedMaterials[type.ordinal];
	}

	public void increaseNumberOfFutureProducedMaterial(EMaterialType type) {
		numberOfFutureProducedMaterials[type.ordinal]++;
		if (numberOfFutureProducedMaterials[type.ordinal] > 20) {
			numberOfFutureProducedMaterials[type.ordinal] = 20;
		}
	}

	public void decreaseNumberOfFutureProducedMaterial(EMaterialType type) {
		numberOfFutureProducedMaterials[type.ordinal]--;
		if (numberOfFutureProducedMaterials[type.ordinal] < 0) {
			numberOfFutureProducedMaterials[type.ordinal] = 0;
		}
	}

	public void setRatioOfMaterial(EMaterialType type, float ratio) {
		ratios[type.ordinal] = ratio;
	}

	public EMaterialType dropWeapon() {
		float sumOfFutureProducedWeapons = 0;
		for (EMaterialType type : WEAPONS) {
			sumOfFutureProducedWeapons += numberOfFutureProducedMaterials[type.ordinal];
		}
		float[] weaponRatio = new float[WEAPONS.length];
		if (sumOfFutureProducedWeapons > 0) {
			for (int i = 0; i < WEAPONS.length; i++) {
				weaponRatio[i] = (numberOfFutureProducedMaterials[WEAPONS[i].ordinal] / sumOfFutureProducedWeapons) * 100;
			}
		} else {
			for (int i = 0; i < WEAPONS.length; i++) {
				weaponRatio[i] = ratios[WEAPONS[i].ordinal] * 100;
			}
		}
		int maxRatio = 0;
		for (float ratio : weaponRatio) {
			maxRatio += ratio;
		}
		int random = RandomSingleton.getInt(maxRatio);
		for (int i = 0; i < WEAPONS.length; i++) {
			if (random <= weaponRatio[i]) {
				decreaseNumberOfFutureProducedMaterial(WEAPONS[i]);
				return WEAPONS[i];
			} else {
				random -= weaponRatio[i];
			}
		}
		return null;
	}
}
