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

import jsettlers.common.buildings.OccupierPlace;
import jsettlers.common.material.ESearchType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.ESoldierType;

import java.io.Serializable;

/**
 * Created by Andreas Eberle on 03.07.2017.
 */
class SoldierRequest implements Serializable {
	final ESoldierClass soldierClass;
	final ESoldierType soldierType;
	final OccupierPlace place;

	SoldierRequest(ESoldierType soldierType, OccupierPlace place) {
		this.soldierType = soldierType;
		soldierClass = null;
		this.place = place;
	}

	SoldierRequest(ESoldierClass soldierClass, OccupierPlace place) {
		this.soldierClass = soldierClass;
		soldierType = null;
		this.place = place;
	}

	ESearchType getSearchType() {
		if (soldierClass != null) {
			switch (soldierClass) {
			case INFANTRY:
				return ESearchType.SOLDIER_INFANTRY;
			case BOWMAN:
				return ESearchType.SOLDIER_BOWMAN;
			}
		} else {
			switch (soldierType) {
			case SWORDSMAN:
				return ESearchType.SOLDIER_SWORDSMAN;
			case PIKEMAN:
				return ESearchType.SOLDIER_PIKEMAN;
			case BOWMAN:
				return ESearchType.SOLDIER_BOWMAN;
			}
		}
		throw new RuntimeException("Unknown soldier or search type");
	}

	boolean isOfTypeOrClass(ESoldierType soldierType) {
		return this.soldierType == soldierType || soldierClass == soldierType.soldierClass;
	}

	boolean isOfTypeOrClass(ESoldierClass soldierClass) {
		return this.soldierClass == soldierClass || (this.soldierType != null && this.soldierType.soldierClass == soldierClass);
	}
}
