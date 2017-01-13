package jsettlers.main.android.ui.fragments.game;

import static jsettlers.main.android.GameService.ACTION_PAUSE;
import static jsettlers.main.android.GameService.ACTION_UNPAUSE;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;
import go.graphics.android.GOSurfaceView;
import go.graphics.android.IContextDestroyedListener;
import go.graphics.area.Area;
import go.graphics.region.Region;

import jsettlers.common.menu.action.EActionType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.controls.SelectionListener;
import jsettlers.main.android.providers.SelectionProvider;
import jsettlers.main.android.ui.dialogs.ConfirmDialog;
import jsettlers.main.android.ui.dialogs.PausedDialog;
import jsettlers.main.android.ui.fragments.game.menus.buildings.BuildingsMenuFragment;
import jsettlers.main.android.ui.fragments.game.menus.goods.GoodsMenuFragment;
import jsettlers.main.android.ui.fragments.game.menus.selection.BuildingSelectionFragment;
import jsettlers.main.android.ui.fragments.game.menus.selection.CarriersSelectionFragment;
import jsettlers.main.android.ui.fragments.game.menus.selection.SoldiersSelectionFragment;
import jsettlers.main.android.ui.fragments.game.menus.selection.SpecialistsSelectionFragment;
import jsettlers.main.android.ui.fragments.game.menus.settlers.SettlersMenuFragment;
import jsettlers.main.android.menus.BuildingsMenu;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.ui.navigation.BackPressedListener;
import jsettlers.main.android.providers.BuildingsMenuProvider;
import jsettlers.main.android.providers.ControlsProvider;
import jsettlers.main.android.providers.GameMenuProvider;
import jsettlers.main.android.providers.MapContentProvider;
import jsettlers.main.android.ui.navigation.MenuNavigator;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


public class MapFragment extends Fragment implements SelectionListener, BackPressedListener, PausedDialog.Listener, ConfirmDialog.ConfirmListener, GameMenuProvider, MenuNavigator, BuildingsMenuProvider, SelectionProvider{
	private static final String TAG_FRAGMENT_PAUSED_MENU = "com.jsettlers.pausedmenufragment";
	private static final String TAG_FRAGMENT_SELECTION_MENU = "com.jsettlers.selectionmenufragment";

	private static final int REQUEST_CODE_CONFIRM_QUIT = 10;

	private static final String SAVE_BOTTOM_SHEET_STATE = "save_bottom_sheet_state";

	private ControlsAdapter controls;
	private GameMenu gameMenu;
	private BuildingsMenu buildingsMenu;

	private LocalBroadcastManager localBroadcastManager;
	private ViewPagerBottomSheetBehavior bottomSheetBehavior;

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
		bottomSheetBehavior = ViewPagerBottomSheetBehavior.from(bottomSheet);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		bottomSheetBehavior.setHideable(false);

		View buildingsMenuButton = view.findViewById(R.id.button_buildings_menu);
		buildingsMenuButton.setOnClickListener(view1 -> showBuildingsMenu());

		View goodsMenuButton = view.findViewById(R.id.button_goods_menu);
		goodsMenuButton.setOnClickListener(view12 -> showGoodsMenu());

		View settlersMenuButton = view.findViewById(R.id.button_settlers_menu);
		settlersMenuButton.setOnClickListener(view13 -> showSettlersMenu());

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		MapContentProvider mapContentProvider = (MapContentProvider) getActivity();
		GameMenuProvider gameMenuProvider = (GameMenuProvider) getActivity();
		ControlsProvider controlsProvider = (ControlsProvider) getActivity();

		controls = controlsProvider.getControls();
		gameMenu = gameMenuProvider.getGameMenu();
		buildingsMenu = new BuildingsMenu(controls);

        controls.setSelectionListener(this);

        MapContent mapContent = mapContentProvider.getMapContent();
		addMapViews(mapContent);

