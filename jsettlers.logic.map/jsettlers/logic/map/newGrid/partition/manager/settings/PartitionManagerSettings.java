package jsettlers.logic.map.newGrid.partition.manager.settings;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.MaterialsOfBuildings;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IMaterialsManagerSettingsProvider;
import jsettlers.logic.map.newGrid.partition.manager.materials.requests.IMaterialsToBuildingsDistributionSettingsProvider;

/**
 * This class bundles all settings for the {@link PartitionManager}.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionManagerSettings implements IMaterialsManagerSettingsProvider {
	private static final long serialVersionUID = -6269898822727665606L;

	private final EMaterialType[] materialTypeForPriorities;
	private final DistributionSettingsForMaterial[] settingsOfMaterials;

	public PartitionManagerSettings() {
		materialTypeForPriorities = new EMaterialType[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
		System.arraycopy(EMaterialType.DROPPABLE_MATERIALS, 0, materialTypeForPriorities, 0, EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS);

		settingsOfMaterials = new DistributionSettingsForMaterial[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			EMaterialType materialType = EMaterialType.values[i];

			if (materialType.isDistributionConfigurable())
				settingsOfMaterials[i] = new DistributionSettingsForMaterial(materialType);
		}
	}

	@Override
	public EMaterialType getMaterialTypeForPrio(int priorityIdx) {
		return materialTypeForPriorities[priorityIdx];
	}

	@Override
	public IMaterialsToBuildingsDistributionSettingsProvider getDistributionSettings(final EMaterialType materialType) {
		return settingsOfMaterials[materialType.ordinal];
	}

	private static final class DistributionSettingsForMaterial implements IMaterialsToBuildingsDistributionSettingsProvider {
		private static final long serialVersionUID = -8519244429973606793L;

		final EMaterialType materialType;
		final EBuildingType[] requestingBuildings;
		final float[] probabilitys;

		DistributionSettingsForMaterial(EMaterialType materialType) {
			this.materialType = materialType;
			this.requestingBuildings = MaterialsOfBuildings.getBuildingTypesRequestingMaterial(materialType);
			this.probabilitys = new float[requestingBuildings.length];

			float value = 1.0f / probabilitys.length;
			for (int i = 0; i < probabilitys.length; i++) {
				probabilitys[i] = value;
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
			return probabilitys[index];
		}

		@Override
		public EMaterialType getMaterialType() {
			return materialType;
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("DistributionSettings for ").append(materialType).append(": ");

			for (int i = 0; i < requestingBuildings.length; i++) {
				buffer.append(requestingBuildings[i]).append("(").append(probabilitys[i]).append("), ");
			}

			return buffer.toString();
		}

	}
}
