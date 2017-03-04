package jsettlers.main.android.mainmenu.views;

/**
 * Created by tompr on 04/03/2017.
 */

public interface MainMenuView {
    void showResourcePicker();
    void hideResourcePicker();

    void updatePauseButton(boolean paused);
    void updateQuitButton(boolean canQuitConfirm);

    void showResumeGameView();
    void hideResumeGameView();
}
