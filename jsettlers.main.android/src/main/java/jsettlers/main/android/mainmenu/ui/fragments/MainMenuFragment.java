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

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.BindingObject;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.DataBound;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.databinding.FragmentMainMenuBinding;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.ui.activities.SettingsActivity_;
import jsettlers.main.android.mainmenu.ui.dialogs.DirectoryPickerDialog;
import jsettlers.main.android.mainmenu.viewmodels.MainMenuViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
@DataBound
@EFragment(R.layout.fragment_main_menu)
@OptionsMenu(R.menu.fragment_mainmenu)
public class MainMenuFragment extends Fragment implements DirectoryPickerDialog.Listener {
	private static final int REQUEST_CODE_PERMISSION_STORAGE = 10;

	private MainMenuViewModel viewModel;
    private MainMenuNavigator mainMenuNavigator;

	@ViewById(R.id.linearLayout_main)
	LinearLayout mainLinearLayout;
	@ViewById(R.id.cardView_resume)
	View resumeView;
	@ViewById(R.id.cardView_resourcePicker)
	View resourcePickerView;
	@ViewById(R.id.button_pause)
	Button pauseButton;
	@ViewById(R.id.button_quit)
	Button quitButton;
    @ViewById(R.id.button_new_single_player_game)
    Button newSinglePlayerButton;
    @ViewById(R.id.button_load_single_player_game)
    Button loadSinglePlayerButton;
    @ViewById(R.id.button_new_multi_player_game)
    Button newMultiPlayerButton;
    @ViewById(R.id.button_join_multi_player_game)
    Button joinMultiPlayerButton;
	@ViewById(R.id.toolbar)
	Toolbar toolbar;

	@BindingObject
	FragmentMainMenuBinding binding;

	public static MainMenuFragment create() {
		return new MainMenuFragment_();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainMenuNavigator = (MainMenuNavigator)getActivity();
		viewModel = ViewModelProviders.of(this, new MainMenuViewModel.Factory(getActivity())).get(MainMenuViewModel.class);
	}

	@AfterViews
	public void afterViews() {
		FragmentUtil.setActionBar(this, toolbar);
		toolbar.setTitle(R.string.app_name);
		binding.setViewmodel(viewModel);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel.getResumeState().observe(this, this::updateResumeView);
		viewModel.getAreResourcesLoaded().observe(this, this::updateResourceView);
		viewModel.getAreResourcesLoaded().observe(this, newSinglePlayerButton::setEnabled);
		viewModel.getAreResourcesLoaded().observe(this, loadSinglePlayerButton::setEnabled);
		viewModel.getAreResourcesLoaded().observe(this, newMultiPlayerButton::setEnabled);
		viewModel.getAreResourcesLoaded().observe(this, joinMultiPlayerButton::setEnabled);
		viewModel.getShowSinglePlayer().observe(this, z -> mainMenuNavigator.showNewSinglePlayerPicker());
		viewModel.getShowLoadSinglePlayer().observe(this, z -> mainMenuNavigator.showLoadSinglePlayerPicker());
		viewModel.getShowMultiplayerPlayer().observe(this, z -> mainMenuNavigator.showNewMultiPlayerPicker());
		viewModel.getShowJoinMultiplayerPlayer().observe(this, z -> mainMenuNavigator.showJoinMultiPlayerPicker());
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
				showDirectoryPicker();
			}
			break;
		}
	}


	/**
	 * DirectoryPickerDialog.Listener implementation
	 */
	@Override
	public void onDirectorySelected() {
		viewModel.resourceDirectoryChosen();
	}


	@Click(R.id.button_resources)
	void showDirectoryPicker() {
		if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_PERMISSION_STORAGE);
		} else {
			DirectoryPickerDialog.newInstance().show(getChildFragmentManager(), null);
		}
	}

	@Click(R.id.cardView_resume)
	void resumeView() {
		mainMenuNavigator.resumeGame();
	}



	private void updateResumeView(MainMenuViewModel.ResumeViewState resumeViewState) {
		if (resumeViewState == null){
			resumeView.setVisibility(View.GONE);
		} else {
			pauseButton.setText(resumeViewState.isPaused() ? R.string.game_menu_unpause : R.string.game_menu_pause);
			quitButton.setText(resumeViewState.isConfirmQuit() ? R.string.game_menu_quit_confirm : R.string.game_menu_quit);
			resumeView.setVisibility(View.VISIBLE);
		}
	}

	private void updateResourceView(boolean areResourcesLoaded) {
		if (areResourcesLoaded) {
			resourcePickerView.setVisibility(View.GONE);
		} else  {
			resourcePickerView.setVisibility(View.VISIBLE);
		}
	}
}
