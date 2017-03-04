package jsettlers.main.android.mainmenu.ui.fragments.setup;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.setup.JoinMultiPlayerSetupPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.MapSetupPresenter;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerSetupView;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerSetupFragment extends MapSetupFragment implements JoinMultiPlayerSetupView {
    private static final String ARG_MAP_ID = "mapid";

    private JoinMultiPlayerSetupPresenter presenter;

    public static Fragment create(IMapDefinition mapDefinition) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAP_ID, mapDefinition.getMapId());

        Fragment fragment = new JoinMultiPlayerSetupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected MapSetupPresenter getPresenter() {
        presenter = PresenterFactory.createJoinMultiPlayerSetupPresenter(getActivity(), this, getArguments().getString(ARG_MAP_ID));
        return presenter;
    }
}
