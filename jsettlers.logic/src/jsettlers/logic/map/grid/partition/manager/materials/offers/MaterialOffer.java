/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.logic.map.grid.partition.manager.materials.offers;

import java.io.Serializable;

import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.materials.MaterialsManager;

/**
 * This class is used by {@link MaterialsManager} to store offers of materials.
 * 
 * @author Andreas Eberle
 * 
 */
public final class MaterialOffer implements Serializable, ILocatable {
	private static final long serialVersionUID = 8516955442065220998L;

	private final ShortPoint2D position;
	private byte amount = 0;

	private boolean isStockOffer;

	MaterialOffer(ShortPoint2D position, byte amount, boolean isStockOffer) {
		this.position = position;
		this.amount = amount;
		this.isStockOffer = isStockOffer;
	}

	@Override
	public String toString() {
		return "Offer: " + position + "    " + amount + (isStockOffer ? " (stock)" : "");
	}

	@Override
	public ShortPoint2D getPos() {
		return position;
	}

	/**
	 * Increases the amount and returns the new value.
	 * 
	 * @return
	 */
	public byte incAmount() {
		return ++amount;
	}

	/**
	 * Decreases the amount and returns the new value.
	 * 
	 * @return
	 */
	public byte decAmount() {
		return --amount;
	}

	public byte getAmount() {
		return amount;
	}

	public boolean isStockOffer() {
		return isStockOffer;
	}

	public void toNormalOffer() {
		isStockOffer = false;
	}

}
