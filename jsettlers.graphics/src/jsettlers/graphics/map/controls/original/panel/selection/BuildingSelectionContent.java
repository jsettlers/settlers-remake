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

import java.util.Collection;
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
import jsettlers.graphics.action.AskSetTradingWaypointAction;
import jsettlers.graphics.action.ChangeTradingRequestAction;
import jsettlers.graphics.action.EActionType;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.action.SetBuildingPriorityAction;
import jsettlers.graphics.action.SetTradingWaypointAction;
import jsettlers.graphics.action.SetTradingWaypointAction.WaypointType;
import jsettlers.graphics.action.SoldierAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.button.MaterialButton;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState.OccupierState;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState.StackState;
import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.UIElement;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.BuildingSelectionLayout;
import jsettlers.graphics.ui.layout.OccupiableSelectionLayout;
import jsettlers.graphics.ui.layout.StockSelectionLayout;
import jsettlers.graphics.ui.layout.TradingSelectionLayout;

/**
 * This is the selection content that is used for displaying a selected building.
 * 
 * @author Michael Zangl
 */
public class BuildingSelectionContent extends AbstractSelectionContent {
	private static final int INCREASE_MULTIPLE_STEP = 5;
	private static final OriginalImageLink SOILDER_MISSING = new OriginalImageLink(
			EImageLinkType.GUI, 3, 45, 0);
	private static final OriginalImageLink SOILDER_COMMING = new OriginalImageLink(
			EImageLinkType.GUI, 3, 48, 0);

	/**
	 * This defines an element that depends on the state of the building.
	 * 
	 * @author Michael Zangl.
	 *
	 */
	private interface StateDependendElement {
		void setState(BuildingState state);
	}

	/**
	 * A button for a given action for a given soldier type.
	 * 
	 * @author Michael Zangl
	 *
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
	 *
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
	 * This is a material that is displayed on the stock screen.
	 * 
	 * @author Michael Zangl
	 */
	public static class StockControlItem extends MaterialButton implements StateDependendElement {
		/**
		 * Creates a new stock control button.
		 * 
		 * @param material
		 *            The material.
		 */
		public StockControlItem(EMaterialType material) {
			super(null, material);
		}

		@Override
		public void setState(BuildingState state) {
			setDotColor(computeColor(state));
		}

		private DotColor computeColor(BuildingState state) {
			if (state.stockAcceptsMaterial(getMaterial())) {
				return DotColor.GREEN;
			} else {
				return DotColor.RED;
			}
		}
	}

	/**
	 * This class manages the selection of the {@link TradingMaterialButton}s.
	 * 
	 * @author Michael Zangl
	 *
	 */
	private static class TradingSelectionManager {
		private Collection<TradingMaterialButton> buttons;
		private EMaterialType selected;

		public void setButtons(Collection<TradingMaterialButton> buttons) {
			this.buttons = buttons;
			updteSelected();
		}

		public Action getSelectAction(final EMaterialType material) {
			return new ExecutableAction() {
				@Override
				public void execute() {
					select(material);
				}
			};
		}

		protected void select(EMaterialType material) {
			this.selected = material;
			updteSelected();
		}

		private void updteSelected() {
			for (TradingMaterialButton b : buttons) {
				b.setSelected(selected == b.getMaterial());
			}
		}

		public EMaterialType getSelected() {
			return selected;
		}
	}

	/**
	 * This is a material button for the trading GUI.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public static class TradingMaterialButton extends MaterialButton {
		private TradingSelectionManager selectionManager;

		/**
		 * Creates a new {@link TradingMaterialButton}.
		 * 
		 * @param material
		 *            The material this button is for.
		 */
		public TradingMaterialButton(EMaterialType material) {
			super(null, material);
		}

		@Override
		public Action getAction() {
			if (selectionManager != null) {
				return selectionManager.getSelectAction(getMaterial());
			} else {
				return null;
			}
		}

