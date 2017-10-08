package jsettlers.main.android.mainmenu.viewmodels.setup;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import java8.util.J8Arrays;
import jsettlers.common.ai.EPlayerType;
import jsettlers.common.player.ECivilisation;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.logic.map.loading.MapLoader;
import jsettlers.logic.player.PlayerSetting;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.presenters.setup.Peacetime;
import jsettlers.main.android.mainmenu.presenters.setup.PlayerCount;
import jsettlers.main.android.mainmenu.presenters.setup.StartResources;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Civilisation;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerType;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PositionChangedListener;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartPosition;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.Team;
import jsettlers.main.android.utils.SingleLiveEvent;

import static java8.util.stream.StreamSupport.stream;

/**
 * Created by Tom Pratt on 07/10/2017.
 */

public abstract class MapSetupViewModel extends ViewModel implements PositionChangedListener {

    private final GameStarter gameStarter;
    private final MapLoader mapLoader;

    protected final List<PlayerSlotPresenter> playerSlotPresenters;

    private final MutableLiveData<PlayerCount[]> playerCountOptions = new MutableLiveData<>();
    private final MutableLiveData<PlayerCount> playerCount = new MutableLiveData<>();
    private final MutableLiveData<StartResources[]> startResourcesOptions = new MutableLiveData<>();
    private final MutableLiveData<StartResources> startResources = new MutableLiveData<>();
    private final MutableLiveData<Peacetime[]> peaceTimeOptions = new MutableLiveData<>();
    private final MutableLiveData<Peacetime> peaceTime = new MutableLiveData<>();
    private final MutableLiveData<short[]> image = new MutableLiveData<>();
    private final MutableLiveData<String> title = new MutableLiveData<>();
    protected final SingleLiveEvent<Void> showMapEvent = new SingleLiveEvent<>();
    protected final MediatorLiveData<PlayerSlotPresenter[]> playerSlots = new MediatorLiveData<>();



    public MapSetupViewModel(GameStarter gameStarter, MapLoader mapLoader) {
        this.gameStarter = gameStarter;
        this.mapLoader = mapLoader;

        playerSlotPresenters = createComputerPlayerSlots();

        playerCountOptions.setValue(playerCountOptions());
        playerCount.setValue(new PlayerCount(mapLoader.getMaxPlayers()));
        startResourcesOptions.setValue(startResourcesOptions());
        startResources.setValue(new StartResources(EMapStartResources.MEDIUM_GOODS));
        peaceTimeOptions.setValue(peaceTimeOptions());
        peaceTime.setValue(peaceTimeOptions.getValue()[0]);
        image.setValue(mapLoader.getImage());
        title.setValue(mapLoader.getMapName());

        playerSlots.addSource(playerCount, playerCount -> {
            playerSlots.setValue(stream(playerSlotPresenters)
                    .limit(playerCount.getNumberOfPlayers())
                    .toArray(PlayerSlotPresenter[]::new));
        });

//        playerSlots = Transformations.map(playerCount, playerCount -> stream(playerSlotPresenters)
//                .limit(playerCount.getNumberOfPlayers())
//                .toArray(PlayerSlotPresenter[]::new));
    }

    public LiveData<PlayerCount[]> getPlayerCountOptions() {
        return playerCountOptions;
    }

    public LiveData<PlayerCount> getPlayerCount() {
        return playerCount;
    }

    public MutableLiveData<StartResources[]> getStartResourcesOptions() {
        return startResourcesOptions;
    }

    public MutableLiveData<StartResources> getStartResources() {
        return startResources;
    }

    public MutableLiveData<Peacetime[]> getPeaceTimeOptions() {
        return peaceTimeOptions;
    }

    public MutableLiveData<Peacetime> getPeaceTime() {
        return peaceTime;
    }

    public MutableLiveData<short[]> getImage() {
        return image;
    }

    public MutableLiveData<String> getTitle() {
        return title;
    }

    public LiveData<PlayerSlotPresenter[]> getPlayerSlots() {
        return playerSlots;
    }

    public LiveData<Void> getShowMapEvent() {
        return showMapEvent;
    }

    public void playerCountSelected(PlayerCount item) {
        playerCount.setValue(item);
    //    updateViewItems();            Also update player slots via Transformation.
    }

    public void startResourcesSelected(StartResources item) {
        startResources.setValue(item);
    }

    public void peaceTimeSelected(Peacetime item) {
        peaceTime.setValue(item);
    }

    public void startGame() {
        // make abstract
    }


    @Override
    protected void onCleared() {
        super.onCleared();

        if (gameStarter.getStartingGame() == null) {
            abort();
        }
    }

    protected void abort() {

    }

