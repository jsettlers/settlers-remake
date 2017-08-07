/*******************************************************************************
 * Copyright (c) 2017
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EShipType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.DockPosition;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.IDockBuilding;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.RequestStack;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.player.Player;

/**
 * An extension to the worker building for dockyards
 */
public class DockyardBuilding extends WorkerBuilding implements IBuilding.IShipConstruction, IDockBuilding {
	private static final long serialVersionUID = -6262522980943839741L;

	private EShipType orderedShipType = null;
	private Movable ship = null;
	private DockPosition dockPosition = null;

	public DockyardBuilding(Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(EBuildingType.DOCKYARD, player, position, buildingsGrid);
	}

	protected List<? extends IRequestStack> createWorkStacks() {
		if (orderedShipType == null) {
			return Collections.emptyList();
		}

		List<RequestStack> newStacks = new LinkedList<>();

		for (RelativeStack stack : type.getRequestStacks()) {
			short requiredAmount = orderedShipType.getRequiredMaterial(stack.getMaterialType());
			if (requiredAmount > 0) {
				newStacks.add(new RequestStack(grid.getRequestStackGrid(), stack.calculatePoint(this.pos), stack.getMaterialType(), type, getPriority(), requiredAmount));
			}
		}

		return newStacks;
	}

	public void buildShipAction() {
		if (this.orderedShipType == null) {
			return;
		}

		if (this.ship == null) {
			ShortPoint2D position = this.dockPosition.getDirection().getNextHexPoint(this.dockPosition.getPosition(), 5);
			// push old ship
			this.ship = (Movable) super.grid.getMovableGrid().getMovableAt(position.x, position.y);
			if (this.ship != null) {
				this.ship.leavePosition();
			}
			// make new ship
			this.ship = new Movable(super.grid.getMovableGrid(), this.orderedShipType.movableType, position, super.getPlayer());
			EDirection direction = dockPosition.getDirection().getNeighbor(-1);
			this.ship.setDirection(direction);
		}

		this.ship.increaseStateProgress((float) (1. / orderedShipType.buildingSteps));

		if (this.ship.getStateProgress() >= .99) {
			this.ship = null;
			this.orderedShipType = null;
		}
	}

	public void setDock(ShortPoint2D requestedDockPosition) {
		if (orderedShipType != null) {
			return;
		}

		DockPosition newDockPosition = findValidDockPosition(requestedDockPosition);
		if (newDockPosition == null) {
			return;
		}

		if (dockPosition != null) { // remove old dock
			grid.setDock(dockPosition, false, this.getPlayer());
		}
		dockPosition = newDockPosition;
		grid.setDock(dockPosition, true, this.getPlayer());
	}

	@Override
	public boolean canDockBePlaced(ShortPoint2D requestedDockPosition) {
		return orderedShipType == null && findValidDockPosition(requestedDockPosition) != null;
	}

	private DockPosition findValidDockPosition(ShortPoint2D requestedDockPosition) {
		return grid.findValidDockPosition(requestedDockPosition, pos, IDockBuilding.MAXIMUM_DOCKYARD_DISTANCE);
	}

	public DockPosition getDock() {
		return this.dockPosition;
	}

	public void removeDock() {
		if (this.dockPosition == null) {
			return;
		}
		this.grid.setDock(this.dockPosition, false, this.getPlayer());
		this.dockPosition = null;
	}

	public void orderShipType(EShipType shipType) {
		if (orderedShipType != null || dockPosition == null || !isOccupied()) {
			return;
		}

		this.orderedShipType = shipType;
		initWorkStacks();
	}

	@Override
	public EShipType getOrderedShipType() { // TODO use EShipType outside of this class
		return orderedShipType;
	}
}
