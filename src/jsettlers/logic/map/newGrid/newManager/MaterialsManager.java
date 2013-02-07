package jsettlers.logic.map.newGrid.newManager;

import java.io.Serializable;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.newManager.interfaces.IJoblessSupplier;
import jsettlers.logic.map.newGrid.newManager.interfaces.IManagerBearer;
import jsettlers.logic.map.newGrid.partition.manager.datastructures.PositionableList;

public class MaterialsManager implements Serializable {
	private static final long serialVersionUID = 6395951461349453696L;

	private final PositionableList<MaterialOffer>[] offersLists;
	private final MaterialRequestPriorityQueue[] requestQueues;
	private final IJoblessSupplier joblessSupplier;

	@SuppressWarnings("unchecked")
	public MaterialsManager(IJoblessSupplier joblessSupplier) {
		this.joblessSupplier = joblessSupplier;
		offersLists = new PositionableList[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			offersLists[i] = new PositionableList<MaterialOffer>();
		}

		requestQueues = new MaterialRequestPriorityQueue[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			requestQueues[i] = new MaterialRequestPriorityQueue();
		}
	}

	/**
	 * Insert an offered material at the given position.
	 * 
	 * @param position
	 *            The position the offered material is located.
	 * @param material
	 *            The material that is offered at the given position.
	 */
	public void addOffer(ShortPoint2D position, EMaterialType material) {
		PositionableList<MaterialOffer> list = offersLists[material.ordinal];

		MaterialOffer existingOffer = list.getObjectAt(position);
		if (existingOffer != null) {
			existingOffer.amount++;
		} else {
			list.insert(new MaterialOffer(position, material, (byte) 1));
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
			distributeJobForSlot(i);
		}
	}

	private void distributeJobForSlot(int slotIdx) {
		PositionableList<MaterialOffer> offersSlot = offersLists[slotIdx];
		if (offersSlot.isEmpty() || joblessSupplier.isEmpty()) // no offers? or no jobless? just return
			return;

		MaterialRequestPriorityQueueItem request = requestQueues[slotIdx].popHighest();

		if (request == null) // no request, return
			return;

		MaterialOffer offer = offersSlot.getObjectCloseTo(request.getPos());

		assert offer != null : "The offer can't be null here!";

		IManagerBearer jobless = joblessSupplier.removeJoblessCloseTo(offer.getPos());

		assert jobless != null : "The jobless can't be null here!";

		offer.amount--;
		if (offer.amount <= 0) { // if the offer is now empty.
			offersSlot.remove(offer);
		}
		request.inDelivery++;

		jobless.deliver(offer, request);
	}
}
