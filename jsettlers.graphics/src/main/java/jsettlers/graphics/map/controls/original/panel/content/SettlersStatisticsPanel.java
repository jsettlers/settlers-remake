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
import jsettlers.common.map.partition.IMovableCounts;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.UIElement;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.StatisticLayoutAmazons;

/**
 * This temporary empty panel is necessary so that you can choose the SETTLERSTATISTIC in the gui
 *
 * @author codingberlin
 */
public class SettlersStatisticsPanel extends AbstractContentProvider {

	private int buildingsCount = 1;
	private IMovableCounts movableCounts;
	private UIPanel panel = new StatisticLayoutAmazons()._root;

	public void setPlayer(IInGamePlayer player) {
		switch(player.getCivilisation()) {
			case ROMAN:
				panel = new StatisticLayoutAmazons()._root;
			case ASIAN:
				panel = new StatisticLayoutAmazons()._root;
			case AMAZON:
				panel = new StatisticLayoutAmazons()._root;
			case EGYPTIAN:
				panel = new StatisticLayoutAmazons()._root;
			default:
				panel = new StatisticLayoutAmazons()._root;
		}
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.SETTLERS;
	}

	@Override
	public UIPanel getPanel() {
		if(movableCounts != null)
			setValues(panel);

		return panel;
	}

	@Override
	public void showMapPosition(ShortPoint2D pos, IGraphicsGrid grid) {
		movableCounts = grid.getPartitionData(pos.x,pos.y).getMovableCounts();
		if(movableCounts != null)
			setValues(panel);
	}

	private String getMovableCountAsString(EMovableType type) {
		return String.valueOf(movableCounts.getMovableCountGlobal(type));
	}

	private void setValues(UIPanel panel) {

		int soldierCount = movableCounts.getMovableCountGlobal(EMovableType.SWORDSMAN_L1)
				+ movableCounts.getMovableCountGlobal(EMovableType.SWORDSMAN_L2)
				+ movableCounts.getMovableCountGlobal(EMovableType.SWORDSMAN_L3)
				+ movableCounts.getMovableCountGlobal(EMovableType.BOWMAN_L1)
				+ movableCounts.getMovableCountGlobal(EMovableType.BOWMAN_L2)
				+ movableCounts.getMovableCountGlobal(EMovableType.BOWMAN_L3)
				+ movableCounts.getMovableCountGlobal(EMovableType.PIKEMAN_L1)
				+ movableCounts.getMovableCountGlobal(EMovableType.PIKEMAN_L2)
				+ movableCounts.getMovableCountGlobal(EMovableType.PIKEMAN_L3)
				+ movableCounts.getMovableCountGlobal(EMovableType.MAGE);

		int genericWorker = movableCounts.getMovableCountGlobal(EMovableType.PIG_FARMER)
				+ movableCounts.getMovableCountGlobal(EMovableType.FARMER)
				+ movableCounts.getMovableCountGlobal(EMovableType.LUMBERJACK)
				+ movableCounts.getMovableCountGlobal(EMovableType.SAWMILLER)
				+ movableCounts.getMovableCountGlobal(EMovableType.FISHERMAN)
				+ movableCounts.getMovableCountGlobal(EMovableType.WATERWORKER)
				+ movableCounts.getMovableCountGlobal(EMovableType.BAKER)
				+ movableCounts.getMovableCountGlobal(EMovableType.MINER)
				+ movableCounts.getMovableCountGlobal(EMovableType.SLAUGHTERER)
				+ movableCounts.getMovableCountGlobal(EMovableType.MILLER)
				+ movableCounts.getMovableCountGlobal(EMovableType.SMITH)
				+ movableCounts.getMovableCountGlobal(EMovableType.FORESTER)
				+ movableCounts.getMovableCountGlobal(EMovableType.MELTER)
				+ movableCounts.getMovableCountGlobal(EMovableType.WINEGROWER)
				+ movableCounts.getMovableCountGlobal(EMovableType.CHARCOAL_BURNER)
				+ movableCounts.getMovableCountGlobal(EMovableType.STONECUTTER);

		int civilianCount = genericWorker
				+ movableCounts.getMovableCountGlobal(EMovableType.BEARER)
				+ movableCounts.getMovableCountGlobal(EMovableType.DIGGER)
				+ movableCounts.getMovableCountGlobal(EMovableType.BRICKLAYER);

		for(UIElement element : panel.getChildren()) {
			if(element instanceof NamedLabel) {
				NamedLabel label = (NamedLabel)element;
				String name = label.getName();

				if(name == "stat_building") {
					label.setText("-");
				} else if(name == "stat_civilian") {
					label.setText(String.valueOf(civilianCount));
				} else if(name == "stat_total") {
					label.setText(String.valueOf(civilianCount+soldierCount));
				} else if(name == "stat_soldier") {
					label.setText(String.valueOf(soldierCount));
				} else if(name == "stat_bearer") {
					label.setText(getMovableCountAsString(EMovableType.BEARER));
				} else if(name == "stat_digger") {
					label.setText(getMovableCountAsString(EMovableType.DIGGER));
				} else if(name == "stat_builder") {
					label.setText(getMovableCountAsString(EMovableType.BRICKLAYER));
				} else if(name == "stat_other") {
					label.setText(String.valueOf(genericWorker));
				} else if(name == "stat_swordsman") {
					int count = movableCounts.getMovableCountGlobal(EMovableType.SWORDSMAN_L1)
							+ movableCounts.getMovableCountGlobal(EMovableType.SWORDSMAN_L2)
							+ movableCounts.getMovableCountGlobal(EMovableType.SWORDSMAN_L3);
					label.setText(String.valueOf(count));
				} else if(name == "stat_bowman") {
					int count = movableCounts.getMovableCountGlobal(EMovableType.BOWMAN_L1)
							+ movableCounts.getMovableCountGlobal(EMovableType.BOWMAN_L2)
							+ movableCounts.getMovableCountGlobal(EMovableType.BOWMAN_L3);
					label.setText(String.valueOf(count));
				} else if(name == "stat_pikeman") {
					int count = movableCounts.getMovableCountGlobal(EMovableType.PIKEMAN_L1)
							+ movableCounts.getMovableCountGlobal(EMovableType.PIKEMAN_L2)
							+ movableCounts.getMovableCountGlobal(EMovableType.PIKEMAN_L3);
					label.setText(String.valueOf(count));
				} else if(name == "stat_mage") {
					label.setText(getMovableCountAsString(EMovableType.MAGE));
				} else if(name == "stat_geo") {
					label.setText(getMovableCountAsString(EMovableType.GEOLOGIST));
				} else if(name == "stat_thief") {
					label.setText(getMovableCountAsString(EMovableType.THIEF));
				} else if(name == "stat_pioneer") {
					label.setText(getMovableCountAsString(EMovableType.PIONEER));
				} else if(name == "stat_animals") {
					label.setText(getMovableCountAsString(EMovableType.DONKEY));
				}
			}
		}
	}

	public static class NamedLabel extends Label {
		private String name;

		public NamedLabel(String name) {
			super("0", EFontSize.NORMAL);
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	}
}
