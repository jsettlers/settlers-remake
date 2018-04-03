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
package jsettlers.graphics.map.controls.original.panel.content.settlers.warriors;

import go.graphics.text.EFontSize;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.action.EActionType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.player.ICombatStrengthInformation;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.common.player.IMannaInformation;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.common.action.SoldierAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.content.AbstractContentProvider;
import jsettlers.graphics.map.controls.original.panel.content.ESecondaryTabType;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiContentUpdater.IUiContentReceiver;
import jsettlers.graphics.map.controls.original.panel.content.updaters.UiPlayerDependingContentUpdater;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.Label;
import jsettlers.graphics.ui.UIElement;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.WarriorsLayout;

/**
 * @author codingberlin
 */
public class WarriorsPanel extends AbstractContentProvider {

	interface IMannaInformationReceiver extends IUiContentReceiver<IMannaInformation> {
	}

	interface ICombatStrengthInformationReceiver extends IUiContentReceiver<ICombatStrengthInformation> {
	}

	private final UIPanel panel;
	private final UiPlayerDependingContentUpdater<IMannaInformation> mannaContentUpdater = new UiPlayerDependingContentUpdater<>(IInGamePlayer::getMannaInformation);
	private final UiPlayerDependingContentUpdater<ICombatStrengthInformation> combatStrengthContentUpdater = new UiPlayerDependingContentUpdater<>(IInGamePlayer::getCombatStrengthInformation);

	public WarriorsPanel() {
		panel = new WarriorsLayout()._root;

		for (final UIElement element : panel.getChildren()) {
			if (element instanceof IMannaInformationReceiver) {
				mannaContentUpdater.addListener((IMannaInformationReceiver) element);
			}
			if (element instanceof ICombatStrengthInformationReceiver) {
				combatStrengthContentUpdater.addListener((ICombatStrengthInformationReceiver) element);
			}
		}
	}

	public void setPlayer(IInGamePlayer player) {
		mannaContentUpdater.updatePlayer(player);
		combatStrengthContentUpdater.updatePlayer(player);
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.SETTLERS;
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

	@Override
	public void contentShowing(ActionFireable actionFireable) {
		mannaContentUpdater.start();
		combatStrengthContentUpdater.start();
	}

	@Override
	public void contentHiding(ActionFireable actionFireable, AbstractContentProvider nextContent) {
		mannaContentUpdater.stop();
		combatStrengthContentUpdater.stop();
	}

	/**
	 * This is a label that displays the upgrade progress percentage of the next upgrade.
	 *
	 * @author codingberlin
	 */
	public static class UpgradeProgressLabel extends Label implements IMannaInformationReceiver {

		public UpgradeProgressLabel() {
			super("", EFontSize.NORMAL);
		}

		@Override
		public void update(IMannaInformation mannaInformation) {
			setText(Labels.getString("upgrade_warriors_progress", mannaInformation.getNextUpdateProgressPercent()));
		}
	}

	/**
	 * This is a label that displays the combat strength percentage.
	 *
	 * @author codingberlin
	 */
	public static class CombatStrengthLabel extends Label implements ICombatStrengthInformationReceiver {

		public CombatStrengthLabel() {
			super("", EFontSize.NORMAL);
		}

		@Override
		public void update(ICombatStrengthInformation combatStrengthInformation) {
			setText(Labels.getString("combat_strength", (int) (combatStrengthInformation.getCombatStrength(false) * 100)));
		}
	}

	/**
	 * This is a button that displays the upgrade possibility of a manna type.
	 *
	 * @author codingberlin
	 */
	public static class UpgradeButton extends Button implements IMannaInformationReceiver {

		private final ESoldierType soldierType;
		private IMannaInformation mannaInformation;
		private final ImageLink[] imageLinksActive;
		private final ImageLink[] imageLinksInActive;

		public UpgradeButton(ESoldierType soldierType) {
			super(new SoldierAction(EActionType.UPGRADE_SOLDIERS, soldierType), null, null, "");
			this.soldierType = soldierType;

			switch (soldierType) {
			case SWORDSMAN:
				imageLinksActive = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 396, 0),
						new OriginalImageLink(EImageLinkType.GUI, 3, 402, 0) };
				imageLinksInActive = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 399, 0),
						new OriginalImageLink(EImageLinkType.GUI, 3, 405, 0) };
				break;
			case BOWMAN:
				imageLinksActive = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 408, 0),
						new OriginalImageLink(EImageLinkType.GUI, 3, 414, 0) };
				imageLinksInActive = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 411, 0),
						new OriginalImageLink(EImageLinkType.GUI, 3, 417, 0) };
				break;
			default:
				imageLinksActive = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 420, 0),
						new OriginalImageLink(EImageLinkType.GUI, 3, 426, 0) };
				imageLinksInActive = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 423, 0),
						new OriginalImageLink(EImageLinkType.GUI, 3, 429, 0) };
				break;
			}
		}

		@Override
		public boolean isActive() {
			return mannaInformation.isUpgradePossible(soldierType);
		}

		@Override
		protected ImageLink getBackgroundImage() {
			if (mannaInformation == null) {
				return null;
			}
			if (mannaInformation.getLevel(soldierType) >= mannaInformation.getMaximumLevel()) {
				return null;
			} else if (isActive()) {
				return imageLinksActive[mannaInformation.getLevel(soldierType)];
			} else {
				return imageLinksInActive[mannaInformation.getLevel(soldierType)];
			}
		}

		@Override
		public String getDescription(float relativeX, float relativeY) {
			return Labels.getString("upgrade_warriors", Labels.getName(soldierType));
		}

		@Override
		public void update(IMannaInformation mannaInformation) {
			this.mannaInformation = mannaInformation;
		}
	}
}
