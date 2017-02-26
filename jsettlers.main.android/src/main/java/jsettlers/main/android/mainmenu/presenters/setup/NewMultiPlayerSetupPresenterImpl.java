package jsettlers.main.android.mainmenu.presenters.setup;

import java.util.List;

import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IMultiplayerListener;
import jsettlers.common.menu.IMultiplayerPlayer;
import jsettlers.common.menu.IStartingGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.startscreen.SettingsManager;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
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
            MapLoader mapLoader) {

        super(view, gameStarter, mapLoader);
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

        updateSlots();
    }

    private void updateSlots() {
        List<PlayerSlotPresenter> playerSlotPresenters = getPlayerSlotPresenters();
        List<IMultiplayerPlayer> players = connector.getPlayers().getItems();
        int numberOfConnectedPlayers = players.size();

        for (int i = 0; i < playerSlotPresenters.size(); i++) {
            PlayerSlotPresenter playerSlotPresenter = playerSlotPresenters.get(i);

            if (i < numberOfConnectedPlayers) {
                setHumanSlotPlayerTypes(playerSlotPresenter);

                IMultiplayerPlayer multiplayerPlayer = players.get(i);
                playerSlotPresenter.setName(multiplayerPlayer.getName());
                playerSlotPresenter.setReady(multiplayerPlayer.isReady());
                playerSlotPresenter.setShowReadyControl(true);

                boolean isMe = multiplayerPlayer.getId().equals(settingsManager.get(SettingsManager.SETTING_UUID));
                playerSlotPresenter.setControlsEnabled(isMe);
            } else {
                setComputerSlotPlayerTypes(playerSlotPresenter);
                playerSlotPresenter.setName("Computer " + i);
                playerSlotPresenter.setShowReadyControl(false);
                playerSlotPresenter.setControlsEnabled(true);
            }
        }
    }

    @Override
    public void initView() {
        super.initView();
        updateViewItems();
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
        updateSlots();

        // trigger a notify data set changed for now. Probably want to update the view more dynamically at some point
        updateViewItems();
    }
}
