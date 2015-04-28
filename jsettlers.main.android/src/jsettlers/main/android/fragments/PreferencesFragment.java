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

import jsettlers.main.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PreferencesFragment extends JsettlersFragment {
	private JsettlersFragment returnTo;

	public PreferencesFragment() {
	}

	public void setReturnTo(JsettlersFragment fragement) {
		this.returnTo = fragement;
	}

	@Override
	public String getName() {
		return "prefs";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.preferences, container, false);
	}

	@Override
	public void onViewCreated(final View view, Bundle savedInstanceState) {
		((TextView) view.findViewById(R.id.preferences_playername))
				.setText(getJsettlersActivity().getPrefs().getPlayerName());
		((TextView) view.findViewById(R.id.preferences_server))
				.setText(getJsettlersActivity().getPrefs().getServer());

		((Button) view.findViewById(R.id.preferences_ok))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String playerName = ((TextView) view
								.findViewById(R.id.preferences_playername))
								.getText().toString();
						getJsettlersActivity().getPrefs().setPlayerName(
								playerName);
						String serverName = ((TextView) view
								.findViewById(R.id.preferences_server))
								.getText().toString();
						getJsettlersActivity().getPrefs().setServer(serverName);

						if (returnTo != null) {
							getJsettlersActivity().showFragment(returnTo);
						} else {
							getJsettlersActivity().showStartScreen();
						}
					}
				});
		super.onViewCreated(view, savedInstanceState);
	}
}
