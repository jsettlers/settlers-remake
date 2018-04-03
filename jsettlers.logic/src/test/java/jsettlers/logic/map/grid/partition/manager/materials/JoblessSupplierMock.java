/*******************************************************************************
 * Copyright (c) 2015 - 2018
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
package jsettlers.logic.map.grid.partition.manager.materials;

import java.util.LinkedList;

import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IJoblessSupplier;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IManagerBearer;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialOffer;
import jsettlers.logic.map.grid.partition.manager.materials.interfaces.IMaterialRequest;

public class JoblessSupplierMock implements IJoblessSupplier {
	private static final long serialVersionUID = -4698558305428775896L;

	private LinkedList<IManagerBearer> jobless = new LinkedList<>();

	public void addJoblessAt(final ShortPoint2D position) {
		jobless.add(new IManagerBearer() {
			private static final long serialVersionUID = 3833820381369081344L;

			@Override
			public ShortPoint2D getPosition() {
				return position;
			}

			@Override
			public void deliver(EMaterialType materialType, IMaterialOffer offer, IMaterialRequest request) {
				offer.distributionAccepted();
				offer.offerTaken();
				request.deliveryAccepted();
				request.deliveryFulfilled();
			}
		});
	}

	@Override
	public boolean isEmpty() {
		return jobless.isEmpty();
	}

	@Override
	public IManagerBearer removeJoblessCloseTo(ShortPoint2D position) {
		int closestDist = Integer.MAX_VALUE;
		IManagerBearer closest = null;

		for (IManagerBearer curr : jobless) {
			int currDist = ShortPoint2D.getOnGridDist(curr.getPosition().x - position.x, curr.getPosition().y - position.y);
			if (closestDist > currDist) {
				closest = curr;
				closestDist = currDist;
			}
		}

		return closest;
	}

}
