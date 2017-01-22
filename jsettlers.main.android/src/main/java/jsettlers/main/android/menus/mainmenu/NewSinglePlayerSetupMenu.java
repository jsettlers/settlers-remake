package jsettlers.main.android.menus.mainmenu;

import jsettlers.common.menu.IStartingGame;
import jsettlers.main.android.providers.GameStarter;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewSinglePlayerSetupMenu extends MapSetupMenu {
    public NewSinglePlayerSetupMenu(GameStarter gameStarter) {
        super(gameStarter);
    }

    @Override
    public void startGame() {
        IStartingGame startingGame = getGameStarter().getStartScreen().startSingleplayerGame(getMapDefinition());
        getGameStarter().setStartingGame(startingGame);
    }
}
