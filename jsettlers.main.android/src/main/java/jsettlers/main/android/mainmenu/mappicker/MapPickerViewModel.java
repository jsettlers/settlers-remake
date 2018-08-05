package jsettlers.main.android.mainmenu.mappicker;

import static java8.util.stream.StreamSupport.stream;

import java.util.List;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.android.core.GameStarter;

/**
 * Created by Tom Pratt on 06/10/2017.
 */

public abstract class MapPickerViewModel extends ViewModel {

	private final GameStarter gameStarter;
	private final ChangingList<? extends MapLoader> changingMaps;

	private final MapsData maps = new MapsData();

	public MapPickerViewModel(GameStarter gameStarter, ChangingList<? extends MapLoader> changingMaps) {
		this.gameStarter = gameStarter;
		this.changingMaps = changingMaps;
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		if (gameStarter.getStartingGame() == null) {
			abort();
		}
	}

	public LiveData<MapLoader[]> getMaps() {
		return maps;
	}

	public abstract void selectMap(MapLoader map);

	protected void abort() {
	}

	/**
	 * Maps list live data
	 */
	class MapsData extends LiveData<MapLoader[]> implements IChangingListListener<MapLoader> {

		@Override
		protected void onActive() {
			super.onActive();
			changingMaps.setListener(this);
			setValue(sortedMaps(changingMaps.getItems()));
		}

		@Override
		protected void onInactive() {
			super.onInactive();
			changingMaps.removeListener(this);
		}

		@Override
		public void listChanged(ChangingList<? extends MapLoader> list) {
			postValue(sortedMaps(list.getItems()));
		}

		private MapLoader[] sortedMaps(List<? extends MapLoader> items) {
			return stream(items)
					.sorted((o1, o2) -> o1.getMapName().compareToIgnoreCase(o2.getMapName()))
					.toArray(MapLoader[]::new);
		}
	}
}
