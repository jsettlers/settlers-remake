package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.controls.ControlsAdapter;

/**
 * Created by tompr on 10/01/2017.
 */

public abstract class SelectionFeature {

    private final IBuilding building;
    private BuildingState buildingState;
    private ControlsAdapter controls;

    public SelectionFeature(IBuilding building) {
        this.building = building;
    }

    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        this.buildingState = buildingState;
        this.controls = controls;
    }
}
