/*
 * Copyright (c) 2017
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

package jsettlers.main.android.gameplay.presenters;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.action.EActionType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.common.action.SoldierAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.core.utils.Dispatcher;
import jsettlers.main.android.gameplay.ui.views.SettlersSoldiersView;

/**
 * Created by tompr on 13/01/2017.
 */
public class SettlersSoldiersMenu implements DrawListener {
	private final ImageLink[] swordsmenPromotionPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 396, 0),
			new OriginalImageLink(EImageLinkType.GUI, 3, 402, 0), null };
	private final ImageLink[] swordsmenPromotionNotPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 399, 0),
			new OriginalImageLink(EImageLinkType.GUI, 3, 405, 0), null };
	private final ImageLink[] bowPromotionPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 408, 0),
			new OriginalImageLink(EImageLinkType.GUI, 3, 414, 0), null };
	private final ImageLink[] bowmenPromotionNotPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 411, 0),
			new OriginalImageLink(EImageLinkType.GUI, 3, 417, 0), null };
	private final ImageLink[] pikemenPromotionPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 420, 0),
			new OriginalImageLink(EImageLinkType.GUI, 3, 426, 0), null };
	private final ImageLink[] pikemenPromotionNotPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 423, 0),
			new OriginalImageLink(EImageLinkType.GUI, 3, 429, 0), null };

	private final SettlersSoldiersView view;
	private final ActionFireable actionFireable;
	private final DrawControls drawControls;
	private final IInGamePlayer player;
	private final Dispatcher dispatcher;

	public SettlersSoldiersMenu(SettlersSoldiersView view, ActionFireable actionFireable, DrawControls drawControls, IInGamePlayer player, Dispatcher dispatcher) {
		this.view = view;
		this.actionFireable = actionFireable;
		this.drawControls = drawControls;
		this.player = player;
		this.dispatcher = dispatcher;
	}

	public void start() {
		drawControls.addInfrequentDrawListener(this);
		updateView();
	}

	public void finish() {
		drawControls.removeInfrequentDrawListener(this);
	}

	public void swordsmenPromotionClicked() {
		promote(ESoldierType.SWORDSMAN);
	}

	public void bowmenPromotionClicked() {
		promote(ESoldierType.BOWMAN);
	}

	public void pikemenPromotionClicked() {
		promote(ESoldierType.PIKEMAN);
	}

	/**
	 * DrawListener implementation
	 */
	@Override
	public void draw() {
		dispatcher.runOnMainThread(this::updateView);
	}

	private void updateView() {
		view.setStrengthText(strengthText());
		view.setPromotionText(promotionText());
		view.setSwordsmenPromotionEnabled(isPromotionPossible(ESoldierType.SWORDSMAN));
		view.setBowmenPromotionEnabled(isPromotionPossible(ESoldierType.BOWMAN));
		view.setPikemenPromotionEnabled(isPromotionPossible(ESoldierType.PIKEMAN));
		view.setSwordsmenImage(getPromotionImageLink(swordsmenPromotionPossibleImages, swordsmenPromotionNotPossibleImages, ESoldierType.SWORDSMAN));
		view.setBowmenImage(getPromotionImageLink(bowPromotionPossibleImages, bowmenPromotionNotPossibleImages, ESoldierType.BOWMAN));
		view.setPikemenImage(getPromotionImageLink(pikemenPromotionPossibleImages, pikemenPromotionNotPossibleImages, ESoldierType.PIKEMAN));
	}

	private String strengthText() {
		return Labels.getString("combat_strength", (int) (player.getCombatStrengthInformation().getCombatStrength(false) * 100));
	}

	private String promotionText() {
		return Labels.getString("upgrade_warriors_progress", player.getMannaInformation().getNextUpdateProgressPercent());
	}

	private boolean isPromotionPossible(ESoldierType soldierType) {
		return player.getMannaInformation().isUpgradePossible(soldierType);
	}

	private void promote(ESoldierType soldierType) {
		if (isPromotionPossible(soldierType)) {
			actionFireable.fireAction(new SoldierAction(EActionType.UPGRADE_SOLDIERS, soldierType));
		}
	}

	private ImageLink getPromotionImageLink(ImageLink[] possibleImages, ImageLink[] notPossibleImages, ESoldierType soldierType) {
		if (isPromotionPossible(soldierType)) {
			return possibleImages[player.getMannaInformation().getLevel(soldierType)];
		} else {
			return notPossibleImages[player.getMannaInformation().getLevel(soldierType)];
		}
	}
}
