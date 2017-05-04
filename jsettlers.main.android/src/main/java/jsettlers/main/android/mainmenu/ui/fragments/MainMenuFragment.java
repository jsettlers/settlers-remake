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

package jsettlers.main.android.mainmenu.ui.fragments;

import static jsettlers.main.android.core.controls.GameMenu.ACTION_PAUSE;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CANCELLED;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_QUIT_CONFIRM;
import static jsettlers.main.android.core.controls.GameMenu.ACTION_UNPAUSE;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.Receiver;
import org.androidannotations.annotations.ViewById;

import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.MainMenuPresenter;
import jsettlers.main.android.mainmenu.ui.activities.SettingsActivity_;
import jsettlers.main.android.mainmenu.ui.dialogs.DirectoryPickerDialog;
import jsettlers.main.android.mainmenu.views.MainMenuView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * A simple {@link Fragment} subclass.
 */
@EFragment(R.layout.fragment_main_menu)
@OptionsMenu(R.menu.fragment_mainmenu)
public class MainMenuFragment extends Fragment implements MainMenuView, DirectoryPickerDialog.Listener {
	private static final int REQUEST_CODE_PERMISSION_STORAGE = 10;

	private MainMenuPresenter presenter;

	@ViewById(R.id.linear_layout_main)
	LinearLayout mainLinearLayout;
	@ViewById(R.id.card_view_resume)
	View resumeView;
	@ViewById(R.id.button_pause)
	Button pauseButton;
	@ViewById(R.id.button_quit)
	Button quitButton;
	@ViewById(R.id.toolbar)
	Toolbar toolbar;

	View resourcesView;

	private boolean showDirectoryPicker = false;

	public static MainMenuFragment create() {
		return new MainMenuFragment_();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		presenter = PresenterFactory.createMainMenuPresenter(getActivity(), this);
	}

	@AfterViews
	public void afterViews() {
		FragmentUtil.setActionBar(this, toolbar);
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

		presenter.updateResumeGameView();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_item_settings:
			SettingsActivity_.intent(this).start();
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
		pauseButton.setText(paused ? R.string.game_menu_unpause : R.string.game_menu_pause);
	}

	@Override
	public void updateQuitButton(boolean canQuitConfirm) {
		quitButton.setText(canQuitConfirm ? R.string.game_menu_quit_confirm : R.string.game_menu_quit);
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

	@Click(R.id.card_view_resume)
	void resumeView() {
		presenter.resumeSelected();
	}

	@Click(R.id.button_quit)
	void quitClicked() {
		presenter.quitSelected();
	}

	@Click(R.id.button_pause)
	void pauseClicked() {
		presenter.pauseSelected();
	}

	@Click(R.id.button_new_single_player_game)
	void newSinglePlayerGameClicked() {
		presenter.newSinglePlayerSelected();
	}

	@Click(R.id.button_load_single_player_game)
	void loadSinglePlayerGameClicked() {
		presenter.loadSinglePlayerSelected();
	}

	@Click(R.id.button_new_multi_player_game)
	void newMultiPlayerGameClicked() {
		presenter.newMultiPlayerSelected();
	}

	@Click(R.id.button_join_multi_player_game)
	void joinMultiplayerGameClicked() {
		presenter.joinMultiPlayerSelected();
	}

	@Receiver(actions = ACTION_QUIT, local = true)
	void quitReceived() {
		presenter.updateResumeGameView();
	}

	@Receiver(actions = ACTION_QUIT_CONFIRM, local = true)
	void quitConfirmReceived() {
		presenter.updateResumeGameView();
	}

	@Receiver(actions = ACTION_QUIT_CANCELLED, local = true)
	void quitCancelled() {
		presenter.updateResumeGameView();
	}

	@Receiver(actions = ACTION_PAUSE, local = true)
	void pauseReceived() {
		presenter.updateResumeGameView();
	}

	@Receiver(actions = ACTION_UNPAUSE, local = true)
	void unpauseReceived() {
		presenter.updateResumeGameView();
	}
}
