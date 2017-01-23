package jsettlers.main.android;

import static jsettlers.main.android.controls.GameMenu.ACTION_PAUSE;
import static jsettlers.main.android.controls.GameMenu.ACTION_QUIT;
import static jsettlers.main.android.controls.GameMenu.ACTION_QUIT_CONFIRM;
import static jsettlers.main.android.controls.GameMenu.ACTION_SAVE;
import static jsettlers.main.android.controls.GameMenu.ACTION_UNPAUSE;

import jsettlers.common.menu.IGameExitListener;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IMultiplayerConnector;
import jsettlers.common.menu.IStartScreen;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.menu.Player;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.controls.GameMenu;
import jsettlers.main.android.providers.GameManager;
import jsettlers.main.android.providers.GameStarter;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;


public class MainApplication extends Application implements GameStarter, GameManager, IGameExitListener {
	private IStartScreen startScreen;
	private IMultiplayerConnector multiplayerConnector;
	private IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector;
	private IMapDefinition mapDefinition;
	private IStartingGame startingGame;
	private IJoiningGame joiningGame;

	private ControlsAdapter controlsAdapter;

	private LocalBroadcastManager localBroadcastManager;

	@Override
	public void onCreate() {
		super.onCreate();
		System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
		localBroadcastManager = LocalBroadcastManager.getInstance(this);

		//TODO register this only when a game starts
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
	public IStartScreen getStartScreen() {
		if (startScreen == null) {
			startScreen = new StartScreenConnector();
		}
		return startScreen;
	}


	@Override
	public IMultiplayerConnector getMultiPlayerConnector() {
		if (multiplayerConnector == null) {
			AndroidPreferences androidPreferences = new AndroidPreferences(this);
			Player player = SettingsManager.getInstance().getPlayer();// new Player(androidPreferences.getPlayerId(), androidPreferences.getPlayerName());
			multiplayerConnector = getStartScreen().getMultiplayerConnector(androidPreferences.getServer(), player);
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
	public IMapDefinition getMapDefinition() {
		return mapDefinition;
	}

	@Override
	public void setMapDefinition(IMapDefinition mapDefinition) {
		this.mapDefinition = mapDefinition;
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

		startService(new Intent(this, GameService.class));

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
		mapDefinition = null;
		joiningGame = null;
		joinPhaseMultiplayerGameConnector = null;

		if (multiplayerConnector != null) {
			closeMultiPlayerConnector();
		}

		// Send a local broadcast so that any UI can update if necessary and the service can stop itself
		localBroadcastManager.sendBroadcast(new Intent(ACTION_QUIT_CONFIRM));
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case ACTION_PAUSE:
					getControlsAdapter().getGameMenu().pause();
					break;
				case ACTION_UNPAUSE:
					getControlsAdapter().getGameMenu().unPause();
					break;
				case ACTION_SAVE:
					getControlsAdapter().getGameMenu().save();
					break;
				case ACTION_QUIT:
					getControlsAdapter().getGameMenu().quit();
					break;
				case ACTION_QUIT_CONFIRM:
					getControlsAdapter().getGameMenu().quitConfirm();
					break;
			}
		}
	};
}
