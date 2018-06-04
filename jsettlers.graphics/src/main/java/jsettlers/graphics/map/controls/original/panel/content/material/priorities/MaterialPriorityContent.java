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
package jsettlers.graphics.map.controls.original.panel.content.material.priorities;

import go.graphics.GLDrawContext;

import java.util.Arrays;
import java.util.BitSet;

import jsettlers.common.images.ImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.map.partition.IStockSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.common.action.Action;
import jsettlers.common.action.SetMaterialPrioritiesAction;
import jsettlers.graphics.map.controls.original.panel.button.MaterialButton.DotColor;
import jsettlers.graphics.map.controls.original.panel.button.SelectionManagedMaterialButton;
import jsettlers.graphics.map.controls.original.panel.button.SelectionManager;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.MaterialPriorityLayout;

/**
 * This panel lets the user select the priorities in which the materials should be transported by settlers.
 *
 * @author Michael Zangl
 */
public class MaterialPriorityContent extends AbstractContentProvider {
	private static final int MAX_MOVE_AMOUNT = 100;

	/**
	 * Displays an ordered list of materials.
	 * 
	 * @author Michael Zangl
	 */
	public static class MaterialPriorityPanel extends UIPanel {
		private static final int COLUMNS = 6;
		private static final int ROWS = 5;
		private static final float BUTTON_HEIGHT_RELATIVE_TO_ROW = 18f / (269 - 236);
		private static final float BUTTON_WIDTH = 1f / COLUMNS;
		private static final float BUTTON_HEIGHT = 1f / ROWS * BUTTON_HEIGHT_RELATIVE_TO_ROW;

		/**
		 * Points on which materials can be placed.
		 * <p>
		 * They are indexed by slot.
		 */
		private final float[] xpoints = new float[EMaterialType.NUMBER_OF_MATERIALS];
		private final float[] ypoints = new float[EMaterialType.NUMBER_OF_MATERIALS];
		/**
		 * This is a mapping: {@link EMaterialType} -> label position.
		 */
		private final AnimatablePosition[] positions = new AnimatablePosition[EMaterialType.NUMBER_OF_MATERIALS];
		private final SelectionManagedMaterialButton[] buttons = new SelectionManagedMaterialButton[EMaterialType.NUMBER_OF_DROPPABLE_MATERIALS];
		private final SelectionManager selectionManager = new SelectionManager();

		/**
		 * The order that is currently displayed.
		 */
		private EMaterialType[] order = new EMaterialType[0];

		private ShortPoint2D currentPos;
		private IGraphicsGrid currentGrid;

		/**
		 * Creates a new material panel.
		 */
		public MaterialPriorityPanel() {
			for (int i = 0; i < ROWS; i++) {
				addRowPositions(i, (i % 2) == 0);
			}

			for (EMaterialType material : EMaterialType.DROPPABLE_MATERIALS) {
				buttons[material.ordinal] = new SelectionManagedMaterialButton(material);
			}
			selectionManager.setButtons(buttons);
		}

		private void addRowPositions(int rowIndex, boolean descent) {
			for (int column = 0; column < COLUMNS; column++) {
				int index = rowIndex * COLUMNS + column;
				if (index < xpoints.length) {
					xpoints[index] = (float) (descent ? column : (COLUMNS - column - 1)) / COLUMNS;
					float inRowY = (float) column / (COLUMNS - 1) * (1 - BUTTON_HEIGHT_RELATIVE_TO_ROW) + BUTTON_HEIGHT_RELATIVE_TO_ROW;
					ypoints[index] = 1 - (rowIndex + inRowY) / ROWS;
				}
			}
		}

		/**
		 * Updates the material oder.
		 * 
		 * @param orderIn
		 *            The new material order.
		 */
		public synchronized void setOrder(EMaterialType[] orderIn) {
			if (orderIn == null) {
				order = null;
				return;
			}
			EMaterialType[] newOrder = Arrays.copyOf(orderIn, orderIn.length);
			// and we assume they contain the same elements
			for (int i = 0; i < newOrder.length; i++) {
				if (order == null || order.length <= i || newOrder[i] != order[i]) {
					EMaterialType material = newOrder[i];
					setToPosition(material, i);
				}
			}
			this.order = newOrder;
		}

		private void setToPosition(EMaterialType material, int newindex) {
			AnimatablePosition pos = positions[material.ordinal];
			if (pos == null) {
				positions[material.ordinal] = new AnimatablePosition(xpoints[newindex], ypoints[newindex]);
			} else {
				pos.setPosition(xpoints[newindex], ypoints[newindex]);
			}
		}

		@Override
		public synchronized void drawAt(GLDrawContext gl) {
			updatePositions();
			super.drawAt(gl);
		}

		private IPartitionData getPartitonData() {
			if (currentGrid == null) {
				return null;
			} else {
				return currentGrid.getPartitionData(currentPos.x, currentPos.y);
			}
		}

