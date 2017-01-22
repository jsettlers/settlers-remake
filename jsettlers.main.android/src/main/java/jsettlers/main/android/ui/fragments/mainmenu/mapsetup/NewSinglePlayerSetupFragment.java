package jsettlers.main.android.ui.fragments.mainmenu.mapsetup;

import jsettlers.main.android.menus.mainmenu.MapSetupMenu;
import jsettlers.main.android.menus.mainmenu.NewSinglePlayerSetupMenu;
import jsettlers.main.android.providers.GameStarter;

public class NewSinglePlayerSetupFragment extends MapSetupFragment {
    @Override
    protected MapSetupMenu createMenu(GameStarter gameStarter, String mapId) {
        return new NewSinglePlayerSetupMenu(gameStarter, mapId);
    }
}
