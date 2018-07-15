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

package jsettlers.main.android.mainmenu.gamesetup;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v4.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;

import jsettlers.main.android.R;

/**
 * Created by tompr on 21/01/2017.
 */
@EFragment(R.layout.fragment_new_single_player_setup)
public class NewMultiPlayerSetupFragment extends MapSetupFragment {

	private NewMultiPlayerSetupViewModel viewModel;

	public static Fragment create(String mapId) {
		return NewMultiPlayerSetupFragment_.builder().mapId(mapId).build();
	}

	@Override
	protected MapSetupViewModel createViewModel() {
		viewModel = ViewModelProviders.of(this, new NewMultiPlayerSetupViewModel.Factory(getActivity(), mapId)).get(NewMultiPlayerSetupViewModel.class);
		return viewModel;
	}

	@AfterViews
	void disableUnavailableSpinners() {
		numberOfPlayersSpinner.setEnabled(false);
		startResourcesSpinner.setEnabled(false);
		peacetimeSpinner.setEnabled(false);
	}

	@Override
	protected int getListItemLayoutId() {
		return R.layout.item_multiplayer_playerslot;
	}
}
