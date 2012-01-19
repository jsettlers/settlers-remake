package jsettlers.main;

import java.util.List;

import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.logic.map.save.MapList;
import jsettlers.logic.network.NetworkConnector;

class StartConnector implements IStartScreenConnector {
	/**
     * 
     */
	private final IGameStarter gamestarter;
	private final MapList mapList;

	/**
	 * @param managedJSettlers
	 */
	StartConnector(IGameStarter managedJSettlers) {
		gamestarter = managedJSettlers;
		mapList = MapList.getDefaultList();
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
	public List<? extends IRecoverableGame> getRecoverableGames() {
		return mapList.getSavedMultiplayerMaps();
	}

	@Override
	public void startNewGame(IGameSettings game) {
		gamestarter.startGame(game);
	}

	@Override
	public void loadGame(ILoadableGame load) {
		gamestarter.loadGame(load);
	}

	@Override
	public void exitGame() {
		System.exit(0);
	}

	private NetworkConnector networker = new NetworkConnector();

	@Override
	public INetworkGame[] getNetworkGames() {
		return networker.getNetworkGames();
	}

	@Override
	public void setNetworkServer(String host) {
		networker.setNetworkServer(host);
	}

	@Override
	public void recoverNetworkGame(IRecoverableGame game) {
		networker.recoverNetworkGame(game);
	}

	@Override
	public void joinNetworkGame(INetworkGame game) {
		networker.joinNetworkGame(game);
	}

	@Override
	public void startMatch(IGameSettings game, String matchName) {
		networker.startMatch(game, matchName);
	}

	@Override
	public void addNetworkGameListener(INetworkGameListener gameListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeNetworkGameListener(INetworkGameListener gameListener) {
		// TODO Auto-generated method stub

	}
}