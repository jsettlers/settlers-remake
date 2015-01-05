package jsettlers.common.statistics;

import jsettlers.common.buildings.EBuildingType;

/**
 * This is a building that contains data on which building type has which priority when consuming stuff.
 * <p>
 * the {@link #getConsumingType()} method must return {@link EConsumingType#WORKING_BUILDING} for objects that implement this interface.
 * 
 * @author michael
 */
public interface IConsumingBuildingType extends IConsuming {

	/**
	 * Gets the type of building that is consuming the material.
	 * 
	 * @return The building type.
	 */
	public EBuildingType getBuildingType();
}
