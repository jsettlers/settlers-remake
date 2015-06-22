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

import java.util.LinkedList;
import java.util.List;

import jsettlers.common.utils.collections.ChangingList;
import jsettlers.graphics.startscreen.interfaces.IMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.interfaces.Player;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.loader.MapLoader;
import jsettlers.main.datatypes.MapDefinition;

/**
 * This class implements the {@link IStartScreen} interface and acts as connector between the start screen and the game logic.
 *
 * @author Andreas Eberle
 *
 */
public class StartScreenConnector implements IStartScreen
{
	private final MapList mapList;
    private final List<MapDefinition> singlePlayerMaps = new LinkedList<MapDefinition>();
    private final List<MapDefinition> singlePlayerSaves = new LinkedList<MapDefinition>();
	private final ChangingList<IMapDefinition> singlePlayerMapsProperty = new ChangingList<IMapDefinition>(singlePlayerMaps);
	private final ChangingList<IMapDefinition> singlePlayerSavesProperty = new ChangingList<IMapDefinition>(singlePlayerSaves);

	public StartScreenConnector() {
		this.mapList = MapList.getDefaultList();
		reloadContent();
	}

	@Override
	public ChangingList<IMapDefinition> getSingleplayerMaps() {
		return singlePlayerMapsProperty;
	}

    @Override
    public ChangingList<IMapDefinition> getMultiplayerMaps() {
        return singlePlayerMapsProperty; //looks like a TODO
    }

	@Override
	public ChangingList<IMapDefinition> getStoredSingleplayerGames() {
	    return singlePlayerSavesProperty;
	}

	@Override
	public ChangingList<IMapDefinition> getRestorableMultiplayerGames() {
		return singlePlayerSavesProperty; //looks like a TODO
	}

    @Override
    public void reloadContent() {
        singlePlayerMaps.clear();
        for (MapLoader map : mapList.getFreshMaps()) {
            singlePlayerMaps.add(new MapDefinition(map));
        }
        singlePlayerMapsProperty.setList(singlePlayerMaps);// Trigger listener update.

        singlePlayerSaves.clear();
        for (MapLoader savedMap : mapList.getSavedMaps()) {
            singlePlayerSaves.add(new MapDefinition(savedMap));// TODO @Andreas Eberle: supply saved player information
        }
        singlePlayerSavesProperty.setList(singlePlayerSaves);// Trigger listener update.
    }

    @Override
    public IMultiplayerConnector getMultiplayerConnector(String serverAddr, Player player) {
        return new MultiplayerConnector(serverAddr, player.getId(), player.getName());
    }

    @Override
    public IStartingGame startSingleplayerGame(IMapDefinition map) {
        return startGame(map.getId());
    }

    @Override
    public IStartingGame loadSingleplayerGame(IMapDefinition map) {
        return startGame(map.getId());
    }

    private IStartingGame startGame(String mapId) {
        MapLoader mapLoader = mapList.getMapById(mapId);
        long randomSeed = 4711L;
        byte playerId = 0;
        boolean[] availablePlayers = new boolean[mapLoader.getMaxPlayers()];
        for (int i = 0; i < availablePlayers.length; i++) {
            availablePlayers[i] = true;
        }

        JSettlersGame game = new JSettlersGame(mapLoader, randomSeed, playerId, availablePlayers);
        return game.start();
    }
}