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
package jsettlers.common.map.partition;

import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.common.material.EMaterialType;

/**
 * This interface gives access to the settings of a partition.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IPartitionSettings {

	/**
	 * This method gives access to the material distribution settings of the partition.
	 * 
	 * @param materialType
	 * @return Returns the distribution settings for the given {@link EMaterialType}.
	 */
	IMaterialsDistributionSettings getDistributionSettings(EMaterialType materialType);

	IMaterialProductionSettings getMaterialProductionSettings();

	/**
	 * This method gives the {@link EMaterialType} for the given priority index.
	 * 
	 * @param priorityIdx
	 *            The priority for which to return the {@link EMaterialType}.<br>
	 *            The priority must be in the interval [0, {@link EMaterialType}.NUMBER_OF_DROPPABLE_MATERIALS-1] where 0 is the highest priority.
	 * @return Returns the {@link EMaterialType} with the given priority.
	 */
	EMaterialType getMaterialTypeForPrio(int priorityIdx);

	/**
	 * Checks if stock buildings accept this material in this partition.
	 * 
	 * @param material
	 *            The material.
	 * @return <code>true</code> if they accept this material.
	 */
	boolean isAcceptByStocks(EMaterialType material);

	void setAcceptedStockMaterial(EMaterialType materialType, boolean accepted);
}
