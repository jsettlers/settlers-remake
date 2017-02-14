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

import jsettlers.common.map.partition.IStockSettings;
import jsettlers.common.material.EMaterialType;

import java.io.Serializable;
import java.util.Arrays;

public class MaterialRequestSettings implements IStockSettings, Serializable {

	private static final short UNLIMITTED_REQUESTS_MAGIC_NUMBER = Short.MAX_VALUE;

	private final MaterialRequestSettings defaultSettings;
	private final short[] requestedMaterials;

	public MaterialRequestSettings(short[] requestedMaterials) {
		if (requestedMaterials.length != EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS) {
			throw new IllegalArgumentException("requestedMaterials has the wrong length" + Arrays.toString(requestedMaterials));
		}
		this.requestedMaterials = requestedMaterials;
		this.defaultSettings = null;
	}

	public MaterialRequestSettings(MaterialRequestSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
		this.requestedMaterials = new short[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
		Arrays.fill(this.requestedMaterials, (short) -1);
	}

	public MaterialRequestSettings() {
		this(new short[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS]);
	}

	public boolean isRequested(EMaterialType materialType) {
		return getRequestedAmount(materialType) > 0;
	}

	public short getRequestedAmount(EMaterialType materialType) {
		short localRequested = requestedMaterials[materialType.ordinal];
		return localRequested >= 0 ? localRequested : defaultSettings != null ? defaultSettings.getRequestedAmount(materialType) : (short) 0;
	}

	public void setRequestedAmount(EMaterialType materialType, short acceptedAmount) {
		requestedMaterials[materialType.ordinal] = acceptedAmount;
	}

	public void setRequested(EMaterialType materialType, boolean accepted) {
		setRequestedAmount(materialType, accepted ? UNLIMITTED_REQUESTS_MAGIC_NUMBER : 0);
	}

	@Override
	public boolean isAccepted(EMaterialType materialType) {
		return isRequested(materialType);
	}

	void changeRequested(EMaterialType materialType, int change) {
		short currentlyRequested = getRequestedAmount(materialType);
		if (currentlyRequested != UNLIMITTED_REQUESTS_MAGIC_NUMBER) {
			requestedMaterials[materialType.ordinal] = (short) Math.min(UNLIMITTED_REQUESTS_MAGIC_NUMBER, Math.max(0, currentlyRequested + change));
		}
	}
}
