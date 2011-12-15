package jsettlers.main.android;

import android.app.Activity;
import android.widget.ProgressBar;
import android.widget.TextView;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.progress.EProgressState;
import jsettlers.graphics.progress.ProgressConnector;

public class AProgressConnector extends ProgressConnector {

	private final Activity activity;

	public AProgressConnector(Activity activity) {
		super(null);
		this.activity = activity;
	}

	@Override
	public void setProgressState(final EProgressState state) {
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				applyStateValues(state);
			}
		});
	}

	protected void applyStateValues(final EProgressState state) {
	    ProgressBar progressBar =
		        (ProgressBar) activity.findViewById(R.id.progress_Bar);
		if (progressBar != null) {
			progressBar.setProgress(1);
		}
		
		String text = Labels.getProgress(state);
		TextView textView =
		        (TextView) activity.findViewById(R.id.progress_text);
		if (textView != null) {
			textView.setText(text);
		}
    }

}
