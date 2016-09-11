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
package jsettlers.logic.map.grid.partition.manager.settings;

import java.io.Serializable;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.MaterialsOfBuildings;
import jsettlers.common.map.partition.IMaterialsDistributionSettings;
import jsettlers.common.material.EMaterialType;

/**
 * This class holds the distribution settings for a given {@link EMaterialType}.
 * 
 * @author Andreas Eberle
 * 
 */
public final class DistributionSettingsForMaterial implements IMaterialsDistributionSettings, Serializable {
	private static final long serialVersionUID = -8519244429973606793L;

	private final EMaterialType materialType;
	private final EBuildingType[] requestingBuildings;
	private final float[] probabilities;

	/**
	 * Creates a new object of {@link DistributionSettingsForMaterial} holding the settings for the given {@link EMaterialType}.
	 * 
	 * @param materialType
	 *            Defines the {@link EMaterialType}, this settings are used for.
	 */
	DistributionSettingsForMaterial(EMaterialType materialType) {
		this.materialType = materialType;
		this.requestingBuildings = MaterialsOfBuildings.getBuildingTypesRequestingMaterial(materialType);
		this.probabilities = new float[requestingBuildings.length];

		if (probabilities.length > 0) {
			float value = 1.0f / probabilities.length;
			for (int i = 0; i < probabilities.length; i++) {
				probabilities[i] = value;
			}
		}
	}

	@Override
	public int getNumberOfBuildingTypes() {
		return requestingBuildings.length;
	}

	@Override
	public EBuildingType getBuildingType(int index) {
		return requestingBuildings[index];
	}

	@Override
	public float getProbablity(int index) {
		return probabilities[index];
	}

	@Override
	public EMaterialType getMaterialType() {
		return materialType;
	}

	public void setProbabilities(float[] probabilities) {
		assert probabilities.length == this.probabilities.length : "The new probabilities have an incrorrect length! (" + materialType + ")";
		assert materialType.isDistributionConfigurable() : "Cannot configure distribution of materialtype " + materialType;

		float sum = 0;
		for (int i = 0; i < this.probabilities.length; i++) {
			sum += probabilities[i];
		}
		assert sum >= 1 : "The sum of the probabilities for the material distribution is < 1 (" + materialType + ")";

		for (int i = 0; i < this.probabilities.length; i++) {
			this.probabilities[i] = probabilities[i];
		}
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("DistributionSettings for ").append(materialType).append(": ");

		for (int i = 0; i < requestingBuildings.length; i++) {
			buffer.append(requestingBuildings[i]).append("(").append(probabilities[i]).append("), ");
		}

		return buffer.toString();
	}
}