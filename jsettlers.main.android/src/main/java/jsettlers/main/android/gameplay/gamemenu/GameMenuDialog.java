/*
 * Copyright (c) 2018
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

package jsettlers.main.android.gameplay.gamemenu;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.SeekBar;

import jsettlers.main.android.R;
import jsettlers.main.android.databinding.DialogGameMenuBinding;
import jsettlers.main.android.mainmenu.MainActivity_;

public class GameMenuDialog extends DialogFragment {

	private GameMenuViewModel viewModel;

	public static GameMenuDialog create() {
		return new GameMenuDialog();
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GameMenuViewModelFactory gameMenuViewModelFactory = new GameMenuViewModelFactory(requireActivity().getApplication());
		viewModel = gameMenuViewModelFactory.get(this);

		viewModel.getGameQuitted().observe(this, x -> MainActivity_.intent(this).start());
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
		DialogGameMenuBinding binding = DialogGameMenuBinding.inflate(layoutInflater);
		binding.setLifecycleOwner(this);
		binding.setViewmodel(viewModel);
		binding.seekBar.setOnSeekBarChangeListener(gameSpeedSeekBarListener);

		AlertDialog dialog = new AlertDialog.Builder(requireActivity(), R.style.GameMenuDialogTheme)
				.setView(binding.getRoot())
				.create();

		dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		applyFullscreenWorkaround(dialog);

		return dialog;
	}

	private SeekBar.OnSeekBarChangeListener gameSpeedSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			if (fromUser) {
				viewModel.gameSpeedMoved(progress);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	};

	/**
	 * Stops the system bars from showing when this dialog appears.
	 */
	private void applyFullscreenWorkaround(AlertDialog dialog) {
		dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
		dialog.setOnShowListener(x -> dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE));
		int activitySystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
		dialog.getWindow().getDecorView().setSystemUiVisibility(activitySystemUiVisibility);
	}
}
