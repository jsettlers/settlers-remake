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
package jsettlers.logic.buildings.military;

import jsettlers.common.movable.EMovableType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.movable.interfaces.IAttackable;
import jsettlers.logic.movable.interfaces.IAttackableMovable;

/**
 * 
 * @author Andreas Eberle
 * 
 */
public interface IBuildingOccupyableMovable {

	EMovableType getMovableType();

	ESoldierClass getSoldierClass();

	IAttackableMovable getMovable();

	void leaveOccupyableBuilding(ShortPoint2D newPosition);

	void setSelected(boolean selected);

	void informAboutAttackable(IAttackable attackable);

	/**
	 * Sets the position of the movable but does not display it.
	 * 
	 * @param door
	 */
	void setPosition(ShortPoint2D door);

	/**
	 * This method is called when this movable has to defend it's building at the given position.
	 * 
	 * @param pos
	 *            The position the defending movable is standing.
	 */
	void setDefendingAt(ShortPoint2D pos);

}
