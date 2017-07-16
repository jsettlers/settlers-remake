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

import go.graphics.text.EFontSize;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.UIElement;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.MaterialInventoryLayout;

/**
 * This is a statistics panel that displays the number of items the user has on the current partition.
 * 
 * @author Michael Zangl
 */
public class InventoryPanel extends AbstractContentProvider {
	public interface IPartitionDataLoadable {
		void loadFromData(IPartitionData data);
	}

	/**
	 * This label displays the number of items in the current area.
	 * 
	 * @author Michael Zangl
	 *
	 */
	public static class InventoryCount extends Label implements IPartitionDataLoadable {

		private final EMaterialType material;
		private boolean plural;

		public InventoryCount(EMaterialType material) {
			super("", EFontSize.NORMAL);
			this.material = material;
		}

		@Override
		public String getDescription(float relativex, float relativey) {
			return Labels.getName(material, plural);
		}

		@Override
		public void loadFromData(IPartitionData data) {
			int amountOf = data.getAmountOf(material);
			plural = amountOf != 1;
			setText(amountOf + "");
		}
	}

	/**
	 * This is a button that displays the icon of a material.
	 * 
	 * @author Michael Zangl
	 */
	public static class MaterialButton extends Button implements IPartitionDataLoadable {

		private final EMaterialType material;
		private boolean plural;

		public MaterialButton(EMaterialType material) {
			super(material.getIcon());
			this.material = material;
		}

		@Override
		public String getDescription(float relativex, float relativey) {
			return Labels.getName(material, plural);
		}

		@Override
		public void loadFromData(IPartitionData data) {
			int amountOf = data.getAmountOf(material);
			plural = amountOf != 1;
		}
	}

	private UIPanel panel;

	public InventoryPanel() {
		panel = new MaterialInventoryLayout()._root;
	}

	@Override
	public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
		super.showMapPosition(pos, grid);

		IPartitionData data = grid.getPartitionData(pos.x, pos.y);
		// TODO: Add a data observer.
		for (UIElement c : panel.getChildren()) {
			if (c instanceof IPartitionDataLoadable) {
				((IPartitionDataLoadable) c).loadFromData(data);
			}
		}
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.GOODS;
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}
}