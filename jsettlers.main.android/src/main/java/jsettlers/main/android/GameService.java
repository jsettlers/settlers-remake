package jsettlers.main.android;

import static jsettlers.main.android.menus.game.GameMenu.NOTIFICATION_ID;

import jsettlers.common.menu.IGameExitListener;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.menus.game.GameMenu;
import jsettlers.main.android.providers.GameManager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class GameService extends Service {
    public static final String ACTION_PAUSE = "com.jsettlers.pause";
    public static final String ACTION_UNPAUSE = "com.jsettlers.unpause";
    public static final String ACTION_SAVE = "com.jsettlers.save";
    public static final String ACTION_QUIT = "com.jsettlers.quit";
    public static final String ACTION_QUIT_CONFIRM = "com.jsettlers.quitconfirm";
    public static final String ACTION_QUIT_CANCELLED = "com.jsettlers.quitcancelled";

    //private GameBinder gameBinder = new GameBinder();
    private LocalBroadcastManager localBroadcastManager;

    @Override
    public void onCreate() {
        super.onCreate();
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_QUIT_CONFIRM);
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        GameManager gameManager = (GameManager) getApplication();
        startForeground(NOTIFICATION_ID, gameManager.getGameMenu().createNotification());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
        //return gameBinder;
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_QUIT_CONFIRM:
                    stopForeground(true);
                    stopSelf();
                    break;
            }
        }
    };

//    /**
//     * Binder
//     */
//    public class GameBinder extends Binder {
//        public GameService getService() {
//            return GameService.this;
//        }
//    }
}
