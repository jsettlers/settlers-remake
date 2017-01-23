package jsettlers.main.android.views;

import java.util.List;

import jsettlers.common.menu.IMultiplayerPlayer;

/**
 * Created by tompr on 22/01/2017.
 */

public interface NewMultiPlayerSetupView extends MapSetupView {
    void setItems(List<IMultiplayerPlayer> items);
}