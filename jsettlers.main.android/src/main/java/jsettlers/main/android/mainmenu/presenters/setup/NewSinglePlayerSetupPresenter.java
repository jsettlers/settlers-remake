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

package jsettlers.main.android.mainmenu.presenters.setup;

import java.util.List;

import jsettlers.common.ai.EPlayerType;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.android.core.AndroidPreferences;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerType;
import jsettlers.main.android.mainmenu.views.NewSinglePlayerSetupView;

import static java8.util.stream.StreamSupport.stream;

/**
 * Created by tompr on 21/01/2017.
 */
public class NewSinglePlayerSetupPresenter extends MapSetupPresenterImpl {
	private final MainMenuNavigator navigator;
	private final GameStarter gameStarter;
	private final MapLoader mapLoader;

	public NewSinglePlayerSetupPresenter(NewSinglePlayerSetupView view, MainMenuNavigator navigator, GameStarter gameStarter,			AndroidPreferences androidPreferences, MapLoader mapLoader) {
		super(view, gameStarter, mapLoader);
		this.navigator = navigator;
		this.gameStarter = gameStarter;
		this.mapLoader = mapLoader;

		PlayerSlotPresenter humanPlayerSlot = getPlayerSlotPresenters().get(0);
		humanPlayerSlot.setName(androidPreferences.getPlayerName());
		setHumanSlotPlayerTypes(humanPlayerSlot);
	}

	@Override
	public void initView() {
		super.initView();
		updateViewItems();
	}

	@Override
	public boolean startGame() {
		List<PlayerSlotPresenter> playerSlotPresenters = getPlayerSlotPresenters();
		PlayerSetting[] playerSettings = new PlayerSetting[playerSlotPresenters.size()];
		byte humanPlayerId = playerSlotPresenters.get(0).getPlayerId();

		// Sort players by position
		PlayerSlotPresenter[] sortedPlayers = stream(playerSlotPresenters)
				.sorted((playerSlot, otherPlayerSlot) -> playerSlot.getStartPosition().asByte() - otherPlayerSlot.getStartPosition().asByte())
				.toArray(PlayerSlotPresenter[]::new);

		// Get player settings if player slot is within player count limit, otherwise use new PlayerSettings() for no player at that position
		for (int i = 0; i < sortedPlayers.length; i++) {
			PlayerSlotPresenter player = sortedPlayers[i];

			if (playerSlotPresenters.indexOf(player) < getPlayerCount().getNumberOfPlayers()) {
				playerSettings[i] = player.getPlayerSettings();
			} else {
				playerSettings[i] = new PlayerSetting();
			}
		}

		JSettlersGame game = new JSettlersGame(mapLoader, 4711L, humanPlayerId, playerSettings);

		gameStarter.setStartingGame(game.start());
		navigator.showGame();
		return true;
	}

	private static void setHumanSlotPlayerTypes(PlayerSlotPresenter playerSlotPresenter) {
		playerSlotPresenter.setPossiblePlayerTypes(new PlayerType[] {
				new PlayerType(EPlayerType.HUMAN),
				new PlayerType(EPlayerType.AI_VERY_HARD),
				new PlayerType(EPlayerType.AI_HARD),
				new PlayerType(EPlayerType.AI_EASY),
				new PlayerType(EPlayerType.AI_VERY_EASY)
		});
		playerSlotPresenter.setPlayerType(new PlayerType(EPlayerType.HUMAN));
	}
}
