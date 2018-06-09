/*
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
 */
package jsettlers.logic.buildings.military.occupying;

import jsettlers.common.buildings.OccupierPlace;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.movable.interfaces.ILogicMovable;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.player.Player;

/**
 * This interface defines the methods needed by a tower that it can request soldiers to get in.
 * 
 * @author Andreas Eberle
 * 
 */
public interface IOccupyableBuilding extends ILocatable{

	/**
	 * 
	 * @param soldier
	 * @return
	 */
	OccupierPlace addSoldier(IBuildingOccupyableMovable soldier);

	ShortPoint2D getDoor();

	void requestFailed(IBuildingOccupyableMovable soldier);

	ShortPoint2D getPosition(IBuildingOccupyableMovable soldier);

	boolean isDestroyed();

	/**
	 * This method is called by the soldier when he finished defending the tower.
	 * 
	 * @param soldier
	 *            The soldier that defended the tower.
	 */
	void towerDefended(IBuildingOccupyableMovable soldier);

	ShortPoint2D getTowerBowmanSearchPosition(OccupierPlace place);

	/**
	 * 
	 * @return The player of this building object.
	 */
	Player getPlayer();

	/**
	 * Removes the given soldier from this building.
	 * 
	 * @param soldier
	 *            The soldier that will be removed.
	 */
	void removeSoldier(IBuildingOccupyableMovable soldier);

	/**
	 * Request this soldier to enter the tower
	 * @param soldier
	 */
	void requestSoldier(ILogicMovable soldier);
}
