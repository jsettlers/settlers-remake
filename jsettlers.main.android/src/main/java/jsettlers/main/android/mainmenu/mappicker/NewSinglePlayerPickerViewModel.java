package jsettlers.main.android.mainmenu.mappicker;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.core.events.SingleLiveEvent;

/**
 * Created by Tom Pratt on 06/10/2017.
 */

public class NewSinglePlayerPickerViewModel extends MapPickerViewModel {

	private final SingleLiveEvent<String> mapSelectedEvent = new SingleLiveEvent<>();

	public NewSinglePlayerPickerViewModel(GameStarter gameStarter, ChangingList<? extends MapLoader> changingMaps) {
		super(gameStarter, changingMaps);
	}

	@Override
	public void selectMap(MapLoader map) {
		mapSelectedEvent.setValue(map.getMapId());
	}

	public LiveData<String> getMapSelectedEvent() {
		return mapSelectedEvent;
	}

	/**
	 * ViewModel factory
	 */
	public static class Factory implements ViewModelProvider.Factory {

		private final Activity activity;
		private final GameStarter gameStarter;

		public Factory(Activity activity) {
			this.activity = activity;
			gameStarter = (GameStarter) activity.getApplication();
		}

		@Override
		public <T extends ViewModel> T create(Class<T> modelClass) {
			if (modelClass == NewSinglePlayerPickerViewModel.class) {
				return (T) new NewSinglePlayerPickerViewModel(gameStarter, gameStarter.getMapList().getFreshMaps());
			}
			throw new RuntimeException("NewSinglePlayerPickerViewModel.Factory doesn't know how to create a: " + modelClass.toString());
		}
	}
}
