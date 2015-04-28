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
package jsettlers.logic.buildings.workers;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.buildings.WorkAreaBuilding;
import jsettlers.logic.map.newGrid.partition.manager.manageables.IManageableWorker;
import jsettlers.logic.map.newGrid.partition.manager.manageables.interfaces.IWorkerRequestBuilding;
import jsettlers.logic.player.Player;
import jsettlers.logic.stack.RequestStack;

/**
 * This class is a building with a worker that can fulfill it's job.
 * 
 * @author Andreas Eberle
 * 
 */
public class WorkerBuilding extends WorkAreaBuilding implements IWorkerRequestBuilding {
	private static final long serialVersionUID = 7050284039312172046L;

	private IManageableWorker worker;

	public WorkerBuilding(EBuildingType type, Player player) {
		super(type, player);
	}

	@Override
	public final EMapObjectType getFlagType() {
		return EMapObjectType.FLAG_ROOF;
	}

	@Override
	protected final int constructionFinishedEvent() {
		requestWorker();
		return -1; // no scheduling required
	}

	private void requestWorker() {
		super.getGrid().requestBuildingWorker(super.getBuildingType().getWorkerType(), this);
	}

	@Override
	protected final int subTimerEvent() {
		assert false : "This should never be called, as this building should not be scheduled.";
		return -1;
	}

	@Override
	public final boolean popMaterial(ShortPoint2D position, EMaterialType material) {
		for (RequestStack stack : super.getStacks()) {
			if (stack.getPosition().equals(position) && stack.getMaterialType() == material) {
				stack.pop();
				return true;
			}
		}
		return false;
	}

	@Override
	public final void occupyBuilding(IManageableWorker worker) {
		if (super.isNotDestroyed()) {
			this.worker = worker;
			super.placeFlag(true);
			super.createWorkStacks();
		}
	}

	@Override
	public final void leaveBuilding(IManageableWorker worker) {
		if (worker == this.worker) {
			this.worker = null;
			super.placeFlag(false);
			super.releaseRequestStacks();
			requestWorker();
		} else {
			System.err.println("A worker not registered at the building wanted to leave it!");
		}
	}

	@Override
	protected final void killedEvent() {
		if (worker != null) {
			this.worker.buildingDestroyed();
			this.worker = null;
		}
	}

	@Override
	public final boolean isOccupied() {
		return worker != null;
	}
}
