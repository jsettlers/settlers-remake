package jsettlers.main.android.ui.fragments.game;

import jsettlers.common.menu.EGameError;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGameListener;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.R;
import jsettlers.main.android.providers.GameManager;
import jsettlers.main.android.ui.navigation.GameNavigator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingFragment extends Fragment implements IStartingGameListener {
	private GameManager gameManager;
	private GameNavigator navigator;

	private ProgressBar progressBar;
	private TextView statusTextView;

	public static LoadingFragment newInstance() {
		return new LoadingFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameManager = (GameManager) getActivity().getApplication();
		navigator = (GameNavigator) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_loading, container, false);
		progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
		statusTextView = (TextView)view.findViewById(R.id.text_view_status);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (gameManager.getStartingGame().isStartupFinished()) {
			navigator.showMap();
		} else {
			gameManager.getStartingGame().setListener(this);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		gameManager.getStartingGame().setListener(null);
	}


	/**
	 * IStartingGameListener implementation
	 */
	@Override
	public void startProgressChanged(final EProgressState state, final float progress) {
		String stateString = Labels.getProgress(state);
		int progressPercentage = (int) (progress * 100);

		getActivity().runOnUiThread(() -> {
			statusTextView.setText(stateString);
			progressBar.setProgress(progressPercentage);
		});
	}

	@Override
	public IMapInterfaceConnector preLoadFinished(IStartedGame game) {
		return gameManager.gameStarted(game);
	}

	@Override
	public void startFailed(final EGameError errorType, Exception exception) {
		gameManager.getStartingGame().setListener(null);

		getActivity().runOnUiThread(() -> {
            Toast.makeText(getActivity(), errorType.toString(), Toast.LENGTH_LONG).show();
            getActivity().finish();
        });
	}

	@Override
	public void startFinished() {
		gameManager.getStartingGame().setListener(null);
		getActivity().runOnUiThread(() -> navigator.showMap());
	}}
