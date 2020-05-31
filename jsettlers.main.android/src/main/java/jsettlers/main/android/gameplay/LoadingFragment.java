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

package jsettlers.main.android.gameplay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import jsettlers.common.menu.EGameError;
import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGameListener;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.gameplay.navigation.GameNavigator;

public class LoadingFragment extends Fragment implements IStartingGameListener {
	private ProgressBar progressBar;
	private TextView statusTextView;

	private GameStarter gameStarter;
	private GameNavigator navigator;

	public static LoadingFragment newInstance() {
		return new LoadingFragment();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameStarter = (GameStarter) getActivity().getApplication();
		navigator = (GameNavigator) getActivity();
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_loading, container, false);
		progressBar = view.findViewById(R.id.progress_bar);
		statusTextView = view.findViewById(R.id.text_view_status);
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
		requireActivity().runOnUiThread(() -> {
			String stateString = Labels.getProgress(state);
			int progressPercentage = (int) (progress * 100);
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
		requireActivity().runOnUiThread(() -> {
			gameStarter.getStartingGame().setListener(null);
			Toast.makeText(getActivity(), errorType.toString(), Toast.LENGTH_LONG).show();
			getActivity().finish();
		});
	}

	@Override
	public void startFinished() {
		gameStarter.getStartingGame().setListener(null);
		getActivity().runOnUiThread(() -> navigator.showMap());
	}

	@Override
	public void startingLoadingGame() {
		ImageProvider.getInstance().startPreloading();
	}

	@Override
	public void waitForPreloading() {
		ImageProvider.getInstance().waitForPreloadingFinish();
	}
}
