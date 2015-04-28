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

import java.io.Serializable;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.map.newGrid.partition.manager.materials.requests.MaterialRequestObject;

/**
 * This class represents a requesting stack of a building. It can handle unlimited and bounded amounts of requests.
 * 
 * @author Andreas Eberle
 * 
 */
public class RequestStack extends MaterialRequestObject implements Serializable, IBuildingMaterial {
	private static final long serialVersionUID = 8082718564781798767L;

	private final ShortPoint2D position;
	private final EMaterialType materialType;
	private final EBuildingType buildingType;

	private final IRequestsStackGrid grid;

	private short stillNeeded;
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

		this.stillNeeded = requestedAmount;
		grid.request(materialType, this);
		super.updatePriority(priority);
	}

	public boolean hasMaterial() {
		return grid.hasMaterial(position, materialType);
	}

	/**
	 * Pops a material from this stack. The material is of the type returned by {@link #getMaterialType()} and specified in the constructor.
	 */
	public void pop() {
		grid.popMaterial(position, materialType);
		popped++;
	}

	/**
	 * This method gives the number of popped materials.
	 * <p />
	 * Due to the size of the variable, this method should only be used on limited stacks. Unlimited stacks my run into an overflow of the popped
	 * value.
	 * 
	 * @return Returns the number of materials popped from this stack.
	 */
	public short getNumberOfPopped() {
		return popped;
	}

	/**
	 * Checks if all needed materials have been delivered. Therefore this method is only useful with bounded request stacks.
	 * 
	 * @return Returns true if this is a bounded stack and all the requested material has been delivered, <br>
	 *         false otherwise.
	 */
	public boolean isFullfilled() {
		return stillNeeded <= 0;
	}

	public ShortPoint2D getPosition() {
		return position;
	}

	@Override
	public EMaterialType getMaterialType() {
		return materialType;
	}

	public void setPriority(EPriority priority) {
		super.updatePriority(priority);
	}

	public void releaseRequests() {
		stillNeeded = 0;
		grid.createOffersForAvailableMaterials(position, materialType);
	}

	@Override
	public ShortPoint2D getPos() {
		return position;
	}

	@Override
	public int getMaterialCount() {
		return grid.getStackSize(position, materialType);
	}

	@Override
	public boolean isOffering() {
		return false;
	}

	@Override
	protected int getStillNeeded() {
		return stillNeeded;
	}

	@Override
	protected int getInDeliveryable() {
		return Constants.STACK_SIZE - getMaterialCount();
	}

	@Override
	protected void materialDelivered() {
		if (stillNeeded < Short.MAX_VALUE) {
			stillNeeded--;
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
	public void setListener(IRequestStackListener listener) {
		this.listener = listener;
	}

	@Override
	protected boolean isRoundRobinRequest() {
		return stillNeeded == Short.MAX_VALUE;
	}

	@Override
	public EBuildingType getBuildingType() {
		return buildingType;
	}
}
