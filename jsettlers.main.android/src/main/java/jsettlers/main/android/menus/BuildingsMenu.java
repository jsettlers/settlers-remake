package jsettlers.main.android.menus;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.map.MapContent;

/**
 * Created by tompr on 22/11/2016.
 */

public class BuildingsMenu {
    private MapContent mapContent;

    public BuildingsMenu(MapContent mapContent) {
        this.mapContent = mapContent;
    }

    public void showConstructionMarkers(EBuildingType buildingType) {
        Action action = new ShowConstructionMarksAction(buildingType);
        mapContent.fireAction(action);
    }
}
