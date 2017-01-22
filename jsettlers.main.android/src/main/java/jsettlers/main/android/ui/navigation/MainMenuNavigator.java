package jsettlers.main.android.ui.navigation;

import jsettlers.common.menu.IMapDefinition;

/**
 * Created by tingl on 27/05/2016.
 */
public interface MainMenuNavigator {
    void showNewSinglePlayerPicker();
    void showLoadSinglePlayerPicker();
    void showNewSinglePlayerSetup(IMapDefinition mapDefinition);

    void showNewMultiPlayerPicker();
    void showJoinMultiPlayerPicker();
    void showNewMultiPlayerSetup(IMapDefinition mapDefinition);
    void showJoinMultiPlayerSetup();

    void showGame();
    void resumeGame();
}
