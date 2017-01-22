package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import jsettlers.main.android.menus.mainmenu.NewSinglePlayerSetupMenu;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.views.NewSinglePlayerSetupView;

import android.support.v4.app.Fragment;

public class NewSinglePlayerSetupFragment extends MapSetupFragment implements NewSinglePlayerSetupView {

    public static Fragment create() {
        return new NewSinglePlayerSetupFragment();
    }

    @Override
    protected NewSinglePlayerSetupMenu getPresenter() {
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        return new NewSinglePlayerSetupMenu(this, gameStarter);
    }
}
