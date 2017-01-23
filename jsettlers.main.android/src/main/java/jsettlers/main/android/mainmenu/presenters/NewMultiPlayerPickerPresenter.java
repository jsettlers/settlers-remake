package jsettlers.main.android.mainmenu.presenters;

import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IOpenMultiplayerGameInfo;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.NewMultiPlayerPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public class NewMultiPlayerPickerPresenter extends MapPickerPresenter implements IJoiningGameListener {
    private final NewMultiPlayerPickerView view;
    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;

    private IJoiningGame joiningGame;

    public NewMultiPlayerPickerPresenter(NewMultiPlayerPickerView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        super(view, gameStarter, navigator, gameStarter.getStartScreen().getMultiplayerMaps());
        this.view = view;
        this.gameStarter = gameStarter;
        this.navigator = navigator;
    }

    @Override
    public void itemSelected(IMapDefinition mapDefinition) {
        cancelJoining();
        gameStarter.setMapDefinition(mapDefinition);

        joiningGame = gameStarter.getMultiPlayerConnector().openNewMultiplayerGame(new IOpenMultiplayerGameInfo() {
            @Override
            public String getMatchName() {
                return "test name";
            }

            @Override
            public IMapDefinition getMapDefinition() {
                return mapDefinition;
            }

            @Override
            public int getMaxPlayers() {
                return mapDefinition.getMaxPlayers();
            }
        });

        joiningGame.setListener(this);

        gameStarter.setJoiningGame(joiningGame);
    }

    @Override
    protected void abort() {
        super.abort();
        cancelJoining();
    }

    public void dispose() {
        if (joiningGame != null) {
            joiningGame.setListener(null);
        }
    }

    private void cancelJoining() {
        if (joiningGame != null) {
            joiningGame.abort();
        }

        gameStarter.setJoiningGame(null);
        gameStarter.setMapDefinition(null);
    }

    /**
     * IJoiningGameListener imeplementation
     */
    @Override
    public void joinProgressChanged(EProgressState state, float progress) {
        String stateString = Labels.getProgress(state);
        int progressPercentage = (int) (progress * 100);

        view.setJoiningProgress(stateString, progressPercentage);
    }

    @Override
    public void gameJoined(IJoinPhaseMultiplayerGameConnector connector) {
        joiningGame.setListener(null);
        gameStarter.setJoiningGame(null);
        view.dismissJoiningProgress();

        gameStarter.setJoinPhaseMultiPlayerConnector(connector);
        navigator.showNewMultiPlayerSetup();
    }
}
