package jsettlers.main.android.presenters;

import java.util.List;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupPresenter extends MapSetupPresenter implements IMultiplayerListener, IChangingListListener<IMultiplayerPlayer> {
    private final NewMultiPlayerSetupView view;

    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;
    private final IJoinPhaseMultiplayerGameConnector connector;
    private final String myPlayerId;

    public NewMultiPlayerSetupPresenter(NewMultiPlayerSetupView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        super(view, gameStarter);
        this.view = view;
        this.gameStarter = gameStarter;
        this.navigator = navigator;

        connector = gameStarter.getJoinPhaseMultiplayerConnector();
        connector.setMultiplayerListener(this);
        connector.getPlayers().setListener(this);

        myPlayerId = SettingsManager.getInstance().get(SettingsManager.SETTING_UUID);

        //TODO temp while no ui for this
        connector.setReady(true);
    }

    public List<IMultiplayerPlayer> getPlayers() {
        return connector.getPlayers().getItems();
    }

    public String getMyPlayerId() {
        return myPlayerId;
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
        connector.getPlayers().setListener(null);
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

    /**
     * ChangingListListener implementation
     */
    @Override
    public void listChanged(ChangingList<? extends IMultiplayerPlayer> list) {
        view.setItems(connector.getPlayers().getItems());
    }
}
