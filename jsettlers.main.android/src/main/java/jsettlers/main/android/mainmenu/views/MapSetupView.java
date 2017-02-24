package jsettlers.main.android.mainmenu.views;

import java.util.List;

import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerCount;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerSlotPresenter;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.StartResources;

/**
 * Created by tompr on 22/01/2017.
 */

public interface MapSetupView {
    void setNumberOfPlayersOptions(PlayerCount[] playerCounts);
    void setStartResourcesOptions(StartResources[] startResources);
    void setPeaceTimeOptions(String[] peaceTimeOptions);
    void setMapName(String mapName);
    void setMapImage(short[] image);
    void setItems(List<PlayerSlotPresenter> items, int playerLimit);
    void setPlayerCount(PlayerCount playerCount);
    void setStartResources(StartResources item);
}
