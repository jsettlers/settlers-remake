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
 * @author codingberlin
 */
public class SetMaterialProductionAction extends Action {

	public enum EMaterialProductionType {
		INCREASE,
		DECREASE,
		SET_PRODUCTION,
		SET_RATIO;

		public static final EMaterialProductionType[] VALUES = EMaterialProductionType.values();
	}

	private final EMaterialType materialType;
	private final EMaterialProductionType productionType;
	private final float ratio;
	private final ShortPoint2D position;

	public SetMaterialProductionAction(ShortPoint2D position, EMaterialType materialType, EMaterialProductionType productionType, float ratio) {
		super(EActionType.SET_MATERIAL_PRODUCTION);
		this.materialType = materialType;
		this.productionType = productionType;
		this.ratio = ratio;
		this.position = position;
	}

	public float getRatio() {
		return ratio;
	}

	public EMaterialProductionType getProductionType() {
		return productionType;
	}

	public EMaterialType getMaterialType() {
		return materialType;
	}

	public ShortPoint2D getPosition() {
		return position;
	}
}
