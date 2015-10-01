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
package jsettlers.graphics.map.controls.original.panel.selection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.buildings.IBuildingOccupyer;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.IMovable;

/**
 * This class saves the state parts of the building that is displayed by the gui, to detect changes
 * 
 * @author michael
 */
public class BuildingState {

	private final EPriority priority;
	private final EPriority[] supportedPriorities;
	private final ArrayList<StackState> stackStates = new ArrayList<>();
	private boolean construction;

	/**
	 * An array: soldier class -> available places.
	 */
	private final Hashtable<ESoldierClass, ArrayList<OccupierState>> occupierStates;

	public static class StackState {
		public final EMaterialType type;
		public final int count;
		public final boolean offering;

		public StackState(IBuildingMaterial mat) {
			type = mat.getMaterialType();
			count = mat.getMaterialCount();
			offering = mat.isOffering();
		}

		public boolean isStillInState(IBuildingMaterial mat) {
			return mat.getMaterialType() == type && mat.getMaterialCount() == count && mat.isOffering() == offering;
		}

	}

	public static class OccupierState {
		private final IMovable movable;
		private final boolean comming;

		private OccupierState(IMovable movable) {
			this.movable = movable;
			comming = false;
		}

		private OccupierState(boolean comming) {
			this.comming = comming;
			movable = null;
		}

		public boolean isComming() {
			return comming;
		}

		public boolean isMissing() {
			return movable == null && !isComming();
		}

		public IMovable getMovable() {
			return movable;
		}
	}

	/**
	 * Saves the current state of the building
	 * 
	 * @param building
	 *            the building
	 */
	public BuildingState(IBuilding building) {
		priority = building.getPriority();
		supportedPriorities = building.getSupportedPriorities();
		construction = building.getStateProgress() < 1;
		Hashtable<ESoldierClass, ArrayList<OccupierState>> occupierStates = computeOccupierStates(building);
		this.occupierStates = occupierStates;
		for (IBuildingMaterial mat : building.getMaterials()) {
			stackStates.add(new StackState(mat));
		}
	}

	private Hashtable<ESoldierClass, ArrayList<OccupierState>> computeOccupierStates(IBuilding building) {
		Hashtable<ESoldierClass, ArrayList<OccupierState>> occupierStates = null;
		if (building instanceof IBuilding.IOccupyed && building.getStateProgress() >= 1) {
			IBuilding.IOccupyed occupyed = (IBuilding.IOccupyed) building;
			occupierStates = new Hashtable<ESoldierClass, ArrayList<OccupierState>>();
			for (ESoldierClass soldierClass : ESoldierClass.values) {
				occupierStates.put(soldierClass, new ArrayList<OccupierState>());
			}

			for (IBuildingOccupyer o : occupyed.getOccupyers()) {
				ESoldierClass soldierClass = o.getPlace().getSoldierClass();
				OccupierState state = new OccupierState(o.getMovable());
				occupierStates.get(soldierClass).add(state);
			}

			for (ESoldierClass soldierClass : ESoldierClass.values) {
				ArrayList<OccupierState> list = occupierStates.get(soldierClass);
				int comming = occupyed.getCurrentlyCommingSoldiers(soldierClass);
				while (list.size() < comming) {
					list.add(new OccupierState(true));
				}
				int requested = occupyed.getMaximumRequestedSoldiers(soldierClass);
				while (list.size() < requested) {
					list.add(new OccupierState(false));
				}
			}
		}
		return occupierStates;
	}

	public EPriority[] getSupportedPriorities() {
		return supportedPriorities;
	}

	public boolean isConstruction() {
		return construction;
	}

	public ArrayList<StackState> getStackStates() {
		return stackStates;
	}

	public boolean isStillInState(IBuilding building) {
		return building.getPriority() == priority
				&& Arrays.equals(supportedPriorities,
						building.getSupportedPriorities())
				&& construction == (building.getStateProgress() < 1)
				&& hasSameStacks(building)
				&& hasSameOccupiers(building);
	}

	private boolean hasSameOccupiers(IBuilding building) {
		Hashtable<ESoldierClass, ArrayList<OccupierState>> states = computeOccupierStates(building);
		if (states == null) {
			return occupierStates == null;
		} else if (occupierStates == null) {
			return false;
		} else {
			return occupierStates.equals(states);
		}
	}

	private boolean hasSameStacks(IBuilding building) {
		List<IBuildingMaterial> materials = building.getMaterials();
		if (materials.size() != stackStates.size()) {
			return false;
		}
		int i = 0;
		for (IBuildingMaterial mat : materials) {
			if (stackStates.get(i++).isStillInState(mat)) {
				return false;
			}
		}
		return true;
	}

	public List<OccupierState> getOccupiers(ESoldierClass soldierClass) {
		return Collections.unmodifiableList(occupierStates.get(soldierClass));
	}
}
