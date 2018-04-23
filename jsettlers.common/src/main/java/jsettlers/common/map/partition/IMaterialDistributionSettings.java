/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;

/**
 * Interface to supply distribution information. Instances of this interface are used to get the distribution probabilities for the buildings that can receive a specific {@link EMaterialType}.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IMaterialDistributionSettings {

	/**
	 * This returns the configured ratio of the material which means how much the material bar is filled.
	 */
	float getUserConfiguredDistributionValue(EBuildingType buildingType);

	/**
	 * This returns the resulting ratio of the material in relation to the other weapons. E.g. when you have 100% swords, 100% bows and 100% spears this would return 0.33f for bows.
	 */
	float getDistributionProbability(EBuildingType buildingType);

	/**
	 * 
	 * @return Returns the {@link EMaterialType} this settings are used for.
	 */
	EMaterialType getMaterialType();
}
