package jsettlers.main.android.gameplay.presenters;

import java.util.Arrays;
import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.map.controls.original.panel.content.BuildingBuildContent;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.ui.views.BuildingsCategoryView;

/**
 * Created by tompr on 22/11/2016.
 */

public class BuildingsCategoryMenu {
	public static final int BUILDINGS_CATEGORY_NORMAL = 10;
	public static final int BUILDINGS_CATEGORY_FOOD = 20;
	public static final int BUILDINGS_CATEGORY_MILITARY = 30;
	public static final int BUILDINGS_CATEGORY_SOCIAL = 40;

	private final BuildingsCategoryView view;
	private final ActionFireable actionFireable;
	private final MenuNavigator menuNavigator;
	private final int buildingsCategory;

	public BuildingsCategoryMenu(
			BuildingsCategoryView view,
			ActionFireable actionFireable,
			MenuNavigator menuNavigator,
			int buildingsCategory) {

		this.view = view;
		this.actionFireable = actionFireable;
		this.menuNavigator = menuNavigator;
		this.buildingsCategory = buildingsCategory;
	}

	public void start() {
		view.setBuildings(getBuildingTypes());
	}

	public void buildingSelected(EBuildingType buildingType) {
		Action action = new ShowConstructionMarksAction(buildingType);
		actionFireable.fireAction(action);
		menuNavigator.dismissMenu();
	}

	private List<EBuildingType> getBuildingTypes() {
		EBuildingType[] buildingTypes;

		switch (buildingsCategory) {
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
			throw new RuntimeException("No such buildings category exists " + buildingsCategory);
		}

		return Arrays.asList(buildingTypes);
	}
}
