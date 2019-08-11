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

package jsettlers.main.android.mainmenu.home;

import java.io.File;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.controls.GameMenu;
import jsettlers.main.android.core.events.SingleLiveEvent;
import jsettlers.main.android.core.resources.scanner.AndroidResourcesLoader;

public class MainMenuViewModel extends ViewModel {

	private final GameManager gameManager;
	private final AndroidResourcesLoader androidResourcesLoader;

	private final ResumeStateData resumeStateData = new ResumeStateData();
	private final MutableLiveData<Boolean> areResourcesLoaded = new MutableLiveData<>();
	private final SingleLiveEvent<Void> showSinglePlayer = new SingleLiveEvent<>();
	private final SingleLiveEvent<Void> showLoadSinglePlayer = new SingleLiveEvent<>();
	private final SingleLiveEvent<Void> showMultiplayerPlayer = new SingleLiveEvent<>();
	private final SingleLiveEvent<Void> showJoinMultiplayerPlayer = new SingleLiveEvent<>();

	public MainMenuViewModel(GameManager gameManager, AndroidResourcesLoader androidResourcesLoader) {
		this.gameManager = gameManager;
		this.androidResourcesLoader = androidResourcesLoader;

		areResourcesLoaded.setValue(androidResourcesLoader.setup());
	}

	public LiveData<ResumeViewState> getResumeState() {
		return resumeStateData;
	}

	public LiveData<Boolean> getAreResourcesLoaded() {
		return areResourcesLoaded;
	}

	public LiveData<Void> getShowSinglePlayer() {
		return showSinglePlayer;
	}

	public LiveData<Void> getShowLoadSinglePlayer() {
		return showLoadSinglePlayer;
	}

	public LiveData<Void> getShowMultiplayerPlayer() {
		return showMultiplayerPlayer;
	}

	public LiveData<Void> getShowJoinMultiplayerPlayer() {
		return showJoinMultiplayerPlayer;
	}

	public void resourceDirectoryChosen(File resourceDirectory) {
		androidResourcesLoader.setResourcesDirectory(resourceDirectory.getAbsolutePath());

		Disposable resourceSetupSubscription = androidResourcesLoader.setupSingle()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(() -> {
					areResourcesLoaded.postValue(true);
				});
	}

	public void quitSelected() {
		if (gameManager.getGameMenu().getGameState().getValue() == GameMenu.GameState.CONFIRM_QUIT) {
			gameManager.getGameMenu().quitConfirm();
		} else {
			gameManager.getGameMenu().quit();
		}
	}

	public void pauseSelected() {
		if (gameManager.getGameMenu().isPausedState().getValue()) {
			gameManager.getGameMenu().unPause();
		} else {
			gameManager.getGameMenu().pause();
		}
	}

	public void newSinglePlayerSelected() {
		if (areResourcesLoaded.getValue() == Boolean.TRUE) {
			showSinglePlayer.call();
		}
	}

	public void loadSinglePlayerSelected() {
		if (areResourcesLoaded.getValue() == Boolean.TRUE) {
			showLoadSinglePlayer.call();
		}
	}

	public void newMultiPlayerSelected() {
		if (areResourcesLoaded.getValue() == Boolean.TRUE) {
			showMultiplayerPlayer.call();
		}
	}

	public void joinMultiPlayerSelected() {
		if (areResourcesLoaded.getValue() == Boolean.TRUE) {
			showJoinMultiplayerPlayer.call();
		}
	}

	/**
	 * ViewState for resume state
	 */
	public static class ResumeViewState {
		private final boolean isPaused;
		private final boolean confirmQuit;

		public ResumeViewState(boolean isPaused, boolean confirmQuit) {
			this.isPaused = isPaused;
			this.confirmQuit = confirmQuit;
		}

		public boolean isPaused() {
			return isPaused;
		}

		public boolean isConfirmQuit() {
			return confirmQuit;
		}
	}

	/**
	 * LiveData for resume state It monitors the current GameManager and relays state changes
	 */
	private class ResumeStateData extends MediatorLiveData<ResumeViewState> {
		private GameMenu gameMenu;

		@Override
		protected void onActive() {
			super.onActive();
			if (gameManager.isGameInProgress()) {
				GameMenu newGameMenu = gameManager.getGameMenu();

				if (gameMenu == newGameMenu) {
					return;
				}

				if (gameMenu != null) {
					removeSource(gameMenu.isPausedState());
					removeSource(gameMenu.getGameState());
				}

				gameMenu = newGameMenu;
				if (gameMenu != null) {
					addSource(gameMenu.isPausedState(), paused -> update());
					addSource(gameMenu.getGameState(), state -> update());
				} else {
					setValue(null);
				}
			} else {
				setValue(null);
				gameMenu = null;
			}
		}

		private void update() {
			if (gameMenu.getGameState().getValue() == GameMenu.GameState.QUITTED) {
				setValue(null);
			} else {
				boolean paused = gameMenu.isPausedState().getValue();
				boolean confirmQuit = gameMenu.getGameState().getValue() == GameMenu.GameState.CONFIRM_QUIT;
				setValue(new ResumeViewState(paused, confirmQuit));
			}
		}
	}

	/**
	 * ViewModel factory
	 */
	public static class Factory implements ViewModelProvider.Factory {

		private final Application application;
		private final GameManager gameManager;

		public Factory(Application application) {
			this.application = application;
			gameManager = (GameManager) application;
		}

		@Override
		public <T extends ViewModel> T create(Class<T> modelClass) {
			if (modelClass == MainMenuViewModel.class) {
				return (T) new MainMenuViewModel(
						gameManager,
						new AndroidResourcesLoader(application));
			}
			throw new RuntimeException("MainMenuViewModel.Factory doesn't know how to create a: " + modelClass.toString());
		}
	}
}
