package jsettlers.main.android.fragmentsnew;

import jsettlers.main.android.R;

import android.os.Bundle;
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

	private ProgressBar progressBar;
	private TextView statusTextView;

	public static LoadingFragment newInstance() {
		return new LoadingFragment();
	}

	public LoadingFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_loading, container, false);
		progressBar = (ProgressBar)view.findViewById(R.id.progress_bar);
		statusTextView = (TextView)view.findViewById(R.id.text_view_status);
		return view;
	}

	public void progressChanged(String status, int progress) {
		statusTextView.setText(status);
		progressBar.setProgress(progress);
	}
}
