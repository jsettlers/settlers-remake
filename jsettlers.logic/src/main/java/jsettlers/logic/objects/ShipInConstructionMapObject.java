/*
 * Copyright (c) 2017
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

package jsettlers.logic.objects;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.movable.EDirection;
import jsettlers.common.movable.EShipType;
import jsettlers.common.movable.IShipInConstruction;
import jsettlers.logic.map.grid.objects.AbstractHexMapObject;

public class ShipInConstructionMapObject extends AbstractHexMapObject implements IShipInConstruction{
	private final EShipType shipType;
	private final EDirection direction;
	private int works = 0;

	public ShipInConstructionMapObject(EShipType shipType, EDirection direction) {
		this.shipType = shipType;
		this.direction = direction;
	}

	@Override
	public EMapObjectType getObjectType() {
		return shipType.mapObjectType;
	}

	@Override
	public float getStateProgress() {
		return works / (float) shipType.buildingSteps;
	}

	public void workOnShip() {
		works++;
	}

	@Override
	public boolean cutOff() {
		return false;
	}

	@Override
	public boolean canBeCut() {
		return false;
	}

	public EDirection getDirection() {
		return direction;
	}

	public boolean isFinished() {
		return works >= shipType.buildingSteps;
	}
}
