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
import java.util.List;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;

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
		if (building instanceof IBuilding.IOccupyed) {
			IBuilding.IOccupyed occupyed = (IBuilding.IOccupyed) building;
			// TODO: use this to store how many people are occupying the
			// building.
		}

		for (IBuildingMaterial mat : building.getMaterials()) {
			stackStates.add(new StackState(mat));
		}
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
				&& hasSameStacks(building);
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

}
