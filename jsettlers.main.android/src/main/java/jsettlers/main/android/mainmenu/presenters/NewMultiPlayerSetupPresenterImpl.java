package jsettlers.main.android.mainmenu.presenters;

import java.util.List;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewMultiPlayerSetupPresenterImpl extends MapSetupPresenterImpl implements NewMultiPlayerSetupPresenter, IMultiplayerListener, IChangingListListener<IMultiplayerPlayer> {
    private final NewMultiPlayerSetupView view;

    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;
    private final IJoinPhaseMultiplayerGameConnector connector;
    private final SettingsManager settingsManager;
    private final String myPlayerId;

    public NewMultiPlayerSetupPresenterImpl(
            NewMultiPlayerSetupView view,
            MainMenuNavigator navigator,
            GameStarter gameStarter,
            IJoinPhaseMultiplayerGameConnector connector,
            SettingsManager settingsManager,
            IMapDefinition mapDefinition) {

        super(view, gameStarter, mapDefinition);
        this.view = view;
        this.navigator = navigator;
        this.gameStarter = gameStarter;
        this.connector = connector;
        this.settingsManager = settingsManager;

        connector.setMultiplayerListener(this);
        connector.getPlayers().setListener(this);

        myPlayerId = settingsManager.get(SettingsManager.SETTING_UUID);

        //TODO temp while no ui for this
        connector.setReady(true);
    }

    @Override
    public void initView() {
        super.initView();
        view.setItems(connector.getPlayers().getItems());
    }

    @Override
    public void viewFinished() {
        connector.abort();
        gameStarter.setJoinPhaseMultiPlayerConnector(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        connector.setMultiplayerListener(null);
        connector.getPlayers().setListener(null);
    }




    @Override
    public String getMyPlayerId() {
        return myPlayerId;
    }

    @Override
    public void startGame() {
        connector.startGame();
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
