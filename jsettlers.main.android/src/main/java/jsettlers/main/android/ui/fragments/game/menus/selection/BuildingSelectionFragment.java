package jsettlers.main.android.ui.fragments.game.menus.selection;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ActionControls;
import jsettlers.main.android.controls.ControlsResolver;
import jsettlers.main.android.controls.DrawControls;
import jsettlers.main.android.ui.fragments.game.menus.selection.features.DestroyFeature;
import jsettlers.main.android.ui.fragments.game.menus.selection.features.MaterialsFeature;
import jsettlers.main.android.ui.fragments.game.menus.selection.features.OccupiedFeature;
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

public class BuildingSelectionFragment extends SelectionFragment {

    private IBuilding building;
    private BuildingState buildingState;

    private final LinkedList<SelectionFeature> features = new LinkedList<>();

    private ViewGroup rootView;

    public static BuildingSelectionFragment newInstance() {
        return new BuildingSelectionFragment();
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
        MenuNavigator menuNavigator = (MenuNavigator) getParentFragment();
        ActionControls actionControls = ControlsResolver.getActionControls(getActivity());
        DrawControls drawControls = ControlsResolver.getDrawControls(getActivity());

        if (building instanceof IBuilding.IOccupied) {
            layoutInflater.inflate(R.layout.menu_selection_building_occupyable, rootView, true);
            features.add(new OccupiedFeature(getView(), building, menuNavigator, actionControls, drawControls));


//        } else if (building instanceof IBuilding.IStock) {
//        } else if (building instanceof IBuilding.ITrading) {
        } else {
            layoutInflater.inflate(R.layout.menu_selection_building_normal, rootView, true);
        }


        features.add(new TitleFeature(getView(), building, menuNavigator, drawControls));
        features.add(new DestroyFeature(getView(), building, menuNavigator, actionControls));
        features.add(new MaterialsFeature(getView(), building, menuNavigator, drawControls));

        if (buildingState.getSupportedPriorities().length > 1) {
            features.add(new PriorityFeature(getView(), building, menuNavigator, actionControls, drawControls));
        }

        if (building.getBuildingType().getWorkRadius() > 0) {
            features.add(new WorkAreaFeature(getView(), building, menuNavigator, actionControls));
        }

        for (SelectionFeature feature : features) {
            feature.initialize(buildingState);
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
