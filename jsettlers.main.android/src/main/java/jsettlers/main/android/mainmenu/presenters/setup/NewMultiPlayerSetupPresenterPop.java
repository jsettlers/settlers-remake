package jsettlers.main.android.mainmenu.presenters.setup;

import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerCount;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartResources;

/**
 * Created by tompr on 03/02/2017.
 */

public class NewMultiPlayerSetupPresenterPop implements NewMultiPlayerSetupPresenter {
    public NewMultiPlayerSetupPresenterPop(MainMenuNavigator navigator) {
        navigator.popToMenuRoot();
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

    @Override
    public void playerCountSelected(PlayerCount item) {

    }

    @Override
    public void startResourcesSelected(StartResources item) {

    }
}
