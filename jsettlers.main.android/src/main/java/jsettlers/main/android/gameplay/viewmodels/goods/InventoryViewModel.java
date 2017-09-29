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

package jsettlers.main.android.gameplay.viewmodels.goods;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.material.EMaterialType;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.core.controls.PositionControls;
import jsettlers.main.android.gameplay.viewstates.InventoryMaterialState;

import static java8.util.J8Arrays.stream;

/**
 * Created by Tom Pratt on 27/09/2017.
 */

public class InventoryViewModel extends ViewModel {
    private final DrawControls drawControls;
    private final PositionControls positionControls;

    private final InventoryMaterialStatesData inventoryMaterialStatesData;

    public InventoryViewModel(DrawControls drawControls, PositionControls positionControls) {
        this.drawControls = drawControls;
        this.positionControls = positionControls;

        inventoryMaterialStatesData = new InventoryMaterialStatesData();
    }

    public LiveData<InventoryMaterialState[]> getMaterialStates() {
        return inventoryMaterialStatesData;
    }

    /**
     * LiveData class for production states
     */
    private class InventoryMaterialStatesData extends LiveData<InventoryMaterialState[]> implements DrawListener {
        private final EMaterialType[] inventoryMaterials = EMaterialType.STOCK_MATERIALS;

        public InventoryMaterialStatesData() {
            setValue(productionStates());
        }

        @Override
        protected void onActive() {
            super.onActive();
            drawControls.addInfrequentDrawListener(this);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            drawControls.removeInfrequentDrawListener(this);
        }

        @Override
        public void draw() {
            postValue(productionStates());
        }

        private InventoryMaterialState[] productionStates() {
            IPartitionData partitionData = positionControls.getCurrentPartitionData();

            return stream(inventoryMaterials)
                    .map(materialType -> materialState(materialType, partitionData))
                    .toArray(InventoryMaterialState[]::new);
        }

        private InventoryMaterialState materialState(EMaterialType materialType, IPartitionData partitionData) {
            int quantity = partitionData.getAmountOf(materialType);
            return new InventoryMaterialState(materialType, quantity);
        }
    }
}
