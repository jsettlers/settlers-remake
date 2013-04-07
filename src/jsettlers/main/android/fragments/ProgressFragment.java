package jsettlers.main.android.fragments;

import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import jsettlers.main.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * This displays a progress bar on the screen. TODO: We should call some abort
 * method when the user presses back!
 * 
 * @author michael
 */
public class ProgressFragment extends JsettlersFragment {

	private class AProgressConnector extends ProgressConnector {

		private EProgressState applyStateOnStart = null;
		private float applyProgressOnStart = -1;

		public AProgressConnector() {
			super(null);
		}

		@Override
		public synchronized void setProgressState(final EProgressState state,
		        final float progress) {
			Activity activity = getActivity();
			if (activity != null) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						applyStateValues(state, progress);
					}
				});
			} else {
				applyStateOnStart = state;
				applyProgressOnStart = progress;
			}
		}

		protected void applyStateValues(final EProgressState state,
		        float progress) {
			String text = Labels.getProgress(state);
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

		public synchronized void onReady() {
			applyStateValues(applyStateOnStart, applyProgressOnStart);
		}

	}

	private AProgressConnector connector = new AProgressConnector();

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
		connector.onReady();
	}

	public ProgressConnector getConnector() {
		return connector;
	}
	
	@Override
	public boolean shouldAddToBackStack() {
	    return false;
	}

}
