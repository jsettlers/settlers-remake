package jsettlers.main.android.fragmentsnew;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import jsettlers.main.android.R;
import jsettlers.main.android.dialogs.DirectoryPickerDialog;
import jsettlers.main.android.resources.scanner.ResourceLocationScanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment implements DirectoryPickerDialog.Listener {
	private static final int REQUEST_CODE_PERMISSION_STORAGE = 10;

	private LinearLayout mainLinearLayout;
	private View resourcesView;
	private boolean showDirectoryPicker = false;

	public static MainMenuFragment newInstance() {
		return new MainMenuFragment();
	}

	public MainMenuFragment() {
	}

	private View.OnClickListener resourcesButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			showDirectoryPicker();
		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_menu, container, false);

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		activity.setSupportActionBar((Toolbar) view.findViewById(R.id.toolbar));

		mainLinearLayout = (LinearLayout)view.findViewById(R.id.linear_layout_main);

		if (!new ResourceLocationScanner(getActivity()).scanForResources()) {
			resourcesView = inflater.inflate(R.layout.include_resources_card, mainLinearLayout, false);
			mainLinearLayout.addView(resourcesView, 0);

			Button button = (Button)resourcesView.findViewById(R.id.button_resources);
			button.setOnClickListener(resourcesButtonListener);
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (showDirectoryPicker) {
			showDirectoryPicker();
			showDirectoryPicker = false;
		}
	}

	@Override
	public void onDirectorySelected() {
		mainLinearLayout.removeView(resourcesView);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_PERMISSION_STORAGE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					showDirectoryPicker = true;
				}
				break;
		}
	}

	private void showDirectoryPicker() {
		int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String [] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_PERMISSION_STORAGE);
		} else {
			DirectoryPickerDialog.newInstance().show(getChildFragmentManager(), null);
		}
	}
}
