package jsettlers.main.android.menus.mainmenu;

import jsettlers.common.menu.IMapDefinition;
import jsettlers.common.utils.collections.ChangingList;
import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.main.StartScreenConnector;
import jsettlers.main.android.providers.GameStarter;

/**
 * Created by tompr on 21/01/2017.
 */

public class NewSinglePlayerSetupMenu {
    private final GameStarter gameStarter;
    private final StartScreenConnector startScreenConnector;
    private final IMapDefinition mapDefinition;

    public NewSinglePlayerSetupMenu(GameStarter gameStarter, String mapId) {
        this.gameStarter = gameStarter;
        this.startScreenConnector = gameStarter.getStartScreenConnector();
        this.mapDefinition = getMap(startScreenConnector.getSingleplayerMaps(), mapId);
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

    public void startGame() {
        gameStarter.startSinglePlayerGame(mapDefinition);
    }

    private static IMapDefinition getMap(ChangingList<? extends IMapDefinition> maps, String mapId) {
        for (IMapDefinition map : maps.getItems()) {
            if (map.getMapId().equals(mapId)) {
                return map;
            }
        }
        throw new RuntimeException("Couldn't get selected map.");
    }
}
