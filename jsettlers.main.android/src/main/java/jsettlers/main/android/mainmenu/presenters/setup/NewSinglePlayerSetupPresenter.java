package jsettlers.main.android.mainmenu.presenters.setup;

import java.util.List;
import java8.util.stream.StreamSupport;

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
            PlayerSlotPresenter playerSlotPresenter = playerSlotPresenters.get(i);
            PlayerSetting playerSetting = playerSettings[i];

            setSlotPlayerTypes(playerSlotPresenter, i == 0);
            setSlotCivilisations(playerSlotPresenter, playerSetting);
            setSlotPositions(playerSlotPresenter, numberOfPlayers, i);
            setSlotTeams(playerSlotPresenter, playerSetting, numberOfPlayers, i);
        }

        view.setItems(playerSlotPresenters);
    }

    @Override
    public void startGame() {
        List<PlayerSlotPresenter> playerSlotPresenters = getPlayerSlotPresenters();

        byte humanPlayerId = playerSlotPresenters.get(0).getPlayerId();

        PlayerSetting[] playerSettings = StreamSupport.stream(playerSlotPresenters)
                .sorted((playerSlot, otherPlayerSlot) -> playerSlot.getPosition() - otherPlayerSlot.getPosition())
                .map(PlayerSlotPresenter::getPlayerSettings)
                .toArray(PlayerSetting[]::new);

        JSettlersGame game = new JSettlersGame(mapLoader, 4711L, humanPlayerId, playerSettings);

        gameStarter.setStartingGame(game.start());
        navigator.showGame();
    }


    private void setSlotPlayerTypes(PlayerSlotPresenter playerSlotPresenter, boolean firstInList) {
        if (firstInList) {
            playerSlotPresenter.setPossiblePlayerTypes(new EPlayerType[] {
                    EPlayerType.HUMAN,
                    EPlayerType.AI_VERY_HARD,
                    EPlayerType.AI_HARD,
                    EPlayerType.AI_EASY,
                    EPlayerType.AI_VERY_EASY
            });
            playerSlotPresenter.setPlayerType(EPlayerType.HUMAN);
        } else {
            playerSlotPresenter.setPossiblePlayerTypes(new EPlayerType[] {
                    EPlayerType.AI_VERY_HARD,
                    EPlayerType.AI_HARD,
                    EPlayerType.AI_EASY,
                    EPlayerType.AI_VERY_EASY
            });
            playerSlotPresenter.setPlayerType(EPlayerType.AI_VERY_HARD);
        }
    }

    private void setSlotCivilisations(PlayerSlotPresenter playerSlotPresenter, PlayerSetting playerSetting) {
        playerSlotPresenter.setPossibleCivilisations(new ECivilisation[] { ECivilisation.ROMAN });

        if (playerSetting.getCivilisation() != null) {
            playerSlotPresenter.setCivilisation(playerSetting.getCivilisation());
        }

        if (playerSetting.getPlayerType() != null) {
            playerSlotPresenter.setPlayerType(playerSetting.getPlayerType());
        }
    }

    private void setSlotPositions(PlayerSlotPresenter playerSlotPresenter, int numberOfPlayers, byte position) {
        playerSlotPresenter.setPossiblePositions(numberOfPlayers);
        playerSlotPresenter.setPosition(new Integer(position + 1));
    }

    private void setSlotTeams(PlayerSlotPresenter playerSlotPresenter, PlayerSetting playerSetting, int numberOfPlayers, byte position) {
        playerSlotPresenter.setPossibleTeams(numberOfPlayers);
        if (playerSetting.getTeamId() != null) {
            playerSlotPresenter.setTeam(playerSetting.getTeamId());
        } else {
            playerSlotPresenter.setTeam(position);
        }
    }
}
