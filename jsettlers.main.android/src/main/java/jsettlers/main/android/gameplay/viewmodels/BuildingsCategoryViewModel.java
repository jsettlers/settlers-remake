package jsettlers.main.android.gameplay.viewmodels;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
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
import jsettlers.main.android.core.controls.PositionControls;
import jsettlers.main.android.core.events.DrawEvents;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.navigation.MenuNavigatorProvider;
import jsettlers.main.android.gameplay.viewstates.BuildingViewState;

import static java8.util.stream.StreamSupport.stream;

/**
 * Created by Tom Pratt on 28/09/2017.
 */

public class BuildingsCategoryViewModel extends ViewModel {
    private final ActionControls actionControls;
    private final PositionControls positionControls;
    private final MenuNavigator menuNavigator;
    private final EBuildingsCategory buildingsCategory;

    private final LiveData<BuildingViewState[]> buildingStates;

    public BuildingsCategoryViewModel(ActionControls actionControls, DrawControls drawControls, PositionControls positionControls, MenuNavigator menuNavigator, EBuildingsCategory buildingsCategory) {
        this.actionControls = actionControls;
        this.positionControls = positionControls;
        this.menuNavigator = menuNavigator;
        this.buildingsCategory = buildingsCategory;

        DrawEvents drawEvents = new DrawEvents(drawControls);
        buildingStates = Transformations.map(drawEvents, x -> buildingStates());
    }

    public LiveData<BuildingViewState[]> getBuildingStates() {
        return buildingStates;
    }

    public void showConstructionMarkers(EBuildingType buildingType) {
        Action action = new ShowConstructionMarksAction(buildingType);
        actionControls.fireAction(action);
        menuNavigator.dismissMenu();
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
