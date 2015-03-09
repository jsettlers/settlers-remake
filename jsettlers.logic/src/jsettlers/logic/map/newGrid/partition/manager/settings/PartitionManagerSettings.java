package jsettlers.logic.map.newGrid.partition.manager.settings;

import java.io.Serializable;

import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;

/**
 * This class bundles all settings for the {@link PartitionManager}.
 * 
 * @author Andreas Eberle
 * 
 */
public final class PartitionManagerSettings implements IPartitionSettings, Serializable {
	private static final long serialVersionUID = -6269898822727665606L;

	private static final DistributionSettingsForMaterial[] defaultSettings = new DistributionSettingsForMaterial[EMaterialType.NUMBER_OF_MATERIALS];
	static {
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			defaultSettings[i] = new DistributionSettingsForMaterial(EMaterialType.values[i]);
		}
	}

	private final EMaterialType[] materialTypeForPriorities;
	private final DistributionSettingsForMaterial[] settingsOfMaterials;

	public PartitionManagerSettings() {
		materialTypeForPriorities = new EMaterialType[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
		System.arraycopy(EMaterialType.DROPPABLE_MATERIALS, 0, materialTypeForPriorities, 0, EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS);

		settingsOfMaterials = new DistributionSettingsForMaterial[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			EMaterialType materialType = EMaterialType.values[i];

			if (materialType.isDistributionConfigurable()) {
				settingsOfMaterials[i] = new DistributionSettingsForMaterial(materialType);
			} else {
				settingsOfMaterials[i] = defaultSettings[i];
			}
		}
	}

	@Override
	public EMaterialType getMaterialTypeForPrio(int priorityIdx) {
		return materialTypeForPriorities[priorityIdx];
	}

	@Override
	public DistributionSettingsForMaterial getDistributionSettings(final EMaterialType materialType) {
		return settingsOfMaterials[materialType.ordinal];
	}

	/**
	 * Sets the setting for the priorities of the droppable {@link EMaterialType}s.
	 * 
	 * @param materialTypeForPriority
	 *            An array of all droppable {@link EMaterialType}s. The first element has the highest priority, the last one has the lowest.
	 */
	public void setMaterialTypesForPriorities(EMaterialType[] materialTypeForPriority) {
		assert this.materialTypeForPriorities.length == materialTypeForPriority.length;

		for (int i = 0; i < materialTypeForPriority.length; i++) {
			this.materialTypeForPriorities[i] = materialTypeForPriorities[i];
		}
	}
}
