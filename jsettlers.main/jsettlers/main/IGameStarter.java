package jsettlers.main;

import jsettlers.graphics.startscreen.IStartScreenConnector.IGameSettings;
import jsettlers.graphics.startscreen.IStartScreenConnector.ILoadableGame;

public interface IGameStarter {

	public abstract void startGame(IGameSettings game);

	public abstract void loadGame(ILoadableGame load);

}