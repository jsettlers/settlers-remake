package jsettlers.main.android.Menus;

import android.content.Context;

import go.graphics.android.AndroidSoundPlayer;
import jsettlers.common.menu.action.EActionType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.MapContent;

/**
 * Created by tompr on 19/11/2016.
 */

public class GameMenu {
    private Context context;
    private MapContent mapContent;
    private AndroidSoundPlayer soundPlayer;

    private boolean paused = false;

    public GameMenu(Context context, MapContent mapContent, AndroidSoundPlayer soundPlayer) {
        this.context = context;
        this.mapContent = mapContent;
        this.soundPlayer = soundPlayer;
    }

    public void save() {

    }

    public void pause() {
        mapContent.fireAction(new Action(EActionType.SPEED_SET_PAUSE));
        mute();
        paused = true;
    }

    public void unPause() {
        mapContent.fireAction(new Action(EActionType.SPEED_UNSET_PAUSE));
        unMute();
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public void quit() {
    }

    public void mute() {
        soundPlayer.setPaused(true);
    }

    public void unMute() {
        soundPlayer.setPaused(false);
    }
}
