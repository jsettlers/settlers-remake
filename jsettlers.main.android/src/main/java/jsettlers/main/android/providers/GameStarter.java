package jsettlers.main.android.providers;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IMultiplayerConnector;
import jsettlers.common.menu.IStartScreen;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;

/**
 * Created by tompr on 21/01/2017.
 */

public interface GameStarter {
    IStartScreen getStartScreen();
    IMultiplayerConnector getMultiPlayerConnector();
    IJoinPhaseMultiplayerGameConnector getJoinPhaseMultiplayerConnector();
    IJoiningGame getJoiningGame();
    IStartingGame getStartingGame();
    void setStartingGame(IStartingGame startingGame);

    void startSinglePlayerGame(IMapDefinition mapDefinition);
    void loadSinglePlayerGame(IMapDefinition mapDefinition);

    void joinMultiPlayerGame(IJoinableGame joinableGame);

    void gameJoined(IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector);
    IMapInterfaceConnector gameStarted(IStartedGame game);
}
