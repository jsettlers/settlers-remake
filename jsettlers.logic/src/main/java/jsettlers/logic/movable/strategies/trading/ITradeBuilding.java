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
package jsettlers.logic.movable.strategies.trading;

import java.io.Serializable;
import java.util.Iterator;

import java8.util.Optional;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ILocatable;
import jsettlers.common.position.ShortPoint2D;

/**
 *
 * @author Andreas Eberle
 */
public interface ITradeBuilding extends ILocatable, Serializable {
	boolean needsTrader();

	ShortPoint2D getPickUpPosition();

	/**
	 *
	 * @param maxAmount
	 * The maximum amount of this material type that can be taken.
	 * @return
	 * null if no material is available. The type of the taken material.
	 */
	Optional<MaterialTypeWithCount> tryToTakeMaterial(int maxAmount);

	Iterator<ShortPoint2D> getWaypointsIterator();

	class MaterialTypeWithCount {
		public final EMaterialType materialType;
		public final int           count;

		public MaterialTypeWithCount(EMaterialType materialType, int count) {
			this.materialType = materialType;
			this.count = count;
		}

		public EMaterialType getMaterialType() {
			return materialType;
		}

		public int getCount() {
			return count;
		}
	}
}
