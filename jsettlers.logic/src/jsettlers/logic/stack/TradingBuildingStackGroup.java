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
package jsettlers.logic.stack;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.stack.TradingBuildingStackGroup.TradingBuildingStack;

/**
 * This is the stack group for a market (or harbour) building. There is one such group per stack position of the building.
 * <p>
 * It requests the materials for the building.
 * 
 * @author Michael Zangl
 *
 */
public class TradingBuildingStackGroup extends StackGroup<TradingBuildingStack> {
	public class TradingBuildingStack extends StackGroup.GroupedRequestStack<TradingBuildingStack> {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5611941653985305888L;

		public TradingBuildingStack(EMaterialType materialType) {
			super(TradingBuildingStackGroup.this, materialType);
		}

		@Override
		public String toString() {
			return "TradingBuildingStack [inDelivery=" + getInDelivery() + ", isActive()=" + isActive() + ", getPosition()=" + getPosition()
					+ ", getMaterialType()=" + getMaterialType() + "]";
		}
	}

	public TradingBuildingStackGroup(IRequestsStackGrid grid, ShortPoint2D position, EBuildingType buildingType) {
		super(grid, position, buildingType);
	}

	@Override
	public TradingBuildingStack createStack(EMaterialType m) {
		return new TradingBuildingStack(m);
	}

	@Override
	protected EPriority getEmptyStackPriority() {
		return EPriority.MARKET;
	}

	@Override
	protected EPriority getStartedStackPriority() {
		return EPriority.MARKET_STARTED;
	}
}
