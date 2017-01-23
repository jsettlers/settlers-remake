package jsettlers.main.android;

import static jsettlers.main.android.controls.GameMenu.ACTION_QUIT_CONFIRM;
import static jsettlers.main.android.controls.GameMenu.NOTIFICATION_ID;

import jsettlers.main.android.providers.GameManager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class GameService extends Service {
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
}
