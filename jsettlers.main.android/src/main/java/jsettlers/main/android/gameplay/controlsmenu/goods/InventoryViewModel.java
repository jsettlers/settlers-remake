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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.material.EMaterialType;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.PositionControls;
import jsettlers.main.android.core.events.DrawEvents;

/**
 * Created by Tom Pratt on 27/09/2017.
 */

public class InventoryViewModel extends ViewModel {
	private final EMaterialType[] inventoryMaterials = EMaterialType.STOCK_MATERIALS;

	private final PositionControls positionControls;

	private final LiveData<InventoryMaterialState[]> inventoryMaterialStatesData;

	public InventoryViewModel(DrawControls drawControls, PositionControls positionControls) {
		this.positionControls = positionControls;

		DrawEvents drawEvents = new DrawEvents(drawControls);
		inventoryMaterialStatesData = Transformations.map(drawEvents, x -> productionStates());
	}

	public LiveData<InventoryMaterialState[]> getMaterialStates() {
		return inventoryMaterialStatesData;
	}

	private InventoryMaterialState[] productionStates() {
		IPartitionData partitionData = positionControls.getCurrentPartitionData();

		return stream(inventoryMaterials)
				.map(materialType -> new InventoryMaterialState(materialType, partitionData))
				.toArray(InventoryMaterialState[]::new);
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
			if (modelClass == InventoryViewModel.class) {
				return (T) new InventoryViewModel(
						controlsResolver.getDrawControls(),
						controlsResolver.getPositionControls());
			}
			throw new RuntimeException("InventoryViewModel.Factory doesn't know how to create a: " + modelClass.toString());
		}
	}
}
