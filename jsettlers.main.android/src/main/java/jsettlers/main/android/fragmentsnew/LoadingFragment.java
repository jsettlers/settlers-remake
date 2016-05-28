package jsettlers.main.android.fragmentsnew;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jsettlers.common.menu.EGameError;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.menu.IStartingGameListener;
import jsettlers.main.android.MainApplication;
import jsettlers.main.android.R;
import jsettlers.main.android.navigation.GameNavigator;
import jsettlers.main.android.providers.GameProvider;
import jsettlers.main.android.providers.GameStarter;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingFragment extends Fragment implements IStartingGameListener {

	private GameProvider gameProvider;
	private GameNavigator gameNavigator;

	public static LoadingFragment newInstance() {
		return new LoadingFragment();
	}

	public LoadingFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameProvider = (GameProvider) getActivity();
		gameNavigator = (GameNavigator)getActivity();
		IStartingGame startingGame = gameProvider.getStartingGame();

		if (startingGame.isStartupFinished()) {
			getActivity().getSupportFragmentManager().popBackStack();
		} else {
			startingGame.setListener(this);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_loading, container, false);
	}

	@Override
	public void startProgressChanged(EProgressState state, float progress) {

	}

	@Override
	public IMapInterfaceConnector preLoadFinished(IStartedGame game) {
		IMapInterfaceConnector mapInterfaceConnector = gameProvider.loadFinished(game);
		gameNavigator.showGame();
		return mapInterfaceConnector;
	}

	@Override
	public void startFailed(EGameError errorType, Exception exception) {

	}

	@Override
	public void startFinished() {

	}
}
