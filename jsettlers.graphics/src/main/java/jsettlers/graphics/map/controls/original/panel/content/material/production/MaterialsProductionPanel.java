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
package jsettlers.graphics.map.controls.original.panel.content.material.production;

import go.graphics.text.EFontSize;
import jsettlers.common.buildings.IMaterialProductionSettings;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.IPositionSupplier;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.common.action.SetMaterialProductionAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.BarFill;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.map.controls.original.panel.content.ActionProvidedBarFill;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiContentUpdater;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiLocationDependingContentUpdater;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.SetMaterialProductionButton;
import jsettlers.graphics.ui.UIPanel;

import java.util.Arrays;

public class MaterialsProductionPanel extends AbstractContentProvider {
	private static final float contentHeight_px = 216;
	private static final float contentWidth_px = 118;

	private static final float titleTop_px = 2;
	private static final float titleTextHeight_px = 12;
	private static final float titleTop = 1 - (titleTop_px / contentHeight_px);
	private static final float titleTextHeight = titleTextHeight_px / contentHeight_px;

	private static final float iconSize_px = 18;
	private static final float rowHeight = iconSize_px / contentHeight_px;
	private static final float marginTop = 1 - (17 / contentHeight_px);
	private static final float marginH = 5 / contentWidth_px;
	private static final int TOOLS_ROWS = 7;
	private static final int WEAPONS_ROWS = 3;

	private static final float weaponsTitleMarginTop_px = 2f;
	private static final float weaponsTitleMarginBottom_px = 4f;
	private static final float weaponsTitleMarginTop = weaponsTitleMarginTop_px / contentHeight_px;
	private static final float weaponsTitleMarginBottom = weaponsTitleMarginBottom_px / contentHeight_px;

	private static class Row extends UIPanel implements UiContentUpdater.IUiContentReceiver<IMaterialProductionSettings> {
		private static final ImageLink arrowsImageLink = new OriginalImageLink(EImageLinkType.GUI, 3, 231, 0); // checked in the original game
		private static final float iconWidth = iconSize_px / contentWidth_px;
		private static final float quantityTextWidth = 18f / contentWidth_px;
		private static final float quantityTextMarginV = 5f / iconSize_px;
		private static final float arrowsWidth = 12f / contentWidth_px;
		private static final float barPaddingLeft = 6f / contentWidth_px;
		private static final float barMarginV = 3f / iconSize_px;

		private final UIPanel goodsIcon;
		private final Label lblQuantity;
		private final UIPanel arrows;
		private final BarFill barFill;
		private final EMaterialType type;

		private ShortPoint2D position;
		private int quantity = 0;

		public Row(final EMaterialType materialType) {
			type = materialType;
			goodsIcon = new UIPanel();
			goodsIcon.setBackground(materialType.getIcon());

			lblQuantity = new Label(Labels.getString(Integer.toString(quantity)), EFontSize.NORMAL);

			IPositionSupplier positionSupplier = () -> position;
			Button upButton = new SetMaterialProductionButton(positionSupplier, type, SetMaterialProductionAction.EMaterialProductionType.INCREASE);
			Button downButton = new SetMaterialProductionButton(positionSupplier, type, SetMaterialProductionAction.EMaterialProductionType.DECREASE);

			arrows = new UIPanel();
			arrows.setBackground(arrowsImageLink);
			arrows.addChild(upButton, 0f, 0.5f, 1f, 1f);
			arrows.addChild(downButton, 0f, 0f, 1f, 0.5f);

			barFill = new ActionProvidedBarFill(fillForClick -> new SetMaterialProductionAction(position, materialType, SetMaterialProductionAction.EMaterialProductionType.SET_RATIO, fillForClick), Labels.getName(materialType, false) + "-production-barfill");

			float left = 0;
			addChild(goodsIcon, left, 0f, left += iconWidth, 1f);
			addChild(lblQuantity, left, quantityTextMarginV, left += quantityTextWidth, 1f - quantityTextMarginV);
			addChild(arrows, left, 0f, left += arrowsWidth, 1f);
			addChild(barFill, left + barPaddingLeft, barMarginV, 1f, 1f - barMarginV);
		}

		public void setPosition(ShortPoint2D position) {
			this.position = position;
		}

		@Override
		public void update(IMaterialProductionSettings materialProductionProvider) {
			if (materialProductionProvider != null) {
				quantity = materialProductionProvider.getAbsoluteProductionRequest(type);
				lblQuantity.setText(Integer.toString(quantity));
				barFill.setBarFill(materialProductionProvider.getUserConfiguredRelativeRequestValue(type), materialProductionProvider.getRelativeRequestProbability(type));
			} else {
				quantity = 0;
				lblQuantity.setText("");
				barFill.setBarFill(0, 0);
			}
		}
	}

	private final Row[] rows = {
			new Row(EMaterialType.HAMMER),
			new Row(EMaterialType.BLADE),
			new Row(EMaterialType.PICK),
			new Row(EMaterialType.AXE),
			new Row(EMaterialType.SAW),
			new Row(EMaterialType.SCYTHE),
			new Row(EMaterialType.FISHINGROD),
			new Row(EMaterialType.SWORD),
			new Row(EMaterialType.BOW),
			new Row(EMaterialType.SPEAR)
	};

	private final UIPanel panel;
	private final UiLocationDependingContentUpdater<IMaterialProductionSettings> uiContentUpdater = new UiLocationDependingContentUpdater<>(
			((grid, position) -> grid.getPartitionData(position.x, position.y).getPartitionSettings().getMaterialProductionSettings()));

	public MaterialsProductionPanel() {
		panel = new UIPanel();

		panel.addChild(new Label(Labels.getString("controlpanel_tools_title"), EFontSize.NORMAL), 0f, titleTop - titleTextHeight, 1f, titleTop);

		int itemIdx = 0;
		float top = marginTop;
		for (int r = 0; r < TOOLS_ROWS; r++, top -= rowHeight) {
			panel.addChild(rows[itemIdx++], marginH, top - rowHeight, 1f - marginH, top);
		}

		top -= weaponsTitleMarginTop;
		panel.addChild(new Label(Labels.getString("controlpanel_weapons_title"), EFontSize.NORMAL), 0f, top - titleTextHeight, 1f, top);

		top -= titleTextHeight + weaponsTitleMarginBottom;
		for (int r = 0; r < WEAPONS_ROWS; r++, top -= rowHeight) {
			panel.addChild(rows[itemIdx++], marginH, top - rowHeight, 1f - marginH, top);
		}

		uiContentUpdater.addListeners(Arrays.asList(rows));
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
	public synchronized void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
		uiContentUpdater.updatePosition(grid, pos);
		for (Row row : rows) {
			row.setPosition(pos);
		}
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
