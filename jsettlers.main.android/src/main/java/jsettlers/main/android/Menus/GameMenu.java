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

    public void pause() {
        mapContent.fireAction(new Action(EActionType.SPEED_SET_PAUSE));
        mute();
        paused = true;

        // Send a local broadcast so that any UI can deal with it
        Intent intent = new Intent(ACTION_PAUSE);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void unPause() {
        mapContent.fireAction(new Action(EActionType.SPEED_UNSET_PAUSE));
        unMute();
        paused = false;

        // Send a local broadcast so that any UI can deal with it
        Intent intent = new Intent(ACTION_UNPAUSE);
        localBroadcastManager.sendBroadcast(intent);
    }

    public boolean isPaused() {
        return paused;
    }

    public void quit() {
        mapContent.fireAction(new Action(EActionType.EXIT));

        // Send a local broadcast so that any UI can deal with it
        Intent intent = new Intent(ACTION_QUIT);
        localBroadcastManager.sendBroadcast(intent);
    }

    public void mute() {
        soundPlayer.setPaused(true);
    }

    public void unMute() {
        soundPlayer.setPaused(false);
    }
}
