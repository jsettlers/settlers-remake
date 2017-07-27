/*******************************************************************************
 * Copyright (c) 2015 - 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
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
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.IRequestStackListener;
import jsettlers.logic.buildings.stack.IRequestsStackGrid;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.grid.partition.manager.materials.requests.MaterialRequestObject;

/**
 * A stack that is capable of requesting multiple materials at the same time. When one of the requested materials will be delivered, the stack only requests that materials as long as it holds any of
 * that material.
 * <p/>
 * Therefore the stack can only hold a single type of material.
 *
 * @author Andreas Eberle
 */
public class MultiRequestStack implements IRequestStack {
	private static final long serialVersionUID = 1735769845576581676L;

	protected final IRequestsStackGrid grid;
	protected final ShortPoint2D position;
	private final EBuildingType buildingType;

	private final MultiRequestStackSharedData sharedData;
	protected final RequestOfMultiRequestStack[] materialRequests = new RequestOfMultiRequestStack[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];

	protected EMaterialType currentMaterialType;
	private short popped;

	protected boolean released;

	/**
	 * Creates a new bounded {@link MultiRequestStack} to request a limited amount of the given {@link EMaterialType} at the given position.
	 *
	 * @param grid
	 *            The {@link IRequestsStackGrid} to be used as base for this {@link IRequestStack}.
	 * @param position
	 *            The position the stack will be.
	 * @param buildingType
	 *            Type of the building using this stack.
	 */
	public MultiRequestStack(IRequestsStackGrid grid, ShortPoint2D position, EBuildingType buildingType, EPriority priority, MultiRequestStackSharedData sharedData) {
		this.grid = grid;
		this.position = position;
		this.buildingType = buildingType;

		this.sharedData = sharedData;

		for (int i = 0; i < EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS; i++) {
			EMaterialType materialType = EMaterialType.DROPPABLE_MATERIALS[i];
			materialRequests[materialType.ordinal] = createRequestForMaterial(priority, materialType);
			grid.request(materialType, materialRequests[materialType.ordinal]);
		}
	}

	protected RequestOfMultiRequestStack createRequestForMaterial(EPriority priority, EMaterialType materialType) {
		return new RequestOfMultiRequestStack(materialType, priority);
	}

	@Override
	public boolean hasMaterial() {
		return currentMaterialType != null && grid.hasMaterial(position, currentMaterialType);
	}

	/**
	 * Pops a material from this stack. The material is of the type returned by {@link #getMaterialType()} and specified in the constructor.
	 *
	 * @return <code>true</code> if there was a material to be popped from this stack. False otherwise.
	 */
	@Override
	public boolean pop() {
		if (currentMaterialType != null && grid.popMaterial(position, currentMaterialType)) {
			popped++;
			checkIfCurrentMaterialShouldBeReset();
			return true;
		} else {
			return false;
		}
	}

	protected void checkIfCurrentMaterialShouldBeReset() {
		if (currentMaterialType != null && materialRequests[currentMaterialType.ordinal].getInDelivery() <= 0 && getStackSize() == 0) {
			sharedData.unregisterHandlingStack(currentMaterialType, this);
			currentMaterialType = null;
		}
	}

	/**
	 * This method gives the number of popped materials.
	 * <p/>
	 * Due to the size of the variable, this method should only be used on limited stacks. Unlimited stacks my run into an overflow of the popped value.
	 *
	 * @return Returns the number of materials popped from this stack.
	 */
	@Override
	public short getNumberOfPopped() {
		return popped;
	}

	/**
	 * Checks if all needed materials have been delivered. Therefore this method is only useful with bounded request stacks.
	 *
	 * @return Returns true if this is a bounded stack and all the requested material has been delivered, <br>
	 *         false otherwise.
	 */
	@Override
	public boolean isFulfilled() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EMaterialType getMaterialType() {
		return currentMaterialType;
	}

	@Override
	public void releaseRequests() {
		for (RequestOfMultiRequestStack materialRequest : materialRequests) {
			materialRequest.updatePriority(EPriority.STOPPED);
			grid.createOffersForAvailableMaterials(position, materialRequest.materialType);
		}
		released = true;
	}

	@Override
	public int getStackSize() {
		return currentMaterialType == null ? 0 : grid.getStackSize(position, currentMaterialType);
	}

	@Override
	public short getStillRequired() {
		return -1;
	}

	@Override
	public void setPriority(EPriority priority) {
		for (MaterialRequestObject materialRequest : materialRequests) {
			materialRequest.updatePriority(priority);
		}
	}

	boolean canAcceptMoreDeliveriesOf(EMaterialType materialType) {
		if (materialType != null && materialType == currentMaterialType) {
			MaterialRequestObject activeRequest = materialRequests[currentMaterialType.ordinal];
			return getInDeliveryable() - activeRequest.getInDelivery() > 0;
		} else {
			return false;
		}
	}

	private int getInDeliveryable() {
		return Constants.STACK_SIZE - getStackSize();
	}

	@Override
	public ShortPoint2D getPosition() {
		return position;
	}

	@Override
	public void setListener(IRequestStackListener listener) {
		throw new UnsupportedOperationException("not implemented yet");
	}

	protected class RequestOfMultiRequestStack extends MaterialRequestObject {
		private static final long serialVersionUID = -7139074965376516629L;

		protected final EMaterialType materialType;

		RequestOfMultiRequestStack(EMaterialType materialType, EPriority priority) {
			super(priority);
			this.materialType = materialType;
		}

		@Override
		public ShortPoint2D getPosition() {
			return position;
		}

		@Override
		protected EBuildingType getBuildingType() {
			return buildingType;
		}

		@Override
		public boolean isFinished() {
			return released;
		}

		@Override
		protected boolean isRoundRobinRequest() {
			return true;
		}

		@Override
		protected void materialDelivered() {
			sharedData.requestSettings.updateRequested(materialType, -1);
		}

		@Override
		protected short getStillNeeded() {
			if (currentMaterialType != null) {
				if (currentMaterialType == materialType) {
					return sharedData.getStillNeeded(materialType);
				} else {
					return 0;
				}
			} else {
				return sharedData.getStillNeededIfNoOthersHandleIt(materialType);
			}
		}

		@Override
		protected int getInDeliveryable() {
			return MultiRequestStack.this.getInDeliveryable();
		}

		@Override
		public void deliveryAccepted() {
			super.deliveryAccepted();
			currentMaterialType = materialType;
			sharedData.registerHandlingStack(materialType, MultiRequestStack.this);
			sharedData.inDelivery[materialType.ordinal]++;
		}

		@Override
		public void deliveryAborted() {
			super.deliveryAborted();
			sharedData.inDelivery[materialType.ordinal]--;
			checkIfCurrentMaterialShouldBeReset();
		}

		@Override
		public void deliveryFulfilled() {
			super.deliveryFulfilled();
			sharedData.inDelivery[materialType.ordinal]--;
		}

		@Override
		public boolean canTakeMoreOffers() {
			return super.canTakeMoreOffers();
		}
	}
}
