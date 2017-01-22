package jsettlers.main.android.views;

import java.util.List;

import jsettlers.common.menu.IJoinableGame;

/**
 * Created by tompr on 22/01/2017.
 */

public interface JoinMultiPlayerPickerView {
    void joinableGamesChanged(List<? extends IJoinableGame> joinableGames);
}
