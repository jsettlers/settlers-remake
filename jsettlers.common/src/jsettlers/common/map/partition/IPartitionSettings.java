package jsettlers.common.map.partition;

import jsettlers.common.material.EMaterialType;

/**
 * This interface gives access to the settings of a partition.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPartitionSettings {

	/**
	 * This method gives access to the material distribution settings of the partition.
	 * 
	 * @param materialType
	 * @return Returns the distribution settings for the given {@link EMaterialType}.
	 */
	IMaterialsDistributionSettings getDistributionSettings(EMaterialType materialType);

	/**
	 * This method gives the {@link EMaterialType} for the given priority index.
	 * 
	 * @param priorityIdx
	 *            The priority for which to return the {@link EMaterialType}.<br>
	 *            The priority must be in the interval [0, {@link EMaterialType}.NUMBER_OF_DROPPABLE_MATERIALS-1] where 0 is the highest priority.
	 * @return Returns the {@link EMaterialType} with the given priority.
	 */
	EMaterialType getMaterialTypeForPrio(int priorityIdx);
}
