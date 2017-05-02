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

package jsettlers.main.android;

import static jsettlers.main.android.core.controls.GameMenu.ACTION_PAUSE;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CONFIRM;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_SAVE;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_UNPAUSE;

import org.androidannotations.annotations.EApplication;

import jsettlers.common.menu.IGameExitListener;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IMultiplayerConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.logic.map.loading.list.MapList;
import jsettlers.main.MultiplayerConnector;
import jsettlers.main.android.core.AndroidPreferences;
import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.GameService_;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.core.controls.ControlsAdapter;
import jsettlers.main.android.core.controls.GameMenu;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

@EApplication
public class MainApplication extends Application implements GameStarter, GameManager, IGameExitListener {
	private MapList mapList;
	private IMultiplayerConnector multiplayerConnector;
	private IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector;
	private IStartingGame startingGame;
	private IJoiningGame joiningGame;

	private ControlsAdapter controlsAdapter;

	private LocalBroadcastManager localBroadcastManager;

	@Override
	public void onCreate() {
		super.onCreate();
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
		localBroadcastManager = LocalBroadcastManager.getInstance(this);

		// TODO register this only when a game starts
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_PAUSE);
		intentFilter.addAction(ACTION_UNPAUSE);
		intentFilter.addAction(ACTION_SAVE);
		intentFilter.addAction(ACTION_QUIT);
		intentFilter.addAction(ACTION_QUIT_CONFIRM);
		registerReceiver(broadcastReceiver, intentFilter);
	}

	/**
	 * GameStarter implementation
	 */
	@Override
	public MapList getMapList() {
		if (mapList == null) {
			mapList = MapList.getDefaultList();
		}
		return mapList;
	}

	@Override
	public IMultiplayerConnector getMultiPlayerConnector() {
		if (multiplayerConnector == null) {
			AndroidPreferences androidPreferences = new AndroidPreferences(this);
			multiplayerConnector = new MultiplayerConnector(androidPreferences.getServer(), androidPreferences.getPlayerId(), androidPreferences.getPlayerName());
		}
		return multiplayerConnector;
	}

	@Override
	public void closeMultiPlayerConnector() {
		if (multiplayerConnector != null) {
			multiplayerConnector.shutdown();
			multiplayerConnector = null;
		}
	}

	@Override
	public IStartingGame getStartingGame() {
		return startingGame;
	}

	@Override
	public void setStartingGame(IStartingGame startingGame) {
		this.startingGame = startingGame;
	}

	@Override
	public IJoiningGame getJoiningGame() {
		return joiningGame;
	}

	@Override
	public void setJoiningGame(IJoiningGame joiningGame) {
		this.joiningGame = joiningGame;
	}

	@Override
	public IJoinPhaseMultiplayerGameConnector getJoinPhaseMultiplayerConnector() {
		return joinPhaseMultiplayerGameConnector;
	}

	@Override
	public void setJoinPhaseMultiPlayerConnector(IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector) {
		this.joinPhaseMultiplayerGameConnector = joinPhaseMultiplayerGameConnector;
	}

	@Override
	public IMapInterfaceConnector gameStarted(IStartedGame game) {
		controlsAdapter = new ControlsAdapter(getApplicationContext(), game);
		GameService_.intent(this).start();

		game.setGameExitListener(this);

		return controlsAdapter.getMapContent().getInterfaceConnector();
	}

	/**
	 * GameManager implementation
	 */
	@Override
	public ControlsAdapter getControlsAdapter() {
		return controlsAdapter;
	}

	@Override
	public GameMenu getGameMenu() {
		return controlsAdapter.getGameMenu();
	}

	@Override
	public boolean isGameInProgress() {
		return controlsAdapter != null;
	}

	/**
	 * IGameExitedListener implementation
	 */
	@Override
	public void gameExited(IStartedGame game) {
		controlsAdapter = null;
		startingGame = null;
		joiningGame = null;
		joinPhaseMultiplayerGameConnector = null;
		mapList = null; // Nulling this means that any new saved games will be available next time mapList is set

		closeMultiPlayerConnector();

		// Send a local broadcast so that any UI can update if necessary and the service can stop itself
		localBroadcastManager.sendBroadcast(new Intent(ACTION_QUIT_CONFIRM));
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case ACTION_PAUSE:
				controlsAdapter.getGameMenu().pause();
				break;
			case ACTION_UNPAUSE:
				controlsAdapter.getGameMenu().unPause();
				break;
			case ACTION_SAVE:
				controlsAdapter.getGameMenu().save();
				break;
			case ACTION_QUIT:
				controlsAdapter.getGameMenu().quit();
				break;
			case ACTION_QUIT_CONFIRM:
				controlsAdapter.getGameMenu().quitConfirm();
				break;
			}
		}
	};
}
