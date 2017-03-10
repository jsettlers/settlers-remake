package jsettlers.main.android.mainmenu.ui.fragments.setup;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.setup.NewMultiPlayerSetupPresenter;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupFragment extends MapSetupFragment implements NewMultiPlayerSetupView {
    private static final String ARG_MAP_ID = "mapid";

    private NewMultiPlayerSetupPresenter presenter;

    public static Fragment create(IMapDefinition mapDefinition) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_MAP_ID, mapDefinition.getMapId());

        Fragment fragment = new NewMultiPlayerSetupFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected NewMultiPlayerSetupPresenter getPresenter() {
        presenter = PresenterFactory.createNewMultiPlayerSetupPresenter(getActivity(), this, getArguments().getString(ARG_MAP_ID));
        return presenter;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //TODO these are disabled as the functionality doesnt exist for multiplayer games yet
        Spinner numberOfPlayersSpinner = (Spinner) view.findViewById(R.id.spinner_number_of_players);
        Spinner startResourcesSpinner = (Spinner) view.findViewById(R.id.spinner_start_resources);
        Spinner peacetimeSpinner = (Spinner) view.findViewById(R.id.spinner_peacetime);
        numberOfPlayersSpinner.setEnabled(false);
        startResourcesSpinner.setEnabled(false);
        peacetimeSpinner.setEnabled(false);
    }

    @Override
    protected int getListItemLayoutId() {
        return R.layout.item_multiplayer_playerslot;
    }
}
