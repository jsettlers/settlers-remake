package jsettlers.logic.map.newGrid.newManager.interfaces;

import jsettlers.logic.map.newGrid.newManager.MaterialsManager;
import jsettlers.logic.map.newGrid.newManager.offers.MaterialOffer;

/**
 * This interface describes a bearer that can be used by the {@link MaterialsManager} to give job orders.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IManagerBearer {

	/**
	 * Sets a job to this {@link IManagerBearer} object. The job is to deliver the given offer tot the given request.
	 * 
	 * @param offer
	 * @param request
	 */
	void deliver(MaterialOffer offer, IMaterialRequest request);

}
