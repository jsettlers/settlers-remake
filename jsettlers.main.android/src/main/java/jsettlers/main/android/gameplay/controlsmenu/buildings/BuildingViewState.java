/*
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
 */

package jsettlers.main.android.gameplay.controlsmenu.buildings;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.partition.IBuildingCounts;
import jsettlers.graphics.localization.Labels;

/**
 * Created by tompr on 29/05/2017.
 */

public class BuildingViewState {

	private final EBuildingType buildingType;
	private final String name;
	private final String count;
	private final String constructionCount;

	public BuildingViewState(EBuildingType buildingType, IBuildingCounts buildingCounts) {
		this.buildingType = buildingType;
		this.name = Labels.getName(buildingType);

		if (buildingCounts != null) {
			this.count = Integer.toString(buildingCounts.buildingsInPartition(buildingType));

			int constructionCount = buildingCounts.buildingsInPartitionUnderConstruction(buildingType);
			if (constructionCount > 0) {
				this.constructionCount = "+" + Integer.toString(constructionCount);
			} else {
				this.constructionCount = "";
			}
		} else {
			this.count = "";
			this.constructionCount = "";
		}
	}

	public EBuildingType getBuildingType() {
		return buildingType;
	}

	public String getName() {
		return name;
	}

	public String getCount() {
		return count;
	}

	public String getConstructionCount() {
		return constructionCount;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof BuildingViewState && ((BuildingViewState) obj).getBuildingType() == getBuildingType();
	}
}
