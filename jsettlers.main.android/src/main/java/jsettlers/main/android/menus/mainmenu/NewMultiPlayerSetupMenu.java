package jsettlers.main.android.menus.mainmenu;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupMenu extends MapSetupMenu {
    private final NewMultiPlayerSetupView view;

    private final GameStarter gameStarter;
    private final IJoinPhaseMultiplayerGameConnector connector;

    public NewMultiPlayerSetupMenu(NewMultiPlayerSetupView view, GameStarter gameStarter) {
        super(view, gameStarter);
        this.view = view;

        connector = gameStarter.getJoinPhaseMultiplayerConnector();
        this.gameStarter = gameStarter;
    }

    @Override
    public void startGame() {

//        IStartingGame startingGame = getGameStarter().get
//        getGameStarter().setStartingGame(startingGame);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public void abort() {
        connector.abort();
        gameStarter.setJoinPhaseMultiPlayerConnector(null);
    }
}
