package jsettlers.main.android.providers;

import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;

/**
 * Created by tingl on 28/05/2016.
 */
public interface GameProvider {
    IStartingGame getStartingGame();
    IMapInterfaceConnector loadFinished(IStartedGame game);
}
