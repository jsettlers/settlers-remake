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

import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.action.SetMaterialProductionAction;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.core.controls.PositionControls;
import jsettlers.main.android.gameplay.viewstates.ProductionState;

import static java8.util.J8Arrays.stream;

/**
 * Created by Tom Pratt on 25/09/2017.
 */

public class ProductionViewModel extends ViewModel {


    private final ActionControls actionControls;
    private final PositionControls positionControls;
    private final DrawControls drawControls;

    private final ProductionStatesData productionStates;

    public ProductionViewModel(ActionControls actionControls, PositionControls positionControls, DrawControls drawControls) {
        this.actionControls = actionControls;
        this.positionControls = positionControls;
        this.drawControls = drawControls;

        this.productionStates  = new ProductionStatesData();
    }

    public LiveData<ProductionState[]> getProductionStates() {
        return productionStates;
    }

    public void increment(EMaterialType materialType) {
        actionControls.fireAction(new SetMaterialProductionAction(positionControls.getCurrentPosition(), materialType, SetMaterialProductionAction.EMaterialProductionType.INCREASE, 0));
    }

    public void decrement(EMaterialType materialType) {
        actionControls.fireAction(new SetMaterialProductionAction(positionControls.getCurrentPosition(), materialType, SetMaterialProductionAction.EMaterialProductionType.DECREASE, 0));
    }

    public void setProductionRatio(EMaterialType materialType, float ratio) {
        actionControls.fireAction(new SetMaterialProductionAction(positionControls.getCurrentPosition(), materialType, SetMaterialProductionAction.EMaterialProductionType.SET_RATIO, ratio));
    }


    /**
     * LiveData class for production states
     */
    private class ProductionStatesData extends LiveData<ProductionState[]> implements DrawListener {
        private final EMaterialType[] productionMaterials = {
                EMaterialType.SWORD,
                EMaterialType.BOW,
                EMaterialType.SPEAR,
                EMaterialType.HAMMER,
                EMaterialType.BLADE,
                EMaterialType.PICK,
                EMaterialType.AXE,
                EMaterialType.SAW,
                EMaterialType.SCYTHE,
                EMaterialType.FISHINGROD
        };

        public ProductionStatesData() {
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

        private ProductionState[] productionStates() {
            IMaterialProductionSettings materialProductionSettings = positionControls.getCurrentPartitionData().getPartitionSettings().getMaterialProductionSettings();

            return stream(productionMaterials)
                    .map(materialType -> productionState(materialType, materialProductionSettings))
                    .toArray(ProductionState[]::new);
        }

        private ProductionState productionState(EMaterialType materialType, IMaterialProductionSettings materialProductionSettings) {
            int quantity = materialProductionSettings.getAbsoluteProductionRequest(materialType);
            float ratio = materialProductionSettings.getUserConfiguredRelativeRequestValue(materialType);

            return new ProductionState(materialType, quantity, ratio);
        }
    }
}
