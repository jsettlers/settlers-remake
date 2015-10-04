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

import go.graphics.GLDrawContext;

import java.util.Arrays;
import java.util.BitSet;

import jsettlers.common.images.ImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.action.SetMaterialPrioritiesAction;
import jsettlers.graphics.action.SetMaterialStockAcceptedAction;
import jsettlers.graphics.map.controls.original.panel.button.MaterialButton;
import jsettlers.graphics.map.controls.original.panel.button.MaterialButton.DotColor;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.MaterialPriorityLayout;

/**
 * This panel lets the user select the priorities in which the materials should be transported by settlers.
 *
 * @author michael
 */
public class MaterialPriorityContent extends AbstractContentProvider {
	public static class MaterialPriorityButton extends MaterialButton {

		public MaterialPriorityButton(MaterialPriorityPanel panel, EMaterialType material) {
			super(new SelectMaterialAction(panel, material), material);
		}
	}

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
		 * This is a mapping: {@link EMaterialType} -> label position
		 */
		private final AnimateablePosition[] positions = new AnimateablePosition[EMaterialType.NUMBER_OF_MATERIALS];
		private final MaterialPriorityButton[] buttons = new MaterialPriorityButton[EMaterialType.NUMBER_OF_MATERIALS];

		/**
		 * The order that is currently displayed.
		 */
		private EMaterialType[] order = new EMaterialType[0];

		private ShortPoint2D currentPos;
		private IGraphicsGrid currentGrid;
		private EMaterialType selected;

		public MaterialPriorityPanel() {
			addRowPositions(0, true);
			addRowPositions(1, false);
			addRowPositions(2, true);
			addRowPositions(3, false);
			addRowPositions(4, true);

			for (EMaterialType material : EMaterialType.DROPPABLE_MATERIALS) {
				buttons[material.ordinal] = new MaterialPriorityButton(this, material);
			}
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
			AnimateablePosition pos = positions[material.ordinal];
			if (pos == null) {
				positions[material.ordinal] = new AnimateablePosition(xpoints[newindex], ypoints[newindex]);
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
			return currentGrid == null ? null : currentGrid.getPartitionData(currentPos.x, currentPos.y);
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
					newOrder[i] = data.getPartitionSettings().getMaterialTypeForPrio(i);
				}
				for (EMaterialType m : EMaterialType.values) {
					materialsAccepted.set(m.ordinal, data.getPartitionSettings().getStockAcceptsMaterial(m));
				}
				setOrder(newOrder);

				for (EMaterialType material : newOrder) {
					MaterialPriorityButton button = buttons[material.ordinal];
					AnimateablePosition position = positions[material.ordinal];

					button.setDotColor(materialsAccepted.get(button.getMaterial().ordinal) ? DotColor.GREEN : DotColor.RED);
					removeChild(button);
					addChild(button, position.getX(), position.getY(), position.getX() + BUTTON_WIDTH, position.getY() + BUTTON_HEIGHT);
				}
			}
		}

		/**
		 * Sets the selected material type.
		 *
		 * @param eMaterialType
		 */
		public void selectMaterial(EMaterialType selected) {
			this.selected = selected;
			for (MaterialPriorityButton b : buttons) {
				if (b != null) {
					b.setSelected(b.getMaterial() == selected);
				}
			}
		}

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

		public int indexOf(EMaterialType type) {
			int oldPos = -1;
			for (int i = 0; i < order.length; i++) {
				if (order[i] == type) {
					oldPos = i;
				}
			}
			return oldPos;
		}

		public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
			currentPos = pos;
			currentGrid = grid;
		}

		public EMaterialType getSelected() {
			return selected;
		}

		public ShortPoint2D getMapPosition() {
			return currentPos;
		}
	}

	private static class SelectMaterialAction extends ExecutableAction {
		private final EMaterialType eMaterialType;
		private final MaterialPriorityPanel panel;

		public SelectMaterialAction(MaterialPriorityPanel panel, EMaterialType eMaterialType) {
			this.panel = panel;
			this.eMaterialType = eMaterialType;
		}

		@Override
		public void execute() {
			panel.selectMaterial(eMaterialType);
		}

	}

	public static class ReorderButton extends Button {

		private MaterialPriorityPanel panel;
		private int add = 0;

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

	public static class ChangeAcceptButton extends Button {
		private MaterialPriorityPanel panel;
		private boolean accept;

		public ChangeAcceptButton(ImageLink image, String description) {
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
			return new SetMaterialStockAcceptedAction(mapPosition, selected, accept);
		}
	}

	private final MaterialPriorityLayout layout;

	public MaterialPriorityContent() {
		layout = new MaterialPriorityLayout();

		layout.stock_accept.panel = layout.panel;
		layout.stock_accept.accept = true;
		layout.stock_reject.panel = layout.panel;
		layout.stock_reject.accept = false;

		layout.all_up.panel = layout.panel;
		layout.all_up.add = -100;
		layout.one_up.panel = layout.panel;
		layout.one_up.add = -1;
		layout.one_down.panel = layout.panel;
		layout.one_down.add = 1;
		layout.all_down.panel = layout.panel;
		layout.all_down.add = 100;
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
