package jsettlers.main.android.providers;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.main.StartScreenConnector;

/**
 * Created by tompr on 21/01/2017.
 */

public interface GameStarter {
    StartScreenConnector getStartScreenConnector();
    void startSinglePlayerGame(IMapDefinition mapDefinition);
    void loadSinglePlayerGame(IMapDefinition mapDefinition);
}
