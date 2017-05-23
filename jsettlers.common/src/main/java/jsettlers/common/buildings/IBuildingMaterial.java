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
package jsettlers.common.buildings;

import jsettlers.common.material.EMaterialType;

/**
 * This is a material (stack) that is needed for a building.
 * 
 * @author michael
 */
public interface IBuildingMaterial {
	/**
	 * Gets the material type that this stack is for.
	 * 
	 * @return The type of material.
	 */
	EMaterialType getMaterialType();

	/**
	 * Gets the amount of material this building has or needs (in case this is a construction stack).
	 * 
	 * @return The number of material items on that given stack.
	 */
	int getMaterialCount();

	/**
	 * If the current stack is offered, so that people can take material from here.
	 * 
	 * @return <code>true</code> if it is an offering stack, <code>false</code> if it is an request stack.
	 */
	boolean isOffering();
}
