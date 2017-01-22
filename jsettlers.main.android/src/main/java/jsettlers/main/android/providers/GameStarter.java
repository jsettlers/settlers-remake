package jsettlers.main.android.providers;

import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IMultiplayerConnector;
import jsettlers.common.menu.IStartScreen;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.graphics.map.MapInterfaceConnector;

/**
 * Created by tompr on 21/01/2017.
 */

public interface GameStarter {
    IStartScreen getStartScreen();
    IMultiplayerConnector getMultiPlayerConnector();
    IStartingGame getStartingGame();
    IJoiningGame getJoiningGame();

    void startSinglePlayerGame(IMapDefinition mapDefinition);
    void loadSinglePlayerGame(IMapDefinition mapDefinition);

    void joinMultiPlayerGame(IJoinableGame joinableGame);

    MapInterfaceConnector gameStarted(IStartedGame game);
}
