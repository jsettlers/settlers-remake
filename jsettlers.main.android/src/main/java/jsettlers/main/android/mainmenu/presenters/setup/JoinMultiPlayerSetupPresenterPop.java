package jsettlers.main.android.mainmenu.presenters.setup;

import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;

/**
 * Created by tompr on 03/02/2017.
 */

public class JoinMultiPlayerSetupPresenterPop implements JoinMultiPlayerSetupPresenter {

    public JoinMultiPlayerSetupPresenterPop(MainMenuNavigator navigator) {
        navigator.popToMenuRoot();
    }

    @Override
    public void setReady(boolean ready) {

    }

    @Override
    public void viewFinished() {

    }

    @Override
    public void dispose() {

    }
}
