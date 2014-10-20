package jsettlers.graphics.map.controls.original.panel.content;

import java.util.ArrayList;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.utils.UIPanel;

public class BuildingBuildContent implements ContentFactory {
	public static final EBuildingType[] normalBuildings = new EBuildingType[] {
	        EBuildingType.LUMBERJACK,
	        EBuildingType.SAWMILL,
	        EBuildingType.STONECUTTER,
	        EBuildingType.FORESTER,
	        EBuildingType.IRONMINE,
	        EBuildingType.IRONMELT,
	        EBuildingType.GOLDMINE,
	        EBuildingType.GOLDMELT,
	        EBuildingType.COALMINE,
	        EBuildingType.TOOLSMITH,
	        EBuildingType.CHARCOAL_BURNER
	};
	public static final EBuildingType[] foodBuildings = new EBuildingType[] {
	        EBuildingType.FISHER,
	        EBuildingType.FARM,
	        EBuildingType.PIG_FARM,
	        EBuildingType.MILL,
	        EBuildingType.SLAUGHTERHOUSE,
	        EBuildingType.BAKER,
	        EBuildingType.WATERWORKS,
	        EBuildingType.DONKEY_FARM,
	        EBuildingType.WINEGROWER
	};
	public static final EBuildingType[] militaryBuildings =
	        new EBuildingType[] {
	                EBuildingType.TOWER,
	                EBuildingType.BIG_TOWER,
	                EBuildingType.CASTLE,
	                EBuildingType.LOOKOUT_TOWER,
	                EBuildingType.WEAPONSMITH,
	                EBuildingType.BARRACK,
	                EBuildingType.DOCKYARD,
	                EBuildingType.HOSPITAL
	        };
	public static final EBuildingType[] socialBuildings = new EBuildingType[] {
	        EBuildingType.SMALL_LIVINGHOUSE,
	        EBuildingType.MEDIUM_LIVINGHOUSE,
	        EBuildingType.BIG_LIVINGHOUSE,
	        EBuildingType.STOCK,
	};

	private static final int ROWS = 6;
	private static final int COLUMNS = 2;

	private final UIPanel panel;

	private final ArrayList<BuildingButton> buttons =
	        new ArrayList<BuildingButton>();

	private BuildingBuildContent(EBuildingType[] buildings) {
		panel = new UIPanel();

		float colWidth = 1f / COLUMNS;
		float rowHeight = 1f / ROWS;

		for (int i = 0; i < buildings.length; i++) {
			BuildingButton button = new BuildingButton(buildings[i]);
			int row = i / COLUMNS;
			int col = i % COLUMNS;
			panel.addChild(button, col * colWidth, 1 - (row + 1) * rowHeight,
			        (col + 1) * colWidth, 1 - row * rowHeight);
			buttons.add(button);
		}
	}

	/**
	 * Sets the active building the user wants to build.
	 * 
	 * @param type
	 *            The type. May be <code>null</code>
	 */
	private void setActiveBuilding(EBuildingType type) {
		for (BuildingButton button : buttons) {
			button.setActive(button.getBuildingType() == type);
		}
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

	public static BuildingBuildContent getNormal() {
		return new BuildingBuildContent(normalBuildings);
	}

	public static BuildingBuildContent getFood() {
		return new BuildingBuildContent(foodBuildings);
	}

	public static BuildingBuildContent getMilitary() {
		return new BuildingBuildContent(militaryBuildings);
	}

	public static BuildingBuildContent getSocial() {
		return new BuildingBuildContent(socialBuildings);
	}
	
	@Override
	public void displayBuildingBuild(EBuildingType type) {
		setActiveBuilding(type);
	}

	@Override
    public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {	    
    }
}
