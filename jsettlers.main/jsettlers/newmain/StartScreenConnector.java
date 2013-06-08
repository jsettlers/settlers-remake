package jsettlers.newmain;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import jsettlers.graphics.startscreen.interfaces.IChangingList;
import jsettlers.graphics.startscreen.interfaces.ILoadableMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IMultiplayerConnector;
import jsettlers.graphics.startscreen.interfaces.IStartScreen;
import jsettlers.graphics.startscreen.interfaces.IStartableMapDefinition;
import jsettlers.graphics.startscreen.interfaces.IStartingGame;
import jsettlers.graphics.startscreen.interfaces.Player;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.map.save.MapLoader;
import jsettlers.newmain.datatypes.ChangingList;
import jsettlers.newmain.datatypes.MapDefinition;

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
	public IChangingList<IStartableMapDefinition> getSingleplayerMaps() {
		ArrayList<MapLoader> maps = mapList.getFreshMaps();
		List<MapDefinition> result = new LinkedList<MapDefinition>();

		for (MapLoader currMap : maps) {
			MapDefinition mapDef = new MapDefinition(currMap);
			result.add(mapDef);
		}

		return new ChangingList<IStartableMapDefinition>(result);
	}

	@Override
	public IChangingList<ILoadableMapDefinition> getStoredSingleplayerGames() {
		ArrayList<MapLoader> maps = mapList.getSavedMaps();
		List<MapDefinition> result = new LinkedList<MapDefinition>();

		for (MapLoader currMap : maps) {
			// TODO @Andreas Eberle: supply saved player information
			MapDefinition mapDef = new MapDefinition(currMap);
			result.add(mapDef);
		}

		return new ChangingList<ILoadableMapDefinition>(result);
	}

	@Override
	public IChangingList<IStartableMapDefinition> getMultiplayerMaps() {
		return getSingleplayerMaps();
	}

	@Override
	public IChangingList<ILoadableMapDefinition> getRestorableMultiplayerGames() {
		return getStoredSingleplayerGames();
	}

	@Override
	public IStartingGame startSingleplayerGame(IStartableMapDefinition map) {
		return startGame(map.getId());
	}

	@Override
	public IStartingGame loadSingleplayerGame(ILoadableMapDefinition map) {
		return startGame(map.getId());
	}

	private IStartingGame startGame(String mapId) {
		MapLoader mapLoader = mapList.getMapById(mapId);
		long randomSeed = 4711L;

		JSettlersGame game = new JSettlersGame(mapLoader, randomSeed, (byte) 0);
		return game.start();
	}

	@Override
	public IMultiplayerConnector getMultiplayerConnector(String serverAddr, Player player) throws UnknownHostException, IOException {
		return new MultiplayerConnector(serverAddr, player.getId(), player.getName());
	}

}
