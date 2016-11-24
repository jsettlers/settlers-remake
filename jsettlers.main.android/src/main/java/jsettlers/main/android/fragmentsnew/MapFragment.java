package jsettlers.main.android.fragmentsnew;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_UNPAUSE;

import go.graphics.android.GOSurfaceView;
import go.graphics.android.IContextDestroyedListener;
import go.graphics.area.Area;
import go.graphics.region.Region;

import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.dialogs.ConfirmDialog;
import jsettlers.main.android.dialogs.PausedDialog;
import jsettlers.main.android.fragmentsnew.menus.BuildingsMenuFragment;
import jsettlers.main.android.fragmentsnew.menus.GoodsMenuFragment;
import jsettlers.main.android.fragmentsnew.menus.SettlersMenuFragment;
import jsettlers.main.android.menus.BuildingsMenu;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.navigation.BackPressedListener;
import jsettlers.main.android.providers.BuildingsMenuProvider;
import jsettlers.main.android.providers.ControlsProvider;
import jsettlers.main.android.providers.GameMenuProvider;
import jsettlers.main.android.providers.MapContentProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.ActionMenuView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class MapFragment extends Fragment implements BackPressedListener, PausedDialog.Listener, ConfirmDialog.ConfirmListener, GameMenuProvider, BuildingsMenuProvider {
	private static final String TAG_FRAGMENT_PAUSED_MENU = "com.jsettlers.pausedmenufragment";

	private static final int REQUEST_CODE_CONFIRM_QUIT = 10;

	private ControlsAdapter controls;
	private GameMenu gameMenu;
	private BuildingsMenu buildingsMenu;

	private LocalBroadcastManager localBroadcastManager;
	private BottomSheetBehavior bottomSheetBehavior;

	private boolean isAttachedToGame = false;

	public static MapFragment newInstance() {
		return new MapFragment();
	}

	public MapFragment() {
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map, container, false);

		ActionMenuView actionMenuView = (ActionMenuView) view.findViewById(R.id.action_menu_view);
		getActivity().getMenuInflater().inflate(R.menu.game, actionMenuView.getMenu());
		actionMenuView.setOnMenuItemClickListener(menuItemClickListener);

		View bottomSheet = view.findViewById(R.id.bottom_sheet);
		bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		bottomSheetBehavior.setHideable(false);

		View buildingsMenuButton = view.findViewById(R.id.button_buildings_menu);
		buildingsMenuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showBuildingsMenu();
			}
		});

		View goodsMenuButton = view.findViewById(R.id.button_goods_menu);
		goodsMenuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showGoodsMenu();
			}
		});

		View settlersMenuButton = view.findViewById(R.id.button_settlers_menu);
		settlersMenuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showSettlersMenu();
			}
		});

		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		getChildFragmentManager().beginTransaction()
				.add(R.id.container_menu, BuildingsMenuFragment.newInstance())
				.commit();
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_PAUSE);
		intentFilter.addAction(ACTION_UNPAUSE);
		localBroadcastManager.registerReceiver(mapVisibileBroadcastReceiver, intentFilter);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isAttachedToGame) {
			resumeView();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (isAttachedToGame) {
			pauseView();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		localBroadcastManager.unregisterReceiver(mapVisibileBroadcastReceiver);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	/**
	 * BackPressedListener implementation
	 */
	@Override
	public boolean onBackPressed() {
		if (controls.isActionPending()) {
			controls.cancelPendingAction();
			return true;
		}

		if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			return true;
		}

		return true;
	}

	/**
	 * ConfirmDialog.ConfirmListener implementation
	 */
	@Override
	public void onConfirm(int requestCode) {
		switch (requestCode) {
			case REQUEST_CODE_CONFIRM_QUIT:
				gameMenu.quitConfirm();
				break;
		}
	}

	/**
	 * PausedDialog.Listener implementation
	 */
	@Override
	public void onUnPause() {
		if (isAttachedToGame) {
			gameMenu.unPause();
		} else {
			showPausedMenu(); // Not ready to unpause so show the menu again
		}
	}

	/**
	 * GameMenuProvider implementation
	 */
	@Override
	public GameMenu getGameMenu() {
		return gameMenu;
	}

	/**
	 * BuildingsMenuProvider implementation
	 */
	@Override
	public BuildingsMenu getBuildingsMenu() {
		return buildingsMenu;
	}

	public void attachToGame() {
		MapContentProvider mapContentProvider = (MapContentProvider) getActivity();
		GameMenuProvider gameMenuProvider = (GameMenuProvider) getActivity();
		ControlsProvider controlsProvider = (ControlsProvider) getActivity();

		MapContent mapContent = mapContentProvider.getMapContent();

		controls = controlsProvider.getControls();
		gameMenu = gameMenuProvider.getGameMenu();
		buildingsMenu = new BuildingsMenu(mapContent);

		addMapViews(mapContent);

		isAttachedToGame = true;
		resumeView();
	}

	/**
	 * Show menu methods
	 */
	private void showPausedMenu() {
		if (getChildFragmentManager().findFragmentByTag(TAG_FRAGMENT_PAUSED_MENU) == null) {
			PausedDialog.newInstance().show(getChildFragmentManager(), TAG_FRAGMENT_PAUSED_MENU);
		}
	}

	private void dismissPausedMenu() {
		DialogFragment pausedMenuFragment = (DialogFragment) getChildFragmentManager().findFragmentByTag(TAG_FRAGMENT_PAUSED_MENU);
		if (pausedMenuFragment != null) {
			pausedMenuFragment.dismiss();
		}
	}

	private void showBuildingsMenu() {
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		getChildFragmentManager().beginTransaction()
				.replace(R.id.container_menu, BuildingsMenuFragment.newInstance())
				.commit();
	}

	private void showGoodsMenu() {
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		getChildFragmentManager().beginTransaction()
				.replace(R.id.container_menu, GoodsMenuFragment.newInstance())
				.commit();
	}

	private void showSettlersMenu() {
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

		getChildFragmentManager().beginTransaction()
				.replace(R.id.container_menu, SettlersMenuFragment.newInstance())
				.commit();
	}


	/**
	 * GameMenu item click listener
	 */
	private final ActionMenuView.OnMenuItemClickListener menuItemClickListener = new ActionMenuView.OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			switch (item.getItemId()) {
				case R.id.menu_item_pause:
					gameMenu.pause();
					break;
				case R.id.menu_item_save:
					break;
				case R.id.menu_item_quit:
					new ConfirmDialog.Builder(REQUEST_CODE_CONFIRM_QUIT)
							.setTitle(R.string.game_menu_quit)
							.setConfirmButtonText(R.string.game_menu_quit)
							.create()
							.show(getChildFragmentManager(), null);
					break;
			}
			return true;
		}
	};

	//
	private void resumeView() {
		if (gameMenu.isPaused()) {
			showPausedMenu();
		} else {
			dismissPausedMenu();
			gameMenu.unMute();
		}
	}

	private void pauseView() {
		gameMenu.mute();
	}

	private void addMapViews(MapContent mapContent) {
		FrameLayout frameLayout = (FrameLayout)getView().findViewById(R.id.frame_layout);

		Region goRegion = new Region(Region.POSITION_CENTER);
		goRegion.setContent(mapContent);

		Area goArea = new Area();
		goArea.add(goRegion);

		GOSurfaceView goView = new GOSurfaceView(getActivity(), goArea);
		goView.setContextDestroyedListener(contextDestroyedListener);
		frameLayout.addView(goView);
	}

	private final BroadcastReceiver mapVisibileBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			switch (intent.getAction()) {
				case ACTION_PAUSE:
					showPausedMenu();
					break;
				case ACTION_UNPAUSE:
					gameMenu.unMute();
					dismissPausedMenu();
					break;
			}
		}
	};

	private IContextDestroyedListener contextDestroyedListener = new IContextDestroyedListener() {
		@Override
		public void glContextDestroyed() {
			ImageProvider.getInstance().invalidateAll();
		}
	};
}
