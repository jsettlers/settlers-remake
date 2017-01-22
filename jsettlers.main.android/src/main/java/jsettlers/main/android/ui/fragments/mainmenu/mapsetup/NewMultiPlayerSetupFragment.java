package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import android.support.v4.app.Fragment;

import jsettlers.main.android.presenters.NewMultiPlayerSetupPresenter;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupFragment extends MapSetupFragment implements NewMultiPlayerSetupView {
    public static Fragment create() {
        return new NewMultiPlayerSetupFragment();
    }

    @Override
    protected NewMultiPlayerSetupPresenter getPresenter() {
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        MainMenuNavigator navigator = (MainMenuNavigator) getActivity();
        return new NewMultiPlayerSetupPresenter(this, gameStarter, navigator);
    }
}
