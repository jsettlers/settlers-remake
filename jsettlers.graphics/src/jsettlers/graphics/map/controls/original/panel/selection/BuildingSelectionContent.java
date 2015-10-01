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
package jsettlers.graphics.map.controls.original.panel.selection;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;

import java.util.List;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.SetBuildingPriorityAction;
import jsettlers.graphics.action.SoldierAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState.OccupierState;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState.StackState;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.UIElement;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.BuildingSelectionLayout;
import jsettlers.graphics.ui.layout.OccupiableSelectionLayout;

public class BuildingSelectionContent extends AbstractSelectionContent {
	private static final OriginalImageLink SOILDER_MISSING = new OriginalImageLink(
			EImageLinkType.GUI, 3, 45, 0);
	private static final OriginalImageLink SOILDER_COMMING = new OriginalImageLink(
			EImageLinkType.GUI, 3, 48, 0);

	public static class SoldierButton extends Button {

		public SoldierButton(EActionType actionType, ESoldierType type, ImageLink image) {
			super(new SoldierAction(actionType, type), image, image, Labels.getString("action_" + actionType + "_" + type));
		}
	}

	public static class SoldierCount extends Label {

		public SoldierCount(ESoldierType type) {
			super("?", EFontSize.NORMAL);
		}
	}

	private final IBuilding building;
	private final UIPanel rootPanel = new ContentRefreshingPanel();

	private BuildingState lastState = null;

	public BuildingSelectionContent(ISelectionSet selection) {
		building = (IBuilding) selection.get(0);

		updatePanelContent();
	}

	private void updatePanelContent() {
		lastState = new BuildingState(building);
		addPanelContent(lastState);
	}

	private void addPanelContent(BuildingState state) {
		rootPanel.removeAll();
		BuidlingBackgroundPanel root;
		if (!state.isConstruction() && building instanceof IBuilding.IOccupyed) {
			root = creteOccupiedBuildingContent(state);
		} else {
			root = createNormalBuildingContent(state);
		}
		ImageLink[] images = building.getBuildingType().getImages();
		root.setImages(images);
		rootPanel.addChild(root, 0, 0, 1, 1);
	}

	private BuidlingBackgroundPanel createNormalBuildingContent(BuildingState state) {
		BuildingSelectionLayout layout = new BuildingSelectionLayout();

		EPriority[] supported = state.getSupportedPriorities();
		if (supported.length < 2) {
			layout.background.removeChild(layout.priority);
		} else {
			layout.priority.setPriority(supported, building.getPriority());
		}

		if (building.getBuildingType().getWorkradius() <= 0) {
			layout.background.removeChild(layout.workRadius);
		}

		layout.nameText.setType(building.getBuildingType(), state.isConstruction());

		String text = "";
		if (building.getStateProgress() < 1) {
			text = Labels.getString("materials_required");
		} else if (building instanceof IBuilding.IResourceBuilding) {
			IBuilding.IResourceBuilding resourceBuilding = (IBuilding.IResourceBuilding) building;
			text = Labels.getString("productivity", (int) (resourceBuilding.getProductivity() * 100));
		}
		layout.materialText.setText(text);

		addRequestAndOfferStacks(layout.materialArea, state);

		BuidlingBackgroundPanel root = layout._root;
		return root;
	}

	private void addRequestAndOfferStacks(UIPanel materialArea, BuildingState state) {
		// hardcoded...
		float buttonWidth = 18f / (127 - 9);
		float buttonSpace = 12f / (127 - 9);
		float requestOfferSpace = 18f / (127 - 9);

		float requestX = buttonSpace;
		float offerX = 1 - buttonSpace - buttonWidth;

		for (StackState mat : state.getStackStates()) {
			MaterialDisplay display = new MaterialDisplay(mat.type, mat.count, -1);
			if (mat.offering) {
				materialArea.addChild(display, offerX, 0, offerX + buttonWidth, 1);
				offerX -= buttonSpace + buttonWidth;
			} else {
				materialArea.addChild(display, requestX, 0, requestX + buttonWidth, 1);
				requestX += buttonSpace + buttonWidth;
			}
		}
	}

	/**
	 * A button with a number of materials below it.
	 * 
	 * @author Michael Zangl
	 */
	public static class MaterialDisplay extends UIPanel {
		private static final float BUTTON_BOTTOM = 1 - 18f / 29;

		public MaterialDisplay(EMaterialType type, int amount, int required) {
			String label = required < 0 ? "building-material-count" : "building-material-required";
			String text = Labels.getString(label, amount, required);
			// TODO: use Labels.getName(type) ?
			addChild(new Button(null, type.getIcon(), type.getIcon(), ""), 0, BUTTON_BOTTOM, 1, 1);
			addChild(new Label(text, EFontSize.NORMAL), 0, 0, 1, BUTTON_BOTTOM);
		}
	}

