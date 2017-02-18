package jsettlers.main.android.mainmenu.presenters.setup;

import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.views.NewSinglePlayerSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewSinglePlayerSetupPresenter extends MapSetupPresenterImpl {
    private final MainMenuNavigator navigator;
    private final GameStarter gameStarter;
    private final MapLoader mapLoader;

    public NewSinglePlayerSetupPresenter(NewSinglePlayerSetupView view, MainMenuNavigator navigator, GameStarter gameStarter, MapLoader mapLoader) {
        super(view, gameStarter, mapLoader);
        this.navigator = navigator;
        this.gameStarter = gameStarter;
        this.mapLoader = mapLoader;
    }

    @Override
    public void startGame() {
        byte playerId = (byte) 0;
        PlayerSetting[] playerSettings = PlayerSetting.createDefaultSettings(playerId, (byte) mapLoader.getMaxPlayers());

        JSettlersGame game = new JSettlersGame(mapLoader, 4711L, playerId, playerSettings);

        gameStarter.setStartingGame(game.start());
        navigator.showGame();
    }
}
