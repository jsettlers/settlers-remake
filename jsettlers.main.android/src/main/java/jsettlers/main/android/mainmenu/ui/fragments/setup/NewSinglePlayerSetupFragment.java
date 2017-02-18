package jsettlers.main.android.mainmenu.ui.fragments.setup;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.setup.NewSinglePlayerSetupPresenter;
import jsettlers.main.android.mainmenu.views.NewSinglePlayerSetupView;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class NewSinglePlayerSetupFragment extends MapSetupFragment implements NewSinglePlayerSetupView {
    private static final String ARG_MAP_ID = "mapid";

    public static Fragment create(IMapDefinition mapDefinition) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAP_ID, mapDefinition.getMapId());

        Fragment fragment = new NewSinglePlayerSetupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected NewSinglePlayerSetupPresenter getPresenter() {
        return PresenterFactory.createNewSinglePlayerSetupPresenter(getActivity(), this, getArguments().getString(ARG_MAP_ID));
    }
}
