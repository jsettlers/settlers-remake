package jsettlers.main.android.mainmenu.presenters;

import java.util.List;

import jsettlers.common.menu.IMultiplayerPlayer;

/**
 * Created by tompr on 03/02/2017.
 */
public interface NewMultiPlayerSetupPresenter extends MapSetupPresenter {
    List<IMultiplayerPlayer> getPlayers();

    String getMyPlayerId();
}
