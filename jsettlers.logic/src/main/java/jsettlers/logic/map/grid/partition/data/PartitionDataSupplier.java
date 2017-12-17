/*******************************************************************************
 * Copyright (c) 2015 - 2017
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
package jsettlers.logic.map.grid.partition.data;

import jsettlers.common.map.partition.IBuildingCounts;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.map.partition.IPartitionSettings;
import jsettlers.common.material.EMaterialType;

public final class PartitionDataSupplier implements IPartitionData {

	private final byte playerId;
	private final short partitionId;
	private final IPartitionSettings settings;
	private final MaterialCounts materialCounts;

	public PartitionDataSupplier(byte playerId, short partitionId, IPartitionSettings settings, MaterialCounts materialCounts) {
		this.playerId = playerId;
		this.partitionId = partitionId;
		this.settings = settings;
		this.materialCounts = materialCounts;
	}

	@Override
	public IPartitionSettings getPartitionSettings() {
		return settings;
	}

	@Override
	public int getAmountOf(EMaterialType materialType) {
		return materialCounts.getAmountOf(materialType);
	}

	@Override
	public IBuildingCounts getBuildingCounts() {
		return new BuildingCounts(playerId, partitionId);
	}
}
