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
package jsettlers.graphics.map.controls.original.panel.content;

import java.util.Arrays;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.material.EPriority;

/**
 * This class saves the state parts of the building that is displayed by the gui, to detect changes
 * 
 * @author michael
 */
public class BuildingState {

	private final EPriority priority;
	private final EPriority[] supportedPriorities;

	/**
	 * Saves the current state of the building
	 * 
	 * @param building
	 *            the building
	 */
	public BuildingState(IBuilding building) {
		priority = building.getPriority();
		supportedPriorities = building.getSupportedPriorities();
		if (building instanceof IBuilding.IOccupyed) {
			IBuilding.IOccupyed occupyed = (IBuilding.IOccupyed) building;
			// TODO: use this to store how many people are occupying the
			// building.
		}
	}

	public boolean isStillInState(IBuilding building) {
		return building.getPriority() == priority
				&& Arrays.equals(supportedPriorities,
						building.getSupportedPriorities());
	}

}
