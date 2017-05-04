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

import org.androidannotations.annotations.EFragment;

import jsettlers.main.android.R;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.picker.MapPickerPresenter;

import android.support.v4.app.Fragment;

/**
 * Created by tompr on 19/01/2017.
 */
@EFragment(R.layout.fragment_map_picker)
public class LoadSinglePlayerPickerFragment extends MapPickerFragment {
	public static Fragment newInstance() {
		return new LoadSinglePlayerPickerFragment_();
	}

	@Override
	protected MapPickerPresenter createPresenter() {
		return PresenterFactory.createLoadSinglePlayerPickerPresenter(getActivity(), this);
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.load_single_player_game);
	}

	@Override
	protected boolean showMapDates() {
		return true;
	}
}
