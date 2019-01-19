/*******************************************************************************
 * Copyright (c) 2015 - 2017
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.map.controls.original.panel.selection;

import java.util.List;

import go.graphics.GLDrawContext;
import go.graphics.text.EFontSize;
import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.material.EPriority;
import jsettlers.common.action.EActionType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.movable.IMovable;
import jsettlers.common.selectable.ISelectionSet;
import jsettlers.common.action.Action;
import jsettlers.graphics.action.AskSetTradingWaypointAction;
import jsettlers.common.action.ChangeTradingRequestAction;
import jsettlers.common.action.SetBuildingPriorityAction;
import jsettlers.common.action.SetTradingWaypointAction;
import jsettlers.common.action.SetTradingWaypointAction.EWaypointType;
import jsettlers.common.action.SoldierAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.button.SelectionManagedMaterialButton;
import jsettlers.graphics.map.controls.original.panel.button.SelectionManager;
import jsettlers.graphics.map.controls.original.panel.button.stock.StockControlButton;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState.OccupierState;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState.StackState;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.UIElement;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.BuildingSelectionLayout;
import jsettlers.graphics.ui.layout.DockyardSelectionLayout;
import jsettlers.graphics.ui.layout.OccupiableSelectionLayout;
import jsettlers.graphics.ui.layout.StockSelectionLayout;
import jsettlers.graphics.ui.layout.TradingSelectionLayout;

/**
 * This is the selection content that is used for displaying a selected building.
 *
 * @author Michael Zangl
 */
public class BuildingSelectionContent extends AbstractSelectionContent {
	private static final int TRADING_MULTIPLE_STEP_INCREASE = 8;
	private static final OriginalImageLink SOLDIER_MISSING = new OriginalImageLink(EImageLinkType.GUI, 3, 45, 0);
	private static final OriginalImageLink SOLDIER_COMING = new OriginalImageLink(EImageLinkType.GUI, 3, 48, 0);

	/**
	 * This defines an element that depends on the state of the building.
	 *
	 * @author Michael Zangl.
	 */
	public interface StateDependingElement {
		void setState(BuildingState state);
	}

	/**
	 * A button for a given action for a given soldier type.
	 *
	 * @author Michael Zangl
	 */
	public static class SoldierButton extends Button {

		/**
		 * Create a new soldier button.
		 *
		 * @param actionType
		 *            The action to perform on click.
		 * @param type
		 *            The soldier type.
		 * @param image
		 *            The image to use.
		 */
		public SoldierButton(EActionType actionType, ESoldierType type, ImageLink image) {
			super(new SoldierAction(actionType, type), image, image, Labels.getString("action_" + actionType + "_" + type));
		}
	}

	/**
	 * This field displays the soldier count.
	 *
	 * @author Michael Zangl
	 */
	public static class SoldierCount extends Label {

		/**
		 * Creates a new soldier count field.
		 *
		 * @param type
		 *            The type of soldiers to count.
		 */
		public SoldierCount(ESoldierType type) {
			super("?", EFontSize.NORMAL);
		}
	}

	/**
	 * This displays the number of materials traded.
	 *
	 * @author Michael Zangl
	 */
	public static class TradingMaterialCount extends Label implements StateDependingElement {

		private final EMaterialType material;

		/**
		 * Creates a new trading material count display.
		 *
		 * @param material
		 *            The material.
		 */
		public TradingMaterialCount(EMaterialType material) {
			super("", EFontSize.NORMAL);
			this.material = material;
		}

		@Override
		public void setState(BuildingState state) {
			int count = state.getTradingCount(material);
			setText(formatCount(count));
		}

		private String formatCount(int count) {
			if (count == Integer.MAX_VALUE) {
				return "\u221E";
			} else {
				return count + "";
			}
		}

	}

	/**
	 * This is the trading path selection display. It shows buttons to select the trading path.
	 *
	 * @author Michael Zangl
	 */
	private static class TradingPath extends UIPanel {
		/**
		 * Creates new trading path buttons.
		 *
		 * @param image
		 *            The image to use.
		 */
		public TradingPath(ImageLink image) {
			setBackground(image);
		}

		protected Action getActionForStep(int step) {
			EWaypointType waypoint;
			if (step <= 0) {
				waypoint = SetTradingWaypointAction.EWaypointType.WAYPOINT_1;
			} else if (step <= 1) {
				waypoint = SetTradingWaypointAction.EWaypointType.WAYPOINT_2;
			} else if (step <= 2) {
				waypoint = SetTradingWaypointAction.EWaypointType.WAYPOINT_3;
			} else {
				waypoint = SetTradingWaypointAction.EWaypointType.DESTINATION;
			}

			return new AskSetTradingWaypointAction(waypoint);
		}
	}

	/**
	 * This displays the land trading path buttons.
	 *
	 * @author Michael Zangl
	 */
	public static class LandTradingPath extends TradingPath {

		private static final int NUMBER_OF_BUTTONS = SetTradingWaypointAction.EWaypointType.VALUES.length;

		/**
		 * Create a new {@link LandTradingPath}.
		 *
		 * @param image
		 *            The image to use.
		 */
		public LandTradingPath(ImageLink image) {
			super(image);
		}

