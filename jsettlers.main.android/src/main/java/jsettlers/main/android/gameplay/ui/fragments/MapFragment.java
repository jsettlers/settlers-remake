/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.main.android.gameplay.ui.fragments;

import static jsettlers.main.android.mainmenu.navigation.Actions.ACTION_PAUSE;
import static jsettlers.main.android.mainmenu.navigation.Actions.ACTION_UNPAUSE;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import go.graphics.android.GOSurfaceView;
import go.graphics.area.Area;
import go.graphics.region.Region;

import jsettlers.common.menu.action.EActionType;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.GameMenu;
import jsettlers.main.android.core.controls.SelectionControls;
import jsettlers.main.android.core.controls.SelectionListener;
import jsettlers.main.android.core.controls.TaskControls;
import jsettlers.main.android.core.navigation.BackPressedListener;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.core.ui.dialogs.ConfirmDialog;
import jsettlers.main.android.core.ui.dialogs.ConfirmDialog_;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.ui.dialogs.PausedDialog;
import jsettlers.main.android.gameplay.ui.fragments.menus.buildings.BuildingsMenuFragment;
import jsettlers.main.android.gameplay.ui.fragments.menus.goods.GoodsMenuFragment;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.BuildingSelectionFragment;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.CarriersSelectionFragment;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.SoldiersSelectionFragment;
import jsettlers.main.android.gameplay.ui.fragments.menus.selection.SpecialistsSelectionFragment;
import jsettlers.main.android.gameplay.ui.fragments.menus.settlers.SettlersMenuFragment;
import jsettlers.main.android.mainmenu.ui.activities.MainActivity_;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;

@EFragment(R.layout.fragment_map)
@OptionsMenu(R.menu.game)
public class MapFragment extends Fragment implements SelectionListener, BackPressedListener, PausedDialog.Listener, ConfirmDialog.ConfirmListener, MenuNavigator {
	private static final String TAG_FRAGMENT_PAUSED_MENU = "com.jsettlers.pausedmenufragment";
	private static final String TAG_FRAGMENT_SELECTION_MENU = "com.jsettlers.selectionmenufragment";
	private static final String TAG_FRAGMENT_BUILDINGS_MENU = "com.jsettlers.buildingsmenufragment";
	private static final String TAG_FRAGMENT_GOODS_MENU = "com.jsettlers.goodsmenufragment";
	private static final String TAG_FRAGMENT_SETTLERS_MENU = "com.jsettlers.settlersmenufragment";
	private static final String SAVE_BOTTOM_SHEET_STATE = "save_bottom_sheet_state";
	private static final int REQUEST_CODE_CONFIRM_QUIT = 10;

	private ActionControls actionControls;
	private SelectionControls selectionControls;
	private TaskControls taskControls;
	private GameMenu gameMenu;
	private ViewPagerBottomSheetBehavior bottomSheetBehavior;

	@ViewById(R.id.toolbar)
	Toolbar toolbar;
	@ViewById(R.id.frame_layout)
	FrameLayout frameLayout;
	@ViewById(R.id.bottom_sheet)
	View bottomSheet;

	@AfterViews
	void setupToolbar() {
		FragmentUtil.setActionBar(this, toolbar);
		FragmentUtil.setDisplayShowTitleEnabled(this, false);
	}

	@AfterViews
	void setupBottomSheet() {
		bottomSheetBehavior = ViewPagerBottomSheetBehavior.from(bottomSheet);
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		bottomSheetBehavior.setHideable(false);
	}

	@AfterViews
	void setupControls() {
		ControlsResolver controlsResolver = new ControlsResolver(getActivity());
		actionControls = controlsResolver.getActionControls();
		selectionControls = controlsResolver.getSelectionControls();
		taskControls = controlsResolver.getTaskControls();
		gameMenu = controlsResolver.getGameMenu();
		addMapViews(controlsResolver.getMapContent());
	}

