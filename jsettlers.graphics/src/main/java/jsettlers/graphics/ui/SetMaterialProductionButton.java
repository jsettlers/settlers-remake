/**
 * ****************************************************************************
 * Copyright (c) 2015 - 2017
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * *****************************************************************************
 */
package jsettlers.graphics.ui;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.IPositionSupplier;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.SetMaterialProductionAction;

/**
 * @author codingberlin
 */
public class SetMaterialProductionButton extends Button {

	private final IPositionSupplier positionSupplier;
	private final EMaterialType materialType;
	private final SetMaterialProductionAction.EMaterialProductionType productionType;

	public SetMaterialProductionButton(IPositionSupplier positionSupplier, EMaterialType materialType, SetMaterialProductionAction.EMaterialProductionType productionType) {
		super(null);
		this.positionSupplier = positionSupplier;
		this.materialType = materialType;
		this.productionType = productionType;
	}

	public Action getAction() {
		return new SetMaterialProductionAction(positionSupplier.getPosition(), materialType, productionType, 0);
	}
}
