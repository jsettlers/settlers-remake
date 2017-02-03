package jsettlers.main.android.mainmenu.presenters;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IStartingGame;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerSetupView;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerSetupPresenterImpl implements JoinMultiPlayerSetupPresenter, IMultiplayerListener {
    private final JoinMultiPlayerSetupView view;
    private final MainMenuNavigator navigator;
    private final GameStarter gameStarter;
    private final IJoinPhaseMultiplayerGameConnector connector;

    public JoinMultiPlayerSetupPresenterImpl(JoinMultiPlayerSetupView view, MainMenuNavigator navigator, GameStarter gameStarter, IJoinPhaseMultiplayerGameConnector connector) {
        this.view = view;
        this.navigator = navigator;
        this.gameStarter = gameStarter;
        this.connector = connector;

        connector.setMultiplayerListener(this);
    }

    @Override
    public void setReady(boolean ready) {
        connector.setReady(ready);
    }

    @Override
    public void viewFinished() {
        if (gameStarter.getStartingGame() == null) {
            abort();
        }
    }

    @Override
    public void dispose() {
        connector.setMultiplayerListener(null);
    }

    private void abort() {
        connector.abort();
        gameStarter.setJoinPhaseMultiPlayerConnector(null);
    }

    /**
     * IMultiplayerListener implementation
     */
    @Override
    public void gameAborted() {
        //TODO pop
    }

    @Override
    public void gameIsStarting(IStartingGame game) {
        gameStarter.setJoinPhaseMultiPlayerConnector(null);
        gameStarter.setStartingGame(game);
        navigator.showGame();
    }
}
