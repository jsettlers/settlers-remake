package jsettlers.main.android.fragments;

import jsettlers.main.android.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

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
		Button newLocal =
		        (Button) view.findViewById(R.id.startscreen_local_new);
		newLocal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getJsettlersActivity().showFragment(new NewLocalGameFragment());
			}
		});

		Button loadLocal =
		        (Button) view.findViewById(R.id.startscreen_local_load);
		loadLocal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getJsettlersActivity()
				        .showFragment(new LoadLocalGameFragment());
			}
		});

		Button newNetwork =
		        (Button) view.findViewById(R.id.startscreen_network_new);
		newNetwork.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getJsettlersActivity().showFragment(
				        new NewNetworkGameFragment());
			}
		});

		Button joinNetwork =
		        (Button) view.findViewById(R.id.startscreen_network_join);
		joinNetwork.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getJsettlersActivity().showFragment(
				        new JoinNetworkGameFragment());
			}
		});
	}

	@Override
	public boolean shouldAddToBackStack() {
		return false;
	}
}
