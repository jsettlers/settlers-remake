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

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.player.EManaType;
import jsettlers.common.player.IManaInformation;
import jsettlers.graphics.action.UpgradeSoldiersAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.ui.Button;
import jsettlers.graphics.ui.UIElement;
import jsettlers.graphics.ui.UIPanel;
import jsettlers.graphics.ui.layout.WarriorsLayout;

/**
 *
 * @author codingberlin
 */
public class WarriorsPanel extends AbstractContentProvider {

	private UIPanel panel;

	public WarriorsPanel() {
	}

	public void setManaInformation(IManaInformation manaInformation) {
		panel = new WarriorsLayout()._root;
		for (UIElement element : panel.getChildren()) {
			if (element instanceof UpgradeButton) {
				((UpgradeButton) element).setManaInformation(manaInformation);
			}
		}
	}

	@Override
	public ESecondaryTabType getTabs() {
		return ESecondaryTabType.SETTLERS;
	}

	@Override
	public UIPanel getPanel() {
		return panel;
	}

	/**
	 * This is a button that displays the upgrade possibility of a mana type.
	 *
	 * @author Michael Zangl
	 */
	public static class UpgradeButton extends Button {

		private final EManaType manaType;
		private IManaInformation manaInformation;
		private final ImageLink[] imageLinksActive;
		private final ImageLink[] imageLinksInActive;

		public UpgradeButton(EManaType manaType) {
			super(new UpgradeSoldiersAction(manaType), null, null, "");
			this.manaType = manaType;
			switch(manaType) {
			case SWORDSMEN:
				imageLinksActive = new OriginalImageLink[] {new OriginalImageLink(EImageLinkType.GUI, 3, 398-3, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 404-3, 0)};
				imageLinksInActive = new OriginalImageLink[] {new OriginalImageLink(EImageLinkType.GUI, 3, 401-3, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 407-3, 0)};
				break;
			case BOWMEN:
				imageLinksActive = new OriginalImageLink[] {new OriginalImageLink(EImageLinkType.GUI, 3, 150, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 150, 0)};
				imageLinksInActive = new OriginalImageLink[] {new OriginalImageLink(EImageLinkType.GUI, 3, 150, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 150, 0)};
				break;
			default:
				imageLinksActive = new OriginalImageLink[] {new OriginalImageLink(EImageLinkType.GUI, 3, 150, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 150, 0)};
				imageLinksInActive = new OriginalImageLink[] {new OriginalImageLink(EImageLinkType.GUI, 3, 150, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 150, 0)};
				break;
			}

		}

		public void setManaInformation(IManaInformation manaInformation) {
			this.manaInformation = manaInformation;
		}

		@Override
		public boolean isActive() {
			return manaInformation.isUpgradePossible(manaType);
		}

		@Override
		protected ImageLink getBackgroundImage() {
			if (isActive()) {
				return imageLinksActive[manaInformation.getLevel(manaType)];
			} else {
				return imageLinksInActive[manaInformation.getLevel(manaType)];
			}
		}

		@Override
		public String getDescription(float relativex, float relativey) {
			return Labels.getString("upgrade_warriors", Labels.getName(manaType));
		}

	}
}
