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

package jsettlers.main.android.mainmenu.presenters;

import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.resources.scanner.AndroidResourcesLoader;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.MainMenuView;

/**
 * Created by tompr on 04/03/2017.
 */
public class MainMenuPresenter {
	private final MainMenuView view;
	private final MainMenuNavigator navigator;
	private final GameManager gameManager;
	private final AndroidResourcesLoader androidResourcesLoader;

	private boolean resourcesLoaded;

	public MainMenuPresenter(MainMenuView view, MainMenuNavigator navigator, GameManager gameManager, AndroidResourcesLoader androidResourcesLoader) {
		this.view = view;
		this.navigator = navigator;
		this.gameManager = gameManager;
		this.androidResourcesLoader = androidResourcesLoader;
		this.resourcesLoaded = androidResourcesLoader.setup();
	}

	public void initView() {
		if (!resourcesLoaded) {
			view.showResourcePicker();
		}
	}

	public void newSinglePlayerSelected() {
		if (resourcesLoaded) {
			navigator.showNewSinglePlayerPicker();
		}
	}

	public void loadSinglePlayerSelected() {
		if (resourcesLoaded) {
			navigator.showLoadSinglePlayerPicker();
		}
	}

	public void newMultiPlayerSelected() {
		if (resourcesLoaded) {
			navigator.showNewMultiPlayerPicker();
		}
	}

	public void joinMultiPlayerSelected() {
		if (resourcesLoaded) {
			navigator.showJoinMultiPlayerPicker();
		}
	}

	public void resumeSelected() {
		navigator.resumeGame();
	}

	public void quitSelected() {
		if (gameManager.getGameMenu().canQuitConfirm()) {
			gameManager.getGameMenu().quitConfirm();
		} else {
			gameManager.getGameMenu().quit();
		}
	}

	public void pauseSelected() {
		if (gameManager.getGameMenu().isPaused()) {
			gameManager.getGameMenu().unPause();
		} else {
			gameManager.getGameMenu().pause();
		}
		updateResumeGameView();
	}

	public void updateResumeGameView() {
		if (gameManager.isGameInProgress()) {
			view.updatePauseButton(gameManager.getGameMenu().isPaused());
			view.updateQuitButton(gameManager.getGameMenu().canQuitConfirm());
			view.showResumeGameView();
		} else {
			view.hideResumeGameView();
		}
	}

	public void resourceDirectoryChosen() {
		resourcesLoaded = androidResourcesLoader.setup();
		if (resourcesLoaded) {
			view.hideResourcePicker();
		} else {
			throw new RuntimeException("Resources not found or not valid after directory chosen by user");
		}
	}
}
