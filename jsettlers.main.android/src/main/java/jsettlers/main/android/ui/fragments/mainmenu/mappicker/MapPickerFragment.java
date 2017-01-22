package jsettlers.main.android.ui.fragments.mainmenu.mappicker;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.PreviewImageConverter;
import jsettlers.main.android.R;
import jsettlers.main.android.presenters.MapPickerPresenter;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.utils.FragmentUtil;
import jsettlers.main.android.utils.NoChangeItemAnimator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import jsettlers.main.android.views.MapPickerView;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MapPickerFragment extends Fragment implements MapPickerView {
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(Labels.getString("date.date-only"), Locale.getDefault());

	private MapPickerPresenter presenter;

	private MapAdapter adapter;

	private RecyclerView recyclerView;

	private boolean isSaving;

	public MapPickerFragment() {
	}

	protected abstract MapPickerPresenter getPresenter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		presenter = getPresenter();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map_picker, container, false);
		FragmentUtil.setActionBar(this, view);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		adapter = new MapAdapter(presenter.getItems());

		recyclerView = (RecyclerView) getView().findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
		recyclerView.setItemAnimator(new NoChangeItemAnimator());
		recyclerView.setAdapter(adapter);
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
			getPresenter().abort();
		}
	}

	/**
	 * MapPickerView implementation
     */
	@Override
	public void setItems(List<? extends IMapDefinition> items) {
		getView().post(() -> adapter.setItems(items));
	}

	/**
	 * Subclass related methods
     */
	protected boolean showMapDates() {
		return false;
	}

	/**
	 * RecyclerView Adapter for displaying list of maps
	 */
	private class MapAdapter extends RecyclerView.Adapter<MapHolder> {
		private List<? extends IMapDefinition> maps;

		private View.OnClickListener itemClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RecyclerView.ViewHolder viewHolder = recyclerView.findContainingViewHolder(v);
				int position = viewHolder.getAdapterPosition();
				IMapDefinition map = maps.get(position);
				presenter.itemSelected(map);
			}
		};

		public MapAdapter(List<? extends IMapDefinition> maps) {
			this.maps = maps;
		}

		@Override
		public int getItemCount() {
			return maps.size();
		}

		@Override
		public MapHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			final View itemView = getActivity().getLayoutInflater().inflate(R.layout.item_map, parent, false);
			final MapHolder mapHolder = new MapHolder(itemView);
			itemView.setOnClickListener(itemClickListener);
			return mapHolder;
		}

		@Override
		public void onBindViewHolder(MapHolder holder, int position) {
			IMapDefinition map = maps.get(position);
			holder.bind(map);
		}

		void setItems(List<? extends IMapDefinition> maps) {
			this.maps = maps;
			notifyDataSetChanged();
		}
	}

	private class MapHolder extends RecyclerView.ViewHolder {
		final TextView nameTextView;
		final TextView dateTextView;
		final TextView playerCountTextView;
		final ImageView mapPreviewImageView;

		Disposable subscription;

		public MapHolder(View itemView) {
			super(itemView);
			nameTextView = (TextView) itemView.findViewById(R.id.text_view_name);
			dateTextView = (TextView) itemView.findViewById(R.id.text_view_date);
			playerCountTextView = (TextView) itemView.findViewById(R.id.text_view_player_count);
			mapPreviewImageView = (ImageView) itemView.findViewById(R.id.image_view_map_preview);

			if (showMapDates()) {
				dateTextView.setVisibility(View.VISIBLE);
			}
		}

		public void bind(IMapDefinition mapDefinition) {
			mapPreviewImageView.setImageDrawable(null);
			nameTextView.setText(mapDefinition.getMapName());
			playerCountTextView.setText(mapDefinition.getMinPlayers() + "-" + mapDefinition.getMaxPlayers());

			if (showMapDates()) {
				dateTextView.setText(dateFormat.format(mapDefinition.getCreationDate()));
			}

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
