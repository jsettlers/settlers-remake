/*******************************************************************************
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
 *******************************************************************************/
package jsettlers.logic.map.grid.partition.data;

import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.grid.partition.manager.materials.offers.IOffersCountListener;

import java.io.Serializable;

public class MaterialCounts implements IOffersCountListener, Serializable {
	private final int[] materialCounts = new int[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
	private final IOffersCountListener listener;

	public MaterialCounts() {
		this.listener = null;
	}

	public MaterialCounts(IOffersCountListener listener) {
		this.listener = listener;
	}

	public int getAmountOf(EMaterialType materialType) {
		byte materialTypeIndex = materialType.ordinal;
		if (materialTypeIndex < materialCounts.length) {
			return materialCounts[materialTypeIndex];
		} else {
			return 0;
		}
	}

	@Override
	public void offersCountChanged(EMaterialType materialType, int delta) {
		byte materialTypeIndex = materialType.ordinal;

		if (materialTypeIndex < materialCounts.length) {
			materialCounts[materialTypeIndex] += delta;
			if (materialCounts[materialTypeIndex] < 0) {
				System.err.println("Sanity check: material count cannot be negative!");
			}
		}

		if (listener != null) {
			listener.offersCountChanged(materialType, delta);
		}
	}
}
