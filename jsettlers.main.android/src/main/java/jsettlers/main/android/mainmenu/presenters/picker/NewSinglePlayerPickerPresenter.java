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

package jsettlers.main.android.mainmenu.presenters.picker;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.MapPickerView;

/**
 * Created by tompr on 22/01/2017.
 */
public class NewSinglePlayerPickerPresenter extends MapPickerPresenter {
	private final MainMenuNavigator navigator;

	public NewSinglePlayerPickerPresenter(MapPickerView view, MainMenuNavigator navigator, GameStarter gameStarter, ChangingList<? extends MapLoader> changingMaps) {
		super(view, navigator, gameStarter, changingMaps);
		this.navigator = navigator;
	}

	@Override
	public void itemSelected(MapLoader mapLoader) {
		navigator.showNewSinglePlayerSetup(mapLoader);
	}
}
