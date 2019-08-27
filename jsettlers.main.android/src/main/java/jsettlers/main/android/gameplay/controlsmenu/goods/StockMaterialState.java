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

package jsettlers.main.android.gameplay.controlsmenu.goods;

/**
 * Created by Tom Pratt on 12/07/2017.
 */

import jsettlers.common.map.partition.IStockSettings;
import jsettlers.common.material.EMaterialType;

/**
 * Model for stock item
 */
public class StockMaterialState {
	private final EMaterialType materialType;
	private final boolean stocked;

	public StockMaterialState(EMaterialType materialType, IStockSettings stockSettings) {
		this.materialType = materialType;
		this.stocked = stockSettings.isAccepted(materialType);
	}

	public EMaterialType getMaterialType() {
		return materialType;
	}

	public boolean isStocked() {
		return stocked;
	}
}