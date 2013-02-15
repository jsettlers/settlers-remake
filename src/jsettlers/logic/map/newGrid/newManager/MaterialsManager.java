package jsettlers.logic.map.newGrid.newManager;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.newGrid.newManager.interfaces.IJoblessSupplier;
import jsettlers.logic.map.newGrid.newManager.interfaces.IManagerBearer;
import jsettlers.logic.map.newGrid.newManager.offers.MaterialOffer;
import jsettlers.logic.map.newGrid.newManager.offers.OffersList;
import jsettlers.logic.map.newGrid.newManager.requests.MaterialRequestPriorityQueue;
import jsettlers.logic.map.newGrid.newManager.requests.MaterialRequestPriorityQueueItem;

/**
 * This class implements an algorithm to distribute material transport jobs to jobless bearers.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MaterialsManager implements Serializable {
	private static final long serialVersionUID = 6395951461349453696L;

	private final OffersList offersList;
	private final MaterialRequestPriorityQueue[] requestQueues;
	private final IJoblessSupplier joblessSupplier;

	/**
	 * Creates a new {@link MaterialsManager} that uses the given {@link IJoblessSupplier} and {@link OffersList} for it's operations.
	 * 
	 * @param joblessSupplier
	 *            {@link IJoblessSupplier} providing the jobless bearers.
	 * @param offersList
	 *            {@link OffersList} providing the offered materials.
	 */
	public MaterialsManager(IJoblessSupplier joblessSupplier, OffersList offersList) {
		this.joblessSupplier = joblessSupplier;
		this.offersList = offersList;

		requestQueues = new MaterialRequestPriorityQueue[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			requestQueues[i] = new MaterialRequestPriorityQueue();
		}
	}

	/**
	 * Adds the given {@link MaterialRequestPriorityQueueItem} as requester for the given material.
	 * 
	 * @param material
	 *            The material that is requested.
	 * @param requestObject
	 *            The {@link MaterialRequestPriorityQueueItem} object that specifies the amount of needed material.
	 */
	public void addRequestObject(EMaterialType material, MaterialRequestPriorityQueueItem requestObject) {
		requestQueues[material.ordinal].insertRequest(requestObject);
	}

	public void distributeJobs() {
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS && !joblessSupplier.isEmpty(); i++) {
			distributeJobForMaterial(EMaterialType.values[i]);
		}
	}

	private void distributeJobForMaterial(EMaterialType materialType) {
		if (offersList.isEmpty(materialType) || joblessSupplier.isEmpty()) // no offers? or no jobless? just return
			return;

		MaterialRequestPriorityQueueItem request = requestQueues[materialType.ordinal].getHighestRequest();

		if (request == null) // no request, return
			return;

		MaterialOffer offer = offersList.removeOfferCloseTo(materialType, request.getPos());

		assert offer != null : "The offer can't be null here!";

		IManagerBearer jobless = joblessSupplier.removeJoblessCloseTo(offer.getPos());

		assert jobless != null : "The jobless can't be null here!";

		jobless.deliver(offer, request);
	}
}
