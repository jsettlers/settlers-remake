/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.logic.stack;

import java.util.ArrayList;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.stack.MaterialRequestCount.IMaterialRequestCountObserver;
import jsettlers.logic.stack.StackGroup.GroupedRequestStack;

public abstract class StackGroup<StackT extends GroupedRequestStack<StackT>> implements IMaterialRequestCountObserver {
	public static class GroupedRequestStack<StackT extends GroupedRequestStack<StackT>> extends RequestStack {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1062638817799183295L;
		private StackGroup<StackT> group;

		public GroupedRequestStack(StackGroup<StackT> group, EMaterialType materialType,
				EPriority priority) {
			super(group.grid, group.position, materialType, group.buildingType, priority);
			this.group = group;
		}

		@Override
		public void deliveryAccepted() {
			super.deliveryAccepted();
			if (!canDeliverThisMaterial()) {
				throw new IllegalStateException("This stack does not accept deliveries.");
			}
			group.setDeliveredStack(this);
			group.requestCounts.changeDelivered(getMaterialType(), 1);
		}

		private boolean canDeliverThisMaterial() {
			StackT currentStack = group.getCurrentDeliveredStack();
			return currentStack == null || currentStack == this;
		}

		@Override
		public void deliveryAborted() {
			super.deliveryAborted();
			group.requestCounts.changeDelivered(getMaterialType(), -1);
		}

		@Override
		public boolean isActive() {
			return canDeliverThisMaterial() && !group.requestCounts.isTooMuch(getMaterialType()) && getInDelivery() <= getInDeliveryable();
		}

		public boolean isActiveStockStack() {
			return isinDelivery() || hasMaterial();
		}
	}

	protected final IRequestsStackGrid grid;
	protected final ShortPoint2D position;
	private final EBuildingType buildingType;

	/**
	 * The stack that currently receives the delivery.
	 */
	private StackT currentDeliveredStack = null;
	private final ArrayList<StackT> requestStacks = new ArrayList<>();

	/**
	 * This is the number of materials the user requested.
	 */
	private MaterialRequestCount requestCounts = new MaterialRequestCount();

	// /**
	// * This is the number of materials we are requesting. While this stack is empty, it is the same as requestCounts.
	// * <p>
	// * As soon as this stack is not empty any more, the requestCounts is masked so that only the material we are listening for is there.
	// */
	// private MaterialRequestCount activeRequestCounts = requestCounts;

	public StackGroup(IRequestsStackGrid grid, ShortPoint2D position, EBuildingType buildingType) {
		this.grid = grid;
		this.position = position;
		this.buildingType = buildingType;
		requestCounts.addMaterialCountObserver(this);
	}

	public void setRequestCounts(MaterialRequestCount requestCounts) {
		this.requestCounts.removeMaterialCountObserver(this);
		for (EMaterialType m : EMaterialType.DROPPABLE_MATERIALS) {
			if (this.requestCounts.getRequestedFor(m) != requestCounts.getRequestedFor(m)) {
				materialCountChanged(m, this.requestCounts.getRequestedFor(m), requestCounts.getRequestedFor(m));
			}
		}
		this.requestCounts = requestCounts;
		requestCounts.addMaterialCountObserver(this);
	}

	/**
	 * Gets the material that is currently delivered or that is on the grid.
	 */
	public void getMaterial() {

	}

	@Override
	public void materialCountChanged(EMaterialType material, short oldValue, short newValue) {
		StackT stack = getStack(material);
		if (newValue == 0) {
			if (stack == null) {
				// nothing to do.
				return;
			}
			// remove that stack.

			requestMaterialRemoval(material);
		} else {
			if (oldValue == 0) {
				stack = createStack(material);
				requestStacks.add(stack);
			}
			// TODO: Do we need to change request?
		}

	}

	/**
	 * Requests to remove the material at our position. Called whenever the user stated that he does not want this material any more.
	 * 
	 * @param material
	 *            The material.
	 */
	protected void requestMaterialRemoval(EMaterialType material) {
	}

	private StackT getStack(EMaterialType material) {
		for (StackT s : requestStacks) {
			if (s.getMaterialType() == material) {
				return s;
			}
		}
		return null;
	}

	public MaterialRequestCount getRequestCounts() {
		return requestCounts;
	}

	public void setDeliveredStack(GroupedRequestStack<StackT> currentDeliveredStack) {
		if (currentDeliveredStack == getCurrentDeliveredStack() || !requestStacks.contains(currentDeliveredStack)) {
			// ignored.
			return;
		}
		this.currentDeliveredStack = null;

		for (StackT stack : requestStacks) {
			if (stack != currentDeliveredStack) {
				stack.releaseRequests();
			} else {
				this.currentDeliveredStack = stack;
				// FIXME: This seems to make bearers drop the material.
				stack.setPriority(EPriority.STOCK_STARTED);
			}
		}
		requestStacks.clear();
		if (this.currentDeliveredStack != null) {
			requestStacks.add(this.currentDeliveredStack);
		} else {
			throw new IllegalStateException("The active stack is not in the request stacks.");
		}
	}

	public StackT getCurrentDeliveredStack() {
		if (currentDeliveredStack != null && !currentDeliveredStack.isActiveStockStack()) {
			currentStackInactivated();
		}
		return currentDeliveredStack;
	}

	/**
	 * The current stack is not active any more (e.g. empty).
	 * <p>
	 * Allow all stacks to re-request the materials.
	 */
	private void currentStackInactivated() {
		currentDeliveredStack.releaseRequests();
		currentDeliveredStack = null;
		reAddRequestStacks();
	}

	private void reAddRequestStacks() {
		System.out.println("Stock: Added all request stacks at " + position);
	}

	public abstract StackT createStack(EMaterialType m);

	public void killEvent() {
		// We simply set all counts to 0. This clears all requests.
		setRequestCounts(new MaterialRequestCount());
	}
}
