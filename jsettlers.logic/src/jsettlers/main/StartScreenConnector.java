package jsettlers.main;

import java.util.ArrayList;
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
public class StartScreenConnector implements IStartScreen {

	private final MapList mapList;

	public StartScreenConnector() {
		this.mapList = MapList.getDefaultList();
	}

	@Override
	public ChangingList<IMapDefinition> getSingleplayerMaps() {
		ArrayList<MapLoader> maps = mapList.getFreshMaps();
		List<MapDefinition> result = new LinkedList<MapDefinition>();

		for (MapLoader currMap : maps) {
			MapDefinition mapDef = new MapDefinition(currMap);
			result.add(mapDef);
		}

		return new ChangingList<IMapDefinition>(result);
	}

	@Override
	public ChangingList<IMapDefinition> getStoredSingleplayerGames() {
		ArrayList<MapLoader> maps = mapList.getSavedMaps();
		List<MapDefinition> result = new LinkedList<MapDefinition>();

		for (MapLoader currMap : maps) {
			// TODO @Andreas Eberle: supply saved player information
			MapDefinition mapDef = new MapDefinition(currMap);
			result.add(mapDef);
		}

		return new ChangingList<IMapDefinition>(result);
	}

	@Override
	public ChangingList<IMapDefinition> getMultiplayerMaps() {
		return getSingleplayerMaps();
	}

	@Override
	public ChangingList<IMapDefinition> getRestorableMultiplayerGames() {
		return getStoredSingleplayerGames();
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

	@Override
	public IMultiplayerConnector getMultiplayerConnector(String serverAddr, Player player) {
		return new MultiplayerConnector(serverAddr, player.getId(), player.getName());
	}

}
