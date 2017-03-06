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
package jsettlers.logic.buildings.stack.multi;

import jsettlers.common.material.EMaterialType;

import java.io.Serializable;
import java.util.Arrays;

public class MultiMaterialRequestSettings implements IMultiMaterialRequestSettings, Serializable {
	private final short[] requestedMaterials;

	public MultiMaterialRequestSettings(short[] requestedMaterials) {
		if (requestedMaterials.length != EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS) {
			throw new IllegalArgumentException("requestedMaterials has the wrong length" + Arrays.toString(requestedMaterials));
		}
		this.requestedMaterials = requestedMaterials;
	}

	public MultiMaterialRequestSettings() {
		this(new short[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS]);
	}

	@Override
	public short getRequestedAmount(EMaterialType materialType) {
		return requestedMaterials[materialType.ordinal];
	}

	public void setRequestedAmount(EMaterialType materialType, short requestedAmount) {
		requestedMaterials[materialType.ordinal] = requestedAmount;
	}

	@Override
	public void updateRequested(EMaterialType materialType, int delta) {
		short currentlyRequested = getRequestedAmount(materialType);
		if (currentlyRequested != UNLIMITED_REQUESTS_MAGIC_NUMBER) {
			requestedMaterials[materialType.ordinal] = (short) Math.min(UNLIMITED_REQUESTS_MAGIC_NUMBER, Math.max(0, currentlyRequested + delta));
		}
	}
}
