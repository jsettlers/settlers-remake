package jsettlers.logic.map.newGrid.partition.manager.settings;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.newGrid.partition.manager.PartitionManager;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IMaterialsManagerSettings;

/**
 * This class bundles all settings for the {@link PartitionManager}.
 * 
 * @author Andreas Eberle
 * 
 */
public class PartitionManagerSettings implements IMaterialsManagerSettings {
	private static final long serialVersionUID = -6269898822727665606L;

	private final EMaterialType[] materialTypePriorities;

	public PartitionManagerSettings() {
		materialTypePriorities = new EMaterialType[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
		System.arraycopy(EMaterialType.DROPPABLE_MATERIALS, 0, materialTypePriorities, 0, EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS);
	}

	@Override
	public EMaterialType getMaterialTypeForPrio(int priorityIdx) {
		return materialTypePriorities[priorityIdx];
	}

}
