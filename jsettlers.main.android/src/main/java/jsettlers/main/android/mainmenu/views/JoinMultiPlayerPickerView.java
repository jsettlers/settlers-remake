package jsettlers.main.android.mainmenu.views;

import java.util.List;

import jsettlers.common.menu.IJoinableGame;

/**
 * Created by tompr on 22/01/2017.
 */

public interface JoinMultiPlayerPickerView {
    void updateJoinableGames(List<? extends IJoinableGame> joinableGames);
    void setJoiningProgress(String stateString, int progressPercentage);
    void dismissJoiningProgress();
    void showSearchingForGamesView();
    void hideSearchingForGamesView();

}
