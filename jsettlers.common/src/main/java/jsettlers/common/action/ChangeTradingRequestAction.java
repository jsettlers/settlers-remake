/*
 * Copyright (c) 2018
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
package jsettlers.common.action;

import jsettlers.common.material.EMaterialType;

/**
 * This {@link Action} changes the trading request of a market place.
 * 
 * @author Michael Zangl
 */
public class ChangeTradingRequestAction extends Action {

	private final EMaterialType material;
	private final int amount;
	private final boolean relative;

	/**
	 * Create a new {@link ChangeTradingRequestAction}.
	 * 
	 * @param material
	 *            Which material to change
	 * @param amount
	 *            The new amount of materials
	 * @param relative
	 *            If <code>true</code>, the amount is treated as relative value.
	 */
	public ChangeTradingRequestAction(EMaterialType material, int amount, boolean relative) {
		super(EActionType.CHANGE_TRADING_REQUEST);
		this.material = material;
		this.amount = amount;
		this.relative = relative;
	}

	/**
	 * The material of which the amount should be changed.
	 * 
	 * @return The material.
	 */
	public EMaterialType getMaterial() {
		return material;
	}

	/**
	 * The new amount. If {@link #isRelative()} returns true, this should be added to the current amount.
	 * 
	 * @return The new amount, {@link Integer#MAX_VALUE} to indicate infinity.
	 */
	public int getAmount() {
		return amount;
	}

	/**
	 * This defines if amount should be interpreted as relative value.
	 * 
	 * @return <code>true</code> if the amount should be added, false if we override it.
	 */
	public boolean isRelative() {
		return relative;
	}
}
