package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import android.view.View;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.controls.ControlsAdapter;

/**
 * Created by tompr on 10/01/2017.
 */

public abstract class SelectionFeature {

    private final IBuilding building;
    private final ControlsAdapter controls;
    private final View view;

    private BuildingState buildingState;

    public SelectionFeature(IBuilding building, ControlsAdapter controls, View view) {
        this.building = building;
        this.controls = controls;
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

    public View getView() {
        return view;
    }

    protected BuildingState getBuildingState() {
        return buildingState;
    }
}