		/**
		 * Binds this button to a selection manager.
		 * 
		 * @param selectionManager
		 *            The manager to use.
		 */
		public void setSelectionManager(TradingSelectionManager selectionManager) {
			this.selectionManager = selectionManager;
		}
	}

	/**
	 * This displays the number of materials traded.
	 * 
	 * @author Michael Zangl
	 */
	public static class TradingMaterialCount extends Label implements StateDependendElement {

		private EMaterialType material;

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
	 *
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
			WaypointType wp;
			if (step <= 0) {
				wp = SetTradingWaypointAction.WaypointType.WAYPOINT_1;
			} else if (step <= 1) {
				wp = SetTradingWaypointAction.WaypointType.WAYPOINT_2;
			} else if (step <= 2) {
				wp = SetTradingWaypointAction.WaypointType.WAYPOINT_3;
			} else {
				wp = SetTradingWaypointAction.WaypointType.DESTINATION;
			}

			return new AskSetTradingWaypointAction(wp);
		}
	}

	/**
	 * This displays the land trading path buttons.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public static class LandTradingPath extends TradingPath {

		private static final int BUTTONS = SetTradingWaypointAction.WaypointType.VALUES.length;

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
		public Action getAction(float relativex, float relativey) {
			int step = (int) (relativex * BUTTONS);
			return getActionForStep(step);
		}
	}

	/**
	 * This displays the sea trading path buttons and the button to build a dock.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public static class SeaTradingPath extends TradingPath {

		private static final int BUTTONS = SetTradingWaypointAction.WaypointType.VALUES.length + 1;

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
		public Action getAction(float relativex, float relativey) {
			int step = (int) (relativex * BUTTONS) - 1;
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
	 *
	 */
	public static class TradingButton extends Button {
		private TradingSelectionManager selectionManager;
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
		public void setSelectionManager(TradingSelectionManager selectionManager) {
			this.selectionManager = selectionManager;
		}
	}

	private final IBuilding building;
	private final UIPanel rootPanel = new ContentRefreshingPanel();

	private BuildingState lastState = null;
	private final TradingSelectionManager selectionManager = new TradingSelectionManager();

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
		BuidlingBackgroundPanel root;
		if (state.isOccupied()) {
			root = createOccupiedBuildingContent(state);
		} else if (state.isStock()) {
			root = createStockBuildingContent(state);
		} else if (state.isTrading()) {
			root = createTradingBuildingContent(state);
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

	private BuidlingBackgroundPanel createOccupiedBuildingContent(BuildingState state) {
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

	private BuidlingBackgroundPanel createStockBuildingContent(BuildingState state) {
		StockSelectionLayout layout = new StockSelectionLayout();
		layout.nameText.setType(building.getBuildingType(), false);
		for (StateDependendElement i : layout.getAll(StateDependendElement.class)) {
			i.setState(state);
		}
		return layout._root;
	}

	private BuidlingBackgroundPanel createTradingBuildingContent(BuildingState state) {
		TradingSelectionLayout layout = new TradingSelectionLayout();
		layout.nameText.setType(building.getBuildingType(), false);
		Collection<TradingMaterialButton> buttons = layout.getAll(TradingMaterialButton.class);
		selectionManager.setButtons(buttons);
		for (TradingMaterialButton b : buttons) {
			b.setSelectionManager(selectionManager);
		}
		for (TradingButton b : layout.getAll(TradingButton.class)) {
			b.setSelectionManager(selectionManager);
		}
		for (StateDependendElement i : layout.getAll(StateDependendElement.class)) {
			i.setState(state);
		}

		if (state.isSeaTrading()) {
			layout._root.removeChild(layout.landTradingPath);
		} else {
			layout._root.removeChild(layout.seaTradingPath);
		}
		layout.tradeAll.amount = Integer.MAX_VALUE;
		layout.tradeMore5.relative = true;
		layout.tradeMore5.amount = INCREASE_MULTIPLE_STEP;
		layout.tradeMore.relative = true;
		layout.tradeMore.amount = 1;
		layout.tradeLess.relative = true;
		layout.tradeLess.amount = -1;
		layout.tradeLess5.relative = true;
		layout.tradeLess5.amount = -INCREASE_MULTIPLE_STEP;

		return layout._root;
	}

	/**
	 * This is the panel displayed in the background during building selection.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public static class BuidlingBackgroundPanel extends UIPanel {
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
				ImageProvider.getInstance().getImage(link).drawAt(gl, cx, cy);
			}
		}

	}

	/**
	 * This is a panel that refreshes the content when it is drawn.
	 * 
	 * @author Michael Zangl.
	 *
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
