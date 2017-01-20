package jsettlers.main.android.providers;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.StartScreenConnector;

/**
 * Created by tingl on 27/05/2016.
 */
public interface GameStarter {
    StartScreenConnector getStartScreenConnector();
    void startSinglePlayerGame(IMapDefinition mapDefinition);
    void loadSinglePlayerGame(IMapDefinition mapDefinition);
}
