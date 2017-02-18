package jsettlers.main.android.mainmenu.presenters.setup;

import java.util.ArrayList;
import java.util.List;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerItemPresenter;
import jsettlers.main.android.mainmenu.views.MapSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public abstract class MapSetupPresenterImpl implements MapSetupPresenter {
    private final MapSetupView view;
    private final GameStarter gameStarter;
    private final IMapDefinition mapDefinition;

    private final List<PlayerItemPresenter> playerItemPresenters = new ArrayList<>();

    public MapSetupPresenterImpl(MapSetupView view, GameStarter gameStarter, IMapDefinition mapDefinition) {
        this.view = view;
        this.gameStarter = gameStarter;
        this.mapDefinition = mapDefinition;
    }

    @Override
    public void initView() {
        view.setNumberOfPlayersOptions(allowedPlayerCounts());
        view.setStartResourcesOptions(startResourcesOptions());
        view.setPeaceTimeOptions(peaceTimeOptions());
        view.setMapImage(mapDefinition.getImage());

        for (int i = 0; i < mapDefinition.getMaxPlayers(); i++) {
            playerItemPresenters.add(new PlayerItemPresenter());
        }

        view.setItems(playerItemPresenters);
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


    //TODO return wrapper object with suitable toString()
    private Integer[] allowedPlayerCounts() {
        int maxPlayers = mapDefinition.getMaxPlayers();
        int minPlayers = mapDefinition.getMinPlayers();
        int numberOfOptions = maxPlayers - minPlayers + 1;

        Integer[] allowedPlayerCounts = new Integer[numberOfOptions];

        for (int i = 0; i < numberOfOptions; i++) {
            allowedPlayerCounts[i] = minPlayers + i;
        }

        return allowedPlayerCounts;
    }

    //TODO return wrapper object with suitable toString()
    private EMapStartResources[] startResourcesOptions() {
        return EMapStartResources.values();
    }

    //TODO return wrapper object with suitable toString()
    private String[] peaceTimeOptions() {
        return new String[] { "Without" };
    }
}
