package jsettlers.main.android.providers;

import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.menus.game.GameMenu;

/**
 * Created by tompr on 21/01/2017.
 */

public interface GameManager {
    ControlsAdapter getControlsAdapter();
    GameMenu getGameMenu();
    boolean isGameInProgress();
}
