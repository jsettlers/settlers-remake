package jsettlers.main.android.core;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
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
    void closeMultiPlayerConnector();

    IStartingGame getStartingGame();
    void setStartingGame(IStartingGame startingGame);

    IJoiningGame getJoiningGame();
    void setJoiningGame(IJoiningGame joiningGame);

    IJoinPhaseMultiplayerGameConnector getJoinPhaseMultiplayerConnector();
    void setJoinPhaseMultiPlayerConnector(IJoinPhaseMultiplayerGameConnector joinPhaseMultiplayerGameConnector);

    IMapInterfaceConnector gameStarted(IStartedGame game);
}
