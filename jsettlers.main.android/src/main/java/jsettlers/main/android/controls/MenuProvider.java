package jsettlers.main.android.controls;

import jsettlers.main.android.menus.BuildingsMenu;
import jsettlers.main.android.menus.GameMenu;
import jsettlers.main.android.menus.SettlersSoldiersMenu;

/**
 * Created by tompr on 13/01/2017.
 */

public interface MenuProvider {
    GameMenu getGameMenu();
    BuildingsMenu getBuildingsMenu();
    SettlersSoldiersMenu getSettlersSoldiersMenu();
}
