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

package jsettlers.logic.buildings.military.occupying;

import jsettlers.common.buildings.IBuildingOccupier;
import jsettlers.common.buildings.OccupierPlace;
import jsettlers.common.movable.IMovable;
import jsettlers.logic.buildings.military.IBuildingOccupyableMovable;

import java.io.Serializable;

/**
 * Created by Andreas Eberle on 03.07.2017.
 */
final class TowerOccupier implements IBuildingOccupier, Serializable {
	final OccupierPlace place;
	final IBuildingOccupyableMovable soldier;

	TowerOccupier(OccupierPlace place, IBuildingOccupyableMovable soldier) {
		this.place = place;
		this.soldier = soldier;
	}

	@Override
	public OccupierPlace getPlace() {
		return place;
	}

	@Override
	public IMovable getMovable() {
		return soldier.getMovable();
	}

	public IBuildingOccupyableMovable getSoldier() {
		return soldier;
	}
}
