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

    public MapSetupPresenter(MapSetupView view, GameStarter gameStarter) {
        this.view = view;
        this.gameStarter = gameStarter;
        this.mapDefinition = gameStarter.getMapDefinition();
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

    public String getMapName() {
        return mapDefinition.getMapName();
    }

    public short[] getMapImage() {
        return mapDefinition.getImage();
    }

    //TODO return wrapper object with suitable toString()
    public Integer[] getAllowedPlayerCounts() {
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
    public EMapStartResources[] getStartResourcesOptions() {
        return EMapStartResources.values();
    }

    //TODO return wrapper object with suitable toString()
    public String[] getPeaceTimeOptions() {
        return new String[] { "Without" };
    }

    public abstract void startGame();




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
