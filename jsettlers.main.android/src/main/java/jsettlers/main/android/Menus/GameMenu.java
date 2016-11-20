package jsettlers.main.android.menus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import go.graphics.android.AndroidSoundPlayer;
import jsettlers.common.menu.action.EActionType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.MapContent;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_QUIT;
import static jsettlers.main.android.GameService.ACTION_UNPAUSE;

/**
 * Created by tompr on 19/11/2016.
 */

public class GameMenu {
    private Context context;
    private LocalBroadcastManager localBroadcastManager;
    private MapContent mapContent;
    private AndroidSoundPlayer soundPlayer;

    private boolean paused = false;

    public GameMenu(Context context, MapContent mapContent, AndroidSoundPlayer soundPlayer) {
        this.context = context;
        this.mapContent = mapContent;
        this.soundPlayer = soundPlayer;

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
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
    }

    // don't unmute here, MapFragment will unmute when receiving unpause broadcast if its visibile.
    public void unPause() {
        mapContent.fireAction(new Action(EActionType.SPEED_UNSET_PAUSE));
        paused = false;

        // Send a local broadcast so that any UI can update if necessary
        localBroadcastManager.sendBroadcast(new Intent(ACTION_UNPAUSE));
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
}
