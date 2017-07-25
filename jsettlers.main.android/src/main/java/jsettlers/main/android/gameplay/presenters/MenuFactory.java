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

package jsettlers.main.android.gameplay.presenters;

import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.controls.ControlsAdapter;
import jsettlers.main.android.core.utils.Dispatcher;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.navigation.MenuNavigatorProvider;
import jsettlers.graphics.map.controls.original.panel.content.buildings.EBuildingsCategory;
import jsettlers.main.android.gameplay.ui.views.BuildingsCategoryView;
import jsettlers.main.android.gameplay.ui.views.SettlersSoldiersView;

import android.app.Activity;

/**
 * Created by tompr on 10/03/2017.
 */
public class MenuFactory {
	private final ControlsAdapter controlsAdapter;
	private final MenuNavigator menuNavigator;

	public MenuFactory(Activity activity) {
		this.controlsAdapter = ((GameManager) activity.getApplication()).getControlsAdapter();
		this.menuNavigator = ((MenuNavigatorProvider) activity).getMenuNavigator();
	}

	public BuildingsCategoryMenu buildingsMenu(BuildingsCategoryView view, EBuildingsCategory buildingsCategory) {
		return new BuildingsCategoryMenu(view, controlsAdapter, controlsAdapter, controlsAdapter, menuNavigator, buildingsCategory);
	}

	public SettlersSoldiersMenu settlersSoldiersMenu(SettlersSoldiersView view) {
		return new SettlersSoldiersMenu(view, controlsAdapter, controlsAdapter, controlsAdapter.getInGamePlayer(), new Dispatcher());
	}
}
