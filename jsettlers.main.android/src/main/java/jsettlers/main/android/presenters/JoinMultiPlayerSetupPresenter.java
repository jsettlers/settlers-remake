package jsettlers.main.android.presenters;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.JoinMultiPlayerSetupView;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerSetupPresenter implements IMultiplayerListener {
    private final JoinMultiPlayerSetupView view;
    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;

    private IJoinPhaseMultiplayerGameConnector connector;

    public JoinMultiPlayerSetupPresenter(JoinMultiPlayerSetupView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        this.view = view;
        this.gameStarter = gameStarter;
        this.navigator = navigator;

        //IJoinableGame joinableGame = findJoinableGame(gameStarter.getMultiPlayerConnector().getJoinableMultiplayerGames(), joinableGameId);

        connector = gameStarter.getJoinPhaseMultiplayerConnector();
        if (connector == null) {
            // pop
        } else {
            connector.setMultiplayerListener(this);
        }
    }

    public void setReady(boolean ready) {
        connector.setReady(ready);
    }

    public void abort() {
        if (connector != null) {
            connector.abort();
            gameStarter.setJoinPhaseMultiPlayerConnector(null);
        }
    }

    public void dispose() {
        if (connector != null) {
            connector.setMultiplayerListener(null);
        }
    }


    /**
     * IMultiplayerListener implementation
     */
    @Override
    public void gameAborted() {

    }

    @Override
    public void gameIsStarting(IStartingGame game) {
        gameStarter.setStartingGame(game);
        navigator.showGame();
    }


    protected static IJoinableGame findJoinableGame(ChangingList<? extends IJoinableGame> joinableGames, String joinableGameId) {
        for (IJoinableGame joinableGame : joinableGames.getItems()) {
            if (joinableGame.getId().equals(joinableGameId)) {
                return joinableGame;
            }
        }
        throw new RuntimeException("Couldn't find joinable game.");
    }
}
