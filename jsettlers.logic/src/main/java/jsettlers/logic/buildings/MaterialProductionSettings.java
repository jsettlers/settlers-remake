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

import static java8.util.stream.StreamSupport.stream;

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

	private static final int MAXIMUM_FUTURE_PRODUCTION = 99;
	private static final float RELATIVE_SCALE_FACTOR = 100;

	private final int[] relativeProductionRequests = new int[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
	private final int[] absoluteProductionRequests = new int[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];

	public MaterialProductionSettings() {
		relativeProductionRequests[EMaterialType.SWORD.ordinal] = 100;
		relativeProductionRequests[EMaterialType.SPEAR.ordinal] = 30;
		relativeProductionRequests[EMaterialType.BOW.ordinal] = 70;
	}

	@Override
	public float getRelativeProductionRequest(EMaterialType type) {
		return relativeProductionRequests[type.ordinal] / RELATIVE_SCALE_FACTOR;
	}

	@Override
	public float resultingRatioOfMaterial(EMaterialType type) {
		if (EMaterialType.WEAPONS.contains(type)) {
			return calculateDistributionValue(type, EMaterialType.WEAPONS);

		} else if (EMaterialType.TOOLS.contains(type)) {
			return calculateDistributionValue(type, EMaterialType.TOOLS);

		} else {
			return 0;
		}
	}

	@Override
	public int getAbsoluteProductionRequest(EMaterialType type) {
		return absoluteProductionRequests[type.ordinal];
	}

	public void increaseAbsoluteProductionRequest(EMaterialType type) {
		setAbsoluteProductionRequest(type, absoluteProductionRequests[type.ordinal] + 1);
	}

	public void decreaseAbsoluteProductionRequest(EMaterialType type) {
		setAbsoluteProductionRequest(type, absoluteProductionRequests[type.ordinal] - 1);
	}

	public void setAbsoluteProductionRequest(EMaterialType type, int count) {
		absoluteProductionRequests[type.ordinal] = Math.min(MAXIMUM_FUTURE_PRODUCTION, Math.max(0, count));
	}

	public void setRelativeProductionRequest(EMaterialType type, float ratio) {
		relativeProductionRequests[type.ordinal] = (int) (ratio * RELATIVE_SCALE_FACTOR);
	}

	public EMaterialType getWeaponToProduce() {
		EMaterialType weapon = getAbsolutelyRequestedMaterial(EMaterialType.WEAPONS);
		if (weapon != null) {
			return weapon;
		} else {
			return getRelativelyRequestedMaterial(EMaterialType.WEAPONS);
		}
	}

	public EMaterialType getAbsolutelyRequestedMaterial(EnumSet<EMaterialType> materialGroup) {
		return getMaterialOfGroupToProduce(materialGroup, absoluteProductionRequests);
	}

	public EMaterialType getRelativelyRequestedMaterial(EnumSet<EMaterialType> materialGroup) {
		return getMaterialOfGroupToProduce(materialGroup, relativeProductionRequests);
	}

	private EMaterialType getMaterialOfGroupToProduce(EnumSet<EMaterialType> materialGroup, int[] productionRequests) {
		float[] materialDistribution = calculateDistribution(materialGroup, productionRequests);

		float random = MatchConstants.random().nextFloat();
		float distributionSum = 0;
		int i = 0;

		for (EMaterialType material : materialGroup) {
			distributionSum += materialDistribution[i];
			if (random <= distributionSum) {
				decreaseAbsoluteProductionRequest(material);
				return material;
			}
			i++;
		}
		return null;
	}

	private float[] calculateDistribution(EnumSet<EMaterialType> materialGroup, int[] valuesForMaterials) {
		int sum = calculateSumOfGroup(materialGroup, valuesForMaterials);
		float[] materialDistribution = new float[materialGroup.size()];

		if (sum <= 0) {
			return materialDistribution;
		}

		CollectionUtils.iterateWithIndex(materialGroup, (i, material) -> materialDistribution[i] = valuesForMaterials[material.ordinal] / (float) sum);
		return materialDistribution;
	}

	private int calculateSumOfGroup(EnumSet<EMaterialType> materialGroup, int[] valuesForMaterials) {
		return stream(materialGroup).mapToInt(materialType -> valuesForMaterials[materialType.ordinal]).sum();
	}

	private float calculateDistributionValue(EMaterialType materialType, EnumSet<EMaterialType> materialGroup) {
		int sum = calculateSumOfGroup(materialGroup, relativeProductionRequests);
		if (sum == 0) {
			return 0;
		}
		return (relativeProductionRequests[materialType.ordinal] / (float) sum);
	}

}
