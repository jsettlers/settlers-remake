package jsettlers.main.android.fragmentsnew;

import jsettlers.main.android.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoadingFragment extends Fragment {
	private static final String SAVE_STATUS = "save_status";
	private static final String SAVE_PROGRESS = "save_progress";

	private ProgressBar progressBar;
	private TextView statusTextView;

	private String status;
	private int progress;

	public static LoadingFragment newInstance() {
		return new LoadingFragment();
	}

	public LoadingFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			status = savedInstanceState.getString(SAVE_STATUS);
			progress = savedInstanceState.getInt(SAVE_PROGRESS);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_loading, container, false);
		progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
		statusTextView = (TextView)view.findViewById(R.id.text_view_status);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		setProgress();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(SAVE_STATUS, status);
		outState.putInt(SAVE_PROGRESS, progress);
	}

	public void progressChanged(String status, int progress) {
		this.status = status;
		this.progress = progress;

		if (this.getView() != null) {
			setProgress();
		}
	}

	private void setProgress() {
		statusTextView.setText(status);
		progressBar.setProgress(progress);
	}
}
