package jsettlers.main.android;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_QUIT;
import static jsettlers.main.android.GameService.ACTION_QUIT_CONFIRM;
import static jsettlers.main.android.GameService.ACTION_SAVE;
import static jsettlers.main.android.GameService.ACTION_UNPAUSE;

import jsettlers.common.menu.IGameExitListener;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.menus.game.GameMenu;
import jsettlers.main.android.providers.GameManager;
import jsettlers.main.android.providers.GameStarter;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;


public class MainApplication extends Application implements GameStarter, GameManager, IGameExitListener {
	private StartScreenConnector startScreenConnector;
	private IStartingGame startingGame;

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
	@Override
	public StartScreenConnector getStartScreenConnector() {
		if (startScreenConnector == null) {
			startScreenConnector = new StartScreenConnector();
		}
		return startScreenConnector;
	}

	@Override
	public void startSinglePlayerGame(IMapDefinition mapDefinition) {
		startingGame = new StartScreenConnector().startSingleplayerGame(mapDefinition);
	}

	@Override
	public void loadSinglePlayerGame(IMapDefinition mapDefinition) {
		startingGame = new StartScreenConnector().loadSingleplayerGame(mapDefinition);
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

	@Override
	public IStartingGame getStartingGame() {
		return startingGame;
	}

	@Override
	public MapInterfaceConnector gameStarted(IStartedGame game) {
		controlsAdapter = new ControlsAdapter(getApplicationContext(), game);

		startService(new Intent(this, GameService.class));

		game.setGameExitListener(this);

		return controlsAdapter.getMapContent().getInterfaceConnector();
	}


	/**
	 * IGameExitedListener implementation
	 */
	@Override
	public void gameExited(IStartedGame game) {
		startingGame = null;
		controlsAdapter = null;

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
