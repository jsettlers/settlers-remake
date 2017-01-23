package jsettlers.main.android.mainmenu.presenters;

import java.util.List;

import jsettlers.common.menu.EProgressState;
import jsettlers.common.menu.IJoinPhaseMultiplayerGameConnector;
import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.menu.IJoiningGameListener;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.views.JoinMultiPlayerPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerPickerPresenter implements IChangingListListener<IJoinableGame>, IJoiningGameListener {
    private final JoinMultiPlayerPickerView view;
    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;
    private final ChangingList<IJoinableGame> changingJoinableGames;

    private IJoiningGame joiningGame;

    public JoinMultiPlayerPickerPresenter(JoinMultiPlayerPickerView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        this.view = view;
        this.gameStarter = gameStarter;
        this.navigator = navigator;

        changingJoinableGames = gameStarter.getMultiPlayerConnector().getJoinableMultiplayerGames();
        changingJoinableGames.setListener(this);

        joiningGame = gameStarter.getJoiningGame();
        if (joiningGame == null) {
            // pop
        } else {
            joiningGame.setListener(this);
        }
    }

    public List<IJoinableGame> getJoinableGames() {
        return changingJoinableGames.getItems();
    }


    public void joinableGameSelected(IJoinableGame joinableGame) {
        abort();
        gameStarter.setMapDefinition(joinableGame.getMap());

        joiningGame = gameStarter.getMultiPlayerConnector().joinMultiplayerGame(joinableGame);
        joiningGame.setListener(this);

        gameStarter.setJoiningGame(joiningGame);
    }

    public void viewFinished() {
        if (gameStarter.getStartingGame() == null) {
            abort();
        }
    }

    public void dispose() {
        changingJoinableGames.removeListener(this);
        if (joiningGame != null) {
            joiningGame.setListener(null);
        }
    }

    private void abort() {
        if (joiningGame != null) {
            joiningGame.abort();
        }
        gameStarter.setJoiningGame(null);
        gameStarter.setMapDefinition(null);
    }

    /**
     * ChangingListListener implementation
     */
    @Override
    public void listChanged(ChangingList<? extends IJoinableGame> list) {
        view.joinableGamesChanged(list.getItems());
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
        navigator.showJoinMultiPlayerSetup();
    }

}
