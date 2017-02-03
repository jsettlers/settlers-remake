package jsettlers.main.android.mainmenu.presenters;

import java.util.ArrayList;
import java.util.List;

import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;

/**
 * Created by tompr on 03/02/2017.
 */

public class NewMultiPlayerSetupPresenterPop implements NewMultiPlayerSetupPresenter {
    public NewMultiPlayerSetupPresenterPop(MainMenuNavigator navigator) {
        navigator.popToMenuRoot();
    }

    @Override
    public List<IMultiplayerPlayer> getPlayers() {
        return new ArrayList<>();
    }

    @Override
    public String getMyPlayerId() {
        return "";
    }

    @Override
    public void initView() {

    }

    @Override
    public void updateViewTitle() {

    }

    @Override
    public void viewFinished() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void startGame() {

    }
}
