package jsettlers.main.android.gameplay.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.gameplay.viewmodels.goods.InventoryViewModel;
import jsettlers.main.android.gameplay.viewmodels.goods.ProductionViewModel;

/**
 * Created by Tom Pratt on 25/09/2017.
 */

public class ControlsViewModelFactory implements ViewModelProvider.Factory {
    private ControlsResolver controlsResolver;

    public ControlsViewModelFactory(Activity activity) {
        this.controlsResolver = new ControlsResolver(activity);
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass == ProductionViewModel.class) {
            return (T)new ProductionViewModel(
                    controlsResolver.getActionControls(),
                    controlsResolver.getPositionControls(),
                    controlsResolver.getDrawControls());
        } else if(modelClass == InventoryViewModel.class) {
            return (T)new InventoryViewModel(
                    controlsResolver.getDrawControls(),
                    controlsResolver.getPositionControls());
        }

        throw new RuntimeException("ControlsViewModelFactory doesn't know how to create a: " + modelClass.toString());
    }
}
