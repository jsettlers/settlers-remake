package jsettlers.main.android.mainmenu.presenters;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.menu.IStartingGame;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.MapPickerView;

/**
 * Created by tompr on 22/01/2017.
 */

public class LoadSinglePlayerPickerPresenter extends MapPickerPresenter {
    private final GameStarter gameStarter;
    private final MainMenuNavigator navigator;

    public LoadSinglePlayerPickerPresenter(MapPickerView view, GameStarter gameStarter, MainMenuNavigator navigator) {
        super(view, gameStarter, navigator, gameStarter.getStartScreen().getStoredSingleplayerGames());
        this.gameStarter = gameStarter;
        this.navigator = navigator;
    }

    @Override
    public void itemSelected(IMapDefinition mapDefinition) {
        IStartingGame startingGame = gameStarter.getStartScreen().loadSingleplayerGame(mapDefinition);
        gameStarter.setStartingGame(startingGame);
        navigator.showGame();
    }
}