		@Override
		public Action getAction(float relativeX, float relativeY) {
			int step = (int) (relativeX * NUMBER_OF_BUTTONS);
			return getActionForStep(step);
		}
	}

	/**
	 * This displays the sea trading path buttons and the button to build a dock.
	 *
	 * @author Michael Zangl
	 */
	public static class SeaTradingPath extends TradingPath {

		private static final int NUMBER_OF_BUTTONS = SetTradingWaypointAction.EWaypointType.VALUES.length + 1;

		/**
		 * Create a new {@link SeaTradingPath}.
		 *
		 * @param image
		 *            The image to use.
		 */
		public SeaTradingPath(ImageLink image) {
			super(image);
		}

		@Override
		public Action getAction(float relativeX, float relativeY) {
			int step = (int) (relativeX * NUMBER_OF_BUTTONS) - 1;
			if (step >= 0) {
				return getActionForStep(step);
			} else {
				return new Action(EActionType.ASK_SET_DOCK);
			}
		}
	}

	/**
	 * This is a trading button that allows to increment or decrement the amount of traded material.
	 *
	 * @author Michael Zangl
	 */
	public static class TradingButton extends Button {
		private SelectionManager selectionManager;
		private int amount;
		private boolean relative;

		/**
		 * Create a new {@link TradingButton}.
		 *
		 * @param image
		 *            The image to use.
		 * @param description
		 *            The description to display to the user.
		 */
		public TradingButton(ImageLink image, String description) {
			super(null, image, image, description);
		}

		@Override
		public Action getAction() {
			if (selectionManager != null) {
				EMaterialType selected = selectionManager.getSelected();
				if (selected != null) {
					return new ChangeTradingRequestAction(selected, amount, relative);
				}
			}
			return null;
		}

		/**
		 * Sets the selection manager that decides which material to increment.
		 *
		 * @param selectionManager
		 *            The selection manager to use.
		 */
		public void setSelectionManager(SelectionManager selectionManager) {
			this.selectionManager = selectionManager;
		}
	}

	private final IBuilding building;
	private final UIPanel rootPanel = new ContentRefreshingPanel();

	private BuildingState lastState = null;
	private final SelectionManager selectionManager = new SelectionManager();

	/**
	 * Create a new {@link BuildingSelectionContent}.
	 *
	 * @param selection
	 *            The selection this content is for.
	 */
	public BuildingSelectionContent(ISelectionSet selection) {
		assert selection.getSize() == 1;
		building = (IBuilding) selection.get(0);

		updatePanelContent();
	}

	private void updatePanelContent() {
		lastState = new BuildingState(building);
		addPanelContent(lastState);
	}

	private void addPanelContent(BuildingState state) {
		rootPanel.removeAll();
		BuildingBackgroundPanel root;

		if (state.isConstruction()) {
			root = createNormalBuildingContent(state);
		} else if (state.isOccupied()) {
			root = createOccupiedBuildingContent(state);
		} else if (state.isStock()) {
			root = createStockBuildingContent(state);
		} else if (state.isTrading()) {
			root = createTradingBuildingContent(state);
		} else if (state.isDockyard()) {
			root = createDockyardBuildingContent(state);
		} else {
			root = createNormalBuildingContent(state);
		}

		ImageLink[] images = building.getBuildingType().getImages();
		root.setImages(images);
		rootPanel.addChild(root, 0, 0, 1, 1);
	}

	private BuildingBackgroundPanel createNormalBuildingContent(BuildingState state) {
		BuildingSelectionLayout layout = new BuildingSelectionLayout();

		loadPriorityButton(layout.background, layout.priority, state);

		if (building.getBuildingType().getWorkRadius() <= 0) {
			layout.background.removeChild(layout.buttonWorkRadius);
		}

		layout.nameText.setType(building.getBuildingType(), state.isConstruction());

		String text = "";
		if (state.isConstruction()) {
			text = Labels.getString("materials_required");
		} else if (building instanceof IBuilding.IResourceBuilding) {
			IBuilding.IResourceBuilding resourceBuilding = (IBuilding.IResourceBuilding) building;
			text = Labels.getString("productivity", (int) (resourceBuilding.getProductivity() * 100));
		}
		layout.materialText.setText(text);

		addRequestAndOfferStacks(layout.materialArea, state);

		return layout._root;
	}

	private void loadPriorityButton(BuildingBackgroundPanel background, PriorityButton priority, BuildingState state) {
		EPriority[] supported = state.getSupportedPriorities();
		if (supported.length < 2) {
			background.removeChild(priority);
		} else {
			priority.setPriority(supported, building.getPriority());
		}
	}

