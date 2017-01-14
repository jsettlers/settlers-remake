package jsettlers.main.android.menus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import java.util.Timer;
import java.util.TimerTask;

import go.graphics.android.AndroidSoundPlayer;
import jsettlers.common.menu.action.EActionType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.main.android.R;
import jsettlers.main.android.ui.activities.GameActivity;
import jsettlers.main.android.ui.navigation.Actions;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_QUIT;
import static jsettlers.main.android.GameService.ACTION_QUIT_CANCELLED;
import static jsettlers.main.android.GameService.ACTION_QUIT_CONFIRM;
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

    private ActionFireable actionFireable;
    private AndroidSoundPlayer soundPlayer;
    private Timer quitConfirmTimer;

    private boolean paused = false;

    public GameMenu(Context context, ActionFireable actionFireable, AndroidSoundPlayer soundPlayer) {
        this.context = context;
        this.actionFireable = actionFireable;
        this.soundPlayer = soundPlayer;

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void save() {

    }

    // mute the game when pausing whether or not its currently visibile
    public void pause() {
        actionFireable.fireAction(new Action(EActionType.SPEED_SET_PAUSE));
        mute();
        paused = true;

        // Send a local broadcast so that any UI can update if necessary
        localBroadcastManager.sendBroadcast(new Intent(ACTION_PAUSE));
        notificationManager.notify(NOTIFICATION_ID, createNotification());
    }

    // don't unmute here, MapFragment will unmute when receiving unpause broadcast if its visibile.
    public void unPause() {
        actionFireable.fireAction(new Action(EActionType.SPEED_UNSET_PAUSE));
        paused = false;

        // Send a local broadcast so that any UI can update if necessary
        localBroadcastManager.sendBroadcast(new Intent(ACTION_UNPAUSE));
        notificationManager.notify(NOTIFICATION_ID, createNotification());
    }

    public boolean isPaused() {
        return paused;
    }

    public void quit() {
        quitConfirmTimer = new Timer();

        quitConfirmTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (quitConfirmTimer != null) {
                    quitConfirmTimer = null;
                    notificationManager.notify(NOTIFICATION_ID, createNotification());
                    localBroadcastManager.sendBroadcast(new Intent(ACTION_QUIT_CANCELLED));
                }
            }
        }, 3000);

        // Send a local broadcast so that any UI can update if necessary
        localBroadcastManager.sendBroadcast(new Intent(ACTION_QUIT));
        notificationManager.notify(NOTIFICATION_ID, createNotification());
    }

    public void quitConfirm() {
        // Trigger quit from here and callback in GameService broadcasts after quit is complete
        quitConfirmTimer = null;
        actionFireable.fireAction(new Action(EActionType.EXIT));
    }

    public boolean canQuitConfirm() {
        return quitConfirmTimer != null;
    }

    public void mute() {
        soundPlayer.setPaused(true);
    }

    public void unMute() {
        soundPlayer.setPaused(false);
    }

    public Notification createNotification() {
        NotificationBuilder notificationBuilder = new NotificationBuilder(context);

        if (quitConfirmTimer == null) {
            notificationBuilder.addQuitButton();
        } else {
            notificationBuilder.addQuitConfirmButton();
        }

        notificationBuilder.addSaveButton();

        if (isPaused()) {
            notificationBuilder.addUnPauseButton();
        } else {
            notificationBuilder.addPauseButton();
        }

        return notificationBuilder.build();
    }



    private class NotificationBuilder {
        private final Context context;
        private final NotificationCompat.Builder builder;

        public NotificationBuilder(Context context) {
            this.context = context;

            Intent gameActivityIntent = new Intent(context, GameActivity.class);
            gameActivityIntent.setAction(Actions.RESUME_GAME);
            PendingIntent gameActivityPendingIntent = PendingIntent.getActivity(context, 0, gameActivityIntent, 0);

            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle("Settlers game in progress")
                    .setContentIntent(gameActivityPendingIntent);
        }

        public NotificationBuilder addQuitButton() {
            PendingIntent quitPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_QUIT), 0);
            builder.addAction(R.drawable.ic_stop, "Quit", quitPendingIntent);
            return this;
        }

        public NotificationBuilder addQuitConfirmButton() {
            PendingIntent quitPendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_QUIT_CONFIRM), 0);
            builder.addAction(R.drawable.ic_stop, "Sure?", quitPendingIntent);
            return this;
        }

        public NotificationBuilder addSaveButton() {
            PendingIntent savePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_SAVE), 0);
            builder.addAction(R.drawable.ic_save, "Save", savePendingIntent);
            return this;
        }

        public NotificationBuilder addPauseButton() {
            PendingIntent pausePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_PAUSE), 0);
            builder.addAction(R.drawable.ic_pause, "Pause", pausePendingIntent);
            return this;
        }

        public NotificationBuilder addUnPauseButton() {
            PendingIntent unPausePendingIntent = PendingIntent.getBroadcast(context, 0, new Intent(ACTION_UNPAUSE), 0);
            builder.addAction(R.drawable.ic_play, "Unpause", unPausePendingIntent);
            return this;
        }

        public Notification build() {
            return builder.build();
        }
    }
}
