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
package jsettlers.logic.map.grid.partition.manager.materials.offers;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IOfferEmptiedListener;

/**
 * Created by Andreas Eberle on 05.03.2017.
 */
public class ListenableMaterialOffer extends MaterialOffer {
	private final IOfferEmptiedListener emptiedListener;

	ListenableMaterialOffer(ShortPoint2D position, EMaterialType materialType, IOffersCountListener countChangedListener, EOfferPriority priority, byte amount, IOfferEmptiedListener
			emptiedListener) {
		super(position, materialType, countChangedListener, priority, amount);
		this.emptiedListener = emptiedListener;
	}

	@Override
	public void offerTaken() {
		super.offerTaken();
		if (canBeRemoved()) {
			emptiedListener.offerEmptied();
		}
	}
}
