package jsettlers.main.android.mainmenu.presenters.setup;

import java.util.List;

import jsettlers.common.ai.EPlayerType;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerType;
import jsettlers.main.android.mainmenu.views.NewSinglePlayerSetupView;

import java8.util.stream.StreamSupport;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewSinglePlayerSetupPresenter extends MapSetupPresenterImpl {
    private final NewSinglePlayerSetupView view;
    private final MainMenuNavigator navigator;
    private final GameStarter gameStarter;
    private final MapLoader mapLoader;

    public NewSinglePlayerSetupPresenter(NewSinglePlayerSetupView view, MainMenuNavigator navigator, GameStarter gameStarter, MapLoader mapLoader) {
        super(view, gameStarter, mapLoader);
        this.view = view;
        this.navigator = navigator;
        this.gameStarter = gameStarter;
        this.mapLoader = mapLoader;

        PlayerSlotPresenter humanPlayerSlot = getPlayerSlotPresenters().get(0);
        humanPlayerSlot.setPossiblePlayerTypes(new PlayerType[] { new PlayerType(EPlayerType.HUMAN) });
        humanPlayerSlot.setPlayerType(new PlayerType(EPlayerType.HUMAN));
    }

    @Override
    public void initView() {
        super.initView();
        updateViewItems();
    }

    @Override
    public void startGame() {
        List<PlayerSlotPresenter> playerSlotPresenters = getPlayerSlotPresenters();
        PlayerSetting[] playerSettings = new PlayerSetting[playerSlotPresenters.size()];
        byte humanPlayerId = playerSlotPresenters.get(0).getPlayerId();

        // Sort players by position
        PlayerSlotPresenter[] sortedPlayers = StreamSupport.stream(playerSlotPresenters)
                .sorted((playerSlot, otherPlayerSlot) -> playerSlot.getStartPosition().asByte() - otherPlayerSlot.getStartPosition().asByte())
                .toArray(PlayerSlotPresenter[]::new);

        // Get player settings if player slot is within player count limit, otherwise use new PlayerSettings() for no player at that position
        for (int i = 0; i < sortedPlayers.length; i++) {
            PlayerSlotPresenter player = sortedPlayers[i];

            if (playerSlotPresenters.indexOf(player) < playerCount.getNumberOfPlayers()) {
                playerSettings[i] = player.getPlayerSettings();
            } else {
                playerSettings[i] = new PlayerSetting();
            }
        }

        JSettlersGame game = new JSettlersGame(mapLoader, 4711L, humanPlayerId, playerSettings);

        gameStarter.setStartingGame(game.start());
        navigator.showGame();
    }
}
