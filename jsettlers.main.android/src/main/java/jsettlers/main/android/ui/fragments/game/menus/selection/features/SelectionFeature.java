package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.ui.navigation.MenuNavigator;

import android.content.Context;
import android.view.View;

/**
 * Created by tompr on 10/01/2017.
 */

public abstract class SelectionFeature {

    private final IBuilding building;
    private final ControlsAdapter controls;
    private final MenuNavigator menuNavigator;
    private final View view;

    private BuildingState buildingState;

    public SelectionFeature(IBuilding building, ControlsAdapter controls, MenuNavigator menuNavigator, View view) {
        this.building = building;
        this.controls = controls;
        this.menuNavigator = menuNavigator;
        this.view = view;
    }

    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        this.buildingState = buildingState;
    }

    public void finish() {

    }

    protected IBuilding getBuilding() {
        return building;
    }

    protected ControlsAdapter getControls() {
        return controls;
    }

    protected MenuNavigator getMenuNavigator() {
        return menuNavigator;
    }

    protected View getView() {
        return view;
    }

    protected Context getContext() {
        return getView().getContext();
    }

    protected BuildingState getBuildingState() {
        return buildingState;
    }

    protected void setBuildingState(BuildingState buildingState) {
        this.buildingState = buildingState;
    }
}