	/**
	 * A panel that displays the name of the building.
	 * 
	 * @author Michael Zangl
	 */
	public static class NamePanel extends Label {
		public NamePanel() {
			super("", EFontSize.HEADLINE);
		}

		public void setType(EBuildingType type, boolean workplace) {
			String text = Labels.getName(type);
			if (workplace) {
				text = Labels.getString("building-build-in-progress", text);
			}
			setText(text);
		}
	}

	/**
	 * A button to change the priority of the current building.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public static class PriorityButton extends Button {
		private ImageLink stopped;
		private ImageLink low;
		private ImageLink high;
		private EPriority next = EPriority.DEFAULT;
		private EPriority current = EPriority.DEFAULT;

		public PriorityButton(ImageLink stopped, ImageLink low, ImageLink high) {
			super(null, null, null, null);
			this.stopped = stopped;
			this.low = low;
			this.high = high;
		}

		public void setPriority(EPriority[] supported, EPriority current) {
			this.current = current;
			next = supported[0];
			for (int i = 0; i < supported.length; i++) {
				if (supported[i] == current) {
					next = supported[(i + 1) % supported.length];
				}
			}
		}

		@Override
		protected ImageLink getBackgroundImage() {
			switch (current) {
			case HIGH:
				return high;
			case LOW:
				return low;
			case STOPPED:
			default:
				return stopped;
			}
		}

		@Override
		public Action getAction() {
			return new SetBuildingPriorityAction(next);
		}

		@Override
		public String getDescription(float relativex, float relativey) {
			return Labels.getString("priority_" + next);
		}

	}

	private BuidlingBackgroundPanel creteOccupiedBuildingContent(BuildingState state) {
		OccupiableSelectionLayout layout = new OccupiableSelectionLayout();
		layout.nameText.setType(building.getBuildingType(), false);
		addOccupyerPlaces(layout.infantry_places, layout.infantry_missing, state.getOccupiers(ESoldierClass.INFANTRY));
		addOccupyerPlaces(layout.bowman_places, layout.bowman_missing, state.getOccupiers(ESoldierClass.BOWMAN));
		return layout._root;
	}

	private void addOccupyerPlaces(UIPanel places, UIPanel missing, List<OccupierState> occupiers) {
		List<UIElement> buttonPlaces = places.getChildren();
		List<UIElement> missingPlaces = missing.getChildren();
		for (int i = 0; i < Math.min(buttonPlaces.size(), missingPlaces.size()); i++) {
			UIPanel icon = (UIPanel) buttonPlaces.get(i);
			UIPanel missingIcon = (UIPanel) missingPlaces.get(i);
			icon.setBackground(null);
			missingIcon.setBackground(null);
			if (i < occupiers.size()) {
				OccupierState state = occupiers.get(i);
				if (state.isComming()) {
					missingIcon.setBackground(SOILDER_COMMING);
				} else if (state.isMissing()) {
					missingIcon.setBackground(SOILDER_MISSING);
				} else {
					icon.setBackground(getIconFor(state.getMovable()));
				}
			}
		}
	}

	private static OriginalImageLink getIconFor(IMovable movable) {
		switch (movable.getMovableType()) {
		case SWORDSMAN_L1:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 213, 0);
		case SWORDSMAN_L2:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 222, 0);
		case SWORDSMAN_L3:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 231, 0);
		case PIKEMAN_L1:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 216, 0);
		case PIKEMAN_L2:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 225, 0);
		case PIKEMAN_L3:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 234, 0);
		case BOWMAN_L1:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 219, 0);
		case BOWMAN_L2:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 228, 0);
		case BOWMAN_L3:
			return new OriginalImageLink(EImageLinkType.GUI, 14, 237, 0);

		default:
			System.err.println("A unknown image was requested for gui. "
					+ "Type=" + movable.getMovableType());
			return new OriginalImageLink(EImageLinkType.GUI, 24, 213, 0);
		}
	}

	public static class BuidlingBackgroundPanel extends UIPanel {
		private ImageLink[] links = new ImageLink[0];

		public void setImages(ImageLink[] links) {
			this.links = links;
		}

		@Override
		public void drawAt(GLDrawContext gl) {
			super.drawAt(gl);
		}

		@Override
		protected void drawBackground(GLDrawContext gl) {
			float cx = getPosition().getCenterX();
			float cy = getPosition().getCenterY();

			for (ImageLink link : links) {
				ImageProvider.getInstance().getImage(link).drawAt(gl, cx, cy);
			}
		}

	}

	private class ContentRefreshingPanel extends UIPanel {
		@Override
		public void drawAt(GLDrawContext gl) {
			// replaces our children.
			refreshContentIfNeeded();
			super.drawAt(gl);
		}
	}

	@Override
	public UIPanel getPanel() {
		return rootPanel;
	}

	public void refreshContentIfNeeded() {
		if (!lastState.isStillInState(building)) {
			rootPanel.removeAll();
			updatePanelContent();
		}
	}
}
