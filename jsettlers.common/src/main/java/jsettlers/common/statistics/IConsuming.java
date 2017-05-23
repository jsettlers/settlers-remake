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
package jsettlers.common.statistics;

import jsettlers.common.material.EMaterialType;

/**
 * This is a job that consumes materials.
 * 
 * @author michael
 */
public interface IConsuming {
	/**
	 * Gets the thing the material is consumed for. If this method returns {@link EConsumingType#WORKING_BUILDING}, you can safely cast this object to
	 * {@link IConsumingBuildingType}
	 * 
	 * @return The type
	 */
	EConsumingType getConsumingType();

	/**
	 * Gets the type of material that is consumed.
	 * 
	 * @return The material
	 */
	EMaterialType getMaterialType();

	/**
	 * Sets the priority that should be used when deciding to which consumers the material is brought. If the priority is out of range, it is clamped
	 * silently.
	 * 
	 * @param priority
	 *            The priority to use. 0 means no goods, 1 means highest priority.
	 */
	void setPriority(float priority);

	/**
	 * Gets the priority used for us,
	 * 
	 * @return The priority, in range 0..1.
	 */
	float getPriority();
}
