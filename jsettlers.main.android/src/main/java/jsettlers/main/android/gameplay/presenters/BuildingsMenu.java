package jsettlers.main.android.gameplay.presenters;

import java.util.Arrays;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.map.MapContent;
import jsettlers.graphics.map.controls.original.panel.content.BuildingBuildContent;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

/**
 * Created by tompr on 22/11/2016.
 */

public class BuildingsMenu {
    public static final int BUILDINGS_CATEGORY_NORMAL = 10;
    public static final int BUILDINGS_CATEGORY_FOOD = 20;
    public static final int BUILDINGS_CATEGORY_MILITARY = 30;
    public static final int BUILDINGS_CATEGORY_SOCIAL = 40;

    private final ActionFireable actionFireable;
    private final MenuNavigator menuNavigator;

    public BuildingsMenu(
            ActionFireable actionFireable,
            MenuNavigator menuNavigator) {

        this.actionFireable = actionFireable;
        this.menuNavigator = menuNavigator;
    }

    public void showConstructionMarkers(EBuildingType buildingType) {
        Action action = new ShowConstructionMarksAction(buildingType);
        actionFireable.fireAction(action);
        menuNavigator.dismissMenu();
    }

    public List<EBuildingType> getBuildingTypesForCategory(int category) {
        EBuildingType[] buildingTypes;

        switch (category) {
            case BUILDINGS_CATEGORY_NORMAL:
                buildingTypes = BuildingBuildContent.normalBuildings;
                break;
            case BUILDINGS_CATEGORY_FOOD:
                buildingTypes = BuildingBuildContent.foodBuildings;
                break;
            case BUILDINGS_CATEGORY_MILITARY:
                buildingTypes = BuildingBuildContent.militaryBuildings;
                break;
            case BUILDINGS_CATEGORY_SOCIAL:
                buildingTypes = BuildingBuildContent.socialBuildings;
                break;
            default:
                throw new RuntimeException("No such buildings category exists " + category);
        }

        return Arrays.asList(buildingTypes);
    }
}
