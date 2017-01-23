package jsettlers.main.android.mainmenu.ui.fragments;

import jsettlers.main.android.mainmenu.presenters.NewSinglePlayerSetupPresenter;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.views.NewSinglePlayerSetupView;

import android.support.v4.app.Fragment;

public class NewSinglePlayerSetupFragment extends MapSetupFragment implements NewSinglePlayerSetupView {

    public static Fragment create() {
        return new NewSinglePlayerSetupFragment();
    }

    @Override
    protected NewSinglePlayerSetupPresenter getPresenter() {
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        MainMenuNavigator navigator = (MainMenuNavigator) getActivity();
        return new NewSinglePlayerSetupPresenter(this, gameStarter, navigator);
    }
}
