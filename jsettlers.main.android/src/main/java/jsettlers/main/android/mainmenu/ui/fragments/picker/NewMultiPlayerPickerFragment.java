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

package jsettlers.main.android.mainmenu.ui.fragments.picker;

import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.picker.MapPickerPresenter;
import jsettlers.main.android.mainmenu.ui.dialogs.JoiningGameProgressDialog;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerPickerView;

import android.support.v4.app.Fragment;

/**
 * Created by tompr on 21/01/2017.
 */
public class NewMultiPlayerPickerFragment extends MapPickerFragment implements NewMultiPlayerPickerView {
	private static final String TAG_JOINING_PROGRESS_DIALOG = "joingingprogress";

	public static Fragment newInstance() {
		return new NewMultiPlayerPickerFragment();
	}

	@Override
	protected MapPickerPresenter getPresenter() {
		return PresenterFactory.createNewMultiPlayerPickerPresenter(getActivity(), this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.new_multi_player_game);
	}

	/**
	 * NewMultiPlayerPickerView implementation
	 */
	@Override
	public void setJoiningProgress(String stateString, int progressPercentage) {
		JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager()
				.findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
		if (joiningProgressDialog == null) {
			JoiningGameProgressDialog.create(stateString, progressPercentage).show(getChildFragmentManager(), TAG_JOINING_PROGRESS_DIALOG);
		} else {
			joiningProgressDialog.setProgress(stateString, progressPercentage);
		}
	}

	@Override
	public void dismissJoiningProgress() {
		JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager()
				.findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
		if (joiningProgressDialog != null) {
			joiningProgressDialog.dismiss();
		}
	}
}
