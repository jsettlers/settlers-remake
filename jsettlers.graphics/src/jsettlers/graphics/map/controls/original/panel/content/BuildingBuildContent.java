/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map.controls.original.panel.content;

import java.util.ArrayList;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.ui.UIPanel;

public class BuildingBuildContent extends AbstractContentProvider {
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
	public static final EBuildingType[] militaryBuildings = new EBuildingType[] {
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
			EBuildingType.TEMPLE
	};

	private static final int ROWS = 6;
	private static final int COLUMNS = 2;

	private final UIPanel panel;

	private final ArrayList<BuildingButton> buttons =
			new ArrayList<BuildingButton>();
	private EBuildingType activeBuilding;

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
		activeBuilding = null;
		for (BuildingButton button : buttons) {
			boolean isActive = button.getBuildingType() == type;
			button.setActive(isActive);
			if (isActive) {
				activeBuilding = type;
			}
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
	public Action catchAction(Action action) {
		if (action.getActionType() == EActionType.SHOW_CONSTRUCTION_MARK) {
			setActiveBuilding(((ShowConstructionMarksAction) action).getBuildingType());
		}
		return super.catchAction(action);
	}

	@Override
	public PointAction getSelectAction(ShortPoint2D position) {
		if (activeBuilding != null) {
			return new BuildAction(activeBuilding, position);
		} else {
			return null;
		}
	}

	@Override
	public void contentHiding(ActionFireable actionFireable) {
		if (activeBuilding != null) {
			actionFireable.fireAction(new ShowConstructionMarksAction(null));
		}
	}
}
