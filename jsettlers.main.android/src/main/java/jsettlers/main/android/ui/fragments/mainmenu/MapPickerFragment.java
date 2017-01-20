package jsettlers.main.android.ui.fragments.mainmenu;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.PreviewImageConverter;
import jsettlers.main.android.R;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.utils.FragmentUtil;
import jsettlers.main.android.utils.NoChangeItemAnimator;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class MapPickerFragment extends Fragment {
	private static final String STATE_MAP_ID = "map_id";

	private MapAdapter adapter;
	private IMapDefinition selectedMap;
	private MainMenuNavigator navigator;
	private GameStarter gameStarter;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat(Labels.getString("date.date-only"), Locale.getDefault());

	private RecyclerView recyclerView;

	public MapPickerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		gameStarter = (GameStarter) getActivity();
		adapter = new MapAdapter(getMaps());
		navigator = (MainMenuNavigator) getActivity();

		//TODO dont need to save current map anymore
		if (savedInstanceState != null) {
			String mapId = savedInstanceState.getString(STATE_MAP_ID);
			if (mapId != null) {
				for (IMapDefinition map : adapter.getMaps()) {
					if (map.getMapId().equals(mapId)) {
						selectedMap = map;
						break;
					}
				}
			}
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_map_picker, container, false);
		FragmentUtil.setActionBar(this, view);

		recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
		recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());
		recyclerView.setItemAnimator(new NoChangeItemAnimator());
		recyclerView.setAdapter(adapter);

		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (selectedMap != null) {
			outState.putString(STATE_MAP_ID, selectedMap.getMapId());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		adapter.onDestroy();
	}

	public IMapDefinition getSelectedMap() {
		return selectedMap;
	}

	protected GameStarter getGameStarter() {
		return gameStarter;
	}

	protected MainMenuNavigator getNavigator() {
		return navigator;
	}

	protected abstract ChangingList<? extends IMapDefinition> getMaps();

	protected void mapSelected(IMapDefinition map) {
		selectedMap = map;
	}

	protected boolean showMapDates() {
		return false;
	}

	/**
	 * RecyclerView Adapter for displaying list of maps
	 */
	private class MapAdapter extends RecyclerView.Adapter<MapHolder> implements IChangingListListener<IMapDefinition> {
		private ChangingList<? extends IMapDefinition> changingMaps;
		private List<? extends IMapDefinition> maps;

		private View.OnClickListener itemClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				RecyclerView.ViewHolder viewHolder = recyclerView.findContainingViewHolder(v);
				int position = viewHolder.getAdapterPosition();
				IMapDefinition map = maps.get(position);
				mapSelected(map);
			}
		};

		public MapAdapter(ChangingList<? extends IMapDefinition> changingMaps) {
			this.maps = changingMaps.getItems();
			this.changingMaps = changingMaps;
			this.changingMaps.setListener(this);
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

		@Override
		public void listChanged(ChangingList<? extends IMapDefinition> list) {
			// final List<IMapDefinition> newList = new ArrayList<>(list.getItems());
			// Collections.sort(newList, getDefaultComparator());
			maps = list.getItems();
			notifyDataSetChanged();
		}

		public void onDestroy() {
			changingMaps.removeListener(this);
		}

		public List<? extends IMapDefinition> getMaps() {
			return maps;
		}

		// private Comparator<IMapDefinition> getDefaultComparator() {
		// return IMapDefinition.MAP_NAME_COMPARATOR;
		// }
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
