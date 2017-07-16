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
import java.util.LinkedList;
import java.util.List;

import static java8.util.stream.StreamSupport.stream;

public class StockSettings implements IMultiMaterialRequestSettings, IStockSettings, Serializable, IStockSettingsListener {

	private static final byte ACCEPT_STATE_USE_DEFAULT = 0;
	private static final byte ACCEPT_STATE_ACCEPT = 1;
	private static final byte ACCEPT_STATE_REJECT = 2;

	private final StockSettings defaultSettings;
	private final byte[] acceptedMaterials = new byte[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
	private final List<IStockSettingsListener> listeners = new LinkedList<>();

	public StockSettings(StockSettings defaultSettings) {
		this.defaultSettings = defaultSettings;
		defaultSettings.registerStockSettingsListener(this);
	}

	public StockSettings(boolean[] settings) {
		this.defaultSettings = null;
		for (int i = 0; i < settings.length; i++) {
			acceptedMaterials[i] = settings[i] ? ACCEPT_STATE_ACCEPT : ACCEPT_STATE_REJECT;
		}
	}

	public void releaseSettings() {
		if (defaultSettings != null) {
			defaultSettings.unregisterStockSettingsListener(this);
		}
	}

	public boolean isRequested(EMaterialType materialType) {
		return getRequestedAmount(materialType) > 0;
	}

	@Override
	public short getRequestedAmount(EMaterialType materialType) {
		byte localRequested = acceptedMaterials[materialType.ordinal];

		if (localRequested != ACCEPT_STATE_USE_DEFAULT) {
			return localRequested == ACCEPT_STATE_ACCEPT ? Short.MAX_VALUE : 0;
		} else {
			return defaultSettings.getRequestedAmount(materialType);
		}
	}

	@Override
	public void updateRequested(EMaterialType materialType, int delta) {
	}

	public void setAccepted(EMaterialType materialType, boolean accept) {
		boolean previouslyAccepted = isAccepted(materialType);

		if (defaultSettings != null && defaultSettings.isAccepted(materialType) == accept) {
			acceptedMaterials[materialType.ordinal] = ACCEPT_STATE_USE_DEFAULT;
		} else {
			acceptedMaterials[materialType.ordinal] = accept ? ACCEPT_STATE_ACCEPT : ACCEPT_STATE_REJECT;
		}

		if (accept != previouslyAccepted) { // only call listeners when it changed
			notifyListeners(materialType, accept);
		}
	}

	@Override
	public boolean isAccepted(EMaterialType materialType) {
		return isRequested(materialType);
	}

	public void registerStockSettingsListener(IStockSettingsListener listener) {
		listeners.add(listener);
	}

	public void unregisterStockSettingsListener(IStockSettingsListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void stockSettingChanged(EMaterialType materialType, boolean accepted) {
		if (this.acceptedMaterials[materialType.ordinal] == ACCEPT_STATE_USE_DEFAULT) {
			notifyListeners(materialType, accepted);
		}
	}

	private void notifyListeners(EMaterialType materialType, boolean accept) {
		stream(listeners).forEach(listener -> listener.stockSettingChanged(materialType, accept));
	}
}
