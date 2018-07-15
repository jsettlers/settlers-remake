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

package jsettlers.main.android.mainmenu.mappicker;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.Semaphore;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.core.ui.NoChangeItemAnimator;
import jsettlers.main.android.core.resources.PreviewImageConverter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;

/**
 * Created by tompr on 21/01/2017.
 */
@EFragment(R.layout.fragment_map_picker_join_multiplayer)
public class JoinMultiPlayerPickerFragment extends Fragment{
	private static final String TAG_JOINING_PROGRESS_DIALOG = "joingingprogress";

	public static JoinMultiPlayerPickerFragment create() {
		return new JoinMultiPlayerPickerFragment_();
	}

	@ViewById(R.id.recycler_view)
	RecyclerView recyclerView;
	@ViewById(R.id.layout_searching_for_games)
	View searchingForGamesView;
	@ViewById(R.id.toolbar)
	Toolbar toolbar;

	JoinMultiPlayerPickerViewModel viewModel;
	JoinableGamesAdapter adapter;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewModel = ViewModelProviders.of(this, new JoinMultiPlayerPickerViewModel.Factory(getActivity())).get(JoinMultiPlayerPickerViewModel.class);
	}

	@AfterViews
	void setupToolbar() {
		FragmentUtil.setActionBar(this, toolbar);
		toolbar.setTitle(R.string.join_multi_player_game);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel.getShowNoGamesMessage().observe(this, showMessage -> searchingForGamesView.setVisibility(showMessage ? View.VISIBLE : View.GONE));

		viewModel.getMapSelectedEvent().observe(this, mapId -> {
			MainMenuNavigator mainMenuNavigator = (MainMenuNavigator) getActivity();
			mainMenuNavigator.showJoinMultiPlayerSetup(mapId);
		});

		viewModel.getJoinableGames().observe(this, joinableGames -> {
			if (adapter == null) {
				adapter = new JoinableGamesAdapter(joinableGames);
			}

			if (recyclerView.getAdapter() == null) {
				recyclerView.setHasFixedSize(true);
				recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
				recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
				recyclerView.setItemAnimator(new NoChangeItemAnimator());
				recyclerView.setAdapter(adapter);
			}

			adapter.setItems(joinableGames);
		});

		viewModel.getJoiningState().observe(this, joiningViewState -> {
			if (joiningViewState == null) {
				dismissJoiningProgress();
			} else {
				setJoiningProgress(joiningViewState.getState(), joiningViewState.getProgress());
			}
		});
	}

	private void setJoiningProgress(String stateString, int progressPercentage) {
		JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager().findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
		if (joiningProgressDialog == null) {
			JoiningGameProgressDialog.create(stateString, progressPercentage).show(getChildFragmentManager(), TAG_JOINING_PROGRESS_DIALOG);
		} else {
			joiningProgressDialog.setProgress(stateString, progressPercentage);
		}
	}

	private void dismissJoiningProgress() {
		JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager().findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
		if (joiningProgressDialog != null) {
			joiningProgressDialog.dismiss();
		}
	}

	/**
	 * RecyclerView Adapter for displaying list of maps
	 */
	private class JoinableGamesAdapter extends RecyclerView.Adapter<JoinableGamesAdapter.JoinableGameHolder> {
		private IJoinableGame[] joinableGames;
		private final Semaphore limitImageLoadingSemaphore = new Semaphore(3, true);

		public JoinableGamesAdapter(IJoinableGame[] joinableGames) {
			this.joinableGames = joinableGames;
		}

		@Override
		public int getItemCount() {
			return joinableGames.length;
		}

		@Override
		public JoinableGameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			final View itemView = getActivity().getLayoutInflater().inflate(R.layout.item_joinable_game, parent, false);
			final JoinableGameHolder mapHolder = new JoinableGameHolder(itemView);

			itemView.setOnClickListener(view -> {
				int position = mapHolder.getAdapterPosition();
				viewModel.joinableGameSelected(joinableGames[position]);
			});

			return mapHolder;
		}

		@Override
		public void onBindViewHolder(JoinableGameHolder holder, int position) {
			IJoinableGame joinableGame = joinableGames[position];
			holder.bind(joinableGame);
		}

		void setItems(IJoinableGame[] joinableGames) {
			this.joinableGames = joinableGames;
			notifyDataSetChanged();
		}

		class JoinableGameHolder extends RecyclerView.ViewHolder {
			final TextView hostNameTextView;
			final TextView mapNameTextView;
			final TextView playerCountTextView;
			final ImageView mapPreviewImageView;

			Disposable subscription;

			public JoinableGameHolder(View itemView) {
				super(itemView);
				hostNameTextView = itemView.findViewById(R.id.text_view_host_name);
				mapNameTextView = itemView.findViewById(R.id.text_view_map_name);
				playerCountTextView = itemView.findViewById(R.id.text_view_player_count);
				mapPreviewImageView = itemView.findViewById(R.id.image_view_map_preview);
			}

			public void bind(IJoinableGame joinableGame) {
				IMapDefinition mapDefinition = joinableGame.getMap();
				if (mapDefinition == null)
					return;

				hostNameTextView.setText(joinableGame.getName());
				mapNameTextView.setText(mapDefinition.getMapName());

				playerCountTextView.setText(mapDefinition.getMinPlayers() + "-" + mapDefinition.getMaxPlayers());

				if (subscription != null) {
					subscription.dispose();
				}

				subscription = PreviewImageConverter.toBitmap(mapDefinition.getImage(), limitImageLoadingSemaphore)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeWith(new DisposableSingleObserver<Bitmap>() {
							@Override
							public void onSuccess(Bitmap bitmap) {
								mapPreviewImageView.setImageBitmap(bitmap);
							}

							@Override
							public void onError(Throwable e) {
								mapPreviewImageView.setImageDrawable(null);
							}
						});
			}
		}
	}
}
