package jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ILocatable;

/**
 * Interface for buildings requesting a digger.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IDiggerRequester extends ILocatable {
	/**
	 * 
	 * @return {@link EBuildingType} of the requesting building.
	 */
	EBuildingType getBuildingType();

	/**
	 * Indicates if the request from this requester is still active or has been canceled.
	 * 
	 * @return true if the request is still active<br>
	 *         false if the request has been canceled.
	 */
	boolean isDiggerRequestActive();

	/**
	 * 
	 * @return Gets the average height the buildings should be. The diggers need to flat the area to fit this height.
	 */
	byte getAverageHeight();
}
