package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import jsettlers.main.android.menus.mainmenu.MapSetupMenu;
import jsettlers.main.android.menus.mainmenu.NewMultiPlayerSetupMenu;
import jsettlers.main.android.providers.GameStarter;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupFragment extends MapSetupFragment {
    @Override
    protected MapSetupMenu createMenu(GameStarter gameStarter, String mapId) {
        return new NewMultiPlayerSetupMenu(gameStarter, mapId);
    }
}
