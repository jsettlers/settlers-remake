/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.main.android.gameplay.ui.fragments.menus.selection;

import static java8.util.stream.StreamSupport.stream;

import java.util.LinkedList;

import org.androidannotations.annotations.EFragment;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.TaskControls;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.features.DestroyFeature;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.features.MaterialsFeature;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.features.OccupiedFeature;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.features.PriorityFeature;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.features.SelectionFeature;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.features.StockFeature;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.features.TitleFeature;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.features.WorkAreaFeature;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * The games buildings have lots of overlapping functionality but don't fit that nicely into a tree of inheritance. So the buildings menu is made up of composable "features" which are a bit like mini
 * fragments with a very simple lifecycle consisting of just initialize() and viewFinished()
 *
 * This class just decides which features a building has and calls the lifecycle methods
 */
@EFragment
public class BuildingSelectionFragment extends SelectionFragment {
	public static BuildingSelectionFragment newInstance() {
		return new BuildingSelectionFragment_();
	}

	private final LinkedList<SelectionFeature> features = new LinkedList<>();

	private IBuilding building;
	private BuildingState buildingState;

	private ViewGroup rootView;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = new FrameLayout(getActivity());
		return rootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (getSelection().getSize() == 0) {
			return;
		}

		building = (IBuilding) getSelection().get(0);
		buildingState = new BuildingState(building);

		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		MenuNavigator menuNavigator = (MenuNavigator) getParentFragment();

		ControlsResolver controlsResolver = new ControlsResolver(getActivity());
		ActionControls actionControls = controlsResolver.getActionControls();
		DrawControls drawControls = controlsResolver.getDrawControls();
		TaskControls taskControls = controlsResolver.getTaskControls();

		if (building instanceof IBuilding.IOccupied) {
			layoutInflater.inflate(R.layout.menu_selection_building_occupyable, rootView, true);
			features.add(new OccupiedFeature(getView(), building, menuNavigator, actionControls, drawControls));

		} else if (building instanceof IBuilding.IStock) {
			layoutInflater.inflate(R.layout.menu_selection_building_stock, rootView, true);
			features.add(new StockFeature(getActivity(), getView(), building, menuNavigator, drawControls, actionControls));



			// } else if (building instanceof IBuilding.ITrading) {
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
			features.add(new WorkAreaFeature(getView(), building, menuNavigator, actionControls, taskControls));
		}

		for (SelectionFeature feature : features) {
			feature.initialize(buildingState);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		stream(features).forEach(SelectionFeature::finish);
	}
}
