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
import jsettlers.common.position.ShortPoint2D;

/**
 * This {@link Action} allows you to change the partition default settings for putting materials in stock.
 * 
 * @author Michael Zangl
 */
public class SetAcceptedStockMaterialAction extends PointAction {

	private final EMaterialType material;
	private final boolean accept;
	private final boolean local;

	/**
	 * Create a new {@link SetAcceptedStockMaterialAction}.
	 * 
	 * @param mapPosition
	 *            The position on the map to control.
	 * @param material
	 *            The material to change
	 * @param accept
	 *            <code>true</code> if that material should be accepted in stock for that partition.
	 * @param local
	 *            If <code>true</code>, this setting is set locally for a single stock.
	 */
	public SetAcceptedStockMaterialAction(ShortPoint2D mapPosition, EMaterialType material, boolean accept, boolean local) {
		super(EActionType.SET_MATERIAL_STOCK_ACCEPTED, mapPosition);
		this.material = material;
		this.accept = accept;
		this.local = local;
	}

	/**
	 * @return Returns the position of the manager whose settings will be changed.
	 */
	@Override
	public ShortPoint2D getPosition() {
		return super.getPosition();
	}

	/**
	 * The material for which the setting should be changed.
	 * 
	 * @return The new material.
	 */
	public EMaterialType getMaterial() {
		return material;
	}

	/**
	 * The new setting for this material.
	 * 
	 * @return True if this material should be brought to stock buildings.
	 */
	public boolean shouldAccept() {
		return accept;
	}

	public boolean isLocalSetting(){
		return local;
	}

	@Override
	public String toString() {
		return "SetAcceptedStockMaterialAction [material=" + material + ", accept=" + accept + ", getPosition()=" + getPosition() + "]";
	}
}
