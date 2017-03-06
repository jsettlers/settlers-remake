/*******************************************************************************
 * Copyright (c) 2016 - 2017
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
package jsettlers.logic.buildings.stack.multi;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.stack.IRequestsStackGrid;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IOfferEmptiedListener;
import jsettlers.logic.map.grid.partition.manager.materials.offers.EOfferPriority;

/**
 * Created by Andreas Eberle on 18.09.2016.
 */
public class MultiRequestAndOfferStack extends MultiRequestStack implements IStockSettingsListener, IOfferEmptiedListener {

	/**
	 * Creates a new bounded {@link MultiRequestStack} to request a limited amount of the given {@link EMaterialType} at the given position.
	 *
	 * @param grid
	 * 		The {@link IRequestsStackGrid} to be used as base for this {@link MultiRequestStack}.
	 * @param position
	 * 		The position the stack will be.
	 * @param buildingType
	 * @param priority
	 * @param sharedData
	 */
	public MultiRequestAndOfferStack(IRequestsStackGrid grid, ShortPoint2D position, EBuildingType buildingType, EPriority priority, MultiRequestStackSharedData sharedData) {
		super(grid, position, buildingType, priority, sharedData);
	}

	protected void deliveryFulFilled(EMaterialType materialType) {
		grid.offer(position, materialType, EOfferPriority.LOWEST, this);
	}

	protected RequestOfMultiRequestStack createRequestForMaterial(EPriority priority, EMaterialType materialType) {
		return new RequestOfMultiRequestAndOfferStack(materialType, priority);
	}

	@Override
	public void releaseRequests() {
		for (RequestOfMultiRequestStack materialRequest : materialRequests) {
			materialRequest.updatePriority(EPriority.STOPPED);
		}
		if (currentMaterialType != null) {
			grid.updateOfferPriorities(position, currentMaterialType, EOfferPriority.OFFER_TO_ALL);
		}
		released = true;
	}

	@Override
	public void stockSettingChanged(EMaterialType materialType, boolean accepted) {
		if (currentMaterialType == materialType && getStackSize() > 0) {
			System.out.println("relevant stock settings changed; " + materialType + " now accepted: " + accepted);
			grid.updateOfferPriorities(position, materialType, accepted ? EOfferPriority.LOW : EOfferPriority.OFFER_TO_ALL);
		}
	}

	@Override
	public void offerEmptied() {
		checkIfCurrentMaterialShouldBeReset();
	}

	protected class RequestOfMultiRequestAndOfferStack extends RequestOfMultiRequestStack {
		RequestOfMultiRequestAndOfferStack(EMaterialType materialType, EPriority priority) {
			super(materialType, priority);
		}

		@Override
		public EOfferPriority getMinimumAcceptedOfferPriority() {
			return EOfferPriority.OFFER_TO_ALL;
		}

		@Override
		public void deliveryFulfilled() {
			super.deliveryFulfilled();
			MultiRequestAndOfferStack.this.deliveryFulFilled(materialType);
		}
	}
}
