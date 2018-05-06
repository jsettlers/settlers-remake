/*
 * Copyright (c) 2015 - 2017
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
 */
package jsettlers.graphics.map.controls.original.panel.content.buildings;

import java.util.ArrayList;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IBuildingCounts;
import jsettlers.common.action.EActionType;
import jsettlers.common.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.common.action.BuildAction;
import jsettlers.common.action.PointAction;
import jsettlers.common.action.ShowConstructionMarksAction;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiLocationDependingContentUpdater;
import jsettlers.graphics.ui.UIPanel;

public class BuildingBuildContent extends AbstractContentProvider {

	private static final int ROWS = 6;
	private static final int COLUMNS = 2;

	private final UIPanel panel;
	private final UiLocationDependingContentUpdater<IBuildingCounts> uiContentUpdater = new UiLocationDependingContentUpdater<>((grid, position) -> grid.getPartitionData(position.x, position.y).getBuildingCounts());

	private final ArrayList<BuildingButton> buttons = new ArrayList<>();
	private EBuildingType activeBuilding;

	public BuildingBuildContent(EBuildingsCategory buildingsCategory) {
		panel = new UIPanel();

		float colWidth = 1f / COLUMNS;
		float rowHeight = 1f / ROWS;

		int i = 0;
		for (EBuildingType buildingType : buildingsCategory.buildingTypes) {
			BuildingButton button = new BuildingButton(buildingType);
			int row = i / COLUMNS;
			int col = i % COLUMNS;
			panel.addChild(button, col * colWidth, 1 - (row + 1) * rowHeight, (col + 1) * colWidth, 1 - row * rowHeight);
			buttons.add(button);
			uiContentUpdater.addListener(button);
			i++;
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
	public void showMapPosition(ShortPoint2D position, IGraphicsGrid grid) {
		uiContentUpdater.updatePosition(grid, position);
		super.showMapPosition(position, grid);
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.BUILD;
	}

	@Override
	public IAction catchAction(IAction action) {
		if ((action.getActionType() == EActionType.MOVE_TO || action.getActionType() == EActionType.ABORT) && activeBuilding != null) {
			action = new ShowConstructionMarksAction(null);
		}

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
	public void contentShowing(ActionFireable actionFireable) {
		uiContentUpdater.start();
	}

	@Override
	public void contentHiding(ActionFireable actionFireable, AbstractContentProvider nextContent) {
		uiContentUpdater.stop();
		if (activeBuilding != null) {
			actionFireable.fireAction(new ShowConstructionMarksAction(null));
		}
	}
}
