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
