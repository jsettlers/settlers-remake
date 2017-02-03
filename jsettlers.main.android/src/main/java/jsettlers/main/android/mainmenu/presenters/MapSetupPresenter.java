package jsettlers.main.android.mainmenu.presenters;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.main.android.core.GameStarter;
import jsettlers.main.android.mainmenu.views.MapSetupView;

/**
 * Created by tompr on 21/01/2017.
 */

public abstract class MapSetupPresenter {
    private final MapSetupView view;
    private final GameStarter gameStarter;
    private final IMapDefinition mapDefinition;

    public MapSetupPresenter(MapSetupView view, GameStarter gameStarter, IMapDefinition mapDefinition) {
        this.view = view;
        this.gameStarter = gameStarter;
        this.mapDefinition = mapDefinition;
    }

    public void initView() {
        view.setNumberOfPlayersOptions(allowedPlayerCounts());
        view.setStartResourcesOptions(startResourcesOptions());
        view.setPeaceTimeOptions(peaceTimeOptions());
        view.setMapImage(mapDefinition.getImage());
    }

    public void updateViewTitle() {
        view.setMapName(mapDefinition.getMapName());
    }

    public void viewFinished() {
        if (gameStarter.getStartingGame() == null) {
            abort();
        }
    }

    public void dispose() {
    }

    protected void abort() {
        gameStarter.setMapDefinition(null);
    }

    public abstract void startGame();



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




    /**
     * protected getters
     */
    protected GameStarter getGameStarter() {
        return gameStarter;
    }

    protected IMapDefinition getMapDefinition() {
        return mapDefinition;
    }
}
