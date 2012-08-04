package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ILocatable;

/**
 * Interface for buildings requesting a digger.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IDiggerRequester extends IRequester, ILocatable {
	/**
	 * 
	 * @return {@link EBuildingType} of the requesting building.
	 */
	EBuildingType getBuildingType();

	@Override
	boolean isRequestActive();

	/**
	 * 
	 * @return Gets the average height the buildings should be. The diggers need to flat the area to fit this height.
	 */
	byte getAverageHeight();
}
