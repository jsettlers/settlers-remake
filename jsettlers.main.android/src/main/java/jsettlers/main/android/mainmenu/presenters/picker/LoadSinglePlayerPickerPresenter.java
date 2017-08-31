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

import static java8.util.stream.StreamSupport.stream;

import java.util.List;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.map.loading.newmap.MapFileHeader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.LoadSinglePlayerPickerView;

import java8.util.stream.Collectors;

/**
 * Created by tompr on 22/01/2017.
 */
public class LoadSinglePlayerPickerPresenter extends MapPickerPresenter {
	private final LoadSinglePlayerPickerView view;
	private final GameStarter gameStarter;
	private final MainMenuNavigator navigator;

	public LoadSinglePlayerPickerPresenter(LoadSinglePlayerPickerView view, MainMenuNavigator navigator, GameStarter gameStarter, ChangingList<? extends MapLoader> changingMaps) {
		super(view, navigator, gameStarter, changingMaps);
		this.view = view;
		this.navigator = navigator;
		this.gameStarter = gameStarter;
	}

	@Override
	public void itemSelected(MapLoader mapLoader) {
		MapFileHeader mapFileHeader = mapLoader.getFileHeader();
		PlayerSetting[] playerSettings = mapFileHeader.getPlayerSettings();
		byte playerId = mapFileHeader.getPlayerId();
		JSettlersGame game = new JSettlersGame(mapLoader, 4711L, playerId, playerSettings);
		gameStarter.setStartingGame(game.start());
		navigator.showGame();
	}

	@Override
	protected void updateViewItems(List<? extends MapLoader> items) {
		List<? extends MapLoader> sortedList = stream(items)
				.sorted((o1, o2) -> o2.getCreationDate().compareTo(o1.getCreationDate()))
				.collect(Collectors.toList());

		view.setItems(sortedList);

		if (sortedList.size() > 0) {
			view.hideNoGamesView();
		} else {
			view.showNoGamesView();
		}
	}
}
