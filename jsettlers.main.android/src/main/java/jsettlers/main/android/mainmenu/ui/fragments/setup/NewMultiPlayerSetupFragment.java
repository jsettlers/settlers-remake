/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.main.android.mainmenu.ui.fragments.setup;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.setup.NewMultiPlayerSetupPresenter;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerSetupView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Spinner;

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

		// TODO these are disabled as the functionality doesnt exist for multiplayer games yet
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
