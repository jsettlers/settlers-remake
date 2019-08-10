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

package jsettlers.main.android.mainmenu.mappicker;

import org.androidannotations.annotations.EFragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;

/**
 * Created by tompr on 21/01/2017.
 */
@EFragment(R.layout.fragment_map_picker)
public class NewMultiPlayerPickerFragment extends MapPickerFragment {
	private static final String TAG_JOINING_PROGRESS_DIALOG = "joingingprogress";

	public static Fragment newInstance() {
		return new NewMultiPlayerPickerFragment_();
	}

	private NewMultiPlayerPickerViewModel viewModel;

	@Override
	protected MapPickerViewModel createViewModel() {
		viewModel = ViewModelProviders.of(this, new NewMultiPlayerPickerViewModel.Factory(getActivity())).get(NewMultiPlayerPickerViewModel.class);
		return viewModel;
	}

	@Override
	void setupToolbar() {
		super.setupToolbar();
		toolbar.setTitle(R.string.new_multi_player_game);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		viewModel.getMapSelectedEvent().observe(this, mapId -> {
			MainMenuNavigator mainMenuNavigator = (MainMenuNavigator) getActivity();
			mainMenuNavigator.showNewMultiPlayerSetup(mapId);
		});

		viewModel.getJoiningState().observe(this, joiningViewState -> {
			if (joiningViewState == null) {
				dismissJoiningProgress();
			} else {
				setJoiningProgress(joiningViewState.getState(), joiningViewState.getProgress());
			}
		});
	}

	private void setJoiningProgress(String stateString, int progressPercentage) {
		JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager().findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
		if (joiningProgressDialog == null) {
			JoiningGameProgressDialog.create(stateString, progressPercentage).show(getChildFragmentManager(), TAG_JOINING_PROGRESS_DIALOG);
		} else {
			joiningProgressDialog.setProgress(stateString, progressPercentage);
		}
	}

	private void dismissJoiningProgress() {
		JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager().findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
		if (joiningProgressDialog != null) {
			joiningProgressDialog.dismiss();
		}
	}
}
