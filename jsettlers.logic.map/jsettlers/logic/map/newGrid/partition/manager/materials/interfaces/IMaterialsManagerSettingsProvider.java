package jsettlers.logic.map.newGrid.partition.manager.materials.interfaces;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.newGrid.partition.manager.materials.requests.IMaterialsToBuildingsDistributionSettingsProvider;

/**
 * This interface defines a method needed to get the priority order of {@link EMaterialType}s.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMaterialsManagerSettingsProvider extends Serializable {

	/**
	 * This method gives the {@link EMaterialType} for the given priority index.
	 * 
	 * @param priorityIdx
	 *            The priority for which to return the {@link EMaterialType}.<br>
	 *            The priority must be in the interval [0, {@link EMaterialType}.NUMBER_OF_DROPPABLE_MATERIALS] where 0 is the highest priority.
	 * @return Returns the {@link EMaterialType} with the given priority.
	 */
	EMaterialType getMaterialTypeForPrio(int priorityIdx);

	/**
	 * 
	 * @param materialType
	 * @return Returns the {@link IMaterialsToBuildingsDistributionSettingsProvider} for the given material type.
	 */
	IMaterialsToBuildingsDistributionSettingsProvider getDistributionSettings(EMaterialType materialType);
}
