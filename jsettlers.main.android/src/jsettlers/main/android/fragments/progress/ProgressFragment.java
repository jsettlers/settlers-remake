package jsettlers.main.android.fragments.progress;

import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.progress.EProgressState;
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
