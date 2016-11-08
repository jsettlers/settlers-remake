package jsettlers.main.android.fragmentsnew;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import jsettlers.common.menu.EGameError;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.menu.IStartingGameListener;
import jsettlers.graphics.androidui.menu.IFragmentHandler;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.GameService;
import jsettlers.main.android.R;
import jsettlers.main.android.navigation.GameNavigator;
import jsettlers.main.android.providers.GameProvider;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingFragment extends Fragment implements IStartingGameListener, IFragmentHandler {

	private GameService gameService;
	private GameNavigator gameNavigator;

	private ProgressBar progressBar;
	private TextView statusTextView;

	public static LoadingFragment newInstance() {
		return new LoadingFragment();
	}

	public LoadingFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameNavigator = (GameNavigator)getActivity();
		getActivity().bindService(new Intent(getActivity(), GameService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_loading, container, false);
		progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
		statusTextView = (TextView)view.findViewById(R.id.text_view_status);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onDestroyView() {
		if (gameService != null) {
			gameService.getStartingGame().setListener(null);
		}
		super.onDestroyView();
	}

	@Override
	public void startProgressChanged(final EProgressState state, final float progress) {
		final String status = Labels.getProgress(state);
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				statusTextView.setText(status);
				progressBar.setProgress((int) (progress * 100));			
			}
		});
	}

	@Override
	public IMapInterfaceConnector preLoadFinished(IStartedGame game) {
		IMapInterfaceConnector mapInterfaceConnector = gameService.gameStarted(game, this);
		gameNavigator.showMap();
		return mapInterfaceConnector;
	}

	@Override
	public void startFailed(EGameError errorType, Exception exception) {

	}

	@Override
	public void startFinished() {

	}

//	public void setGameService(GameProvider gameService) {
//		this.gameService = gameService;
//
//		IStartingGame startingGame = gameService.getStartingGame();
//		if (startingGame.isStartupFinished()) {
//			getActivity().getSupportFragmentManager().popBackStack();
//		} else {
//			startingGame.setListener(this);
//		}
//	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			GameService.GameBinder binder = (GameService.GameBinder) service;
			gameService = binder.getService();

			IStartingGame startingGame = gameService.getStartingGame();
			startingGame.setListener(LoadingFragment.this);

		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};

	@Override
	public void hideMenu() {

	}
}
