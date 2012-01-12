package jsettlers.main;

import java.io.File;
import java.util.List;

import jsettlers.common.resources.ResourceManager;
import jsettlers.graphics.startscreen.IStartScreenConnector;
import jsettlers.logic.map.save.MapList;

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
        mapList = new MapList(new File(ResourceManager.getSaveDirectory(), "maps"));
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
	public IRecoverableGame[] getRecoverableGames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public INetworkGame[] getNetworkGames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNetworkServer(String host) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startNewGame(IGameSettings game) {
		gamestarter.startGame(game);
	}


	@Override
	public void loadGame(ILoadableGame load) {
		
	}

	@Override
	public void recoverNetworkGame(IRecoverableGame game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void joinNetworkGame(INetworkGame game) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addNetworkGameListener(INetworkGameListener gameListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeNetworkGameListener(INetworkGameListener gameListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startGameServer(IGameSettings game, String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void exitGame() {
		System.exit(0);
	}

}