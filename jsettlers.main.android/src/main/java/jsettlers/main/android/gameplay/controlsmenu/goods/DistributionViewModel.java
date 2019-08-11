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

package jsettlers.main.android.gameplay.controlsmenu.goods;

import static java8.util.J8Arrays.stream;

import android.app.Activity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import jsettlers.common.action.SetMaterialDistributionSettingsAction;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.MaterialsOfBuildings;
import jsettlers.common.map.partition.IMaterialDistributionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.PositionControls;

/**
 * Created by Tom Pratt on 29/09/2017.
 */

public class DistributionViewModel extends ViewModel {
	private static final EMaterialType[] MATERIAL_TYPES_FOR_DISTRIBUTION = new EMaterialType[] {
			EMaterialType.COAL,
			EMaterialType.IRON,
			EMaterialType.PLANK,
			EMaterialType.CROP,
			EMaterialType.WATER,
			EMaterialType.BREAD,
			EMaterialType.MEAT,
			EMaterialType.FISH
	};

	private final PositionControls positionControls;
	private final ActionControls actionControls;

	public DistributionViewModel(PositionControls positionControls, ActionControls actionControls) {
		this.positionControls = positionControls;
		this.actionControls = actionControls;
	}

	public EMaterialType[] getDistributionMaterials() {
		return MATERIAL_TYPES_FOR_DISTRIBUTION;
	}

	public DistributionState[] getDistributionStates(EMaterialType materialType) {
		IMaterialDistributionSettings materialDistributionSettings = positionControls.getCurrentPartitionData().getPartitionSettings().getDistributionSettings(materialType);
		EBuildingType[] buildingsForMaterial = MaterialsOfBuildings.getBuildingTypesRequestingMaterial(materialType);

		return stream(buildingsForMaterial)
				.map(buildingType -> new DistributionState(buildingType, materialDistributionSettings))
				.toArray(DistributionState[]::new);
	}

	public void setDistributionRatio(EMaterialType materialType, EBuildingType buildingType, float ratio) {
		actionControls.fireAction(new SetMaterialDistributionSettingsAction(positionControls.getCurrentPosition(), materialType, buildingType, ratio));
	}

	/**
	 * ViewModel factory
	 */
	public static class Factory implements ViewModelProvider.Factory {
		private final ControlsResolver controlsResolver;

		public Factory(Activity activity) {
			this.controlsResolver = new ControlsResolver(activity);
		}

		@Override
		public <T extends ViewModel> T create(Class<T> modelClass) {
			if (modelClass == DistributionViewModel.class) {
				return (T) new DistributionViewModel(
						controlsResolver.getPositionControls(),
						controlsResolver.getActionControls());
			}
			throw new RuntimeException("DistributionViewModel.Factory doesn't know how to create a: " + modelClass.toString());
		}
	}
}
