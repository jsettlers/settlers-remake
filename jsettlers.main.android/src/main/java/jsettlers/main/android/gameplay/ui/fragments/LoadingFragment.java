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

package jsettlers.main.android.gameplay.ui.fragments;

import jsettlers.common.menu.EGameError;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGameListener;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.R;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.gameplay.navigation.GameNavigator;

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
	private GameStarter gameStarter;
	private GameNavigator navigator;

	private ProgressBar progressBar;
	private TextView statusTextView;

	public static LoadingFragment newInstance() {
		return new LoadingFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameStarter = (GameStarter) getActivity().getApplication();
		navigator = (GameNavigator) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_loading, container, false);
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		statusTextView = (TextView) view.findViewById(R.id.text_view_status);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		if (gameStarter.getStartingGame().isStartupFinished()) {
			navigator.showMap();
		} else {
			gameStarter.getStartingGame().setListener(this);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		gameStarter.getStartingGame().setListener(null);
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
		return gameStarter.gameStarted(game);
	}

	@Override
	public void startFailed(final EGameError errorType, Exception exception) {
		gameStarter.getStartingGame().setListener(null);

		getActivity().runOnUiThread(() -> {
			Toast.makeText(getActivity(), errorType.toString(), Toast.LENGTH_LONG).show();
			getActivity().finish();
		});
	}

	@Override
	public void startFinished() {
		gameStarter.getStartingGame().setListener(null);
		getActivity().runOnUiThread(() -> navigator.showMap());
	}
}
