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

import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

/**
 * Interface for a building that can request beares to go there and become soldiers.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IBarrack extends ILocatable {

	/**
	 * 
	 * @return position of the door.
	 */
	ShortPoint2D getDoor();

	/**
	 * Get a weapon from the barrack for this bearer.
	 * 
	 * @return The movable type this bearer should convert to.
	 */
	EMovableType popWeaponForBearer();

	/**
	 * This method is called when a movable wasn't able to become a soldier, that means when it wasn't able to fullfil the request.
	 */
	void bearerRequestFailed();

	/**
	 * 
	 * @return Returns the position where the newly created soldier should walk.
	 */
	ShortPoint2D getSoldierTargetPosition();
}
