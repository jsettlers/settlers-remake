package jsettlers.main.android.gameplay.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.partition.IBuildingCounts;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.map.controls.original.panel.content.buildings.EBuildingsCategory;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.core.controls.PositionControls;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.navigation.MenuNavigatorProvider;
import jsettlers.main.android.gameplay.viewstates.BuildingViewState;

import static java8.util.stream.StreamSupport.stream;

/**
 * Created by Tom Pratt on 28/09/2017.
 */

public class BuildingsCategoryViewModel extends ViewModel {
    private final ActionControls actionControls;
    private final DrawControls drawControls;
    private final PositionControls positionControls;
    private final MenuNavigator menuNavigator;
    private final EBuildingsCategory buildingsCategory;

    private final BuildingStatesData buildingStatesData;

    public BuildingsCategoryViewModel(ActionControls actionControls, DrawControls drawControls, PositionControls positionControls, MenuNavigator menuNavigator, EBuildingsCategory buildingsCategory) {
        this.actionControls = actionControls;
        this.drawControls = drawControls;
        this.positionControls = positionControls;
        this.menuNavigator = menuNavigator;
        this.buildingsCategory = buildingsCategory;

        this.buildingStatesData = new BuildingStatesData();
    }

    public LiveData<BuildingViewState[]> getBuildingStates() {
        return buildingStatesData;
    }

    public void showConstructionMarkers(EBuildingType buildingType) {
        Action action = new ShowConstructionMarksAction(buildingType);
        actionControls.fireAction(action);
        menuNavigator.dismissMenu();
    }

    /**
     * LiveData class for building states
     */
    private class BuildingStatesData extends LiveData<BuildingViewState[]> implements DrawListener {

        public BuildingStatesData() {
            setValue(buildingStates());
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
            postValue(buildingStates());
        }

        private BuildingViewState[] buildingStates() {
            final IBuildingCounts buildingCounts;

            if (positionControls.isInPlayerPartition()) {
                buildingCounts = positionControls.getCurrentPartitionData().getBuildingCounts();
            } else {
                buildingCounts = null;
            }

            return stream(buildingsCategory.buildingTypes)
                    .map(buildingType -> new BuildingViewState(buildingType, buildingCounts))
                    .toArray(BuildingViewState[]::new);
        }
    }


    /**
     * ViewModel factory
     */
    public static class Factory implements ViewModelProvider.Factory {
        private final ControlsResolver controlsResolver;
        private final MenuNavigator menuNavigator;
        private final EBuildingsCategory buildingsCategory;

        public Factory(Activity activity, EBuildingsCategory buildingsCategory) {
            this.controlsResolver = new ControlsResolver(activity);
            this.menuNavigator = ((MenuNavigatorProvider) activity).getMenuNavigator();
            this.buildingsCategory = buildingsCategory;
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass == BuildingsCategoryViewModel.class) {
                return (T) new BuildingsCategoryViewModel(
                        controlsResolver.getActionControls(),
                        controlsResolver.getDrawControls(),
                        controlsResolver.getPositionControls(),
                        menuNavigator,
                        buildingsCategory);
            }
            throw new RuntimeException("BuildingsCategoryViewModel.Factory doesn't know how to create a: " + modelClass.toString());
        }
    }
}
