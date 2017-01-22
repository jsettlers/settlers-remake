package jsettlers.main.android.presenters;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IStartingGame;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupPresenter extends MapSetupPresenter implements IMultiplayerListener {
    private final NewMultiPlayerSetupView view;

    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;
    private final IJoinPhaseMultiplayerGameConnector connector;

    public NewMultiPlayerSetupPresenter(NewMultiPlayerSetupView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        super(view, gameStarter);
        this.view = view;
        this.gameStarter = gameStarter;
        this.navigator = navigator;

        connector = gameStarter.getJoinPhaseMultiplayerConnector();
        connector.setMultiplayerListener(this);

        //TODO temp while no ui for this
        connector.setReady(true);
    }

    @Override
    public void startGame() {
        connector.startGame();
    }

    @Override
    public void abort() {
        connector.abort();
        gameStarter.setJoinPhaseMultiPlayerConnector(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        connector.setMultiplayerListener(null);
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
