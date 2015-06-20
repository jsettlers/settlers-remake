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
package jsettlers.logic.map.grid.partition.manager;

import jsettlers.common.map.partition.IMaterialsDistributionSettings;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.material.EMaterialType;
import jsettlers.logic.map.grid.partition.manager.materials.offers.OffersList;
import jsettlers.logic.map.grid.partition.manager.settings.PartitionManagerSettings;

public final class PartitionDataSupplier implements IPartitionData {

	private final PartitionManagerSettings settings;
	private final OffersList offers;

	public PartitionDataSupplier(PartitionManagerSettings settings, OffersList offers) {
		this.settings = settings;
		this.offers = offers;
	}

	@Override
	public IMaterialsDistributionSettings getDistributionSettings(EMaterialType materialType) {
		return settings.getDistributionSettings(materialType);
	}

	@Override
	public EMaterialType getMaterialTypeForPrio(int priorityIdx) {
		return settings.getMaterialTypeForPrio(priorityIdx);
	}

	@Override
	public int getAmountOf(EMaterialType materialType) {
		return offers.getOffersOf(materialType);
	}
}
