/*******************************************************************************
 * Copyright (c) 2015 - 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.map.grid.partition.manager.materials;

import java.io.Serializable;

import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IJoblessSupplier;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IManagerBearer;
import jsettlers.logic.map.grid.partition.manager.materials.offers.EOfferPriority;
import jsettlers.logic.map.grid.partition.manager.materials.offers.MaterialOffer;
import jsettlers.logic.map.grid.partition.manager.materials.offers.OffersList;
import jsettlers.logic.map.grid.partition.manager.materials.requests.AbstractMaterialRequestPriorityQueue;
import jsettlers.logic.map.grid.partition.manager.materials.requests.MaterialRequestObject;
import jsettlers.logic.map.grid.partition.manager.materials.requests.MaterialsForBuildingsRequestPriorityQueue;
import jsettlers.logic.map.grid.partition.manager.materials.requests.SimpleMaterialRequestPriorityQueue;
import jsettlers.logic.map.grid.partition.manager.settings.PartitionManagerSettings;


/**
 * This class implements an algorithm to distribute material transport jobs to jobless bearers.
 *
 * @author Andreas Eberle
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
	 * 		{@link IJoblessSupplier} providing the jobless bearers.
	 * @param offersList
	 * 		{@link OffersList} providing the offered materials.
	 * @param settings
	 * 		{@link IPartitionSettings} providing the settings of the partition.
	 */
	public MaterialsManager(IJoblessSupplier joblessSupplier, OffersList offersList, PartitionManagerSettings settings) {
		this.joblessSupplier = joblessSupplier;
		this.offersList = offersList;
		this.settings = settings;

		requestQueues = new AbstractMaterialRequestPriorityQueue[EMaterialType.NUMBER_OF_MATERIALS];
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			EMaterialType materialType = EMaterialType.VALUES[i];
			if (materialType.isDistributionConfigurable()) {
				requestQueues[i] = new MaterialsForBuildingsRequestPriorityQueue(settings.getDistributionSettings(materialType));
			} else {
				requestQueues[i] = new SimpleMaterialRequestPriorityQueue();
			}
		}
	}

	/**
	 * Adds the given {@link MaterialRequestObject} as requester for the given material.
	 *
	 * @param material
	 * 		The material that is requested.
	 * @param requestObject
	 * 		The {@link MaterialRequestObject} object that specifies the amount of needed material.
	 */
	public void addRequestObject(EMaterialType material, MaterialRequestObject requestObject) {
		requestQueues[material.ordinal].insertRequest(requestObject);
	}

	public void distributeJobs() {
		for (int i = 0; i < EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS && !joblessSupplier.isEmpty(); i++) {
			if (joblessSupplier.isEmpty()) // no jobless? just return
				break;

			distributeJobForMaterial(settings.getMaterialTypeForPriority(i));
		}
	}

	private void distributeJobForMaterial(EMaterialType materialType) {
		if (offersList.isEmpty(materialType, EOfferPriority.LOWEST)) {
			return;
		}

		AbstractMaterialRequestPriorityQueue requestQueue = requestQueues[materialType.ordinal];
		MaterialRequestObject request = requestQueue.getHighestRequest();

		if (request == null) // no request => return
			return;

		EOfferPriority minimumIncludedOfferPriority = request.getMinimumAcceptedOfferPriority();
		if (offersList.isEmpty(materialType, minimumIncludedOfferPriority)) {
			return; // no offers => return
		}

		MaterialOffer offer = offersList.getOfferCloseTo(materialType, minimumIncludedOfferPriority, request.getPosition());

		assert offer != null : "The offer can't be null here!";

		IManagerBearer jobless = joblessSupplier.removeJoblessCloseTo(offer.getPosition());

		assert jobless != null : "The jobless can't be null here!";

		jobless.deliver(materialType, offer, request);
	}

	public void movePositionTo(ShortPoint2D position, MaterialsManager newManager) {
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			requestQueues[i].moveObjectsOfPositionTo(position, newManager.requestQueues[i]);
		}
	}

	public void mergeInto(MaterialsManager newManager) {
		for (int i = 0; i < EMaterialType.NUMBER_OF_MATERIALS; i++) {
			requestQueues[i].mergeInto(newManager.requestQueues[i]);
		}
	}
}