	private void addRequestAndOfferStacks(UIPanel materialArea, BuildingState state) {
		// hardcoded...
		float buttonWidth = 18f / (127 - 9);
		float buttonSpace = 12f / (127 - 9);

		float requestX = buttonSpace;
		float offerX = 1 - buttonSpace - buttonWidth;

		for (StackState mat : state.getStackStates()) {
			MaterialDisplay display = new MaterialDisplay(mat.getType(), mat.getCount(), -1);
			if (mat.isOffering()) {
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

		/**
		 * Create a new {@link MaterialDisplay}
		 *
		 * @param type
		 *            The type of material.
		 * @param amount
		 *            The number of materials to show.
		 * @param required
		 *            <code>true</code> if those are required materials for build.
		 */
		public MaterialDisplay(EMaterialType type, int amount, int required) {
			String label;
			if (required < 0) {
				label = "building-material-count";
			} else {
				label = "building-material-required";
			}
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
		/**
		 * Create a new building name panel.
		 */
		public NamePanel() {
			super("", EFontSize.HEADLINE);
		}

		/**
		 * Sets the type of the building to display.
		 *
		 * @param type
		 *            The type.
		 * @param workplace
		 *            <code>true</code> if it is currently under construction.
		 */
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
	 */
	public static class PriorityButton extends Button {
		private final ImageLink stopped;
		private final ImageLink low;
		private final ImageLink high;
		private EPriority next = EPriority.DEFAULT;
		private EPriority current = EPriority.DEFAULT;

		public PriorityButton(ImageLink stopped, ImageLink low, ImageLink high) {
			super(null, null, null, null);
			this.stopped = stopped;
			this.low = low;
			this.high = high;
		}

		/**
		 * Sets the current building priority.
		 *
		 * @param supported
		 *            The supported priorities.
		 * @param current
		 *            The current priority.
		 */
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

	private BuildingBackgroundPanel createOccupiedBuildingContent(BuildingState state) {
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
					missingIcon.setBackground(SOLDIER_COMING);
				} else if (state.isMissing()) {
					missingIcon.setBackground(SOLDIER_MISSING);
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

	private BuildingBackgroundPanel createStockBuildingContent(BuildingState state) {
		StockSelectionLayout layout = new StockSelectionLayout();
		layout.nameText.setType(building.getBuildingType(), false);
		selectionManager.setButtons(layout.getAll(StockControlButton.class));
		for (StateDependingElement i : layout.getAll(StateDependingElement.class)) {
			i.setState(state);
		}

		layout.stock_accept.configure(selectionManager::getSelected, building::getPosition, true, true);
		layout.stock_reject.configure(selectionManager::getSelected, building::getPosition, false, true);

		return layout._root;
	}

	private BuildingBackgroundPanel createTradingBuildingContent(BuildingState state) {
		TradingSelectionLayout layout = new TradingSelectionLayout();
		layout.nameText.setType(building.getBuildingType(), false);
		selectionManager.setButtons(layout.getAll(SelectionManagedMaterialButton.class));
		EPriority[] supported = state.getSupportedPriorities();
		if (supported.length < 2) {
			layout.background.removeChild(layout.priority);
		} else {
			layout.priority.setPriority(supported, building.getPriority());
		}
		for (TradingButton b : layout.getAll(TradingButton.class)) {
			b.setSelectionManager(selectionManager);
		}
		for (StateDependingElement i : layout.getAll(StateDependingElement.class)) {
			i.setState(state);
		}

		if (state.isSeaTrading()) {
			layout._root.removeChild(layout.landTradingPath);
		} else {
			layout._root.removeChild(layout.seaTradingPath);
		}
		layout.tradeAll.amount = Integer.MAX_VALUE;
		layout.tradeMore5.relative = true;
		layout.tradeMore5.amount = TRADING_MULTIPLE_STEP_INCREASE;
		layout.tradeMore.relative = true;
		layout.tradeMore.amount = 1;
		layout.tradeLess.relative = true;
		layout.tradeLess.amount = -1;
		layout.tradeLess5.relative = true;
		layout.tradeLess5.amount = -TRADING_MULTIPLE_STEP_INCREASE;

		return layout._root;
	}

	private BuildingBackgroundPanel createDockyardBuildingContent(BuildingState state) {
		DockyardSelectionLayout layout = new DockyardSelectionLayout();
		loadPriorityButton(layout.background, layout.priority, state);
		layout.nameText.setType(building.getBuildingType(), state.isConstruction());

		if (state.isWorkingDockyard()) {
			layout.materialText.setText(Labels.getString("materials_required"));
			addRequestAndOfferStacks(layout.materialArea, state);
		}
		return layout._root;
	}

	/**
	 * This is the panel displayed in the background during building selection.
	 *
	 * @author Michael Zangl
	 */
	public static class BuildingBackgroundPanel extends UIPanel {
		private ImageLink[] links = new ImageLink[0];

		/**
		 * Sets the images to display.
		 *
		 * @param links
		 *            The images.
		 */
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
				ImageProvider.getInstance().getImage(link).drawAt(gl, cx, cy, 0, null, 1);
			}
		}

	}

	/**
	 * This is a panel that refreshes the content when it is drawn.
	 *
	 * @author Michael Zangl.
	 */
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

	/**
	 * Checks if this content needs to be refreshed and rebuilds it if the building state has changed.
	 */
	public void refreshContentIfNeeded() {
		if (!lastState.isStillInState(building)) {
			rootPanel.removeAll();
			updatePanelContent();
		}
	}
}
