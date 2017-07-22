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
package jsettlers.graphics.map.controls.original.panel.content.material.distribution;

import java.util.List;

import go.graphics.text.EFontSize;

import jsettlers.common.buildings.EBuildingType;
import jsettlers.common.buildings.MaterialsOfBuildings;
import jsettlers.common.map.IGraphicsGrid;
import jsettlers.common.map.partition.IMaterialDistributionSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.common.position.IPositionSupplier;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.ExecutableAction;
import jsettlers.graphics.action.SetMaterialDistributionSettingsAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.button.MaterialButton;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.ActionProvidedBarFill;
import jsettlers.graphics.map.controls.original.panel.content.BarFill;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiContentUpdater.IUiContentReceiver;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiLocationDependingContentUpdater;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.Label.EHorizontalAlignment;
import jsettlers.graphics.ui.UIPanel;

import java8.util.J8Arrays;
import java8.util.stream.Collectors;

public class DistributionPanel extends AbstractContentProvider implements IUiContentReceiver<IMaterialDistributionSettings> {
	private static final EMaterialType[] MATERIAL_TYPES_FOR_DISTRIBUTION = new EMaterialType[] {
			EMaterialType.COAL,
			EMaterialType.IRON,
			EMaterialType.PLANK,
			EMaterialType.CROP,
			EMaterialType.WATER,
			EMaterialType.BREAD,
			EMaterialType.MEAT,
			EMaterialType.FISH
	};

	private static final float contentHeight_px = 216;
	private static final float contentWidth_px = 118;

	private static final float titleTop_px = 2;
	private static final float titleTextHeight_px = 12;
	private static final float iconSize_px = 18;

	private static final float marginTop_px = 23f;

	private static final float titleTop = 1 - (titleTop_px / contentHeight_px);
	private static final float titleTextHeight = titleTextHeight_px / contentHeight_px;

	private static final float rowHeight = iconSize_px / contentHeight_px;
	private static final float tileWidth = iconSize_px / contentWidth_px;
	private static final float marginTop = 1 - (marginTop_px / contentHeight_px);
	private static final float marginV = 3 / contentHeight_px;
	private static final float marginH = 5 / contentWidth_px;

	private static final float rowHeight_px = 25f;
	private static final float rowSpacingV_px = 8f;
	private static final float rowTextHeight_px = 9f;
	private static final float rowTextMarginBottom_px = 4f;
	private static final float rowTextMarginLeft_px = 6f;

	private static final float rowTextMarginLeft = rowTextMarginLeft_px / contentWidth_px;

	private static class BuildingDistributionSettingPanel extends UIPanel {
		private static final float textHeight = rowTextHeight_px / rowHeight_px;
		private static final float textMarginBottom = rowTextMarginBottom_px / rowHeight_px;
		private static final float textPercentageWidth = 30f / 84f;

		private final Label lblPercentage = new Label("0%", EFontSize.NORMAL, EHorizontalAlignment.LEFT);
		private final BarFill barFill;
		private final EBuildingType buildingType;

		BuildingDistributionSettingPanel(EMaterialType materialType, EBuildingType buildingType, IPositionSupplier positionSupplier) {
			this.buildingType = buildingType;
			barFill = new ActionProvidedBarFill(ratio -> {
				ShortPoint2D position = positionSupplier.getPosition();
				if (position != null) {
					return new SetMaterialDistributionSettingsAction(position, materialType, buildingType, ratio);
				} else {
					return null;
				}
			});
			Label rowTitle = new Label(Labels.getName(buildingType), EFontSize.SMALL, EHorizontalAlignment.LEFT);

			addChild(rowTitle, 0f, 1f - textHeight, 1f, 1f);
			addChild(lblPercentage, 0f, 0f, textPercentageWidth, 1f - (textHeight + textMarginBottom));
			addChild(barFill, textPercentageWidth, 0f, 1f, 1f - (textHeight + textMarginBottom));
		}

		void update(IMaterialDistributionSettings distributionSettings) {
			float probability = distributionSettings.getDistributionProbability(buildingType);
			float userBarValue = distributionSettings.getUserConfiguredDistributionValue(buildingType);

			lblPercentage.setText(Integer.toString((int) (probability * 100)) + "%");
			barFill.setBarFill(userBarValue, probability);
		}
	}

	private static class MaterialDistributionPanel extends UIPanel {
		private static final float panelHeight_px = contentHeight_px - marginTop_px;
		private static final float rowHeight = rowHeight_px / panelHeight_px;
		private static final float rowSpacing = rowSpacingV_px / panelHeight_px;

