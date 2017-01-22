package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import android.support.v4.app.Fragment;

import jsettlers.main.android.menus.mainmenu.NewMultiPlayerSetupMenu;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupFragment extends MapSetupFragment<NewMultiPlayerSetupMenu> implements NewMultiPlayerSetupView {
    public static Fragment create() {
        return new NewMultiPlayerSetupFragment();
    }

    @Override
    protected NewMultiPlayerSetupMenu getPresenter() {
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        return new NewMultiPlayerSetupMenu(this, gameStarter);
    }
}
