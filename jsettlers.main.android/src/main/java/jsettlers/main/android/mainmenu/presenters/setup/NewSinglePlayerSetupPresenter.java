package jsettlers.main.android.mainmenu.presenters.setup;

import java.util.List;

import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.JSettlersGame;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.navigation.MainMenuNavigator;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.views.NewSinglePlayerSetupView;

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
    }

    @Override
    public void initView() {
        super.initView();


        List<PlayerSlotPresenter> playerSlotPresenters = getPlayerSlotPresenters();
        PlayerSetting[] playerSettings = mapLoader.getFileHeader().getPlayerSettings();
        int numberOfPlayers = playerSlotPresenters.size();

        for (byte i = 0; i < numberOfPlayers; i++) {
            PlayerSlotPresenter playerSlot = playerSlotPresenters.get(i);
            PlayerSetting playerSetting = playerSettings[i];

            // Player types
            if (i == 0) {
                playerSlot.setPossiblePlayerTypes(new EPlayerType[] {
                        EPlayerType.HUMAN,
                        EPlayerType.AI_VERY_HARD,
                        EPlayerType.AI_HARD,
                        EPlayerType.AI_EASY,
                        EPlayerType.AI_VERY_EASY
                });
                playerSlot.setPlayerType(EPlayerType.HUMAN);
            } else {
                playerSlot.setPossiblePlayerTypes(new EPlayerType[] {
                        EPlayerType.AI_VERY_HARD,
                        EPlayerType.AI_HARD,
                        EPlayerType.AI_EASY,
                        EPlayerType.AI_VERY_EASY
                });
                playerSlot.setPlayerType(EPlayerType.AI_VERY_HARD);
            }

            // Civilisations
            playerSlot.setPossibleCivilisations(new ECivilisation[] { ECivilisation.ROMAN });

            if (playerSetting.getCivilisation() != null) {
                playerSlot.setCivilisation(playerSetting.getCivilisation());
            }

            if (playerSetting.getPlayerType() != null) {
                playerSlot.setPlayerType(playerSetting.getPlayerType());
            }

            // Slots
            playerSlot.setPossibleSlots(numberOfPlayers);
            playerSlot.setSlot(i);

            // Teams
            playerSlot.setPossibleTeams(numberOfPlayers);
            if (playerSetting.getTeamId() != null) {
                playerSlot.setTeam(playerSetting.getTeamId());
            } else {
                playerSlot.setTeam(i);
            }
        }

        view.setItems(playerSlotPresenters);
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
