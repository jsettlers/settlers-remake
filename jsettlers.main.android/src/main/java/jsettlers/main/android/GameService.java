package jsettlers.main.android;

import static jsettlers.main.android.menus.GameMenu.NOTIFICATION_ID;

import go.graphics.android.AndroidSoundPlayer;

import jsettlers.common.menu.IGameExitListener;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.graphics.androidui.menu.IFragmentHandler;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.menus.GameMenu;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class GameService extends Service implements IGameExitListener {
    public static final String ACTION_PAUSE = "com.jsettlers.pause";
    public static final String ACTION_UNPAUSE = "com.jsettlers.unpause";
    public static final String ACTION_SAVE = "com.jsettlers.save";
    public static final String ACTION_QUIT = "com.jsettlers.quit";
    public static final String ACTION_QUIT_CONFIRM = "com.jsettlers.quitconfirm";
    public static final String ACTION_QUIT_CANCELLED = "com.jsettlers.quitcancelled";

    private static final int SOUND_THREADS = 6;

    private GameBinder gameBinder = new GameBinder();
    private LocalBroadcastManager localBroadcastManager;

    private IStartingGame startingGame;
    private MapContent mapContent;
    private AndroidSoundPlayer soundPlayer;
    private ControlsAdapter controlsAdapter;
    private GameMenu gameMenu;

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_UNPAUSE);
        intentFilter.addAction(ACTION_SAVE);
        intentFilter.addAction(ACTION_QUIT);
        intentFilter.addAction(ACTION_QUIT_CONFIRM);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return gameBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * GameService API
     */
    public boolean isGameInProgress() {
        return mapContent != null;
    }

    public void startSinglePlayerGame(IMapDefinition mapDefinition) {
        startingGame = new StartScreenConnector().startSingleplayerGame(mapDefinition);
    }

    public IStartingGame getStartingGame() {
        return startingGame;
    }

    public MapInterfaceConnector gameStarted(IStartedGame game, IFragmentHandler fragmentHandler) {
        game.setGameExitListener(this);

        soundPlayer = new AndroidSoundPlayer(SOUND_THREADS);
        controlsAdapter = new ControlsAdapter(getApplicationContext(), soundPlayer, game.getInGamePlayer());
        mapContent = new MapContent(game, soundPlayer, controlsAdapter);
        gameMenu = controlsAdapter.getGameMenu();

        startForeground(NOTIFICATION_ID, gameMenu.createNotification());

        return mapContent.getInterfaceConnector();
    }

    public MapContent getMapContent() {
        return mapContent;
    }

    public ControlsAdapter getControls() {
        return controlsAdapter;
    }

    public GameMenu getGameMenu() {
        return gameMenu;
    }

    /**
     * IGameExitedListener implementation
     */
    @Override
    public void gameExited(IStartedGame game) {
        stopForeground(true);
        stopSelf();

        startingGame = null;
        mapContent = null;
        soundPlayer = null;
        gameMenu = null;
        controlsAdapter = null;

        // Send a local broadcast so that any UI can update if necessary
        localBroadcastManager.sendBroadcast(new Intent(ACTION_QUIT_CONFIRM));
    }

    /**
     * Binder
     */
    public class GameBinder extends Binder {
        public GameService getService() {
            return GameService.this;
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_PAUSE:
                    gameMenu.pause();
                    break;
                case ACTION_UNPAUSE:
                    gameMenu.unPause();
                    break;
                case ACTION_SAVE:
                    gameMenu.save();
                    break;
                case ACTION_QUIT:
                    gameMenu.quit();
                    break;
                case ACTION_QUIT_CONFIRM:
                    gameMenu.quitConfirm();
                    break;
            }
        }
    };
}
