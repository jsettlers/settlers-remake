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
package jsettlers.graphics.action;

import jsettlers.common.buildings.EBuildingType;

/**
 * This is a build action.
 * 
 * @author michael
 *
 */
public class BuildAction extends Action {

	private final EBuildingType building;

	/**
	 * Creates a new build action.
	 * 
	 * @param building
	 *            The building to be built.
	 */
	public BuildAction(EBuildingType building) {
		super(EActionType.BUILD);
		this.building = building;
	}

	/**
	 * gets the building that corresponds with this action, if the action is an build action.
	 * 
	 * @return The building, <code>null</code> if it is not a build action.
	 */
	public EBuildingType getBuilding() {
		return this.building;
	}

}
