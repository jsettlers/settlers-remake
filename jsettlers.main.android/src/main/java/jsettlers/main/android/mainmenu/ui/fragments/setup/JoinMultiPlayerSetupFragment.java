package jsettlers.main.android.mainmenu.ui.fragments.setup;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.setup.JoinMultiPlayerSetupPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.MapSetupPresenter;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerSetupView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerSetupFragment extends MapSetupFragment implements JoinMultiPlayerSetupView {
	private static final String ARG_MAP_ID = "mapid";

	private JoinMultiPlayerSetupPresenter presenter;

	private Spinner numberOfPlayersSpinner;
	private Spinner startResourcesSpinner;
	private Spinner peacetimeSpinner;

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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);

		numberOfPlayersSpinner = (Spinner) view.findViewById(R.id.spinner_number_of_players);
		startResourcesSpinner = (Spinner) view.findViewById(R.id.spinner_start_resources);
		peacetimeSpinner = (Spinner) view.findViewById(R.id.spinner_peacetime);

		return view;
	}

	@Override
	protected int getListItemLayoutId() {
		return R.layout.item_multiplayer_playerslot;
	}

	/**
	 * JoinMultiPlayerSetupView implementation
	 */
	@Override
	public void disableHostOnlyControls() {
		numberOfPlayersSpinner.setEnabled(false);
		startResourcesSpinner.setEnabled(false);
		peacetimeSpinner.setEnabled(false);
	}
}
