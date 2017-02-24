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
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Civilisation;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerType;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartPosition;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Team;
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
            setSlotStartPositions(playerSlotPresenter, numberOfPlayers, i);
            setSlotTeams(playerSlotPresenter, playerSetting, numberOfPlayers, i);
        }

        view.setItems(playerSlotPresenters);
    }

    @Override
    public void startGame() {
        List<PlayerSlotPresenter> playerSlotPresenters = getPlayerSlotPresenters();

        byte humanPlayerId = playerSlotPresenters.get(0).getPlayerId();

        PlayerSetting[] playerSettings = StreamSupport.stream(playerSlotPresenters)
                .sorted((playerSlot, otherPlayerSlot) -> playerSlot.getStartPosition().asByte() - otherPlayerSlot.getStartPosition().asByte())
                .map(PlayerSlotPresenter::getPlayerSettings)
                .toArray(PlayerSetting[]::new);

        JSettlersGame game = new JSettlersGame(mapLoader, 4711L, humanPlayerId, playerSettings);

        gameStarter.setStartingGame(game.start());
        navigator.showGame();
    }


    private void setSlotPlayerTypes(PlayerSlotPresenter playerSlotPresenter, boolean firstInList) {
        if (firstInList) {
            playerSlotPresenter.setPossiblePlayerTypes(new PlayerType[] {
                    new PlayerType(EPlayerType.HUMAN)
            });
            playerSlotPresenter.setPlayerType(new PlayerType(EPlayerType.HUMAN));
        } else {
            playerSlotPresenter.setPossiblePlayerTypes(new PlayerType[] {
                    new PlayerType(EPlayerType.AI_VERY_HARD),
                    new PlayerType(EPlayerType.AI_HARD),
                    new PlayerType(EPlayerType.AI_EASY),
                    new PlayerType(EPlayerType.AI_VERY_EASY)
            });
            playerSlotPresenter.setPlayerType(new PlayerType(EPlayerType.AI_VERY_HARD));
        }
    }

    private void setSlotCivilisations(PlayerSlotPresenter playerSlotPresenter, PlayerSetting playerSetting) {
        playerSlotPresenter.setPossibleCivilisations(new Civilisation[] { new Civilisation(ECivilisation.ROMAN) });

        if (playerSetting.getCivilisation() != null) {
            playerSlotPresenter.setCivilisation(new Civilisation(playerSetting.getCivilisation()));
        }
    }

    private void setSlotStartPositions(PlayerSlotPresenter playerSlotPresenter, int numberOfPlayers, byte slotNumber) {
        playerSlotPresenter.setPossibleStartPositions(numberOfPlayers);
        playerSlotPresenter.setStartPosition(new StartPosition(slotNumber));
    }

    private void setSlotTeams(PlayerSlotPresenter playerSlotPresenter, PlayerSetting playerSetting, int numberOfPlayers, byte slotNumber) {
        playerSlotPresenter.setPossibleTeams(numberOfPlayers);
        if (playerSetting.getTeamId() != null) {
            playerSlotPresenter.setTeam(new Team(playerSetting.getTeamId()));
        } else {
            playerSlotPresenter.setTeam(new Team(slotNumber));
        }
    }
}
