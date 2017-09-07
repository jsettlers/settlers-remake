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
package jsettlers.logic.map.grid.partition.manager.objects;

import java.io.Serializable;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;

public final class WorkerRequest extends WorkerCreationRequest implements ILocatable, Serializable {
	private static final long serialVersionUID = 6420250669583553112L;

	public final EMovableType movableType;
	public final IWorkerRequestBuilding building;
	public boolean creationRequested = false;

	public WorkerRequest(EMovableType movableType, IWorkerRequestBuilding building) {
		this.building = building;
		this.movableType = movableType;
	}

	@Override
	public ShortPoint2D getPosition() {
		return building.getDoor();
	}

	@Override
	public String toString() {
		return movableType + "    " + creationRequested + "     " + building.getBuildingType();
	}

	@Override
	public boolean isRequestAlive() {
		return !building.isDestroyed();
	}

	@Override
	public EMovableType requestedMovableType() {
		return movableType;
	}
}