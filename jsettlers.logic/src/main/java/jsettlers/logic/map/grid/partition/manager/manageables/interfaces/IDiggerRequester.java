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
package jsettlers.logic.map.grid.partition.manager.manageables.interfaces;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ILocatable;

/**
 * Interface for buildings requesting a digger.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IDiggerRequester extends ILocatable {
	/**
	 * 
	 * @return {@link EBuildingType} of the requesting building.
	 */
	EBuildingType getBuildingType();

	/**
	 * Indicates if the request from this requester is still active or has been canceled.
	 * 
	 * @return true if the request is still active<br>
	 *         false if the request has been canceled.
	 */
	boolean isDiggerRequestActive();

	/**
	 * 
	 * @return Gets the average height the buildings should be. The diggers need to flat the area to fit this height.
	 */
	byte getAverageHeight();

	void diggerRequestFailed();
}
