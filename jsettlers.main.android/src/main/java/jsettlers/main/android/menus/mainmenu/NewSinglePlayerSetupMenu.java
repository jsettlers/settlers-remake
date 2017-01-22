package jsettlers.main.android.menus.mainmenu;

import jsettlers.main.android.providers.GameStarter;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewSinglePlayerSetupMenu extends MapSetupMenu {
    public NewSinglePlayerSetupMenu(GameStarter gameStarter, String mapId) {
        super(gameStarter, findMap(gameStarter.getStartScreen().getSingleplayerMaps(), mapId));
    }

    @Override
    public void startGame() {
        getGameStarter().startSinglePlayerGame(getMapDefinition());
    }
}
