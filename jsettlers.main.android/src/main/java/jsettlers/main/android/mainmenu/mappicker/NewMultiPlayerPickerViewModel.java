package jsettlers.main.android.mainmenu.mappicker;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IOpenMultiplayerGameInfo;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.localization.Labels;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.android.core.AndroidPreferences;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.core.events.SingleLiveEvent;

/**
 * Created by Tom Pratt on 06/10/2017.
 */

public class NewMultiPlayerPickerViewModel extends MapPickerViewModel implements IJoiningGameListener {

	private final GameStarter gameStarter;
	private final AndroidPreferences androidPreferences;

	private final SingleLiveEvent<String> mapSelectedEvent = new SingleLiveEvent<>();
	private final MutableLiveData<JoiningViewState> joiningState = new MutableLiveData<>();

	private IJoiningGame joiningGame;
	private IMapDefinition tempMapDefinition;

	public NewMultiPlayerPickerViewModel(GameStarter gameStarter, AndroidPreferences androidPreferences, ChangingList<? extends MapLoader> changingMaps) {
		super(gameStarter, changingMaps);
		this.gameStarter = gameStarter;
		this.androidPreferences = androidPreferences;
	}

	@Override
	public void selectMap(MapLoader map) {
		cancelJoining();
		tempMapDefinition = map;

		joiningGame = gameStarter.getMultiPlayerConnector().openNewMultiplayerGame(new IOpenMultiplayerGameInfo() {
			@Override
			public String getMatchName() {
				return androidPreferences.getPlayerName();
			}

			@Override
			public IMapDefinition getMapDefinition() {
				return map;
			}

			@Override
			public int getMaxPlayers() {
				return map.getMaxPlayers();
			}
		});

		joiningGame.setListener(this);

		gameStarter.setJoiningGame(joiningGame);
	}

	@Override
	protected void abort() {
		super.abort();
		cancelJoining();
	}

	public LiveData<String> getMapSelectedEvent() {
		return mapSelectedEvent;
	}

	public LiveData<JoiningViewState> getJoiningState() {
		return joiningState;
	}

	private void cancelJoining() {
		if (joiningGame != null) {
			joiningGame.abort();
		}

		gameStarter.setJoiningGame(null);
		gameStarter.closeMultiPlayerConnector();
	}

	/**
	 * IJoiningGameListener imeplementation
	 */
	@Override
	public void joinProgressChanged(EProgressState state, float progress) {
		String stateString = Labels.getProgress(state);
		int progressPercentage = (int) (progress * 100);

		joiningState.postValue(new JoiningViewState(stateString, progressPercentage));
	}

	@Override
	public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
		joiningGame.setListener(null);
		gameStarter.setJoiningGame(null);
		joiningState.postValue(null);

		gameStarter.setJoinPhaseMultiPlayerConnector(connector);
		mapSelectedEvent.postValue(tempMapDefinition.getMapId());
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
			if (modelClass == NewMultiPlayerPickerViewModel.class) {
				return (T) new NewMultiPlayerPickerViewModel(gameStarter, new AndroidPreferences(activity), gameStarter.getMapList().getFreshMaps());
			}
			throw new RuntimeException("NewSinglePlayerPickerViewModel.Factory doesn't know how to create a: " + modelClass.toString());
		}
	}
}