	private void addMapViews(MapContent mapContent) {
		Region goRegion = new Region(Region.POSITION_CENTER);
		goRegion.setContent(mapContent);

		Area goArea = new Area();
		goArea.add(goRegion);

		GOSurfaceView goView = new GOSurfaceView(getActivity(), goArea);
		goView.setContextDestroyedListener(() -> ImageProvider.getInstance().invalidateAll());
		frameLayout.addView(goView);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (savedInstanceState == null) {
			addBuildingsMenuFragment();
		}
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
	public void onStart() {
		super.onStart();
		selectionControls.addSelectionListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		selectionControls.removeSelectionListener(this);
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
		if (taskControls.isTaskActive()) {
			taskControls.endTask();
			return true;
		}

		if (selectionControls.getCurrentSelection() != null) {
			selectionControls.deselect();
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
			MainActivity_.intent(this).start();
			break;
		}
	}

	/**
	 * PausedDialog.Listener implementation
	 */
	@Override
	public void onGameUnPause() {
		gameMenu.unPause();
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

	private void addBuildingsMenuFragment() {
		if (getChildFragmentManager().findFragmentByTag(TAG_FRAGMENT_BUILDINGS_MENU) == null) {
			getChildFragmentManager().beginTransaction()
					.replace(R.id.container_menu, BuildingsMenuFragment.newInstance(), TAG_FRAGMENT_BUILDINGS_MENU)
					.commit();
		}
	}

	@Click(R.id.button_buildings_menu)
	void showBuildingsMenu() {
		showMenu();
		addBuildingsMenuFragment();
	}

	@Click(R.id.button_goods_menu)
	void showGoodsMenu() {
		showMenu();

		if (getChildFragmentManager().findFragmentByTag(TAG_FRAGMENT_GOODS_MENU) == null) {
			getChildFragmentManager().beginTransaction()
					.replace(R.id.container_menu, GoodsMenuFragment.newInstance(), TAG_FRAGMENT_GOODS_MENU)
					.commit();
		}
	}

	@Click(R.id.button_settlers_menu)
	void showSettlersMenu() {
		showMenu();

		if (getChildFragmentManager().findFragmentByTag(TAG_FRAGMENT_SETTLERS_MENU) == null) {
			getChildFragmentManager().beginTransaction()
					.replace(R.id.container_menu, SettlersMenuFragment.newInstance(), TAG_FRAGMENT_SETTLERS_MENU)
					.commit();
		}
	}

	private void showSelectionMenu() {
		switch (selectionControls.getCurrentSelection().getSelectionType()) {
		case BUILDING:
			showMenu();
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
			showMenu();
			getChildFragmentManager().beginTransaction()
					.replace(R.id.container_menu, SpecialistsSelectionFragment.newInstance(), TAG_FRAGMENT_SELECTION_MENU)
					.commit();
			break;
		case PEOPLE:
			showMenu();
			getChildFragmentManager().beginTransaction()
					.replace(R.id.container_menu, CarriersSelectionFragment.newInstance(), TAG_FRAGMENT_SELECTION_MENU)
					.commit();
			break;
		case SHIPS:
			showMenu();
			getChildFragmentManager().beginTransaction()
					.replace(R.id.container_menu, ShipsSelectionFragment.newInstance(), TAG_FRAGMENT_SELECTION_MENU)
					.commit();
			break;
		default:
			Log.d("Settlers", "No selection menu for selection type " + selectionControls.getCurrentSelection().getSelectionType().name());
			break;
		}
	}

	@OptionsItem(R.id.menu_item_pause_game)
	void pauseGameClicked() {
		gameMenu.pause();
	}

	@OptionsItem(R.id.menu_item_save)
	void saveClicked() {
		gameMenu.save();
	}

	@OptionsItem(R.id.menu_item_quit)
	void quitClicked() {
		ConfirmDialog_.builder()
				.requestCode(REQUEST_CODE_CONFIRM_QUIT)
				.titleResId(R.string.game_menu_quit)
				.confirmButtonTextResId(R.string.game_menu_quit)
				.build()
				.show(getChildFragmentManager(), null);
	}

	@OptionsItem(R.id.menu_item_faster)
	void fasterClicked() {
		actionControls.fireAction(new Action(EActionType.SPEED_FASTER));
	}

	@OptionsItem(R.id.menu_item_slower)
	void slowerClicked() {
		actionControls.fireAction(new Action(EActionType.SPEED_SLOWER));
	}

	@OptionsItem(R.id.menu_item_fast)
	void fastClicked() {
		actionControls.fireAction(new Action(EActionType.SPEED_FAST));
	}

	@OptionsItem(R.id.menu_item_skip_one_minute)
	void skipOneMinuteClicked() {
		actionControls.fireAction(new Action(EActionType.FAST_FORWARD));
	}

	@Receiver(actions = ACTION_PAUSE, local = true, registerAt = Receiver.RegisterAt.OnResumeOnPause)
	void pauseGameReceived() {
		showPausedMenu();
	}

	@Receiver(actions = ACTION_UNPAUSE, local = true, registerAt = Receiver.RegisterAt.OnStartOnStop)
	void unPauseGameReceived() {
		gameMenu.unMute();
		dismissPausedMenu();
	}
}
