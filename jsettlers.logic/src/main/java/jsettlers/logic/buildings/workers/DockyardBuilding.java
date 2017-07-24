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

import java.util.ArrayList;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.DockPosition;
import jsettlers.logic.buildings.BuildingMaterial;
import jsettlers.logic.buildings.IBuildingsGrid;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.player.Player;

/**
 * An extension to the worker building for dockyards
 */
public class DockyardBuilding extends WorkerBuilding {
	private static final long serialVersionUID = -6262522980943839741L;
	
	private EMaterialType[] order = null;
	private int orderPointer = 0;
	private EMovableType orderedShipType = null;
	private int shipBuildingSteps = 1;
	private Movable ship = null;
	private DockPosition dockPosition = null;

	public DockyardBuilding(EBuildingType type, Player player, ShortPoint2D position, IBuildingsGrid buildingsGrid) {
		super(type, player, position, buildingsGrid);
	}

	@Override
	public EMaterialType getOrderedMaterial() {
		if (this.order != null && this.orderPointer < this.order.length) {
			return this.order[this.orderPointer];
		}
		return null;
	}

	private void setOrder(EMaterialType[] list, EMovableType type) {
		if (list == null) {
			return;
		}
		this.order = list;
		this.orderPointer = 0;
		this.orderedShipType = type;
		this.shipBuildingSteps = 6 * list.length;
	}

	public ArrayList<EMaterialType> getRemainingOrder() {
		if (order == null) {
			return null;
		}
		ArrayList<EMaterialType> list = new ArrayList<>();
		for (int i = this.orderPointer; i < this.order.length; i++) {
			list.add(this.order[i]);
		}
		return list;
	}
	
	@Override
	public List<IBuildingMaterial> getMaterials() {
		if (isConstructionFinished()) {
			ArrayList<IBuildingMaterial> materials = new ArrayList<>();
			ArrayList<EMaterialType> remaining = getRemainingOrder();
			if (remaining != null) {
				// TODO: Dirty and costly hack for now, make it nice.
				int[] counts = new int[EMaterialType.NUMBER_OF_MATERIALS];
				for (EMaterialType t : remaining) {
					counts[t.ordinal()]++;
				}
				for (EMaterialType t : EMaterialType.VALUES) {
					if (counts[t.ordinal()] > 0) {
						materials.add(new BuildingMaterial(t, counts[t.ordinal()]));
					}
				}
			}
			return materials;
		} else {
			return super.getMaterials();
		}
	}

	@Override
	public void reduceOrder() {
		this.orderPointer++;
	}

	public void buildShipAction() {
		if (this.ship == null) {
			ShortPoint2D position = this.dockPosition.getDirection().getNextHexPoint(this.dockPosition.getPosition(), 5);
			// push old ship
			this.ship = (Movable) super.grid.getMovableGrid().getMovableAt(position.x, position.y);
			if (this.ship != null) {
				this.ship.leavePosition();
			}
			// make new ship
			this.ship = new Movable(super.grid.getMovableGrid(), this.orderedShipType,
					position, super.getPlayer());
			EDirection direction = dockPosition.getDirection().rotateRight(1);
			this.ship.setDirection(direction);
			this.ship.increaseStateProgress((float) (1./shipBuildingSteps));
		} else {
			this.ship.increaseStateProgress((float) (1./shipBuildingSteps));
			if (this.ship.getStateProgress() >= .99) {
				this.ship = null;
				this.order = null;
			}
		}
	}

	public boolean setDock(DockPosition dockPosition) {
		if (this.type != EBuildingType.DOCKYARD) {
			return false;
		}
		if (this.dockPosition != null) { // replace dock
			if (this.ship != null) {
				return false; // do not change the dock when a ship is tied to it
			}
			this.grid.setDock(this.dockPosition, false, this.getPlayer());
		}
		this.dockPosition = dockPosition;
		this.grid.setDock(dockPosition, true, this.getPlayer());
		return true;
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

	public void orderFerry() {
		if (getOrderedMaterial() != null) {
			return;
		}
		EMaterialType[] material = new EMaterialType[] {
				EMaterialType.PLANK,
				EMaterialType.PLANK,
				EMaterialType.PLANK,
				EMaterialType.PLANK,
				EMaterialType.IRON};
		setOrder(material, EMovableType.FERRY);
	}

	public void orderCargoBoat() {
		if (getOrderedMaterial() != null) {
			return;
		}
		EMaterialType[] material = new EMaterialType[] {
				EMaterialType.PLANK,
				EMaterialType.PLANK,
				EMaterialType.PLANK,
				EMaterialType.PLANK,
				EMaterialType.PLANK,
				EMaterialType.PLANK,
				EMaterialType.IRON};
		setOrder(material, EMovableType.CARGO_BOAT);
	}
}
