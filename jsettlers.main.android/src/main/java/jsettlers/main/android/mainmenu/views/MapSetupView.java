package jsettlers.main.android.mainmenu.views;

import java.util.List;

import jsettlers.logic.map.loading.EMapStartResources;
import jsettlers.main.android.mainmenu.presenters.setup.playeritem.PlayerItemPresenter;

/**
 * Created by tompr on 22/01/2017.
 */

public interface MapSetupView {
    void setNumberOfPlayersOptions(Integer[] numberOfPlayersOptions);
    void setStartResourcesOptions(EMapStartResources[] startResourcesOptions);
    void setPeaceTimeOptions(String[] peaceTimeOptions);
    void setMapName(String mapName);
    void setMapImage(short[] image);
    void setItems(List<PlayerItemPresenter> items);
}
