package jsettlers.logic.map.newGrid.partition.manager.settings;

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

	final EMaterialType materialType;
	final EBuildingType[] requestingBuildings;
	final float[] probabilities;

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
	public int getNumberOfBuildings() {
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