package jsettlers.main.android.ui.fragments.game.menus.selection;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.ui.fragments.game.menus.selection.features.ConstructionFeature;
import jsettlers.main.android.ui.fragments.game.menus.selection.features.DestroyFeature;
import jsettlers.main.android.ui.fragments.game.menus.selection.features.PriorityFeature;
import jsettlers.main.android.ui.fragments.game.menus.selection.features.SelectionFeature;
import jsettlers.main.android.ui.fragments.game.menus.selection.features.TitleFeature;
import jsettlers.main.android.ui.fragments.game.menus.selection.features.WorkAreaFeature;
import jsettlers.main.android.ui.navigation.MenuNavigator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.LinkedList;

/**
 * Created by tompr on 10/01/2017.
 */

public class SelectionBuildingFragment extends SelectionFragment {

    private IBuilding building;
    private BuildingState buildingState;

    private final LinkedList<SelectionFeature> features = new LinkedList<>();

    private ViewGroup rootView;

    public static SelectionBuildingFragment newInstance() {
        return new SelectionBuildingFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = new FrameLayout(getActivity());
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        building = (IBuilding) getSelection().get(0);
        buildingState = new BuildingState(building);

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());


        if (buildingState.isOccupied()) {
        } else if (buildingState.isStock()) {
        } else if (buildingState.isTrading()) {
        } else {
            layoutInflater.inflate(R.layout.menu_selection_building_normal, rootView, true);
        }

        MenuNavigator menuNavigator = (MenuNavigator) getParentFragment();

        features.add(new TitleFeature(building, getControls(), menuNavigator, getView()));
        features.add(new PriorityFeature(building, getControls(), menuNavigator, getView()));
        features.add(new WorkAreaFeature(building, getControls(), menuNavigator, getView()));
        features.add(new DestroyFeature(building, getControls(), menuNavigator, getView()));

        if (buildingState.isConstruction()) {
            features.add(new ConstructionFeature(building, getControls(), menuNavigator, getView()));
        }

        for (SelectionFeature feature : features) {
            feature.initialize(buildingState, getControls());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        for (SelectionFeature feature : features) {
            feature.finish();
        }
    }
}
