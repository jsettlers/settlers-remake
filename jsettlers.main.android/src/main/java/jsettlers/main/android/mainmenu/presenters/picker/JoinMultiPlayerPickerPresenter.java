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

import java.util.List;

import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerPickerPresenter implements IChangingListListener<IJoinableGame>, IJoiningGameListener {
	private final JoinMultiPlayerPickerView view;
	private final GameStarter gameStarter;
	private final MainMenuNavigator navigator;
	private final ChangingList<IJoinableGame> changingJoinableGames;

	private IJoiningGame joiningGame;
	private IMapDefinition mapDefinition;

	public JoinMultiPlayerPickerPresenter(JoinMultiPlayerPickerView view, MainMenuNavigator navigator, GameStarter gameStarter) {
		this.view = view;
		this.gameStarter = gameStarter;
		this.navigator = navigator;

		changingJoinableGames = gameStarter.getMultiPlayerConnector().getJoinableMultiplayerGames();
		changingJoinableGames.setListener(this);

		joiningGame = gameStarter.getJoiningGame();
		if (joiningGame == null) {
			// pop
		} else {
			joiningGame.setListener(this);
		}
	}

	public void initView() {
		updateViewJoinableGames();
	}

	public void viewFinished() {
		if (gameStarter.getStartingGame() == null) {
			abort();
		}
	}

	private void abort() {
		if (joiningGame != null) {
			joiningGame.abort();
		}
		gameStarter.setJoiningGame(null);
		gameStarter.closeMultiPlayerConnector();
		joiningGame = null;
		mapDefinition = null;
	}

	public void dispose() {
		changingJoinableGames.removeListener(this);
		if (joiningGame != null) {
			joiningGame.setListener(null);
		}
	}

	public void joinableGameSelected(IJoinableGame joinableGame) {
		abort();
		mapDefinition = joinableGame.getMap();

		joiningGame = gameStarter.getMultiPlayerConnector().joinMultiplayerGame(joinableGame);
		joiningGame.setListener(this);

		gameStarter.setJoiningGame(joiningGame);
	}

	/**
	 * ChangingListListener implementation
	 */
	@Override
	public void listChanged(ChangingList<? extends IJoinableGame> list) {
		updateViewJoinableGames();
	}

	/**
	 * IJoiningGameListener imeplementation
	 */
	@Override
	public void joinProgressChanged(EProgressState state, float progress) {
		String stateString = Labels.getProgress(state);
		int progressPercentage = (int) (progress * 100);

		view.setJoiningProgress(stateString, progressPercentage);
	}

	@Override
	public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
		joiningGame.setListener(null);
		gameStarter.setJoiningGame(null);
		view.dismissJoiningProgress();

		gameStarter.setJoinPhaseMultiPlayerConnector(connector);
		navigator.showJoinMultiPlayerSetup(mapDefinition);
	}

	private void updateViewJoinableGames() {
		List<IJoinableGame> joinableGames = changingJoinableGames.getItems();
		view.updateJoinableGames(joinableGames);

		if (joinableGames.size() > 0) {
			view.hideSearchingForGamesView();
		} else {
			view.showSearchingForGamesView();
		}
	}
}
