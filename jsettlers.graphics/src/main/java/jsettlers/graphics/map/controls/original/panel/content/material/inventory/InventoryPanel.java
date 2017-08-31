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
package jsettlers.graphics.map.controls.original.panel.content.material.inventory;

import static java8.util.stream.StreamSupport.stream;

import go.graphics.text.EFontSize;

import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IPartitionData;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiContentUpdater;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiContentUpdater.IUiContentReceiver;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiLocationDependingContentUpdater;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.MaterialInventoryLayout;

/**
 * This is a statistics panel that displays the number of items the user has on the current partition.
 *
 * @author Michael Zangl
 */
public class InventoryPanel extends AbstractContentProvider {

	/**
	 * This label displays the number of items in the current area.
	 *
	 * @author Michael Zangl
	 */
	public static class InventoryCount extends Label implements IUiContentReceiver<IPartitionData> {

		private final EMaterialType material;
		private boolean plural;

		public InventoryCount(EMaterialType material) {
			super("", EFontSize.NORMAL);
			this.material = material;
		}

		@Override
		public String getDescription(float relativeX, float relativeY) {
			return Labels.getName(material, plural);
		}

		@Override
		public void update(IPartitionData partitionData) {
			int amountOf = partitionData != null ? partitionData.getAmountOf(material) : 0;
			plural = amountOf != 1;
			setText(amountOf + "");
		}
	}

	/**
	 * This is a button that displays the icon of a material.
	 *
	 * @author Michael Zangl
	 */
	public static class MaterialButton extends Button implements IUiContentReceiver<IPartitionData> {

		private final EMaterialType material;
		private boolean plural;

		public MaterialButton(EMaterialType material) {
			super(material.getIcon());
			this.material = material;
		}

		@Override
		public String getDescription(float relativeX, float relativeY) {
			return Labels.getName(material, plural);
		}

		@Override
		public void update(IPartitionData partitionData) {
			int amountOf = partitionData != null ? partitionData.getAmountOf(material) : 0;
			plural = amountOf != 1;
		}
	}

	private final UIPanel panel;
	private final UiLocationDependingContentUpdater<IPartitionData> uiContentUpdater = new UiLocationDependingContentUpdater<>((grid, position) -> grid.getPartitionData(position.x, position.y));

	public InventoryPanel() {
		panel = new MaterialInventoryLayout()._root;

		// noinspection unchecked
		stream(panel.getChildren())
				.filter(c -> c instanceof UiContentUpdater.IUiContentReceiver)
				.map(c -> (IUiContentReceiver<IPartitionData>) c)
				.forEach(uiContentUpdater::addListener);
	}

	@Override
	public void showMapPosition(ShortPoint2D position, IGraphicsGrid grid) {
		super.showMapPosition(position, grid);
		uiContentUpdater.updatePosition(grid, position);
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.GOODS;
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

	@Override
	public void contentShowing(ActionFireable actionFireable) {
		uiContentUpdater.start();
	}

	@Override
	public void contentHiding(ActionFireable actionFireable, AbstractContentProvider nextContent) {
		uiContentUpdater.stop();
	}

}