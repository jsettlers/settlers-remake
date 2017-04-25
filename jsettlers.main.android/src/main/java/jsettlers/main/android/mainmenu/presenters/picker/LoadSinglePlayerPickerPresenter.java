package jsettlers.main.android.mainmenu.presenters.picker;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.MapPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public class LoadSinglePlayerPickerPresenter extends MapPickerPresenter {
	private final GameStarter gameStarter;
	private final MainMenuNavigator navigator;

	public LoadSinglePlayerPickerPresenter(MapPickerView view, MainMenuNavigator navigator, GameStarter gameStarter,
			ChangingList<? extends MapLoader> changingMaps) {
		super(view, navigator, gameStarter, changingMaps);
		this.navigator = navigator;
		this.gameStarter = gameStarter;
	}

	@Override
	public void itemSelected(MapLoader mapLoader) {
		// IStartingGame startingGame = gameStarter.getStartScreen().loadSingleplayerGame(mapDefinition);
		PlayerSetting[] playerSettings = mapLoader.getFileHeader().getPlayerSettings();

		byte playerId = 0; // find playerId of HUMAN player
		for (byte i = 0; i < playerSettings.length; i++) {
			if (playerSettings[i].getPlayerType() == EPlayerType.HUMAN) {
				playerId = i;
				break;
			}
		}

		JSettlersGame game = new JSettlersGame(mapLoader, 4711L, playerId, playerSettings);

		gameStarter.setStartingGame(game.start());
		navigator.showGame();
	}
}