		private final List<BuildingDistributionSettingPanel> buildingDistributionSettings;

		MaterialDistributionPanel(EMaterialType materialType, IPositionSupplier positionSupplier) {
			EBuildingType[] buildingsForMaterial = MaterialsOfBuildings.getBuildingTypesRequestingMaterial(materialType);

			buildingDistributionSettings = J8Arrays.stream(buildingsForMaterial)
					.map(buildingType -> new BuildingDistributionSettingPanel(materialType, buildingType, positionSupplier))
					.collect(Collectors.toList());

			placeRows(buildingDistributionSettings);
		}

		private void placeRows(List<BuildingDistributionSettingPanel> rows) {
			float top = 1f;
			for (BuildingDistributionSettingPanel row : rows) {
				addChild(row, 0f, top - rowHeight, 1f, top);
				top -= rowHeight + rowSpacing;
			}
		}

		public void update(IMaterialDistributionSettings materialsDistributionSettings) {
			for (int i = 0; i < buildingDistributionSettings.size(); i++) {
				buildingDistributionSettings.get(i).update(materialsDistributionSettings);
			}
		}
	}

	private class MaterialDistributionTab {
		private final MaterialButton materialButton;
		private final MaterialDistributionPanel configurationPanel;

		private MaterialDistributionTab(EMaterialType materialType, IPositionSupplier positionSupplier) {
			MaterialDistributionTab thisTab = this;
			materialButton = new MaterialButton(new ExecutableAction() {
				@Override
				public void execute() {
					setCurrentTab(thisTab);
				}
			}, materialType);

			configurationPanel = new MaterialDistributionPanel(materialType, positionSupplier);
		}
	}

	private final UIPanel panel;
	private final UiLocationDependingContentUpdater<IMaterialDistributionSettings> uiContentUpdater = new UiLocationDependingContentUpdater<>(this::currentDistributionSettingsProvider);

	private MaterialDistributionTab currentTab;

	public DistributionPanel() {
		panel = buildPanel();
		uiContentUpdater.addListener(this);
	}

	private UIPanel buildPanel() {
		List<MaterialDistributionTab> materialDistributionTabs = createTabs(uiContentUpdater::getPosition);

		UIPanel panel = new UIPanel();
		panel.addChild(new Label(Labels.getString("controlpanel_distribution_title"), EFontSize.NORMAL), 0f, titleTop - titleTextHeight, 1f, titleTop);

		float top = marginTop;
		for (MaterialDistributionTab materialDistributionTab : materialDistributionTabs) {
			MaterialButton materialButton = materialDistributionTab.materialButton;
			panel.addChild(materialButton, marginH, top - rowHeight, marginH + tileWidth, top);
			top -= (rowHeight + marginV);
		}

		return panel;
	}

	private List<MaterialDistributionTab> createTabs(IPositionSupplier positionSupplier) {
		return J8Arrays.stream(MATERIAL_TYPES_FOR_DISTRIBUTION)
				.map(materialType -> new MaterialDistributionTab(materialType, positionSupplier))
				.collect(Collectors.toList());
	}

	private void setCurrentTab(MaterialDistributionTab tab) {
		if (currentTab != null) {
			panel.removeChild(this.currentTab.configurationPanel);
			currentTab.materialButton.setSelected(false);
		}

		this.currentTab = tab;

		if (currentTab != null) {
			currentTab.materialButton.setSelected(true);
			panel.addChild(tab.configurationPanel, marginH + tileWidth + rowTextMarginLeft, 0f, 1f - marginH, marginTop);
			uiContentUpdater.updateUi();
		}
	}

	@Override
	public void update(IMaterialDistributionSettings materialsDistributionSettings) {
		if (currentTab != null) {
			if (materialsDistributionSettings == null) {
				setCurrentTab(null);
			} else {
				currentTab.configurationPanel.update(materialsDistributionSettings);
			}
		}
	}

	private IMaterialDistributionSettings currentDistributionSettingsProvider(IGraphicsGrid grid, ShortPoint2D position) {
		if (currentTab != null) {
			if (grid.getPlayerIdAt(position.x, position.y) >= 0) {
				return grid.getPartitionData(position.x, position.y).getPartitionSettings().getDistributionSettings(currentTab.materialButton.getMaterial());
			}
		}

		return null;
	}

	@Override
	public void showMapPosition(ShortPoint2D position, IGraphicsGrid grid) {
		uiContentUpdater.updatePosition(grid, position);
		super.showMapPosition(position, grid);
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
