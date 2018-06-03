/*******************************************************************************
 * Copyright (c) 2015, 2016
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
package jsettlers.logic.buildings.stack;

import java.io.Serializable;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.grid.partition.manager.materials.requests.MaterialRequestObject;

/**
 * This class represents a requesting stack of a building. It can handle unlimited and bounded amounts of requests.
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestStack extends MaterialRequestObject implements Serializable, IRequestStack {
	private static final long serialVersionUID = 8082718564781798767L;

	private final ShortPoint2D position;
	private final EMaterialType materialType;
	private final EBuildingType buildingType;

	private final IRequestsStackGrid grid;

	private short stillRequired;
	private short popped;

	private IRequestStackListener listener = null;

	/**
	 * Creates a new unbounded {@link RequestStack} to request an unlimited amount of the given {@link EMaterialType} at the given position.
	 * 
	 * @param grid
	 *            The {@link IRequestsStackGrid} to be used as base for this {@link RequestStack}.
	 * @param position
	 *            The position the stack will be.
	 * @param materialType
	 *            The {@link EMaterialType} requested by this stack.
	 */
	public RequestStack(IRequestsStackGrid grid, ShortPoint2D position, EMaterialType materialType, EBuildingType buildingType, EPriority priority) {
		this(grid, position, materialType, buildingType, priority, Short.MAX_VALUE);
	}

	/**
	 * Creates a new bounded {@link RequestStack} to request a limited amount of the given {@link EMaterialType} at the given position.
	 * 
	 * @param grid
	 *            The {@link IRequestsStackGrid} to be used as base for this {@link RequestStack}.
	 * @param position
	 *            The position the stack will be.
	 * @param materialType
	 *            The {@link EMaterialType} requested by this stack.
	 * @param buildingType
	 * @param requestedAmount
	 *            The number of materials requested.
	 */
	public RequestStack(IRequestsStackGrid grid, ShortPoint2D position, EMaterialType materialType, EBuildingType buildingType, EPriority priority,
			short requestedAmount) {
		this.grid = grid;
		this.position = position;
		this.materialType = materialType;
		this.buildingType = buildingType;

		this.stillRequired = requestedAmount;
		grid.request(materialType, this);
		super.updatePriority(priority);
	}

	@Override
	public boolean hasMaterial() {
		return grid.hasMaterial(position, materialType);
	}

	/**
	 * Pops a material from this stack. The material is of the type returned by {@link #getMaterialType()} and specified in the constructor.
	 * 
	 * @return <code>true</code> if there was a material to be popped from this stack. False otherwise.
	 */
	@Override
	public boolean pop() {
		if (grid.popMaterial(position, materialType)) {
			popped++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * This method gives the number of popped materials.
	 * <p />
	 * Due to the size of the variable, this method should only be used on limited stacks. Unlimited stacks my run into an overflow of the popped
	 * value.
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
		return stillRequired <= 0;
	}

	public ShortPoint2D getPosition() {
		return position;
	}

	@Override
	public EMaterialType getMaterialType() {
		return materialType;
	}

	@Override
	public void setPriority(EPriority priority) {
		super.updatePriority(priority);
	}

	@Override
	public void releaseRequests() {
		stillRequired = 0;
		grid.createOffersForAvailableMaterials(position, materialType);
	}

	@Override
	public int getStackSize() {
		return grid.getStackSize(position, materialType);
	}

	@Override
	protected short getStillNeeded() {
		return (short) (stillRequired - getInDelivery());
	}

	@Override
	public short getStillRequired() {
		return stillRequired;
	}

	@Override
	protected int getInDeliveryable() {
		return Constants.STACK_SIZE - getStackSize();
	}

	@Override
	protected void materialDelivered() {
		if (stillRequired < Short.MAX_VALUE) {
			stillRequired--;
		}

		if (listener != null) {
			listener.materialDelivered(this);
		}
	}

	/**
	 * Registers the given listener to receive the events of this stack. <br>
	 * To remove the listener just set null as the new listener.
	 * <p />
	 * NOTE: Only one listener can be registered at a time!
	 * 
	 * @param listener
	 *            The new listener or null if the old listener should just be removed.
	 */
	@Override
	public void setListener(IRequestStackListener listener) {
		this.listener = listener;
	}

	@Override
	protected boolean isRoundRobinRequest() {
		return stillRequired == Short.MAX_VALUE;
	}

	@Override
	public EBuildingType getBuildingType() {
		return buildingType;
	}
}
