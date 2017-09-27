package jsettlers.main.android.gameplay.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.PositionControls;

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
            PositionControls positionControls = controlsResolver.getPositionControls();
            IMaterialProductionSettings materialProductionSettings = positionControls.getCurrentPartitionData().getPartitionSettings().getMaterialProductionSettings();

            return (T)new ProductionViewModel(
                    controlsResolver.getActionControls(),
                    positionControls,
                    controlsResolver.getDrawControls(),
                    materialProductionSettings);
        }

        throw new RuntimeException("ControlsViewModelFactory doesn't know how to create a: " + modelClass.toString());
    }
}
