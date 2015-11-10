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
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.Label.EHorizontalAlignment;
import jsettlers.graphics.ui.UIPanel;

public class DistributionPanel extends AbstractContentProvider {
	private static class ConfigurationPanelRow extends UIPanel {
		private static final float textHeight = rowTextHeight_px / rowHeight_px;
		private static final float textMarginBottom = rowTextMarginBottom_px / rowHeight_px;
		private static final float textPercentageWidth = 30f / 84f;

		private final Label lblPercentage;
		private final BarFill barFill;

		public ConfigurationPanelRow(String receiver) {
			Label rowTitle = new Label(Labels.getString(receiver), EFontSize.SMALL, EHorizontalAlignment.LEFT);

			lblPercentage = new Label("0%", EFontSize.NORMAL, EHorizontalAlignment.LEFT);

			barFill = new BarFill();

			addChild(rowTitle, 0f, 1f - textHeight, 1f, 1f);
			addChild(lblPercentage, 0f, 0f, textPercentageWidth, 1f - (textHeight + textMarginBottom));
			addChild(barFill, textPercentageWidth, 0f, 1f, 1f - (textHeight + textMarginBottom));
		}

		public void setPercentage(int percentage) {
			lblPercentage.setText(Integer.toString(percentage) + "%");
		}
	}

	private static class ConfigurationPanel extends UIPanel {
		private static final float panelHeight_px = contentHeight_px - marginTop_px;
		private static final float rowHeight = rowHeight_px / panelHeight_px;
		private static final float rowSpacing = rowSpacingV_px / panelHeight_px;

		public ConfigurationPanel(final ConfigurationPanelRow[] rows) {
			int percentage = 100 / rows.length;
			float top = 1f;
			for (ConfigurationPanelRow row : rows) {
				row.barFill.setAction(new ExecutableAction() {
					@Override
					public void execute() {
						float total = 0f;
						for (ConfigurationPanelRow r : rows) {
							total += r.barFill.getBarFillPercentage();
						}
						for (ConfigurationPanelRow r : rows) {
							int percentage = (int) (100 * (r.barFill.getBarFillPercentage() / total));
							r.setPercentage(percentage);
						}
					}
				});
				row.setPercentage(percentage);
				addChild(row, 0f, top - rowHeight, 1f, top);
				top -= rowHeight + rowSpacing;
			}
		}
	}

	private static class Tab {
		private final Button icon;
		private final ConfigurationPanel configurationPanel;

		private Tab(Button icon, ConfigurationPanel configurationPanel) {
			this.icon = icon;
			this.configurationPanel = configurationPanel;
		}
	}

	private final Tab[] tabs = {
			new Tab(new Button(new OriginalImageLink(EImageLinkType.GUI, 3, 144, 0)),
					new ConfigurationPanel(new ConfigurationPanelRow[] {
							new ConfigurationPanelRow("Ironsmith"),
							new ConfigurationPanelRow("Goldsmith"),
							new ConfigurationPanelRow("Weaponsmith"),
							new ConfigurationPanelRow("Toolsmith"),
					})),
			new Tab(new Button(new OriginalImageLink(EImageLinkType.GUI, 3, 132, 0)),
					new ConfigurationPanel(new ConfigurationPanelRow[] {
							new ConfigurationPanelRow("Weaponsmith"),
							new ConfigurationPanelRow("Toolsmith"),
							new ConfigurationPanelRow("Shipyard"),
							new ConfigurationPanelRow("Catapult Workshop"),
					})),
			new Tab(new Button(new OriginalImageLink(EImageLinkType.GUI, 3, 168, 0)),
					new ConfigurationPanel(new ConfigurationPanelRow[] {
							new ConfigurationPanelRow("Building"),
							new ConfigurationPanelRow("Shipyard"),
							new ConfigurationPanelRow("Charcoal"),
							new ConfigurationPanelRow("Catapult Workshop"),
					})),
			new Tab(new Button(new OriginalImageLink(EImageLinkType.GUI, 3, 174, 0)),
					new ConfigurationPanel(new ConfigurationPanelRow[] {
							new ConfigurationPanelRow("Building"),
							new ConfigurationPanelRow("Catapult Workshop"),
					})),
			new Tab(new Button(new OriginalImageLink(EImageLinkType.GUI, 3, 180, 0)),
					new ConfigurationPanel(new ConfigurationPanelRow[] {
							new ConfigurationPanelRow("Windmill"),
							new ConfigurationPanelRow("Pig Farm"),
							new ConfigurationPanelRow("Donkey Farm"),
					})),
			new Tab(new Button(new OriginalImageLink(EImageLinkType.GUI, 3, 156, 0)),
					new ConfigurationPanel(new ConfigurationPanelRow[] {
							new ConfigurationPanelRow("Bakery"),
							new ConfigurationPanelRow("Pig Farm"),
							new ConfigurationPanelRow("Donkey Farm"),
					})),
			new Tab(new Button(new OriginalImageLink(EImageLinkType.GUI, 3, 186, 0)),
					new ConfigurationPanel(new ConfigurationPanelRow[] {
							new ConfigurationPanelRow("Coal Mine"),
							new ConfigurationPanelRow("Iron Mine"),
							new ConfigurationPanelRow("Gold Mine"),
					})),
			new Tab(new Button(new OriginalImageLink(EImageLinkType.GUI, 3, 162, 0)),
					new ConfigurationPanel(new ConfigurationPanelRow[] {
							new ConfigurationPanelRow("Iron Mine"),
							new ConfigurationPanelRow("Coal Mine"),
							new ConfigurationPanelRow("Gold Mine"),
					})),
			new Tab(new Button(new OriginalImageLink(EImageLinkType.GUI, 3, 189, 0)),
					new ConfigurationPanel(new ConfigurationPanelRow[] {
							new ConfigurationPanelRow("Gold Mine"),
							new ConfigurationPanelRow("Coal Mine"),
							new ConfigurationPanelRow("Iron Mine"),
					})),
	};

