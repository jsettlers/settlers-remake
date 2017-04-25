package jsettlers.main.android.mainmenu.ui.fragments;

import static jsettlers.main.android.core.controls.GameMenu.ACTION_PAUSE;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CANCELLED;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CONFIRM;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_UNPAUSE;

import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.MainMenuPresenter;
import jsettlers.main.android.mainmenu.ui.activities.SettingsActivity;
import jsettlers.main.android.mainmenu.ui.dialogs.DirectoryPickerDialog;
import jsettlers.main.android.mainmenu.views.MainMenuView;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment implements MainMenuView, DirectoryPickerDialog.Listener {
	private static final int REQUEST_CODE_PERMISSION_STORAGE = 10;

	private MainMenuPresenter presenter;
	private LocalBroadcastManager localBroadcastManager;

	private LinearLayout mainLinearLayout;
	private View resourcesView;
	private View resumeView;
	private Button pauseButton;
	private Button quitButton;

	private boolean showDirectoryPicker = false;

	public static MainMenuFragment create() {
		return new MainMenuFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		presenter = PresenterFactory.createMainMenuPresenter(getActivity(), this);
		localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
		FragmentUtil.setActionBar(this, view);

		mainLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_main);
		resumeView = view.findViewById(R.id.card_view_resume);
		quitButton = (Button) view.findViewById(R.id.button_quit);
		pauseButton = (Button) view.findViewById(R.id.button_pause);

		resumeView.setOnClickListener(view1 -> presenter.resumeSelected());
		quitButton.setOnClickListener(view12 -> presenter.quitSelected());
		pauseButton.setOnClickListener(view13 -> presenter.pauseSelected());

		Button newSinglePlayerGameButton = (Button) view.findViewById(R.id.button_new_single_player_game);
		Button loadSinglePlayerGameButton = (Button) view.findViewById(R.id.button_load_single_player_game);
		Button newMultiPlayerGameButton = (Button) view.findViewById(R.id.button_new_multi_player_game);
		Button joinMultiPlayerGameButton = (Button) view.findViewById(R.id.button_join_multi_player_game);

		newSinglePlayerGameButton.setOnClickListener(view14 -> presenter.newSinglePlayerSelected());
		loadSinglePlayerGameButton.setOnClickListener(view15 -> presenter.loadSinglePlayerSelected());
		newMultiPlayerGameButton.setOnClickListener(view16 -> presenter.newMultiPlayerSelected());
		joinMultiPlayerGameButton.setOnClickListener(view17 -> presenter.joinMultiPlayerSelected());

		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		presenter.bindView();
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.app_name);

		// Work around for IllegalStateException when trying to show dialog from onPermissionResult which is too soon in the lifecycle.
		if (showDirectoryPicker) {
			showDirectoryPicker();
			showDirectoryPicker = false;
		}

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_QUIT);
		intentFilter.addAction(ACTION_QUIT_CONFIRM);
		intentFilter.addAction(ACTION_QUIT_CANCELLED);
		intentFilter.addAction(ACTION_PAUSE);
		intentFilter.addAction(ACTION_UNPAUSE);
		localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

		presenter.updateResumeGameView();
	}

	@Override
	public void onPause() {
		super.onPause();
		localBroadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_mainmenu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_settings:
			startActivity(new Intent(getActivity(), SettingsActivity.class));
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return true;
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

	/**
	 * MainMenuView implementation
	 */
	@Override
	public void showResourcePicker() {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		resourcesView = inflater.inflate(R.layout.include_resources_card, mainLinearLayout, false);
		mainLinearLayout.addView(resourcesView, 0);

		Button button = (Button) resourcesView.findViewById(R.id.button_resources);
		button.setOnClickListener(v -> showDirectoryPicker());
	}

	@Override
	public void hideResourcePicker() {
		mainLinearLayout.removeView(resourcesView);
	}

	@Override
	public void updatePauseButton(boolean paused) {
		if (paused) {
			pauseButton.setText(R.string.game_menu_unpause);
		} else {
			pauseButton.setText(R.string.game_menu_pause);
		}
	}

	@Override
	public void updateQuitButton(boolean canQuitConfirm) {
		if (canQuitConfirm) {
			quitButton.setText(R.string.game_menu_quit_confirm);
		} else {
			quitButton.setText(R.string.game_menu_quit);
		}
	}

	@Override
	public void showResumeGameView() {
		resumeView.setVisibility(View.VISIBLE);
	}

	@Override
	public void hideResumeGameView() {
		resumeView.setVisibility(View.GONE);
	}

	/**
	 * DirectoryPickerDialog.Listener implementation
	 */
	@Override
	public void onDirectorySelected() {
		presenter.resourceDirectoryChosen();
	}

	private void showDirectoryPicker() {
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_PERMISSION_STORAGE);
		} else {
			DirectoryPickerDialog.newInstance().show(getChildFragmentManager(), null);
		}
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
			case ACTION_QUIT:
				presenter.updateResumeGameView();
				break;
			case ACTION_QUIT_CONFIRM:
				presenter.updateResumeGameView();
				break;
			case ACTION_QUIT_CANCELLED:
				presenter.updateResumeGameView();
				break;
			case ACTION_PAUSE:
				presenter.updateResumeGameView();
				break;
			case ACTION_UNPAUSE:
				presenter.updateResumeGameView();
				break;
			}
		}
	};
}
