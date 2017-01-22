package jsettlers.main.android.menus.mainmenu;

import jsettlers.main.android.providers.GameStarter;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupMenu extends MapSetupMenu {
    public NewMultiPlayerSetupMenu(GameStarter gameStarter, String mapId) {
        super(gameStarter, findMap(gameStarter.getStartScreen().getMultiplayerMaps(), mapId));
    }

    @Override
    public void startGame() {

    }
}
