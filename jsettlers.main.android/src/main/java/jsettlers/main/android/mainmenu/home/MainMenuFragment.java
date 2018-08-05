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

package jsettlers.main.android.mainmenu.home;

import java.util.ArrayList;
import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.settings.SettingsActivity_;

@EFragment(R.layout.fragment_main_menu)
@OptionsMenu(R.menu.fragment_mainmenu)
public class MainMenuFragment extends Fragment {
	private static final int REQUEST_CODE_PERMISSION_STORAGE = 10;

	private MainMenuViewModel viewModel;
	private MainMenuNavigator mainMenuNavigator;
	private HomeAdapter homeAdapter;

	@ViewById(R.id.toolbar)
	Toolbar toolbar;
	@ViewById(R.id.recyclerView)
	RecyclerView recyclerView;

	public static MainMenuFragment create() {
		return new MainMenuFragment_();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainMenuNavigator = (MainMenuNavigator) getActivity();
		viewModel = ViewModelProviders.of(this, new MainMenuViewModel.Factory(getActivity().getApplication())).get(MainMenuViewModel.class);
	}

	@AfterViews
	public void afterViews() {
		FragmentUtil.setActionBar(this, toolbar);
		toolbar.setTitle(R.string.app_name);

		homeAdapter = new HomeAdapter(LayoutInflater.from(getActivity()), this, mainMenuNavigator);
		homeAdapter.setHasStableIds(true);
		recyclerView.setAdapter(homeAdapter);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel.getResumeState().observe(this, homeAdapter::showOrHideGameInProgress);
		viewModel.getAreResourcesLoaded().observe(this, homeAdapter::showOrHideDirectoryPicker);
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
				DirectoryPickerViewHolder directoryPickerViewHolder = (DirectoryPickerViewHolder) recyclerView.findViewHolderForItemId(R.layout.vh_directory_picker);
				homeAdapter.expandDirectoryPicker(directoryPickerViewHolder);
			}
			break;
		}
	}

	private class HomeAdapter extends RecyclerView.Adapter {
		private final LayoutInflater layoutInflater;
		private final Fragment viewModelOwner;
		private final MainMenuNavigator mainMenuNavigator;

		private final List<Integer> layouts = new ArrayList<>();

		public HomeAdapter(LayoutInflater layoutInflater, Fragment parent, MainMenuNavigator mainMenuNavigator) {
			this.layoutInflater = layoutInflater;
			this.viewModelOwner = parent;
			this.mainMenuNavigator = mainMenuNavigator;

			layouts.add(R.layout.vh_single_player);
			layouts.add(R.layout.vh_multi_player);
		}

		@Override
		public int getItemCount() {
			return layouts.size();
		}

		@Override
		public int getItemViewType(int position) {
			return layouts.get(position);
		}

		@Override
		public long getItemId(int position) {
			return layouts.get(position);
		}

		@NonNull
		@Override
		public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = layoutInflater.inflate(viewType, parent, false);

			switch (viewType) {
			case R.layout.vh_directory_picker: {
				DirectoryPickerViewHolder viewHolder = new DirectoryPickerViewHolder(view, viewModelOwner);

				Button chooseDirectoryButton = view.findViewById(R.id.button_resources);
				chooseDirectoryButton.setOnClickListener(v -> {
					if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
						requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQUEST_CODE_PERMISSION_STORAGE);
					} else {
						expandDirectoryPicker(viewHolder);
						// TODO save expanded state for rotation
					}
				});

				return viewHolder;
			}
			case R.layout.vh_game_in_progress:
				return new GameInProgressViewHolder(view, viewModelOwner, mainMenuNavigator);
			case R.layout.vh_single_player:
				return new SinglePlayerViewHolder(view, viewModelOwner, mainMenuNavigator);
			case R.layout.vh_multi_player:
				return new MultiPlayerViewHolder(view, viewModelOwner, mainMenuNavigator);
			default:
				throw new RuntimeException("Layout not support");
			}
		}

		@Override
		public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		}

		private void expandDirectoryPicker(DirectoryPickerViewHolder viewHolder) {
			ViewGroup.LayoutParams layoutParams = viewHolder.itemView.getLayoutParams();
			layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
			viewHolder.itemView.setLayoutParams(layoutParams);
			viewHolder.onExpand();
		}

		private void showOrHideDirectoryPicker(boolean areResourcesLoaded) {
			showOrHide(!areResourcesLoaded, R.layout.vh_directory_picker);
		}

		private void showOrHideGameInProgress(MainMenuViewModel.ResumeViewState resumeViewState) {
			showOrHide(resumeViewState != null, R.layout.vh_game_in_progress);
		}

		private void showOrHide(boolean show, int layout) {
			Integer layoutInteger = layout;
			int index = layouts.indexOf(layoutInteger);

			if (show) {
				if (index == -1) {
					layouts.add(0, layoutInteger);
					notifyItemInserted(0);
				}
			} else {
				if (index > -1) {
					layouts.remove(layoutInteger);
					notifyItemRemoved(index);
				}
			}
		}
	}
}
