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

package jsettlers.main.android.gameplay.controlsmenu.selection.features;

import android.content.Context;
import android.view.View;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

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
