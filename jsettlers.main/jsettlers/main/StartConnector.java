package jsettlers.main;

import java.util.List;

import jsettlers.common.network.IMatch;
import jsettlers.common.network.IMatchSettings;
import jsettlers.graphics.startscreen.INetworkConnector;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.logic.map.save.MapList;
import jsettlers.main.network.NetworkConnector;

/**
 * This class implements the {@link IStartScreenConnector} interface and offers the UI the methods and objects it needs.
 * 
 * @author Andreas Eberle
 * 
 */
public class StartConnector implements IStartScreenConnector {

	private final ManagedJSettlers managedJSettlers;
	private final MapList mapList;
	private final NetworkConnector networkConnector;

	public StartConnector(ManagedJSettlers managedJSettlers) {
		this.managedJSettlers = managedJSettlers;
		this.mapList = MapList.getDefaultList();
		this.networkConnector = new NetworkConnector(managedJSettlers);
	}

	@Override
	public List<? extends IMapItem> getMaps() {
		return mapList.getFreshMaps();
	}

	@Override
	public List<? extends ILoadableGame> getLoadableGames() {
		return mapList.getSavedMaps();
	}

	@Override
	public void deleteLoadableGame(ILoadableGame game) {
		mapList.deleteLoadableGame(game);
	}

	@Override
	public void startNewGame(IGameSettings game) {
		managedJSettlers.startGame((IGameCreator) game.getMap());
	}

	@Override
	public void loadGame(ILoadableGame load) {
		managedJSettlers.startGame((IGameCreator) load);
	}

	@Override
	public void exitGame() {
		System.exit(0); // TODO check if there's a better way for this.
	}

	@Override
	public INetworkConnector getNetworkConnector() {
		return networkConnector;
	}

	@Override
	public void openNewNetworkGame(IMatchSettings gameSettings) {
		networkConnector.openNewNetworkGame(gameSettings);
	}

	@Override
	public void joinNetworkGame(IMatch match) {
		networkConnector.joinNetworkGame(match);
	}

}