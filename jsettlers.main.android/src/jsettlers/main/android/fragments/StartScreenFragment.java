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
