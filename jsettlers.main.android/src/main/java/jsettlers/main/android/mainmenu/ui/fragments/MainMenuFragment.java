package jsettlers.main.android.mainmenu.ui.fragments;

import static jsettlers.main.android.core.controls.GameMenu.ACTION_PAUSE;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CANCELLED;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CONFIRM;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_UNPAUSE;

import jsettlers.main.android.R;
import jsettlers.main.android.core.GameManager;
import jsettlers.main.android.core.resources.scanner.ResourceLocationScanner;
import jsettlers.main.android.mainmenu.ui.activities.SettingsActivity;
import jsettlers.main.android.mainmenu.ui.dialogs.DirectoryPickerDialog;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.core.ui.FragmentUtil;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
public class MainMenuFragment extends Fragment implements DirectoryPickerDialog.Listener {
	private static final int REQUEST_CODE_PERMISSION_STORAGE = 10;

	private GameManager gameManager;
	private LocalBroadcastManager localBroadcastManager;
	private MainMenuNavigator navigator;

	private boolean showDirectoryPicker = false;
	private boolean resourcesLoaded = false;

	private LinearLayout mainLinearLayout;
	private View resourcesView;
	private View resumeView;
	private Button pauseButton;
	private Button quitButton;

	public static MainMenuFragment newInstance() {
		return new MainMenuFragment();
	}

	public MainMenuFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameManager = (GameManager) getActivity().getApplication();
		navigator = (MainMenuNavigator) getActivity();
		localBroadcastManager = LocalBroadcastManager.getInstance(getActivity());
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
		FragmentUtil.setActionBar(this, view);

		mainLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_main);
		resumeView = view.findViewById(R.id.card_view_resume);
		quitButton = (Button) view.findViewById(R.id.button_quit);
		pauseButton = (Button) view.findViewById(R.id.button_pause);

		resumeView.setOnClickListener(view1 -> navigator.resumeGame());

		quitButton.setOnClickListener(view12 -> {
            if (gameManager.getGameMenu().canQuitConfirm()) {
                gameManager.getGameMenu().quitConfirm();
            } else {
                gameManager.getGameMenu().quit();
            }
        });

		pauseButton.setOnClickListener(view13 -> {
            if (gameManager.getGameMenu().isPaused()) {
                gameManager.getGameMenu().unPause();
            } else {
                gameManager.getGameMenu().pause();
            }
            setPauseButtonText();
        });


		if (!tryLoadResources()) {
			resourcesView = inflater.inflate(R.layout.include_resources_card, mainLinearLayout, false);
			mainLinearLayout.addView(resourcesView, 0);

			Button button = (Button) resourcesView.findViewById(R.id.button_resources);
			button.setOnClickListener(v -> showDirectoryPicker());
		}

		Button newSinglePlayerGameButton = (Button) view.findViewById(R.id.button_new_single_player_game);
		newSinglePlayerGameButton.setOnClickListener(new GameButtonClickListener() {
			@Override
			protected void doAction() {
				navigator.showNewSinglePlayerPicker();
			}
		});

		Button loadSinglePlayerGameButton = (Button) view.findViewById(R.id.button_load_single_player_game);
		loadSinglePlayerGameButton.setOnClickListener(new GameButtonClickListener() {
			@Override
			protected void doAction() {
				navigator.showLoadSinglePlayerPicker();
			}
		});

		Button newMultiPlayerGameButton = (Button) view.findViewById(R.id.button_new_multi_player_game);
		newMultiPlayerGameButton.setOnClickListener(new GameButtonClickListener() {
			@Override
			protected void doAction() {
				navigator.showNewMultiPlayerPicker();
			}
		});

		Button joinMultiPlayerGameButton = (Button) view.findViewById(R.id.button_join_multi_player_game);
		joinMultiPlayerGameButton.setOnClickListener(new GameButtonClickListener() {
			@Override
			protected void doAction() {
				navigator.showJoinMultiPlayerPicker();
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.app_name);

		// Work around for IllegalStateException when trying to show dialog from onPermissionResult. Meant to be fixed in v24 support library
		if (showDirectoryPicker) {
			showDirectoryPicker();
			showDirectoryPicker = false;
		}

		if (gameManager.isGameInProgress()) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(ACTION_QUIT);
			intentFilter.addAction(ACTION_QUIT_CONFIRM);
			intentFilter.addAction(ACTION_QUIT_CANCELLED);
			intentFilter.addAction(ACTION_PAUSE);
			intentFilter.addAction(ACTION_UNPAUSE);
			localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);
		}

		setResumeViewState();
	}

	@Override
	public void onPause() {
		super.onPause();
		localBroadcastManager.unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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

	/**
	 * DirectoryPickerDialog.Listener implementation
	 */
	@Override
	public void onDirectorySelected() {
		if (tryLoadResources()) {
			mainLinearLayout.removeView(resourcesView);
		} else {
			throw new RuntimeException("Resources not found or not valid after directory chosen by user");
		}
	}

	private void setResumeViewState() {
		if (gameManager.isGameInProgress()) {
			setPauseButtonText();
			setQuitConfirmButtonText();
			resumeView.setVisibility(View.VISIBLE);
		} else {
			resumeView.setVisibility(View.GONE);
		}
	}

	private void setQuitConfirmButtonText() {

		if (gameManager.getGameMenu().canQuitConfirm()) {
			quitButton.setText(R.string.game_menu_quit_confirm);
		} else {
			quitButton.setText(R.string.game_menu_quit);
		}
	}

	private void setPauseButtonText() {
		if (gameManager.getGameMenu().isPaused()) {
			pauseButton.setText(R.string.game_menu_unpause);
		} else {
			pauseButton.setText(R.string.game_menu_pause);
		}
	}

	private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case ACTION_QUIT:
					setQuitConfirmButtonText();
					break;
				case ACTION_QUIT_CONFIRM:
					setResumeViewState();
					break;
				case ACTION_QUIT_CANCELLED:
					setQuitConfirmButtonText();
					break;
				case ACTION_PAUSE:
					setPauseButtonText();
					break;
				case ACTION_UNPAUSE:
					setPauseButtonText();
					break;
			}
		}
	};

	private boolean tryLoadResources() {
		resourcesLoaded = new ResourceLocationScanner(getActivity()).scanForResources();
		return resourcesLoaded;
	}

	private void showDirectoryPicker() {
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_PERMISSION_STORAGE);
		} else {
			DirectoryPickerDialog.newInstance().show(getChildFragmentManager(), null);
		}
	}

	private abstract class GameButtonClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			if (resourcesLoaded) {
				doAction();
			}
		}

		protected abstract void doAction();
	}
}
