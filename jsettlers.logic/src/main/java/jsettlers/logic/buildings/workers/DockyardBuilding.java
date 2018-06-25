/*
 * Copyright (c) 2017 - 2018
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
 */
package jsettlers.logic.buildings.workers;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.stacks.RelativeStack;
import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EShipType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.utils.coordinates.CoordinateStream;
import jsettlers.logic.DockPosition;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.buildings.IDockBuilding;
import jsettlers.logic.buildings.stack.IRequestStack;
import jsettlers.logic.buildings.stack.RequestStack;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.objects.ShipInConstructionMapObject;
import jsettlers.logic.player.Player;

/**
 * An extension to the worker building for dockyards
 */
public class DockyardBuilding extends WorkerBuilding implements IBuilding.IShipConstruction, IDockBuilding {
	private static final long serialVersionUID = -2206596924857431982L;

	private static final int SHIP_PUSH_DISTANCE = 10;

	private EShipType                   orderedShipType = null;
	private ShipInConstructionMapObject ship            = null;
	private DockPosition                dockPosition    = null;

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
		if (orderedShipType == null || isDestroyed()) {
			return;
		}

		if (ship == null) {
			// make new ship
			EDirection direction = dockPosition.direction.getNeighbor(-1);
			ship = new ShipInConstructionMapObject(orderedShipType, direction);
			grid.getMapObjectsManager().addMapObject(getShipPosition(), ship);
		}

		ship.workOnShip();

		if (ship.isFinished()) { // replace ShipInConstructionMapObject with Movable
			Movable shipMovable = new Movable(super.grid.getMovableGrid(), orderedShipType.movableType, getShipPosition(), super.getPlayer());
			shipMovable.setDirection(ship.getDirection());
			removeShipInConstructionMapObject();


			ship = null;
			orderedShipType = null;
		}
	}

	private ShortPoint2D getShipPosition() {
		return dockPosition.direction.getNextHexPoint(dockPosition.coastPosition, 5);
	}

	public void setDock(ShortPoint2D requestedDockPosition) {
		if (orderedShipType != null) {
			return;
		}

		DockPosition newDockPosition = findValidDockPosition(requestedDockPosition);
		if (newDockPosition == null) {
			return;
		}

		removeDock();
		dockPosition = newDockPosition;
		grid.setDock(dockPosition, this.getPlayer());
		grid.setProtected(getShipProtectedArea(), true);
	}

	private CoordinateStream getShipProtectedArea() {
		return HexGridArea.stream(dockPosition.coastPosition.x, dockPosition.coastPosition.y, 0, SHIP_PUSH_DISTANCE)
						  .filterBounds(grid.getWidth(), grid.getHeight())
						  .filter(grid::isWater);
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

	@Override
	protected void killedEvent() {
		if (ship != null) {
			removeShipInConstructionMapObject();
		}
		removeDock();
		super.killedEvent();
	}

	private void removeDock() {
		if (this.dockPosition == null) {
			return;
		}

		grid.setProtected(getShipProtectedArea(), false);
		this.grid.removeDock(this.dockPosition);
		this.dockPosition = null;
	}

	private void removeShipInConstructionMapObject() {
		ShortPoint2D shipPosition = getShipPosition();
		grid.getMapObjectsManager().removeMapObjectType(shipPosition.x, shipPosition.y, orderedShipType.mapObjectType);
	}
}
