package jsettlers.main.android.presenters;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartingGame;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.ui.navigation.MainMenuNavigator;
import jsettlers.main.android.views.NewSinglePlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewSinglePlayerSetupPresenter extends MapSetupPresenter {
    private final MainMenuNavigator navigator;

    public NewSinglePlayerSetupPresenter(NewSinglePlayerSetupView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        super(view, gameStarter);
        this.navigator = navigator;

        IMapDefinition mapDefinition = gameStarter.getMapDefinition();
    }

    @Override
    public void startGame() {
        IStartingGame startingGame = getGameStarter().getStartScreen().startSingleplayerGame(getMapDefinition());
        getGameStarter().setStartingGame(startingGame);
        navigator.showGame();
    }
}
