package jsettlers.main.android.gameplay.ui.fragments.menus.selection.features;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

import android.content.Context;
import android.view.View;

/**
 * Created by tompr on 10/01/2017.
 */

public abstract class SelectionFeature {

    private final View view;
    private final IBuilding building;
    private final MenuNavigator menuNavigator;

    private BuildingState buildingState;

    public SelectionFeature(View view, IBuilding building, MenuNavigator menuNavigator) {
        this.view = view;
        this.building = building;
        this.menuNavigator = menuNavigator;
    }

    public void initialize(BuildingState buildingState) {
        this.buildingState = buildingState;
    }

    public void finish() {

    }

    public boolean hasNewState() {
        if (!getBuildingState().isStillInState(getBuilding())) {
            buildingState = new BuildingState(getBuilding());
            return true;
        }
        return false;
    }

    protected IBuilding getBuilding() {
        return building;
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
}
