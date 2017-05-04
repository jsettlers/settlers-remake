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
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.MapPickerView;

/**
 * Created by tompr on 22/01/2017.
 */
public abstract class MapPickerPresenter implements IChangingListListener<MapLoader> {
	private final MapPickerView view;
	private final GameStarter gameStarter;
	private final MainMenuNavigator navigator;
	private final ChangingList<? extends MapLoader> changingMaps;

	public MapPickerPresenter(MapPickerView view, MainMenuNavigator navigator, GameStarter gameStarter,			ChangingList<? extends MapLoader> changingMaps) {
		this.view = view;
		this.gameStarter = gameStarter;
		this.navigator = navigator;
		this.changingMaps = changingMaps;

		changingMaps.setListener(this);
	}

	public void initView() {
		view.setItems(changingMaps.getItems());
	}

	public void viewFinished() {
		if (gameStarter.getStartingGame() == null) {
			abort();
		}
	}

	protected void abort() {
	}

	public void dispose() {
		changingMaps.removeListener(this);
	}

	public abstract void itemSelected(MapLoader mapLoader);

	/**
	 * ChangingListListener implementation
	 */
	@Override
	public void listChanged(ChangingList<? extends MapLoader> list) {
		view.setItems(list.getItems());
	}
}
