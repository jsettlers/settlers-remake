package jsettlers.main.android.menus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import go.graphics.android.AndroidSoundPlayer;
import jsettlers.common.menu.action.EActionType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.MapContent;
import jsettlers.main.android.R;
import jsettlers.main.android.activities.GameActivity;
import jsettlers.main.android.navigation.Actions;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_QUIT;
import static jsettlers.main.android.GameService.ACTION_SAVE;
import static jsettlers.main.android.GameService.ACTION_UNPAUSE;

/**
 * Created by tompr on 19/11/2016.
 */

public class GameMenu {
    public static final int NOTIFICATION_ID = 100;

    private Context context;
    private LocalBroadcastManager localBroadcastManager;
    private NotificationManager notificationManager;

    private MapContent mapContent;
    private AndroidSoundPlayer soundPlayer;

    private boolean paused = false;

    public GameMenu(Context context, MapContent mapContent, AndroidSoundPlayer soundPlayer) {
        this.context = context;
        this.mapContent = mapContent;
        this.soundPlayer = soundPlayer;

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void save() {

    }

    // mute the game when pausing whether or not its currently visibile
    public void pause() {
        mapContent.fireAction(new Action(EActionType.SPEED_SET_PAUSE));
        mute();
        paused = true;

        // Send a local broadcast so that any UI can update if necessary
        localBroadcastManager.sendBroadcast(new Intent(ACTION_PAUSE));
        notificationManager.notify(NOTIFICATION_ID, createNotification());
    }

    // don't unmute here, MapFragment will unmute when receiving unpause broadcast if its visibile.
    public void unPause() {
        mapContent.fireAction(new Action(EActionType.SPEED_UNSET_PAUSE));
        paused = false;

        // Send a local broadcast so that any UI can update if necessary
        localBroadcastManager.sendBroadcast(new Intent(ACTION_UNPAUSE));
        notificationManager.notify(NOTIFICATION_ID, createNotification());
    }

    public boolean isPaused() {
        return paused;
    }

    public void quit() {
        // Trigger quit from here and callback in GameService broadcasts after quit is complete
        mapContent.fireAction(new Action(EActionType.EXIT));
    }

    public void mute() {
        soundPlayer.setPaused(true);
    }

    public void unMute() {
        soundPlayer.setPaused(false);
    }



    public Notification createNotification() {
        Intent gameActivityIntent = new Intent(context, GameActivity.class);
        gameActivityIntent.setAction(Actions.RESUME_GAME);
        PendingIntent gameActivityPendingIntent = PendingIntent.getActivity(context, 0, gameActivityIntent, 0);

        PendingIntent quitPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_QUIT), 0);
        PendingIntent savePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SAVE), 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Settlers game in progress")
                .setContentIntent(gameActivityPendingIntent)
                .addAction(R.drawable.ic_stop, "Quit", quitPendingIntent)
                .addAction(R.drawable.ic_save, "Save", savePendingIntent);

        if (isPaused()) {
            PendingIntent unPausePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_UNPAUSE), 0);
            builder.addAction(R.drawable.ic_play, "Unpause", unPausePendingIntent);
        } else {
            PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_PAUSE), 0);
            builder.addAction(R.drawable.ic_pause, "Pause", pausePendingIntent);
        }

        return builder.build();
    }
}
