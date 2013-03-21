package jsettlers.logic.map.newGrid.partition.manager.materials.requests;

import java.io.Serializable;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;

/**
 * Interface to supply distribution informations. Instances of this interface are used to get the distribution probabilitys for the buildings that can
 * receive a specific {@link EMaterialType}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMaterialsToBuildingsDistributionSettingsProvider extends Serializable {
	/**
	 * 
	 * @return Returns the number of buildings that can request the material this setting is used for.
	 */
	int getNumberOfBuildings();

	/**
	 * 
	 * @param index
	 *            Index in the interval [0, {@link #getNumberOfBuildings()} - 1].
	 * @return Returns the {@link EBuildingType} represented by the given index.
	 */
	EBuildingType getBuildingType(int index);

	/**
	 * 
	 * @param index
	 *            Index of the {@link EBuildingType}. The index has to be in the interval [0, {@link #getNumberOfBuildings()} - 1]. <br>
	 *            To get the {@link EBuildingType} call {@link #getBuildingType(index)}
	 * @return Returns the probability that a material should be send to a requester of the {@link EBuildingType} represented by the given index. <br>
	 * 
	 * @see #getBuildingType(int)
	 */
	float getProbablity(int index);

	/**
	 * 
	 * @return Returns the {@link EMaterialType} this settings are used for.
	 */
	EMaterialType getMaterialType();
}
