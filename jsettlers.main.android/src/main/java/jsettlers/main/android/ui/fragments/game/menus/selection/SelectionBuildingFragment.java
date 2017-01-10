package jsettlers.main.android.ui.fragments.game.menus.selection;

import android.os.Bundle;
import android.support.annotation.Nullable;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.logic.buildings.Building;

/**
 * Created by tompr on 10/01/2017.
 */

public class SelectionBuildingFragment extends SelectionFragment {

    private Building building;

    public static SelectionBuildingFragment newInstance() {
        return new SelectionBuildingFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        building = (Building) getSelection().get(0);

        if (building.isConstructionFinished()) {

            EBuildingType type = building.getBuildingType();

        } else {
            //TODO show under construction menu
        }
    }
}
