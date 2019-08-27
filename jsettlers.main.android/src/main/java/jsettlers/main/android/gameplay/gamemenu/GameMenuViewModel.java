/*
 * Copyright (c) 2018
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

package jsettlers.main.android.gameplay.gamemenu;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.view.View;

import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.GameMenu;
import jsettlers.main.android.core.events.SingleLiveEvent;

public class GameMenuViewModel extends ViewModel {
	@NonNull
	private final Application application;
	@NonNull
	private final GameMenu gameMenu;

	private final LiveData<String> quitTextLiveData;
	private final LiveData<String> pauseTextLiveData;
	private final LiveData<String> gameSpeedTextLiveData;
	private final LiveData<Integer> gameSpeedLiveData;
	private final SingleLiveEvent<Void> gameQuittedLiveData = new SingleLiveEvent<>();

	public GameMenuViewModel(
			@NonNull Application application,
			@NonNull GameMenu gameMenu) {
		this.application = application;
		this.gameMenu = gameMenu;

		quitTextLiveData = Transformations.map(gameMenu.getGameState(), this::mapQuitText);
		pauseTextLiveData = Transformations.map(gameMenu.isPausedState(), this::mapPausedText);
		gameSpeedTextLiveData = Transformations.map(gameMenu.getGameSpeed(), this::mapGameSpeedText);
		gameSpeedLiveData = Transformations.map(gameMenu.getGameSpeed(), this::mapGameSpeed);
	}

	public LiveData<String> getQuitText() {
		return quitTextLiveData;
	}

	public LiveData<String> getPauseText() {
		return pauseTextLiveData;
	}

	public LiveData<String> getGameSpeedText() {
		return gameSpeedTextLiveData;
	}

	public LiveData<Integer> getGameSpeed() {
		return gameSpeedLiveData;
	}

	public SingleLiveEvent<Void> getGameQuitted() {
		return gameQuittedLiveData;
	}

	public int getShowGameSpeedControl() {
		return gameMenu.isMultiplayer() ? View.GONE : View.VISIBLE;
	}

	public int getShowSkipMinute() {
		return gameMenu.isMultiplayer() ? View.GONE : View.VISIBLE;
	}

	public void quitClicked() {
		if (gameMenu.getGameState().getValue() == GameMenu.GameState.CONFIRM_QUIT) {
			gameMenu.quitConfirm();
			gameQuittedLiveData.call();
		} else {
			gameMenu.quit();
		}
	}

	public void saveClicked() {
		gameMenu.save();
	}

	public void pauseClicked() {
		if (gameMenu.isPausedState().getValue() == Boolean.TRUE) {
			gameMenu.unPause();
		} else {
			gameMenu.pause();
		}
	}

	public void skipMinuteClicked() {
		gameMenu.skipMinute();
	}

	public void gameSpeedMoved(int progress) {
		float speed = (progress + 1f) / 2f;
		gameMenu.setGameSpeed(speed);
	}

	private String mapQuitText(GameMenu.GameState gameState) {
		return gameState == GameMenu.GameState.CONFIRM_QUIT ? application.getString(R.string.game_menu_quit_confirm) : application.getString(R.string.game_menu_quit);
	}

	private String mapPausedText(boolean isPaused) {
		return isPaused ? application.getString(R.string.game_menu_unpause) : application.getString(R.string.game_menu_pause);
	}

	private String mapGameSpeedText(float gameSpeed) {
		return application.getString(R.string.game_menu_speed_title, gameSpeed);
	}

	private int mapGameSpeed(float gameSpeed) {
		return Math.round((gameSpeed * 2) - 1);
	}
}
