package jsettlers.main.android.gameplay.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import jsettlers.common.material.EMaterialType;
import jsettlers.main.android.core.controls.PositionControls;
import jsettlers.main.android.gameplay.viewstates.ProductionState;

/**
 * Created by Tom Pratt on 25/09/2017.
 */

public class ProductionViewModel extends ViewModel {

    private final PositionControls positionControls;

    private final MutableLiveData<ProductionState[]> productionStates = new MutableLiveData<>();

    public ProductionViewModel(PositionControls positionControls) {
        this.positionControls = positionControls;

        productionStates.postValue(new ProductionState[]{
                new ProductionState(EMaterialType.AXE, 5),
                new ProductionState(EMaterialType.BOW, 10),
                new ProductionState(EMaterialType.BREAD, 1),
                new ProductionState(EMaterialType.PLANK, 0)
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    public LiveData<ProductionState[]> getProductionStates() {
        return productionStates;
    }
}