	private static final float contentHeight_px = 216; // 360
	private static final float contentWidth_px = 118; // 197

	private static final float titleTop_px = 2;
	private static final float titleTextHeight_px = 12;
	private static final float iconSize_px = 18; // 30

	private static final float marginTop_px = 23f;

	private static final float titleTop = 1 - (titleTop_px / contentHeight_px);
	private static final float titleTextHeight = titleTextHeight_px / contentHeight_px;

	private static final float rowHeight = iconSize_px / contentHeight_px;
	private static final float tileWidth = iconSize_px / contentWidth_px;
	private static final float marginTop = 1 - (marginTop_px / contentHeight_px);
	private static final float marginV = 3 / contentHeight_px;
	private static final float marginH = 5 / contentWidth_px;
	private static final int ROWS = 9;

	private static final float rowHeight_px = 25f;
	private static final float rowSpacingV_px = 8f;
	private static final float rowTextHeight_px = 9f;
	private static final float rowTextMarginBottom_px = 4f;
	private static final float rowTextMarginLeft_px = 6f;

	private static final float rowTextMarginLeft = rowTextMarginLeft_px / contentWidth_px;

	private final UIPanel panel;
	private final UIPanel selectionFrame;
	private Tab tab;

	public DistributionPanel() {
		selectionFrame = new UIPanel();
		selectionFrame.setBackground(new OriginalImageLink(EImageLinkType.GUI, 3, 339, 0));

		panel = new UIPanel();
		panel.addChild(new Label(Labels.getString("controlpanel_distribution_title"), EFontSize.NORMAL), 0f, titleTop - titleTextHeight, 1f, titleTop);

		int tabIdx = 0;
		float top = marginTop;
		for (int r = 0; r < ROWS; r++, top -= (rowHeight + marginV)) {
			final Tab item = tabs[tabIdx];
			Button icon = item.icon;
			panel.addChild(
					tabs[tabIdx].icon,
					marginH,
					top - rowHeight,
					marginH + tileWidth,
					top
					);
			// icon.setAction(new ExecutableAction() {
			// @Override
			// public void execute() {
			// setConfigurationPanel(item);
			// }
			// });
			tabIdx++;
		}
		tab = tabs[0];
	}

	public void setConfigurationPanel(Tab item) {
		panel.removeChild(this.tab.configurationPanel);
		this.tab.icon.removeChild(selectionFrame);
		this.tab = item;
		item.icon.addChild(selectionFrame, 0f, 0f, 1f, 1f);
		panel.addChild(item.configurationPanel, marginH + tileWidth + rowTextMarginLeft, 0f, 1f - marginH, marginTop);
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
