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

import static java8.util.stream.StreamSupport.stream;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.partition.IBuildingCounts;
import jsettlers.logic.buildings.Building;

class BuildingCounts implements IBuildingCounts {

	private final int[] buildingsInPartitionUnderConstruction = new int[EBuildingType.NUMBER_OF_BUILDINGS];
	private final int[] buildingsInPartition = new int[EBuildingType.NUMBER_OF_BUILDINGS];
	private final int[] buildingsUnderConstruction = new int[EBuildingType.NUMBER_OF_BUILDINGS];
	private final int[] buildings = new int[EBuildingType.NUMBER_OF_BUILDINGS];

	public BuildingCounts(byte playerId, short partitionId) {
		stream(Building.getAllBuildings()).filter(building -> building.getPlayer().getPlayerId() == playerId).forEach(building -> {
			int buildingTypeIdx = building.getBuildingType().ordinal;
			boolean finishedConstruction = building.isConstructionFinished();

			if (finishedConstruction) {
				buildings[buildingTypeIdx]++;
			} else {
				buildingsUnderConstruction[buildingTypeIdx]++;
			}

			if (building.getPartitionId() == partitionId) {
				if (finishedConstruction) {
					buildingsInPartition[buildingTypeIdx]++;
				} else {
					buildingsInPartitionUnderConstruction[buildingTypeIdx]++;
				}
			}
		});
	}

	@Override
	public int buildingsInPartitionUnderConstruction(EBuildingType buildingType) {
		return buildingsInPartitionUnderConstruction[buildingType.ordinal];
	}

	@Override
	public int buildingsInPartition(EBuildingType buildingType) {
		return buildingsInPartition[buildingType.ordinal];
	}

	@Override
	public int buildingsUnderConstruction(EBuildingType buildingType) {
		return buildingsUnderConstruction[buildingType.ordinal];
	}

	@Override
	public int buildings(EBuildingType buildingType) {
		return buildings[buildingType.ordinal];
	}
}