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
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.JoinMultiPlayerPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerPickerPresenter implements IChangingListListener<IJoinableGame>, IJoiningGameListener {
    private final JoinMultiPlayerPickerView view;
    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;
    private final ChangingList<IJoinableGame> changingJoinableGames;

    private IJoiningGame joiningGame;

    public JoinMultiPlayerPickerPresenter(JoinMultiPlayerPickerView view, MainMenuNavigator navigator, GameStarter gameStarter) {
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

    public void initView() {
    }

    public void viewFinished() {
        if (gameStarter.getStartingGame() == null) {
            abort();
        }
    }

    private void abort() {
        if (joiningGame != null) {
            joiningGame.abort();
        }
        gameStarter.setJoiningGame(null);
    }

    public void dispose() {
        changingJoinableGames.removeListener(this);
        if (joiningGame != null) {
            joiningGame.setListener(null);
        }
    }





    public void joinableGameSelected(IJoinableGame joinableGame) {
        abort();
        //gameStarter.setMapDefinition(joinableGame.getMap());

        joiningGame = gameStarter.getMultiPlayerConnector().joinMultiplayerGame(joinableGame);
        joiningGame.setListener(this);

        gameStarter.setJoiningGame(joiningGame);
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
