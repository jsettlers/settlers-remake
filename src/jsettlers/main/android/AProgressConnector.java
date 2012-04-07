package jsettlers.main.android;

import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AProgressConnector extends ProgressConnector {

	private final JsettlersActivity activity;

	public AProgressConnector(JsettlersActivity activity) {
		super(null);
		this.activity = activity;
	}

	@Override
	public void setProgressState(final EProgressState state, final float progress) {
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				applyStateValues(state, progress);
			}
		});
	}

	protected void applyStateValues(final EProgressState state, float progress) {
		String text = Labels.getProgress(state);
		TextView textView =
		        (TextView) activity.findViewById(R.id.progress_text);
		if (textView != null) {
			textView.setText(text);
		}

		ProgressBar bar =
		        (ProgressBar) activity.findViewById(R.id.progress_progress);
		if (bar != null) {
			bar.setIndeterminate(progress < 0);
			if (progress >= 0) {
				bar.setProgress((int) (progress * 100));
			}
		}
    }

}
