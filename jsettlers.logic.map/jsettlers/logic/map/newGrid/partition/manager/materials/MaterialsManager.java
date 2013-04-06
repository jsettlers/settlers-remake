package jsettlers.logic.map.newGrid.partition.manager.materials;

import java.io.Serializable;

import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IJoblessSupplier;
import jsettlers.logic.map.newGrid.partition.manager.materials.interfaces.IManagerBearer;
import jsettlers.logic.map.newGrid.partition.manager.materials.offers.MaterialOffer;
import jsettlers.logic.map.newGrid.partition.manager.materials.offers.OffersList;
import jsettlers.logic.map.newGrid.partition.manager.materials.requests.AbstractMaterialRequestPriorityQueue;
import jsettlers.logic.map.newGrid.partition.manager.materials.requests.MaterialRequestObject;
import jsettlers.logic.map.newGrid.partition.manager.materials.requests.MaterialsForBuildingsRequestPrioQueue;
import jsettlers.logic.map.newGrid.partition.manager.materials.requests.SimpleMaterialRequestPriorityQueue;

/**
 * This class implements an algorithm to distribute material transport jobs to jobless bearers.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MaterialsManager implements Serializable {
	private static final long serialVersionUID = 6395951461349453696L;

	private final OffersList offersList;
	private final AbstractMaterialRequestPriorityQueue[] requestQueues;
	private final IJoblessSupplier joblessSupplier;

	private final IPartitionSettings settings;

	/**
	 * Creates a new {@link MaterialsManager} that uses the given {@link IJoblessSupplier} and {@link OffersList} for it's operations.
	 * 
	 * @param joblessSupplier
	 *            {@link IJoblessSupplier} providing the jobless bearers.
	 * @param offersList
	 *            {@link OffersList} providing the offered materials.
	 */
	public MaterialsManager(IJoblessSupplier joblessSupplier, OffersList offersList, IPartitionSettings settings) {
		this.joblessSupplier = joblessSupplier;
		this.offersList = offersList;
		this.settings = settings;

		requestQueues = new AbstractMaterialRequestPriorityQueue[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			EMaterialType materialType = EMaterialType.values[i];
			if (materialType.isDistributionConfigurable()) {
				requestQueues[i] = new MaterialsForBuildingsRequestPrioQueue(settings.getDistributionSettings(materialType));
			} else {
				requestQueues[i] = new SimpleMaterialRequestPriorityQueue();
			}
		}
	}

	/**
	 * Adds the given {@link MaterialRequestObject} as requester for the given material.
	 * 
	 * @param material
	 *            The material that is requested.
	 * @param requestObject
	 *            The {@link MaterialRequestObject} object that specifies the amount of needed material.
	 */
	public void addRequestObject(EMaterialType material, MaterialRequestObject requestObject) {
		requestQueues[material.ordinal].insertRequest(requestObject);
	}

	public void distributeJobs() {
		for (int i = 0; i < EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS && !joblessSupplier.isEmpty(); i++) {
			distributeJobForMaterial(settings.getMaterialTypeForPrio(i));
		}
	}

	private void distributeJobForMaterial(EMaterialType materialType) {
		if (offersList.isEmpty(materialType) || joblessSupplier.isEmpty()) // no offers? or no jobless? just return
			return;

		MaterialRequestObject request = requestQueues[materialType.ordinal].getHighestRequest();

		if (request == null) // no request, return
			return;

		MaterialOffer offer = offersList.removeOfferCloseTo(materialType, request.getPos());

		assert offer != null : "The offer can't be null here!";

		IManagerBearer jobless = joblessSupplier.removeJoblessCloseTo(offer.getPos());

		assert jobless != null : "The jobless can't be null here!";

		jobless.deliver(materialType, offer.getPos(), request);
	}

	public void movePositionTo(ShortPoint2D position, MaterialsManager newManager) {
		for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			requestQueues[i].moveObjectsOfPositionTo(position, newManager.requestQueues[i]);
		}
	}

	public void mergeInto(MaterialsManager newManager) {
		for (int i = 0; i < EDirection.NUMBER_OF_DIRECTIONS; i++) {
			requestQueues[i].mergeInto(newManager.requestQueues[i]);
		}
	}
}