		if (savedInstanceState == null) {
			addBuildsingMenuFragment();
		}
	}

	@Override
	public void onStart() {
		super.onStart();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_PAUSE);
		intentFilter.addAction(ACTION_UNPAUSE);
		localBroadcastManager.registerReceiver(mapVisibleBroadcastReceiver, intentFilter);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (gameMenu.isPaused()) {
			showPausedMenu();
		} else {
			dismissPausedMenu();
			gameMenu.unMute();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		gameMenu.mute();
	}

	@Override
	public void onStop() {
		super.onStop();
		localBroadcastManager.unregisterReceiver(mapVisibleBroadcastReceiver);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		controls.setSelectionListener(null);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(SAVE_BOTTOM_SHEET_STATE, bottomSheetBehavior.getState());
	}

	@Override
	public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		if (savedInstanceState != null) {
			int bottomSheetState = savedInstanceState.getInt(SAVE_BOTTOM_SHEET_STATE);
			bottomSheetBehavior.setState(bottomSheetState);
		}
	}

	/**
	 * BackPressedListener implementation
	 */
	@Override
	public boolean onBackPressed() {
		if (controls.isTaskActive()) {
			controls.endTask();
			return true;
		}

		if (isMenuOpen()) {
            dismissMenu();
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
		gameMenu.unPause();
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

    /**
     * SelectionProvider implementation
     */
    @Override
    public ISelectionSet getCurrentSelection() {
        return controls.getSelection();
    }

    /**
	 * SelectionListener implementation
     */
	@Override
	public void selectionChanged(ISelectionSet selection) {
        if (selection != null) {
            showSelectionMenu();
        } else {
			if (removeSelectionMenu()) {
				dismissMenu();
			}
        }
    }


	/**
	 * Menu methods
	 */
    @Override
    public boolean isMenuOpen() {
        return bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }

    @Override
    public void dismissMenu() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

	@Override
	public boolean removeSelectionMenu() {
		Fragment fragment = getChildFragmentManager().findFragmentByTag(TAG_FRAGMENT_SELECTION_MENU);
		if (fragment != null) {
			getChildFragmentManager().beginTransaction().remove(fragment).commit();
			return true;
		}
		return false;
	}

	private void showMenu() {
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
	}

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
		showMenu();
		addBuildsingMenuFragment();
	}

	private void addBuildsingMenuFragment() {
		getChildFragmentManager().beginTransaction()
				.replace(R.id.container_menu, BuildingsMenuFragment.newInstance())
				.commit();
	}

	private void showGoodsMenu() {
		showMenu();

		getChildFragmentManager().beginTransaction()
				.replace(R.id.container_menu, GoodsMenuFragment.newInstance())
				.commit();
	}

	private void showSettlersMenu() {
		showMenu();

		getChildFragmentManager().beginTransaction()
				.replace(R.id.container_menu, SettlersMenuFragment.newInstance())
				.commit();
	}

	private void showSelectionMenu() {
		showMenu();

        switch (controls.getSelection().getSelectionType()) {
            case BUILDING:
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.container_menu, BuildingSelectionFragment.newInstance(), TAG_FRAGMENT_SELECTION_MENU)
                        .commit();
                break;
			case SOLDIERS:
				getChildFragmentManager().beginTransaction()
						.replace(R.id.container_menu, SoldiersSelectionFragment.newInstance(), TAG_FRAGMENT_SELECTION_MENU)
						.commit();
				break;
			case SPECIALISTS:
				getChildFragmentManager().beginTransaction()
						.replace(R.id.container_menu, SpecialistsSelectionFragment.newInstance(), TAG_FRAGMENT_SELECTION_MENU)
						.commit();
				break;
			case PEOPLE:
				getChildFragmentManager().beginTransaction()
						.replace(R.id.container_menu, CarriersSelectionFragment.newInstance(), TAG_FRAGMENT_SELECTION_MENU)
						.commit();
				break;
            default:
                Log.d("Settlers", "No selection menu for selection type " + controls.getSelection().getSelectionType().name());
                break;
        }
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
				case R.id.menu_item_faster:
					controls.fireAction(new Action(EActionType.SPEED_FASTER));
					break;
				case R.id.menu_item_slower:
					controls.fireAction(new Action(EActionType.SPEED_SLOWER));
					break;
				case R.id.menu_item_fastest:
					controls.fireAction(new Action(EActionType.SPEED_FAST));
					break;
				case R.id.menu_item_skip:
					controls.fireAction(new Action(EActionType.FAST_FORWARD));
					break;
			}
			return true;
		}
	};

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

	private final BroadcastReceiver mapVisibleBroadcastReceiver = new BroadcastReceiver() {
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

	private IContextDestroyedListener contextDestroyedListener = () -> ImageProvider.getInstance().invalidateAll();
}
