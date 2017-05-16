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
package jsettlers.logic.buildings;

import java.io.Serializable;
import java.util.EnumSet;

import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.utils.collections.CollectionUtils;
import jsettlers.logic.constants.MatchConstants;

/**
 * @author codingberlin
 */
public class MaterialProductionSettings implements IMaterialProductionSettings, Serializable {
	private static final long serialVersionUID = 5315922528738308895L;
	public static final int MAXIMUM_FUTURE_PRODUCTION = 20;

	private final float[] ratios = new float[EMaterialType.NUMBER_OF_MATERIALS];
	private final int[] numberOfFutureProducedMaterials = new int[EMaterialType.NUMBER_OF_MATERIALS];

	public MaterialProductionSettings() {
		for (EMaterialType type : EMaterialType.VALUES) {
			ratios[type.ordinal] = 0;
			numberOfFutureProducedMaterials[type.ordinal] = 0;
		}
		ratios[EMaterialType.SWORD.ordinal] = 1f;
		ratios[EMaterialType.SPEAR.ordinal] = 0.3f;
		ratios[EMaterialType.BOW.ordinal] = 0.7f;
	}

	@Override
	public float configuredRatioOfMaterial(EMaterialType type) {
		return ratios[type.ordinal];
	}

	@Override
	public float resultingRatioOfMaterial(EMaterialType type) {
		if (EMaterialType.WEAPONS.contains(type)) {
			float allWeapons = 0;
			for (EMaterialType currentWeapon : EMaterialType.WEAPONS) {
				allWeapons += configuredRatioOfMaterial(currentWeapon);
			}
			if (allWeapons == 0) {
				return 0;
			}
			return (configuredRatioOfMaterial(type) / allWeapons);
		} else if (EMaterialType.TOOLS.contains(type)) {
			float allTools = 0;
			for (EMaterialType currentTool : EMaterialType.TOOLS) {
				allTools += configuredRatioOfMaterial(currentTool);
			}
			if (allTools == 0) {
				return 0;
			}
			return (configuredRatioOfMaterial(type) / allTools);
		} else {
			return 0;
		}
	}

	@Override
	public int numberOfFutureProducedMaterial(EMaterialType type) {
		return numberOfFutureProducedMaterials[type.ordinal];
	}

	public void increaseNumberOfFutureProducedMaterial(EMaterialType type) {
		setNumberOfFutureProducedMaterial(type, numberOfFutureProducedMaterials[type.ordinal] + 1);
	}

	public void decreaseNumberOfFutureProducedMaterial(EMaterialType type) {
		setNumberOfFutureProducedMaterial(type, numberOfFutureProducedMaterials[type.ordinal] - 1);
	}

	public void setNumberOfFutureProducedMaterial(EMaterialType type, int count) {
		if (count > MAXIMUM_FUTURE_PRODUCTION) {
			numberOfFutureProducedMaterials[type.ordinal] = MAXIMUM_FUTURE_PRODUCTION;
		} else if (count < 0) {
			numberOfFutureProducedMaterials[type.ordinal] = 0;
		} else {
			numberOfFutureProducedMaterials[type.ordinal] = count;
		}
	}

	public void setRatioOfMaterial(EMaterialType type, float ratio) {
		ratios[type.ordinal] = ratio;
	}

	public boolean materialIsRequestedByNumbers(EnumSet<EMaterialType> materialGroup) {
		for (EMaterialType type : materialGroup) {
			if (numberOfFutureProducedMaterials[type.ordinal] > 0) {
				return true;
			}
		}
		return false;
	}

	public EMaterialType getWeaponToProduce() {
		return getMaterialOfGroupToProduce(EMaterialType.WEAPONS);
	}

	public EMaterialType getToolToProduce() {
		return getMaterialOfGroupToProduce(EMaterialType.TOOLS);
	}

	private EMaterialType getMaterialOfGroupToProduce(EnumSet<EMaterialType> materialGroup) {
		float[] materialRatios = calculateMaterialRatios(materialGroup);

		float ratioSum = 0;
		for (float ratio : materialRatios) {
			ratioSum += ratio;
		}

		if (ratioSum == 0) {
			return null;
		}

		float random = MatchConstants.random().nextFloat(ratioSum);
		int i = 0;
		for (EMaterialType material : materialGroup) {
			if (random <= materialRatios[i]) {
				decreaseNumberOfFutureProducedMaterial(material);
				return material;
			} else {
				random -= materialRatios[i];
			}
			i++;
		}
		return null;
	}

	private float[] calculateMaterialRatios(EnumSet<EMaterialType> materialGroup) {
		float sumOfFutureProducedMaterials = 0;
		for (EMaterialType type : materialGroup) {
			sumOfFutureProducedMaterials += numberOfFutureProducedMaterials[type.ordinal];
		}
		float finalSumOfFutureProducedMaterials = sumOfFutureProducedMaterials;

		float[] materialRatio = new float[materialGroup.size()];
		if (sumOfFutureProducedMaterials > 0) {
			CollectionUtils.iterateWithIndex(materialGroup,	(i, material) -> materialRatio[i] = (numberOfFutureProducedMaterials[material.ordinal] / finalSumOfFutureProducedMaterials)	* 100);
		} else {
			CollectionUtils.iterateWithIndex(materialGroup, (i, material) -> materialRatio[i] = ratios[material.ordinal] * 100);
		}
		return materialRatio;
	}
}
