/*******************************************************************************
 * Copyright (c) 2015
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
 *******************************************************************************/
package jsettlers.main.android.fragments;

import jsettlers.common.CommitInfo;
import jsettlers.main.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class StartScreenFragment extends JsettlersFragment {

	@Override
	public String getName() {
		return "start";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.startscreen, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Button newLocal = (Button) view
				.findViewById(R.id.startscreen_local_new);
		newLocal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getJsettlersActivity().showFragment(new NewLocalGameFragment());
			}
		});

		Button loadLocal = (Button) view
				.findViewById(R.id.startscreen_local_load);
		loadLocal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNetworkFragment(new LoadLocalGameFragment());
			}
		});

		Button newNetwork = (Button) view
				.findViewById(R.id.startscreen_network_new);
		newNetwork.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNetworkFragment(
				new NewNetworkGameFragment());
			}
		});

		Button joinNetwork = (Button) view
				.findViewById(R.id.startscreen_network_join);
		joinNetwork.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNetworkFragment(new JoinNetworkGameFragment());
			}
		});

		Button preferencesButton = (Button) view
				.findViewById(R.id.startscreen_preferences_button);
		preferencesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showNetworkFragment(new PreferencesFragment());
			}
		});

		TextView rev = (TextView) view.findViewById(R.id.startscreen_rev);
		rev.setText("build: " + CommitInfo.COMMIT_HASH_SHORT);
	}

	private void showNetworkFragment(JsettlersFragment joinFragement) {
		if (getJsettlersActivity().getPrefs()
				.hasMissingMultiplayerPreferences()) {
			PreferencesFragment fragment = new PreferencesFragment();
			fragment.setReturnTo(joinFragement);
			getJsettlersActivity().showFragment(fragment);
		} else {
			getJsettlersActivity().showFragment(joinFragement);
		}
	}

	@Override
	public boolean shouldAddToBackStack() {
		return false;
	}
}
