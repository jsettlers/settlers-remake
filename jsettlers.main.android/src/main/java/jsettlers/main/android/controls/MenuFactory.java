package jsettlers.main.android.controls;

import jsettlers.main.android.gameplay.presenters.BuildingsMenu;
import jsettlers.main.android.gameplay.presenters.SettlersSoldiersMenu;

/**
 * Created by tompr on 13/01/2017.
 */

public interface MenuFactory {
    GameMenu getGameMenu();
    BuildingsMenu getBuildingsMenu();
    SettlersSoldiersMenu getSettlersSoldiersMenu();
}
