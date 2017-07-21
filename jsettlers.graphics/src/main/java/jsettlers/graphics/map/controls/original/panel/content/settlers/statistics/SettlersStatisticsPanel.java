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
package jsettlers.graphics.map.controls.original.panel.content.settlers.statistics;

import go.graphics.text.EFontSize;
import jsettlers.common.movable.EMovableType;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.player.ISettlerInformation;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiContentUpdater.IUiContentReceiver;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiPlayerDependingContentUpdater;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.UIElement;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.StatisticLayoutRomans;

/**
 * The ingame settler statistics panel
 *
 * @author codingberlin
 * @author nptr
 * @author Andreas Eberle
 */
public class SettlersStatisticsPanel extends AbstractContentProvider implements IUiContentReceiver<ISettlerInformation> {

	private final UIPanel panel = new StatisticLayoutRomans()._root;
	private final UiPlayerDependingContentUpdater<ISettlerInformation> uiContentUpdater = new UiPlayerDependingContentUpdater<>(IInGamePlayer::getSettlerInformation);

	public SettlersStatisticsPanel() {
		uiContentUpdater.addListener(this);
	}

	public void setPlayer(IInGamePlayer player) {
		uiContentUpdater.updatePlayer(player);
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.SETTLERS;
	}

	private static String getMovableCountAsString(ISettlerInformation settlerInformation, EMovableType type) {
		return String.valueOf(settlerInformation.getMovableCount(type));
	}

	@Override
	public void update(ISettlerInformation settlerInformation) {
		int soldierCount = calculateSoldiersCount(settlerInformation);
		int genericWorker = calculateGenericWorkersCount(settlerInformation);
		int civilianCount = calculateCiviliansCount(settlerInformation, genericWorker);

		for (UIElement element : panel.getChildren()) {
			if (element instanceof NamedLabel) {
				NamedLabel label = (NamedLabel) element;
				String name = label.getName();

				switch (name) {
				case "stat_beds":
					label.setText("-"); // there is not concept of "beds" yet
					break;
				case "stat_civilian":
					label.setText(String.valueOf(civilianCount));
					break;
				case "stat_total":
					label.setText(String.valueOf(civilianCount + soldierCount));
					break;
				case "stat_soldier":
					label.setText(String.valueOf(soldierCount));
					break;
				case "stat_bearer":
					label.setText(getMovableCountAsString(settlerInformation, EMovableType.BEARER));
					break;
				case "stat_digger":
					label.setText(getMovableCountAsString(settlerInformation, EMovableType.DIGGER));
					break;
				case "stat_builder":
					label.setText(getMovableCountAsString(settlerInformation, EMovableType.BRICKLAYER));
					break;
				case "stat_other":
					label.setText(String.valueOf(genericWorker));
					break;
				case "stat_swordsman": {
					int count = settlerInformation.getMovableCount(EMovableType.SWORDSMAN_L1)
							+ settlerInformation.getMovableCount(EMovableType.SWORDSMAN_L2)
							+ settlerInformation.getMovableCount(EMovableType.SWORDSMAN_L3);
					label.setText(String.valueOf(count));
					break;
				}
				case "stat_bowman": {
					int count = settlerInformation.getMovableCount(EMovableType.BOWMAN_L1)
							+ settlerInformation.getMovableCount(EMovableType.BOWMAN_L2)
							+ settlerInformation.getMovableCount(EMovableType.BOWMAN_L3);
					label.setText(String.valueOf(count));
					break;
				}
				case "stat_pikeman": {
					int count = settlerInformation.getMovableCount(EMovableType.PIKEMAN_L1)
							+ settlerInformation.getMovableCount(EMovableType.PIKEMAN_L2)
							+ settlerInformation.getMovableCount(EMovableType.PIKEMAN_L3);
					label.setText(String.valueOf(count));
					break;
				}
				case "stat_mage":
					label.setText(getMovableCountAsString(settlerInformation, EMovableType.MAGE));
					break;
				case "stat_geo":
					label.setText(getMovableCountAsString(settlerInformation, EMovableType.GEOLOGIST));
					break;
				case "stat_thief":
					label.setText(getMovableCountAsString(settlerInformation, EMovableType.THIEF));
					break;
				case "stat_pioneer":
					label.setText(getMovableCountAsString(settlerInformation, EMovableType.PIONEER));
					break;
				case "stat_animals":
					label.setText(getMovableCountAsString(settlerInformation, EMovableType.DONKEY));
					break;
				}
			}
		}
	}

	private int calculateSoldiersCount(ISettlerInformation settlerInformation) {
		return settlerInformation.getMovableCount(EMovableType.SWORDSMAN_L1)
				+ settlerInformation.getMovableCount(EMovableType.SWORDSMAN_L2)
				+ settlerInformation.getMovableCount(EMovableType.SWORDSMAN_L3)
				+ settlerInformation.getMovableCount(EMovableType.BOWMAN_L1)
				+ settlerInformation.getMovableCount(EMovableType.BOWMAN_L2)
				+ settlerInformation.getMovableCount(EMovableType.BOWMAN_L3)
				+ settlerInformation.getMovableCount(EMovableType.PIKEMAN_L1)
				+ settlerInformation.getMovableCount(EMovableType.PIKEMAN_L2)
				+ settlerInformation.getMovableCount(EMovableType.PIKEMAN_L3)
				+ settlerInformation.getMovableCount(EMovableType.MAGE);
	}

	private int calculateCiviliansCount(ISettlerInformation settlerInformation, int genericWorker) {
		return genericWorker
				+ settlerInformation.getMovableCount(EMovableType.BEARER)
				+ settlerInformation.getMovableCount(EMovableType.DIGGER)
				+ settlerInformation.getMovableCount(EMovableType.BRICKLAYER);
	}

	private int calculateGenericWorkersCount(ISettlerInformation settlerInformation) {
		return settlerInformation.getMovableCount(EMovableType.PIG_FARMER)
				+ settlerInformation.getMovableCount(EMovableType.FARMER)
				+ settlerInformation.getMovableCount(EMovableType.LUMBERJACK)
				+ settlerInformation.getMovableCount(EMovableType.SAWMILLER)
				+ settlerInformation.getMovableCount(EMovableType.FISHERMAN)
				+ settlerInformation.getMovableCount(EMovableType.WATERWORKER)
				+ settlerInformation.getMovableCount(EMovableType.BAKER)
				+ settlerInformation.getMovableCount(EMovableType.MINER)
				+ settlerInformation.getMovableCount(EMovableType.SLAUGHTERER)
				+ settlerInformation.getMovableCount(EMovableType.MILLER)
				+ settlerInformation.getMovableCount(EMovableType.SMITH)
				+ settlerInformation.getMovableCount(EMovableType.FORESTER)
				+ settlerInformation.getMovableCount(EMovableType.MELTER)
				+ settlerInformation.getMovableCount(EMovableType.WINEGROWER)
				+ settlerInformation.getMovableCount(EMovableType.CHARCOAL_BURNER)
				+ settlerInformation.getMovableCount(EMovableType.STONECUTTER);
	}

	@Override
	public void contentShowing(ActionFireable actionFireable) {
		uiContentUpdater.start();
	}

	@Override
	public void contentHiding(ActionFireable actionFireable, AbstractContentProvider nextContent) {
		uiContentUpdater.stop();
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
