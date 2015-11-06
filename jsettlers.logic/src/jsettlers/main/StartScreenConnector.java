/*******************************************************************************
 * Copyright (c) 2015
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
 *******************************************************************************/
package jsettlers.main;

import jsettlers.common.CommonConstants;
import jsettlers.common.ai.EWhatToDoAiType;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.interfaces.Player;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.MapLoader;
import jsettlers.logic.player.PlayerSetting;

/**
 * This class implements the {@link IStartScreen} interface and acts as connector between the start screen and the game logic.
 * 
 * @author Andreas Eberle
 * 
 */
public class StartScreenConnector implements IStartScreen {

	private final MapList mapList;

	public StartScreenConnector() {
		this.mapList = MapList.getDefaultList();
	}

	@Override
	public ChangingList<? extends IMapDefinition> getSingleplayerMaps() {
		return mapList.getFreshMaps();
	}

	@Override
	public ChangingList<? extends IMapDefinition> getStoredSingleplayerGames() {
		return mapList.getSavedMaps();
	}

	@Override
	public ChangingList<? extends IMapDefinition> getMultiplayerMaps() {
		return getSingleplayerMaps();
	}

	@Override
	public ChangingList<? extends IMapDefinition> getRestorableMultiplayerGames() {
		return getStoredSingleplayerGames();
	}

	@Override
	public IStartingGame startSingleplayerGame(IMapDefinition map) {
		return startGame(map.getMapId());
	}

	@Override
	public IStartingGame loadSingleplayerGame(IMapDefinition map) {
		return startGame(map.getMapId());
	}

	private IStartingGame startGame(String mapId) {
		MapLoader mapLoader = mapList.getMapById(mapId);
		long randomSeed = 4711L;
		byte playerId = 0;
		PlayerSetting[] playerSettings = new PlayerSetting[mapLoader.getMaxPlayers()];
		playerSettings[playerId] = new PlayerSetting(true);
		for (byte i = 0; i < playerSettings.length; i++) {
			if (i != playerId) {
				playerSettings[i] = new PlayerSetting(true, CommonConstants.ENABLE_AI ? EWhatToDoAiType.getTypeByIndex(i) : null);
			}
		}

		JSettlersGame game = new JSettlersGame(mapLoader, randomSeed, playerId, playerSettings);
		return game.start();
	}

	@Override
	public IMultiplayerConnector getMultiplayerConnector(String serverAddr, Player player) {
		return new MultiplayerConnector(serverAddr, player.getId(), player.getName());
	}

}
