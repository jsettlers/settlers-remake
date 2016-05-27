package jsettlers.main.android.fragmentsnew;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jsettlers.main.android.R;
import jsettlers.main.android.dialogs.DirectoryPickerDialog;
import jsettlers.main.android.resources.scanner.ResourceLocationScanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment implements DirectoryPickerDialog.Listener {

	public static MainMenuFragment newInstance() {
		return new MainMenuFragment();
	}

	public MainMenuFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		activity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));


		Button button = (Button)view.findViewById(R.id.button_resources);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DirectoryPickerDialog.newInstance().show(getChildFragmentManager(), null);
			}
		});

		return view;
	}

	@Override
	public void onDirectorySelected() {

	}

	private boolean checkResouces() {
		return new ResourceLocationScanner(getActivity()).scanForResources();
	}
}
