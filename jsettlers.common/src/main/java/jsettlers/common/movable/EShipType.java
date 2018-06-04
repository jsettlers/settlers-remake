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

package jsettlers.common.movable;

import jsettlers.common.mapobject.EMapObjectType;
import jsettlers.common.material.EMaterialType;

/**
 * Created by Andreas Eberle on 26.07.2017.
 */
public enum EShipType {
	FERRY(EMovableType.FERRY, 4, 1, EMapObjectType.FERRY),
	CARGO_SHIP(EMovableType.CARGO_SHIP, 6, 1, EMapObjectType.CARGO_SHIP);

	public static final EShipType[] VALUES = values();
	private static final int BUILD_STEPS_PER_MATERIAL = 6;

	public final EMovableType movableType;
	public final short requiredPlanks;
	public final short requiredIron;
	public final EMapObjectType mapObjectType;
	public final int buildingSteps;

	EShipType(EMovableType movableType, int requiredPlanks, int requiredIron, EMapObjectType mapObjectType) {
		this.movableType = movableType;
		this.requiredPlanks = (short) requiredPlanks;
		this.requiredIron = (short) requiredIron;
		this.mapObjectType = mapObjectType;
		this.buildingSteps = (requiredIron + requiredPlanks) * BUILD_STEPS_PER_MATERIAL;
	}

	public EShipType get(EMovableType movableType) {
		switch (movableType) {
		case FERRY:
			return FERRY;
		case CARGO_SHIP:
			return CARGO_SHIP;
		default:
			throw new IllegalArgumentException("MovableType is no ship: " + movableType);
		}
	}

	public short getRequiredMaterial(EMaterialType materialType) {
		switch (materialType) {
		case PLANK:
			return requiredPlanks;
		case IRON:
			return requiredIron;
		default:
			return 0;
		}
	}
}
