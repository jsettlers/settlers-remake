package jsettlers.main.android.Menus;

import go.graphics.android.AndroidSoundPlayer;
import jsettlers.common.menu.action.EActionType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.MapContent;

/**
 * Created by tompr on 19/11/2016.
 */

public class GameMenu {
    private MapContent mapContent;
    private AndroidSoundPlayer soundPlayer;

    public GameMenu(MapContent mapContent, AndroidSoundPlayer soundPlayer) {
        this.mapContent = mapContent;
        this.soundPlayer = soundPlayer;
    }

    public void save() {

    }

    public void pause() {
        mapContent.fireAction(new Action(EActionType.SPEED_SET_PAUSE));
        mute();
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
