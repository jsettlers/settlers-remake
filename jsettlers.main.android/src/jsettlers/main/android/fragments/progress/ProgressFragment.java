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
package jsettlers.main.android.fragments.progress;

import jsettlers.common.menu.EProgressState;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.R;
import jsettlers.main.android.fragments.JsettlersFragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This displays a progress bar on the screen. TODO: We should call some abort method when the user presses back!
 * 
 * @author michael
 */
public class ProgressFragment extends JsettlersFragment {

	private String applyStateOnStart = "";
	private float applyProgressOnStart = -1;

	@Override
	public String getName() {
		return "progress";
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.progress, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		applyStateValues(applyStateOnStart, applyProgressOnStart);
	}

	@Override
	public boolean onBackButtonPressed() {
		Toast.makeText(getActivity(), "TODO: Back while in progress",
				Toast.LENGTH_LONG).show();
		return true;
	}

	public synchronized void setProgressState(final EProgressState state, final float progress) {
		final String text = Labels.getProgress(state);
		setProgressState(text, progress);
	}

	public void setProgressState(final String text, final float progress) {
		Activity activity = getActivity();
		if (activity != null) {
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					applyStateValues(text, progress);
				}
			});
		} else {
			applyStateOnStart = text;
			applyProgressOnStart = progress;
		}
	}

	protected void applyStateValues(final String text,
			float progress) {
		TextView textView =
				(TextView) getView().findViewById(R.id.progress_text);
		if (textView != null) {
			textView.setText(text);
		}

		ProgressBar bar =
				(ProgressBar) getView()
						.findViewById(R.id.progress_progress);
		if (bar != null) {
			bar.setIndeterminate(progress < 0);
			if (progress >= 0) {
				bar.setProgress((int) (progress * 100));
			}
		}
	}

}
