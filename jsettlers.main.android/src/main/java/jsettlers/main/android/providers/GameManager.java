package jsettlers.main.android.providers;

import jsettlers.common.menu.IStartedGame;
import jsettlers.common.menu.IStartingGame;
import jsettlers.graphics.map.MapInterfaceConnector;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.menus.game.GameMenu;

/**
 * Created by tompr on 21/01/2017.
 */

public interface GameManager {
    ControlsAdapter getControlsAdapter();
    GameMenu getGameMenu();
    boolean isGameInProgress();
    IStartingGame getStartingGame();
    MapInterfaceConnector gameStarted(IStartedGame game);
}