		private void updatePositions() {
			IPartitionData data = getPartitonData();
			if (data == null) {
				setOrder(null);
				removeAll();
			} else {
				EMaterialType[] newOrder = new EMaterialType[EMaterialType.DROPPABLE_MATERIALS.length];
				BitSet materialsAccepted = new BitSet();

				for (int i = 0; i < newOrder.length; i++) {
					// FIXME: Synchronize!
					newOrder[i] = data.getPartitionSettings().getMaterialTypeForPriority(i);
				}

				IStockSettings stockSettings = data.getPartitionSettings().getStockSettings();
				for (EMaterialType materialType : EMaterialType.DROPPABLE_MATERIALS) {
					materialsAccepted.set(materialType.ordinal, stockSettings.isAccepted(materialType));
				}
				setOrder(newOrder);

				for (EMaterialType material : newOrder) {
					SelectionManagedMaterialButton button = buttons[material.ordinal];
					AnimatablePosition position = positions[material.ordinal];

					button.setDotColor(getColor(materialsAccepted, button));
					removeChild(button);
					addChild(button, position.getX(), position.getY(), position.getX() + BUTTON_WIDTH, position.getY() + BUTTON_HEIGHT);
				}
			}
		}

		private DotColor getColor(BitSet materialsAccepted, SelectionManagedMaterialButton button) {
			if (materialsAccepted.get(button.getMaterial().ordinal)) {
				return DotColor.GREEN;
			} else {
				return DotColor.RED;
			}
		}

		/**
		 * Reorder a material.
		 * 
		 * @param type
		 *            The material to move.
		 * @param desiredNewPosition
		 *            The new position of that material.
		 * @return The new ordered array.
		 */
		public synchronized EMaterialType[] reorder(EMaterialType type, int desiredNewPosition) {
			int oldPos = indexOf(type);
			EMaterialType[] newOrder = order.clone();
			if (oldPos < 0) {
				return newOrder;
			}
			int newPos = Math.max(Math.min(desiredNewPosition, order.length - 1), 0);

			if (newPos > oldPos) {
				for (int i = oldPos; i < newPos; i++) {
					newOrder[i] = newOrder[i + 1];
				}
			} else {
				for (int i = oldPos; i > newPos; i--) {
					newOrder[i] = newOrder[i - 1];
				}
			}
			newOrder[newPos] = type;
			return newOrder;
		}

		/**
		 * Gets the current position of that material.
		 * 
		 * @param type
		 *            The material to search.
		 * @return The position of that material.
		 */
		public int indexOf(EMaterialType type) {
			for (int i = 0; i < order.length; i++) {
				if (order[i] == type) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * Loads the content for the given map position.
		 * 
		 * @param pos
		 *            The map position
		 * @param grid
		 *            The map grid to use.
		 */
		public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
			currentPos = pos;
			currentGrid = grid;
		}

		/**
		 * Gets the selected material type.
		 * 
		 * @return The selected material.
		 */
		public EMaterialType getSelected() {
			return selectionManager.getSelected();
		}

		/**
		 * Gets the currently used map position.
		 * 
		 * @return The map position in use.
		 */
		public ShortPoint2D getMapPosition() {
			return currentPos;
		}
	}

	/**
	 * This is a reorder button that allows you to reorder the selected material.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public static class ReorderButton extends Button {

		private MaterialPriorityPanel panel;
		private int add = 0;

		/**
		 * Creates a new reoder button.
		 * 
		 * @param image
		 *            The image to display.
		 * @param description
		 *            The description this button should have.
		 */
		public ReorderButton(ImageLink image, String description) {
			super(null, image, image, description);
		}

		@Override
		public Action getAction() {
			if (panel == null) {
				return null;
			}
			EMaterialType selected = panel.getSelected();
			ShortPoint2D mapPosition = panel.getMapPosition();
			if (selected == null || mapPosition == null) {
				return null;
			}
			EMaterialType[] order = panel.reorder(selected, panel.indexOf(selected) + add);
			return new SetMaterialPrioritiesAction(mapPosition, order);
		}
	}

	private final MaterialPriorityLayout layout;

	/**
	 * Creates a new {@link MaterialPriorityContent}.
	 */
	public MaterialPriorityContent() {
		layout = new MaterialPriorityLayout();

		layout.stock_accept.configure(layout.panel::getSelected, layout.panel::getMapPosition, true, false);
		layout.stock_reject.configure(layout.panel::getSelected, layout.panel::getMapPosition, false, false);

		layout.all_up.panel = layout.panel;
		layout.all_up.add = -MAX_MOVE_AMOUNT;
		layout.one_up.panel = layout.panel;
		layout.one_up.add = -1;
		layout.one_down.panel = layout.panel;
		layout.one_down.add = 1;
		layout.all_down.panel = layout.panel;
		layout.all_down.add = MAX_MOVE_AMOUNT;
	}

	@Override
	public synchronized void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
		layout.panel.showMapPosition(pos, grid);
	}

	@Override
	public UIPanel getPanel() {
		return layout._root;
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.GOODS;
	}
}
