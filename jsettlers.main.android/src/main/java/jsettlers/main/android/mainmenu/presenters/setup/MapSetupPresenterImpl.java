package jsettlers.main.android.mainmenu.presenters.setup;

import java.util.ArrayList;
import java.util.List;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerCount;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PositionChangedListener;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartPosition;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartResources;
import jsettlers.main.android.mainmenu.views.MapSetupView;

import java8.util.J8Arrays;

/**
 * Created by tompr on 21/01/2017.
 */

public abstract class MapSetupPresenterImpl implements MapSetupPresenter, PositionChangedListener {
    private final MapSetupView view;
    private final GameStarter gameStarter;
    private final IMapDefinition mapDefinition;

    private final List<PlayerSlotPresenter> playerSlotPresenters = new ArrayList<>();

    protected PlayerCount playerCount;
    protected StartResources startResources;

    public MapSetupPresenterImpl(MapSetupView view, GameStarter gameStarter, IMapDefinition mapDefinition) {
        this.view = view;
        this.gameStarter = gameStarter;
        this.mapDefinition = mapDefinition;

        createPlayerSlots();
        playerCount = new PlayerCount(playerSlotPresenters.size());
    }

    @Override
    public void initView() {
        view.setNumberOfPlayersOptions(allowedPlayerCounts());
        view.setPlayerCount(playerCount);

        view.setStartResourcesOptions(startResourcesOptions());
        view.setStartResources(new StartResources(EMapStartResources.MEDIUM_GOODS));

        view.setPeaceTimeOptions(peaceTimeOptions());
        view.setMapImage(mapDefinition.getImage());
    }

    @Override
    public void updateViewTitle() {
        view.setMapName(mapDefinition.getMapName());
    }

    @Override
    public void viewFinished() {
        if (gameStarter.getStartingGame() == null) {
            abort();
        }
    }

    @Override
    public void dispose() {
    }

    protected void abort() {
    }


    @Override
    public void playerCountSelected(PlayerCount item) {
        playerCount = item;
        updateViewItems();
    }

    @Override
    public void startResourcesSelected(StartResources item) {
        startResources = item;
    //    view.setStartResources(item);
    }

    protected void updateViewItems() {
        view.setItems(getPlayerSlotPresenters(), playerCount.getNumberOfPlayers());
    }

    private PlayerCount[] allowedPlayerCounts() {
        int maxPlayers = mapDefinition.getMaxPlayers();
        int minPlayers = mapDefinition.getMinPlayers();
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

    //TODO return wrapper object with suitable toString()
    private String[] peaceTimeOptions() {
        return new String[] { "Without" };
    }

    private void createPlayerSlots() {
        for (int i = 0; i < mapDefinition.getMaxPlayers(); i++) {
            playerSlotPresenters.add(new PlayerSlotPresenter(this));
        }
    }

    protected List<PlayerSlotPresenter> getPlayerSlotPresenters() {
        return playerSlotPresenters;
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
}
