/*******************************************************************************
 * Copyright (c) 2017 - 2018
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.logic.movable.interfaces;

import java.io.Serializable;

import jsettlers.algorithms.fogofwar.IViewDistancable;
import jsettlers.algorithms.path.IPathCalculatable;
import jsettlers.algorithms.path.Path;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.input.IGuiMovable;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;
import jsettlers.logic.buildings.military.occupying.IOccupyableBuilding;
import jsettlers.logic.player.Player;
import jsettlers.logic.timer.IScheduledTimerable;

public interface ILogicMovable extends IScheduledTimerable, IPathCalculatable, IDebugable, Serializable, IViewDistancable, IGuiMovable, IAttackableMovable {
	boolean push(ILogicMovable pushingMovable);

	Path getPath();

	void goSinglePathStep();

	ShortPoint2D getPosition();

	ILogicMovable getPushedFrom();

	boolean isProbablyPushable(ILogicMovable pushingMovable);

	void leavePosition();

	boolean canOccupyBuilding();

	void checkPlayerOfPosition(Player playerOfPosition);

	void convertTo(EMovableType newMovableType);

	Player getPlayer();

	IBuildingOccupyableMovable setOccupyableBuilding(IOccupyableBuilding building);

	void moveTo(ShortPoint2D targetPosition);

	void unloadFerry();

	boolean addPassenger(ILogicMovable movable);

	void moveToFerry(ILogicMovable ferry, ShortPoint2D entrancePosition);

	void leaveFerryAt(ShortPoint2D position);
}
