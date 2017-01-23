package jsettlers.main.android.mainmenu.navigation;

/**
 * Created by tingl on 27/05/2016.
 */
public interface MainMenuNavigator {
    void showNewSinglePlayerPicker();
    void showLoadSinglePlayerPicker();
    void showNewSinglePlayerSetup();

    void showNewMultiPlayerPicker();
    void showJoinMultiPlayerPicker();
    void showNewMultiPlayerSetup();
    void showJoinMultiPlayerSetup();

    void showGame();
    void resumeGame();
}
