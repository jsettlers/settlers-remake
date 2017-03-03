package jsettlers.main.android.core;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IMapInterfaceConnector;
import jsettlers.common.menu.IMultiplayerConnector;
import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.logic.map.loading.list.MapList;

/**
 * Created by tompr on 21/01/2017.
 */

public interface GameStarter {
    //IStartScreen getStartScreen();

    MapList getMapList();

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
