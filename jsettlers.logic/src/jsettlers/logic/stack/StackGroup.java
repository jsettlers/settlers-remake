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

		public GroupedRequestStack(StackGroup<StackT> group, EMaterialType materialType) {
			super(group.grid, group.position, materialType, group.buildingType, group.getStartedStackPriority());
			this.group = group;
		}

		@Override
		public void deliveryAccepted() {
			if (!canDeliverThisMaterial()) {
				throw new IllegalStateException("This stack does not accept deliveries. Attempted to deliver to " + this + " but should have been "
						+ group.getCurrentDeliveredStack());
			}
			super.deliveryAccepted();
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

		@Override
		public short getStillNeeded() {
			short missing = group.requestCounts.getMissingFor(getMaterialType());
			if (missing == Short.MAX_VALUE) {
				return missing;
			} else {
				// parent assumes this count contains inDelivery.
				return (short) (missing + getInDelivery());
			}
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
		assert (stack == null) == (oldValue == 0);
		if (newValue == 0) {
			if (stack != null) {
				// remove that stack.
				requestMaterialRemoval(material);
				stack.releaseRequests();
				requestStacks.remove(stack);
			}
		} else {
			if (oldValue == 0) {
				createRequestFor(material);
			} else {
				// if this request was unscheduled, we need to re-schedule it.
				stack.reschedule();
			}
		}

	}

	private void createRequestFor(EMaterialType material) {
		StackT stack;
		stack = createStack(material);
		if (getCurrentDeliveredStack() != null) {
			stack.setPriority(EPriority.STOPPED);
		}
		requestStacks.add(stack);
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
				stack.setPriority(EPriority.STOPPED);
			} else {
				this.currentDeliveredStack = stack;
				// FIXME: This seems to make bearers drop the material.
				stack.setPriority(getStartedStackPriority());
			}
		}
		if (this.currentDeliveredStack == null) {
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
		currentDeliveredStack = null;
		for (StackT stack : requestStacks) {
			stack.setPriority(getEmptyStackPriority());
		}
	}

	public abstract StackT createStack(EMaterialType m);

	protected abstract EPriority getEmptyStackPriority();

	protected abstract EPriority getStartedStackPriority();

	public void killEvent() {
		// We simply set all counts to 0. This clears all requests.
		setRequestCounts(new MaterialRequestCount());
	}
}
