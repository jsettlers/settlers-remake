package jsettlers.main.android.mainmenu.gamesetup;

import java.util.List;

import android.app.Activity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.android.core.AndroidPreferences;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.gamesetup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.gamesetup.playeritem.ReadyListener;

/**
 * Created by Tom Pratt on 07/10/2017.
 */

public class JoinMultiPlayerSetupViewModel extends MapSetupViewModel implements IMultiplayerListener, IChangingListListener<IMultiplayerPlayer>, ReadyListener {

	private final GameStarter gameStarter;
	private final AndroidPreferences androidPreferences;
	private final IJoinPhaseMultiplayerGameConnector connector;
	private final MapLoader mapLoader;

	public JoinMultiPlayerSetupViewModel(GameStarter gameStarter, AndroidPreferences androidPreferences, IJoinPhaseMultiplayerGameConnector connector, MapLoader mapLoader) {
		super(gameStarter, mapLoader);
		this.gameStarter = gameStarter;
		this.androidPreferences = androidPreferences;
		this.connector = connector;
		this.mapLoader = mapLoader;

		connector.setMultiplayerListener(this);
		connector.getPlayers().setListener(this);

		updateSlots();
	}

	@Override
	public void startGame() {
		connector.startGame();
	}

	/**
	 * IMultiplayerListener implementation
	 */
	@Override
	public void gameAborted() {
		gameStarter.setJoinPhaseMultiPlayerConnector(null);

		// TODO pop
	}

	@Override
	public void gameIsStarting(IStartingGame game) {
		gameStarter.setJoinPhaseMultiPlayerConnector(null);
		gameStarter.setStartingGame(game);
		showMapEvent.postValue(null);
	}

	/**
	 * ChangingListListener implementation
	 */
	@Override
	public void listChanged(ChangingList<? extends IMultiplayerPlayer> list) {
		updateSlots();
		playerSlots.postValue(playerSlots.getValue());
		// updateViewItems(); // trigger a notify data set changed for now. Probably want to update the view more dynamically at some point
	}

	/**
	 * ReadyListener implementation
	 */
	@Override
	public void readyChanged(boolean ready) {
		connector.setReady(ready);
	}

	private void updateSlots() {
		List<IMultiplayerPlayer> players = connector.getPlayers().getItems();
		int numberOfConnectedPlayers = players.size();

		for (int i = 0; i < playerSlotPresenters.size(); i++) {
			PlayerSlotPresenter playerSlotPresenter = playerSlotPresenters.get(i);

			if (i < numberOfConnectedPlayers) {
				setHumanSlotPlayerTypes(playerSlotPresenter);

				IMultiplayerPlayer multiplayerPlayer = players.get(i);
				playerSlotPresenter.setName(multiplayerPlayer.getName());
				playerSlotPresenter.setReady(multiplayerPlayer.isReady());
				playerSlotPresenter.setShowReadyControl(true);

				boolean isMe = multiplayerPlayer.getId().equals(androidPreferences.getPlayerId());

				if (isMe) {
					playerSlotPresenter.setControlsEnabled(true);
					playerSlotPresenter.setReadyListener(this);
				} else {
					playerSlotPresenter.setControlsEnabled(false);
					playerSlotPresenter.setReadyListener(null);
				}
			} else {
				setComputerSlotPlayerTypes(playerSlotPresenter);
				playerSlotPresenter.setName("Computer " + i);
				playerSlotPresenter.setShowReadyControl(false);
				playerSlotPresenter.setControlsEnabled(true);
				playerSlotPresenter.setReadyListener(null);
			}
		}
	}

	/**
	 * ViewModel factory
	 */
	public static class Factory implements ViewModelProvider.Factory {

		private final Activity activity;
		private final String mapId;

		public Factory(Activity activity, String mapId) {
			this.activity = activity;
			this.mapId = mapId;
		}

		@Override
		public <T extends ViewModel> T create(Class<T> modelClass) {
			GameStarter gameStarter = (GameStarter) activity.getApplication();
			MapLoader mapLoader = gameStarter.getMapList().getMapById(mapId);
			IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector = gameStarter.getJoinPhaseMultiplayerConnector();

			if (joinPhaseMultiplayerGameConnector == null) {
				throw new MultiPlayerConnectorUnavailableException();
			}

			if (modelClass == JoinMultiPlayerSetupViewModel.class) {
				return (T) new JoinMultiPlayerSetupViewModel(gameStarter, new AndroidPreferences(activity), joinPhaseMultiplayerGameConnector, mapLoader);
			}
			throw new RuntimeException("JoinMultiPlayerSetupViewModel.Factory doesn't know how to create a: " + modelClass.toString());
		}
	}
}
