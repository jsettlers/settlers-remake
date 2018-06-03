/*
 * Copyright (c) 2018
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
package jsettlers.logic.movable.strategies.military;

import java.util.ArrayList;

import jsettlers.common.map.shapes.HexGridArea;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.constants.Constants;
import jsettlers.logic.movable.Movable;
import jsettlers.logic.movable.MovableStrategy;
import jsettlers.logic.movable.interfaces.AbstractMovableGrid;
import jsettlers.logic.movable.interfaces.ILogicMovable;

import static java8.util.stream.StreamSupport.stream;

public class FerryStrategy extends MovableStrategy {
	private static final int MAX_NUMBER_OF_PASSENGERS = 7;

	private final ArrayList<ILogicMovable> passengers = new ArrayList<>(MAX_NUMBER_OF_PASSENGERS);

	public FerryStrategy(Movable movable) {
		super(movable);
	}

	@Override
	protected boolean canBeControlledByPlayer() {
		return true;
	}

	@Override
	public boolean addPassenger(ILogicMovable movable) {
		if (passengers.size() < MAX_NUMBER_OF_PASSENGERS) {
			this.passengers.add(movable);
			return true;
		}
		return false;
	}

	@Override
	public ArrayList<ILogicMovable> getPassengers() {
		return passengers;
	}

	@Override
	protected void unloadFerry() {
		if (passengers.isEmpty()) {
			return;
		}

		ShortPoint2D position = super.getPosition();
		AbstractMovableGrid grid = super.getGrid();

		HexGridArea.stream(position.x, position.y, 2, Constants.MAX_FERRY_UNLOADING_RADIUS)
				   .filterBounds(grid.getWidth(), grid.getHeight())
				   .filter((x, y) -> !grid.isWater(x, y))
				   .iterate((x, y) -> {
					   ILogicMovable passenger = passengers.get(passengers.size() - 1);

					   if (grid.isValidPosition(passenger, x, y) && grid.isFreePosition(x,y)) {
						   passenger.leaveFerryAt(new ShortPoint2D(x, y));
						   passengers.remove(passengers.size() - 1);
					   }

					   return !passengers.isEmpty();
				   });
	}

	@Override
	protected void strategyKilledEvent(ShortPoint2D pathTarget) {
		stream(passengers).forEach(ILogicMovable::kill);
		super.strategyKilledEvent(pathTarget);
	}
}
