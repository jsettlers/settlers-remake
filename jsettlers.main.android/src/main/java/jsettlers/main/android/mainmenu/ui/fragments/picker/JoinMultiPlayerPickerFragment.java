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

package jsettlers.main.android.mainmenu.ui.fragments.picker;

import java.util.List;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.android.R;
import jsettlers.main.android.core.ui.FragmentUtil;
import jsettlers.main.android.core.ui.NoChangeItemAnimator;
import jsettlers.main.android.core.ui.PreviewImageConverter;
import jsettlers.main.android.mainmenu.factories.PresenterFactory;
import jsettlers.main.android.mainmenu.presenters.picker.JoinMultiPlayerPickerPresenter;
import jsettlers.main.android.mainmenu.ui.dialogs.JoiningGameProgressDialog;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerPickerView;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by tompr on 21/01/2017.
 */
@EFragment(R.layout.fragment_map_picker)
public class JoinMultiPlayerPickerFragment extends Fragment implements JoinMultiPlayerPickerView {
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

	JoinMultiPlayerPickerPresenter presenter;
	JoinableGamesAdapter adapter;

	boolean isSaving = false;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		presenter = PresenterFactory.createJoinMultiPlayerPickerPresenter(getActivity(), this);
	}

	@AfterViews
	void setupToolbar() {
		FragmentUtil.setActionBar(this, toolbar);
		presenter.initView(); // will need to remove this if this class ends up using the general Picker base class, because its already called there
	}

	@Override
	public void onResume() {
		super.onResume();
		getActivity().setTitle(R.string.join_multi_player_game);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		isSaving = true;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		presenter.dispose();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if (isRemoving() && !isSaving) {
			presenter.viewFinished();
		}
	}

	/**
	 * JoinMultiPlayerPickerView implementation
	 * 
	 * @param joinableGames
	 */
	@Override
	@UiThread
	public void updateJoinableGames(List<? extends IJoinableGame> joinableGames) {
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
	}

	@Override
	public void setJoiningProgress(String stateString, int progressPercentage) {
		JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager().findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
		if (joiningProgressDialog == null) {
			JoiningGameProgressDialog.create(stateString, progressPercentage).show(getChildFragmentManager(), TAG_JOINING_PROGRESS_DIALOG);
		} else {
			joiningProgressDialog.setProgress(stateString, progressPercentage);
		}
	}

	@Override
	public void dismissJoiningProgress() {
		JoiningGameProgressDialog joiningProgressDialog = (JoiningGameProgressDialog) getChildFragmentManager().findFragmentByTag(TAG_JOINING_PROGRESS_DIALOG);
		if (joiningProgressDialog != null) {
			joiningProgressDialog.dismiss();
		}
	}

	@Override
	@UiThread
	public void showSearchingForGamesView() {
		searchingForGamesView.setVisibility(View.VISIBLE);
	}

	@Override
	@UiThread
	public void hideSearchingForGamesView() {
		searchingForGamesView.setVisibility(View.GONE);
	}

	private void joinableGameSelected(IJoinableGame joinableGame) {
		presenter.joinableGameSelected(joinableGame);
	}

	/**
	 * RecyclerView Adapter for displaying list of maps
	 */
	private class JoinableGamesAdapter extends RecyclerView.Adapter<JoinableGameHolder> {
		private List<? extends IJoinableGame> joinableGames;

		public JoinableGamesAdapter(List<? extends IJoinableGame> joinableGames) {
			this.joinableGames = joinableGames;
		}

		@Override
		public int getItemCount() {
			return joinableGames.size();
		}

		@Override
		public JoinableGameHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			final View itemView = getActivity().getLayoutInflater().inflate(R.layout.item_joinable_game, parent, false);
			final JoinableGameHolder mapHolder = new JoinableGameHolder(itemView);

			itemView.setOnClickListener(view -> {
				int position = mapHolder.getAdapterPosition();
				joinableGameSelected(joinableGames.get(position));
			});

			return mapHolder;
		}

		@Override
		public void onBindViewHolder(JoinableGameHolder holder, int position) {
			IJoinableGame joinableGame = joinableGames.get(position);
			holder.bind(joinableGame);
		}

		void setItems(List<? extends IJoinableGame> joinableGames) {
			this.joinableGames = joinableGames;
			notifyDataSetChanged();
		}
	}

	private class JoinableGameHolder extends RecyclerView.ViewHolder {
		final TextView hostNameTextView;
		final TextView mapNameTextView;
		final TextView playerCountTextView;
		final ImageView mapPreviewImageView;

		Disposable subscription;

		public JoinableGameHolder(View itemView) {
			super(itemView);
			hostNameTextView = (TextView) itemView.findViewById(R.id.text_view_host_name);
			mapNameTextView = (TextView) itemView.findViewById(R.id.text_view_map_name);
			playerCountTextView = (TextView) itemView.findViewById(R.id.text_view_player_count);
			mapPreviewImageView = (ImageView) itemView.findViewById(R.id.image_view_map_preview);
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

			subscription = PreviewImageConverter.toBitmap(mapDefinition.getImage())
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
