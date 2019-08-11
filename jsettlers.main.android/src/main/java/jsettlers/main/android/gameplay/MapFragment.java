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

package jsettlers.main.android.gameplay;

import java.util.LinkedList;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import androidx.lifecycle.Observer;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import biz.laenger.android.vpbs.ViewPagerBottomSheetBehavior;
import go.graphics.android.GOSurfaceView;
import go.graphics.area.Area;
import go.graphics.region.Region;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.GameMenu;
import jsettlers.main.android.core.controls.SelectionControls;
import jsettlers.main.android.core.controls.SelectionListener;
import jsettlers.main.android.core.controls.TaskControls;
import jsettlers.main.android.core.navigation.BackPressedListener;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.gameplay.controlsmenu.buildings.BuildingsMenuFragment;
import jsettlers.main.android.gameplay.controlsmenu.goods.GoodsMenuFragment;
import jsettlers.main.android.gameplay.controlsmenu.selection.BuildingSelectionFragment;
import jsettlers.main.android.gameplay.controlsmenu.selection.CarriersSelectionFragment;
import jsettlers.main.android.gameplay.controlsmenu.selection.ShipsSelectionFragment;
import jsettlers.main.android.gameplay.controlsmenu.selection.SoldiersSelectionFragment;
import jsettlers.main.android.gameplay.controlsmenu.selection.SpecialistsSelectionFragment;
import jsettlers.main.android.gameplay.controlsmenu.settlers.SettlersMenuFragment;
import jsettlers.main.android.gameplay.gamemenu.GameMenuDialog;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

@EFragment(R.layout.fragment_map)
@OptionsMenu(R.menu.game)
public class MapFragment extends Fragment implements SelectionListener, BackPressedListener, MenuNavigator {
	private static final String TAG_GAME_MENU_DIALOG = "com.jsettlers.gamemenufragment";
	private static final String TAG_FRAGMENT_SELECTION_MENU = "com.jsettlers.selectionmenufragment";
	private static final String TAG_FRAGMENT_BUILDINGS_MENU = "com.jsettlers.buildingsmenufragment";
	private static final String TAG_FRAGMENT_GOODS_MENU = "com.jsettlers.goodsmenufragment";
	private static final String TAG_FRAGMENT_SETTLERS_MENU = "com.jsettlers.settlersmenufragment";
	private static final String SAVE_BOTTOM_SHEET_STATE = "save_bottom_sheet_state";

	private SelectionControls selectionControls;
	private TaskControls taskControls;
	private GameMenu gameMenu;
	private ViewPagerBottomSheetBehavior bottomSheetBehavior;

	private final LinkedList<BackPressedListener> backPressedListeners = new LinkedList<>();

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
		selectionControls = controlsResolver.getSelectionControls();
		taskControls = controlsResolver.getTaskControls();
		gameMenu = controlsResolver.getGameMenu();
		addMapViews(controlsResolver.getMapContent());
	}

	@AfterViews
	void registerObservers() {
		gameMenu.isPausedState().observe(this, pauseObserver);
		gameMenu.getGameState().observeForever(gameStateObserver);
	}

	private void addMapViews(MapContent mapContent) {
		Region goRegion = new Region(Region.POSITION_CENTER);
		goRegion.setContent(mapContent);

		Area goArea = new Area();
		goArea.set(goRegion);

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
		if (gameMenu.isPausedState().getValue() == Boolean.FALSE) {
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
	public void onDestroy() {
		super.onDestroy();
		gameMenu.getGameState().removeObserver(gameStateObserver);
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
		for (BackPressedListener backPressedListener : backPressedListeners) {
			boolean handled = backPressedListener.onBackPressed();
			if (handled) {
				return true;
			}
		}

		if (taskControls.isTaskActive()) {
			taskControls.endTask();
			return true;
		}

		if (isMenuOpen()) {
			dismissMenu();
			return true;
		}

		if (selectionControls.getCurrentSelection() != null) {
			selectionControls.deselect();
			return true;
		}

		return true;
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

	@Override
	public void addBackPressedListener(BackPressedListener backPressedListener) {
		backPressedListeners.add(backPressedListener);
	}

	@Override
	public void removeBackPressedListener(BackPressedListener backPressedListener) {
		backPressedListeners.remove(backPressedListener);
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

	@OptionsItem(R.id.menu_item_show_game_menu)
	void showGameMenu() {
		dismissMenu();

		if (getChildFragmentManager().findFragmentByTag(TAG_GAME_MENU_DIALOG) == null) {
			GameMenuDialog.create().show(getChildFragmentManager(), TAG_GAME_MENU_DIALOG);
		}
	}

	private void showMenu() {
		bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
	}

	private void dismissGameMenu() {
		DialogFragment gameMenuDialog = (DialogFragment) getChildFragmentManager().findFragmentByTag(TAG_GAME_MENU_DIALOG);
		if (gameMenuDialog != null) {
			gameMenuDialog.dismiss();
		}
	}

	private void addBuildingsMenuFragment() {
		if (getChildFragmentManager().findFragmentByTag(TAG_FRAGMENT_BUILDINGS_MENU) == null) {
			getChildFragmentManager().beginTransaction()
					.replace(R.id.container_menu, BuildingsMenuFragment.newInstance(), TAG_FRAGMENT_BUILDINGS_MENU)
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
			getChildFragmentManager().beginTransaction()
					.replace(R.id.container_menu, ShipsSelectionFragment.newInstance(), TAG_FRAGMENT_SELECTION_MENU)
					.commit();
			break;
		default:
			Log.d("Settlers", "No selection menu for selection type " + selectionControls.getCurrentSelection().getSelectionType().name());
			break;
		}
	}

	private Observer<Boolean> pauseObserver = paused -> {
		if (paused) {
			showGameMenu();
			frameLayout.setForeground(new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.paused_mask)));
		} else {
			gameMenu.unMute();
			frameLayout.setForeground(null);
		}
	};

	private Observer<GameMenu.GameState> gameStateObserver = gameState -> {
		if (gameState == GameMenu.GameState.QUITTED) {
			getActivity().finish();
		}
	};
}
