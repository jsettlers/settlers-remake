package jsettlers.main.android.presenters;

import java.util.List;

import jsettlers.common.menu.IJoinableGame;
import jsettlers.common.menu.IJoiningGame;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.common.utils.collections.IChangingListListener;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.dialogs.JoiningGameProgressDialog;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.JoinMultiPlayerPickerView;
import jsettlers.main.datatypes.JoinableGame;

/**
 * Created by tompr on 22/01/2017.
 */

public class JoinMultiPlayerPickerPresenter implements IChangingListListener<IJoinableGame> {
    private final JoinMultiPlayerPickerView view;
    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;
    private final ChangingList<IJoinableGame> changingJoinableGames;

    public JoinMultiPlayerPickerPresenter(JoinMultiPlayerPickerView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        this.view = view;
        this.gameStarter = gameStarter;
        this.navigator = navigator;

        changingJoinableGames = gameStarter.getMultiPlayerConnector().getJoinableMultiplayerGames();
        changingJoinableGames.setListener(this);
    }

    public List<IJoinableGame> getJoinableGames() {
        return changingJoinableGames.getItems();
    }


    public void joinableGameSelected(IJoinableGame joinableGame) {
        navigator.showJoinMultiPlayerSetup(joinableGame);
    }

    public void dispose() {
        changingJoinableGames.removeListener(this);

    }

    /**
     * ChangingListListener implementation
     */
    @Override
    public void listChanged(ChangingList<? extends IJoinableGame> list) {
        view.joinableGamesChanged(list.getItems());
    }

}
