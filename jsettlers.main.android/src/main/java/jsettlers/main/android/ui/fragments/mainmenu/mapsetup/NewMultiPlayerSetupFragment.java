package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import android.support.v4.app.Fragment;

import jsettlers.main.android.presenters.NewMultiPlayerSetupPrenter;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupFragment extends MapSetupFragment implements NewMultiPlayerSetupView {
    public static Fragment create() {
        return new NewMultiPlayerSetupFragment();
    }

    @Override
    protected NewMultiPlayerSetupPrenter getPresenter() {
        GameStarter gameStarter = (GameStarter) getActivity().getApplication();
        return new NewMultiPlayerSetupPrenter(this, gameStarter);
    }
}
