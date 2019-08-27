package jsettlers.main.android.mainmenu.gamesetup;

import static java8.util.stream.StreamSupport.stream;

import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java8.util.Optional;
import jsettlers.common.ai.EPlayerType;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.android.core.AndroidPreferences;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.gamesetup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.gamesetup.playeritem.PlayerType;

/**
 * Created by Tom Pratt on 07/10/2017.
 */

public class NewSinglePlayerSetupViewModel extends MapSetupViewModel {

	private final GameStarter gameStarter;
	private final AndroidPreferences androidPreferences;
	private final MapLoader mapLoader;

	public NewSinglePlayerSetupViewModel(GameStarter gameStarter, AndroidPreferences androidPreferences, MapLoader mapLoader) {
		super(gameStarter, mapLoader);
		this.gameStarter = gameStarter;
		this.androidPreferences = androidPreferences;
		this.mapLoader = mapLoader;

		updateHumanPlayerSlot();
	}

	@Override
	public void startGame() {
		int maxPlayers = mapLoader.getMaxPlayers();

		List<PlayerSlotPresenter> playerSlotPresenters = Arrays.asList(getPlayerSlots().getValue());
		PlayerSetting[] playerSettings = new PlayerSetting[maxPlayers];
		byte humanPlayerId = playerSlotPresenters.get(0).getPlayerId();

		for (int i = 0; i < maxPlayers; i++) {
			final int position = i;

			Optional<PlayerSlotPresenter> player = stream(playerSlotPresenters)
					.filter(playerSlotPresenter -> playerSlotPresenter.getStartPosition().asByte() == position)
					.findFirst();

			if (player.isPresent()) {
				playerSettings[position] = player.get().getPlayerSettings();
			} else {
				playerSettings[position] = new PlayerSetting();
			}
		}

		JSettlersGame game = new JSettlersGame(mapLoader, 4711L, humanPlayerId, playerSettings);

		gameStarter.setStartingGame(game.start());
		showMapEvent.call();
	}

	private void updateHumanPlayerSlot() {
		PlayerSlotPresenter humanPlayerSlot = playerSlotPresenters.get(0);
		humanPlayerSlot.setName(androidPreferences.getPlayerName());
		humanPlayerSlot.setPossiblePlayerTypes(new PlayerType[] {
				new PlayerType(EPlayerType.HUMAN),
				new PlayerType(EPlayerType.AI_VERY_HARD),
				new PlayerType(EPlayerType.AI_HARD),
				new PlayerType(EPlayerType.AI_EASY),
				new PlayerType(EPlayerType.AI_VERY_EASY)
		});
		humanPlayerSlot.setPlayerType(new PlayerType(EPlayerType.HUMAN));
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

			if (modelClass == NewSinglePlayerSetupViewModel.class) {
				return (T) new NewSinglePlayerSetupViewModel(gameStarter, new AndroidPreferences(activity), mapLoader);
			}
			throw new RuntimeException("NewSinglePlayerSetupViewModel.Factory doesn't know how to create a: " + modelClass.toString());
		}
	}
}
