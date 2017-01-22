package jsettlers.main.android.presenters;

import jsettlers.common.menu.ILoadableMapPlayer;
import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartingGame;
import jsettlers.main.android.providers.GameStarter;
import jsettlers.main.android.views.NewSinglePlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewSinglePlayerSetupPresenter extends MapSetupPresenter {
    public NewSinglePlayerSetupPresenter(NewSinglePlayerSetupView view, GameStarter gameStarter) {
        super(view, gameStarter);

        IMapDefinition mapDefinition = gameStarter.getMapDefinition();
    }

    @Override
    public void startGame() {
        IStartingGame startingGame = getGameStarter().getStartScreen().startSingleplayerGame(getMapDefinition());
        getGameStarter().setStartingGame(startingGame);
    }
}
