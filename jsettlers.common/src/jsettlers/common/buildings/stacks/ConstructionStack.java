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
package jsettlers.common.buildings.stacks;

import jsettlers.common.material.EMaterialType;

/**
 * This is a stack request that can be positioned relatively to a building.
 * 
 * @author michael
 */
public class ConstructionStack extends RelativeStack {
	private static final long serialVersionUID = -3592197606402226146L;

	private final short requiredForBuild;

	public ConstructionStack(int dx, int dy, EMaterialType type, short requiredForBuild) {
		super(dx, dy, type);
		this.requiredForBuild = requiredForBuild;
	}

	/**
	 * If this property is not zero, the specified amount of this material is needed to build the building. If it is 0, the material is needed after
	 * building the building.
	 * 
	 * @return the number of materials of this type required to construct a building.
	 */
	public short requiredForBuild() {
		return requiredForBuild;
	}
}
