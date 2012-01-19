package jsettlers.main;

import java.util.List;

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
}