    private List<PlayerSlotPresenter> createComputerPlayerSlots() {
        List<PlayerSlotPresenter> playerSlotPresenters = new ArrayList<>();
        PlayerSetting[] playerSettings = mapLoader.getFileHeader().getPlayerSettings();
        int playerCountValue = mapLoader.getMaxPlayers();

        for (byte i = 0; i < playerCountValue; i++) {
            PlayerSlotPresenter playerSlotPresenter = new PlayerSlotPresenter(this);
            PlayerSetting playerSetting = playerSettings[i];

            playerSlotPresenter.setName("Computer " + i);
            playerSlotPresenter.setShowReadyControl(false);

            setComputerSlotPlayerTypes(playerSlotPresenter);
            setSlotCivilisations(playerSlotPresenter, playerSetting);
            setSlotStartPositions(playerSlotPresenter, playerCountValue, i);
            setSlotTeams(playerSlotPresenter, playerSetting, playerCountValue, i);

            playerSlotPresenters.add(playerSlotPresenter);
        }

        return playerSlotPresenters;
    }

    protected static void setComputerSlotPlayerTypes(PlayerSlotPresenter playerSlotPresenter) {
        playerSlotPresenter.setPossiblePlayerTypes(new PlayerType[] {
                new PlayerType(EPlayerType.AI_VERY_HARD),
                new PlayerType(EPlayerType.AI_HARD),
                new PlayerType(EPlayerType.AI_EASY),
                new PlayerType(EPlayerType.AI_VERY_EASY)
        });
        playerSlotPresenter.setPlayerType(new PlayerType(EPlayerType.AI_VERY_HARD));
    }

    protected static void setHumanSlotPlayerTypes(PlayerSlotPresenter playerSlotPresenter) {
        playerSlotPresenter.setPossiblePlayerTypes(new PlayerType[] {
                new PlayerType(EPlayerType.HUMAN)
        });
        playerSlotPresenter.setPlayerType(new PlayerType(EPlayerType.HUMAN));
    }

    private static void setSlotCivilisations(PlayerSlotPresenter playerSlotPresenter, PlayerSetting playerSetting) {
        playerSlotPresenter.setPossibleCivilisations(new Civilisation[] { new Civilisation(ECivilisation.ROMAN) });

        if (playerSetting.getCivilisation() != null) {
            playerSlotPresenter.setCivilisation(new Civilisation(playerSetting.getCivilisation()));
        } else {
            playerSlotPresenter.setCivilisation(new Civilisation(ECivilisation.ROMAN));
        }
    }

    private static void setSlotStartPositions(PlayerSlotPresenter playerSlotPresenter, int numberOfPlayers, byte orderNumber) {
        playerSlotPresenter.setPossibleStartPositions(numberOfPlayers);
        playerSlotPresenter.setStartPosition(new StartPosition(orderNumber));
    }

    private static void setSlotTeams(PlayerSlotPresenter playerSlotPresenter, PlayerSetting playerSetting, int numberOfPlayers, byte orderNumber) {
        playerSlotPresenter.setPossibleTeams(numberOfPlayers);
        if (playerSetting.getTeamId() != null) {
            playerSlotPresenter.setTeam(new Team(playerSetting.getTeamId()));
        } else {
            playerSlotPresenter.setTeam(new Team(orderNumber));
        }
    }

    /**
     * PositionChangedListener implementation
     */
    @Override
    public void positionChanged(PlayerSlotPresenter updatedPlayerSlotPresenter, StartPosition oldPosition, StartPosition newPosition) {
        for (PlayerSlotPresenter playerSlotPresenter : playerSlotPresenters) {
            if (playerSlotPresenter != updatedPlayerSlotPresenter && playerSlotPresenter.getStartPosition().equals(newPosition)) {
                playerSlotPresenter.setStartPosition(oldPosition);
            }
        }
    }

    /**
     * Get items for the main map options
     */
    private PlayerCount[] playerCountOptions() {
        int maxPlayers = mapLoader.getMaxPlayers();
        int minPlayers = mapLoader.getMinPlayers();
        int numberOfOptions = maxPlayers - minPlayers + 1;

        PlayerCount[] allowedPlayerCounts = new PlayerCount[numberOfOptions];

        for (int i = 0; i < numberOfOptions; i++) {
            allowedPlayerCounts[i] = new PlayerCount(minPlayers + i);
        }

        return allowedPlayerCounts;
    }

    private StartResources[] startResourcesOptions() {
        return J8Arrays.stream(EMapStartResources.values())
                .map(StartResources::new)
                .toArray(StartResources[]::new);
    }

    private Peacetime[] peaceTimeOptions() {
        return new Peacetime[] { new Peacetime("Without") };
    }
}
