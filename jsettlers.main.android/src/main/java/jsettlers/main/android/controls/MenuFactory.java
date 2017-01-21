package jsettlers.main.android.controls;

import jsettlers.main.android.menus.game.BuildingsMenu;
import jsettlers.main.android.menus.game.GameMenu;
import jsettlers.main.android.menus.game.SettlersSoldiersMenu;

/**
 * Created by tompr on 13/01/2017.
 */

public interface MenuFactory {
    GameMenu getGameMenu();
    BuildingsMenu getBuildingsMenu();
    SettlersSoldiersMenu getSettlersSoldiersMenu();
}
