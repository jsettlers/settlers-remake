/*******************************************************************************
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
 *******************************************************************************/
package jsettlers.graphics.map.controls.original.panel.content;

import java.util.ArrayList;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IBuildingCounts;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.BuildAction;
import jsettlers.graphics.action.PointAction;
import jsettlers.graphics.action.ShowConstructionMarksAction;
import jsettlers.graphics.map.controls.original.panel.content.BuildingBuildContent.BuildingCountState;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.utils.UIUpdater;
import jsettlers.graphics.utils.UIUpdater.IDataProvider;

public class BuildingBuildContent extends AbstractContentProvider implements IDataProvider<BuildingCountState> {

	public static class BuildingCountState {
		private final IGraphicsGrid grid;
		private final ShortPoint2D pos;

		public BuildingCountState(ShortPoint2D pos, IGraphicsGrid grid) {
			this.pos = pos;
			this.grid = grid;
		}

		public int getCount(EBuildingType buildingType, boolean construction) {
			if (grid == null) {
				return 0;
			} else {
				IBuildingCounts counts = grid.getPartitionData(pos.x, pos.y).getBuildingCounts();
				return construction ? counts.buildingsInPartitionUnderConstruction(buildingType) : counts.buildingsInPartiton(buildingType);
			}
		}

		public boolean isInPlayerPartition() {
			// TODO: Check current player
			return grid != null && grid.getPlayerIdAt(pos.x, pos.y) >= 0;
		}
	}

	private static final int ROWS = 6;
	private static final int COLUMNS = 2;

	private final UIPanel panel;

	private final ArrayList<BuildingButton> buttons = new ArrayList<>();
	private EBuildingType activeBuilding;

	private final UIUpdater<BuildingCountState> updater;
	private BuildingCountState buildingCount = new BuildingCountState(null, null);

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
			i++;
		}
		updater = UIUpdater.getUpdater(this, buttons);
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
	public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
		buildingCount = new BuildingCountState(pos, grid);
		updater.forceUpdate();
		super.showMapPosition(pos, grid);
	}

	@Override
	public BuildingCountState getCurrentUIData() {
		return buildingCount;
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
		updater.start(true);
	}

	@Override
	public void contentHiding(ActionFireable actionFireable, AbstractContentProvider nextContent) {
		updater.stop();
		if (activeBuilding != null) {
			actionFireable.fireAction(new ShowConstructionMarksAction(null));
		}
	}
}
