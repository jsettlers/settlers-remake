package jsettlers.main.android;

import go.graphics.android.AndroidSoundPlayer;

import jsettlers.common.menu.IGameExitListener;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.graphics.androidui.MobileControls;
import jsettlers.graphics.androidui.menu.AndroidMenuPutable;
import jsettlers.graphics.androidui.menu.IFragmentHandler;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.activities.GameActivity;
import jsettlers.main.android.navigation.Actions;
import jsettlers.main.android.navigation.QuitListener;
import jsettlers.main.android.providers.GameMenuProvider;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class GameService extends Service implements GameMenuProvider, IGameExitListener {
    private static final String ACTION_PAUSE = "com.jsettlers.pause";
    private static final String ACTION_SAVE = "com.jsettlers.save";
    private static final String ACTION_QUIT = "com.jsettlers.quit";

    private static final int SOUND_THREADS = 6;

    private IStartingGame startingGame;
    private MapContent mapContent;
    private AndroidSoundPlayer soundPlayer;

    private GameMenu gameMenu;

    private QuitListener quitListener;

    private GameBinder gameBinder = new GameBinder();

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_PAUSE:
                    gameMenu.pause();
                    break;
                case ACTION_SAVE:
                    gameMenu.save();
                    break;
                case ACTION_QUIT:
                    gameMenu.quit();
                    break;
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PAUSE);
        intentFilter.addAction(ACTION_SAVE);
        intentFilter.addAction(ACTION_QUIT);
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
        return startingGame != null || mapContent != null;
    }

    public void startSinglePlayerGame(IMapDefinition mapDefinition) {
        Intent gameActivityIntent = new Intent(this, GameActivity.class);
        gameActivityIntent.setAction(Actions.RESUME_GAME);
        PendingIntent gameActivityPendingIntent = PendingIntent.getActivity(this, 0, gameActivityIntent, 0);

        PendingIntent quitPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_QUIT), 0);
        PendingIntent savePendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_SAVE), 0);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_PAUSE), 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Settlers game in progress")
                .setContentIntent(gameActivityPendingIntent)
                .addAction(R.drawable.ic_quit, "Quit", quitPendingIntent)
                .addAction(R.drawable.ic_save, "Save", savePendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
                .build();

        startForeground(100, notification);

        startingGame = new StartScreenConnector().startSingleplayerGame(mapDefinition);
    }

    public IStartingGame getStartingGame() {
        return startingGame;
    }

    public MapInterfaceConnector gameStarted(IStartedGame game, IFragmentHandler fragmentHandler) {
        // startingGame == null ??????

        soundPlayer = new AndroidSoundPlayer(SOUND_THREADS);
        mapContent = new MapContent(game, soundPlayer, new MobileControls(new AndroidMenuPutable(this, fragmentHandler)));

        game.setGameExitListener(this);

        gameMenu = new GameMenu(getApplicationContext(), mapContent, soundPlayer);

        return mapContent.getInterfaceConnector();
    }

    public MapContent getMapContent() {
        return mapContent;
    }

    public void setQuitListener(QuitListener quitListener) {
        this.quitListener = quitListener;
    }

    /**
     * GameMenuProvider implementation
     */
    @Override
    public GameMenu getGameMenu() {
        return gameMenu;
    }



    @Override
    public void gameExited(IStartedGame game) {
        stopForeground(true);
        stopSelf();

        if (quitListener != null) {
            quitListener.onQuit();
            startingGame = null;
            mapContent = null;
            soundPlayer = null;
        }
    }

    /**
     * Binder
     */
    public class GameBinder extends Binder {
        public GameService getService() {
            return GameService.this;
        }
    }
}
