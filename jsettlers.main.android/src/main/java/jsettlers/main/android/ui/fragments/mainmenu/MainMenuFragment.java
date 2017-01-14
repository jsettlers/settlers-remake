package jsettlers.main.android.ui.fragments.mainmenu;

import jsettlers.main.android.GameService;
import jsettlers.main.android.R;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.ui.dialogs.DirectoryPickerDialog;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.resources.scanner.ResourceLocationScanner;
import jsettlers.main.android.utils.FragmentUtil;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_QUIT;
import static jsettlers.main.android.GameService.ACTION_QUIT_CANCELLED;
import static jsettlers.main.android.GameService.ACTION_QUIT_CONFIRM;
import static jsettlers.main.android.GameService.ACTION_UNPAUSE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainMenuFragment extends Fragment implements DirectoryPickerDialog.Listener {
	private static final int REQUEST_CODE_PERMISSION_STORAGE = 10;

	private GameService gameService;
	private GameMenu gameMenu;

	private LocalBroadcastManager localBroadcastManager;
	private MainMenuNavigator navigator;

	private boolean showDirectoryPicker = false;
	private boolean resourcesLoaded = false;

	private LinearLayout mainLinearLayout;
	private View resourcesView;
	private View resumeView;
	private Button pauseButton;
	private Button quitButton;

	private boolean bound = false;

	public static MainMenuFragment newInstance() {
		return new MainMenuFragment();
	}

	public MainMenuFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		navigator = (MainMenuNavigator) getActivity();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main_menu, container, false);
		FragmentUtil.setActionBar(this, view);

		mainLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_main);
		resumeView = view.findViewById(R.id.card_view_resume);
		quitButton = (Button) view.findViewById(R.id.button_quit);
		pauseButton = (Button) view.findViewById(R.id.button_pause);

		// The resumeView getsupdated in setResumeViewState once the service is bound.
		resumeView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				navigator.resumeGame();
			}
		});

		quitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (gameMenu.canQuitConfirm()) {
					gameMenu.quitConfirm();
				} else {
					gameMenu.quit();
				}
			}
		});

		pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (gameMenu.isPaused()) {
					gameMenu.unPause();
				} else {
					gameMenu.pause();
				}
				setPauseButtonText();
			}
		});


		if (!tryLoadResources()) {
			resourcesView = inflater.inflate(R.layout.include_resources_card, mainLinearLayout, false);
			mainLinearLayout.addView(resourcesView, 0);

			Button button = (Button) resourcesView.findViewById(R.id.button_resources);
			button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					showDirectoryPicker();
				}
			});
		}

		Button newSingleGameButton = (Button) view.findViewById(R.id.button_new_single_game);
		newSingleGameButton.setOnClickListener(new GameButtonClickListener() {
			@Override
			protected void doAction() {
				navigator.showNewSinglePlayerMapPicker();
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// Work around for IllegalStateException when trying to show dialog from onPermissionResult. Meant to be fixed in v24 support library
		if (showDirectoryPicker) {
			showDirectoryPicker();
			showDirectoryPicker = false;
		}

		getActivity().bindService(new Intent(getActivity(), GameService.class), serviceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (bound) {
			getActivity().unbindService(serviceConnection);
			localBroadcastManager.unregisterReceiver(broadcastReceiver);
		}
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
		if (gameService.isGameInProgress()) {
			setPauseButtonText();
			setQuitConfirmButtonText();
			resumeView.setVisibility(View.VISIBLE);
		} else {
			resumeView.setVisibility(View.GONE);
		}
	}

	private void setQuitConfirmButtonText() {
		if (gameMenu.canQuitConfirm()) {
			quitButton.setText(R.string.game_menu_quit_confirm);
		} else {
			quitButton.setText(R.string.game_menu_quit);
		}
	}

	private void setPauseButtonText() {
		if (gameMenu.isPaused()) {
			pauseButton.setText(R.string.game_menu_unpause);
		} else {
			pauseButton.setText(R.string.game_menu_pause);
		}
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder binder) {
			GameService.GameBinder gameBinder = (GameService.GameBinder) binder;
			gameService = gameBinder.getService();
			if (gameService.isGameInProgress()) {
				gameMenu = gameService.getControlsAdapter().getGameMenu();
			}

			localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(ACTION_QUIT);
			intentFilter.addAction(ACTION_QUIT_CONFIRM);
			intentFilter.addAction(ACTION_QUIT_CANCELLED);
			intentFilter.addAction(ACTION_PAUSE);
			intentFilter.addAction(ACTION_UNPAUSE);
			localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

			setResumeViewState();

			bound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			bound = false;
		}
	};

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
