package jsettlers.main.android.menus;

import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.SoldierAction;
import jsettlers.graphics.localization.Labels;

/**
 * Created by tompr on 13/01/2017.
 */

public class SettlersSoldiersMenu {
    private final ImageLink[] swordsmenPromotionPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 396, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 402, 0), null };
    private final ImageLink[] swordsmenPromotionNotPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 399, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 405, 0), null };
    private final ImageLink[] bowPromotionPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 408, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 414, 0), null };
    private final ImageLink[] bowmenPromotionNotPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 411, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 417, 0), null };
    private final ImageLink[] pikemenPromotionPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 420, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 426, 0), null };
    private final ImageLink[] pikemenPromotionNotPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 423, 0), new OriginalImageLink(EImageLinkType.GUI, 3, 429, 0), null };

    private final ActionFireable actionFireable;
    private final IInGamePlayer player;

    public SettlersSoldiersMenu(ActionFireable actionFireable, IInGamePlayer player) {
        this.actionFireable = actionFireable;
        this.player = player;
    }

    public String getStrengthText() {
        return Labels.getString("combat_strength", (int) (player.getCombatStrengthInformation().getCombatStrength(false) * 100));
    }

    public String getPromotionText() {
        return Labels.getString("upgrade_warriros_progress", player.getManaInformation().getNextUpdateProgressPercent());
    }

    public boolean isSwordsmenPromotionPossible() {
        return isPromotionPossible(ESoldierType.SWORDSMAN);
    }

    public boolean isBowmenPromotionPossible() {
        return isPromotionPossible(ESoldierType.BOWMAN);
    }

    public boolean isPikemenPromotionPossible() {
        return isPromotionPossible(ESoldierType.PIKEMAN);
    }

    private boolean isPromotionPossible(ESoldierType soldierType) {
        return player.getManaInformation().isUpgradePossible(soldierType);

    }

    public ImageLink getSwordsmenPromotionImageLink() {
        return  getPromotionImageLink(swordsmenPromotionPossibleImages, swordsmenPromotionNotPossibleImages, ESoldierType.SWORDSMAN);
    }

    public ImageLink getBowmenPromotionImageLink() {
        return  getPromotionImageLink(bowPromotionPossibleImages, bowmenPromotionNotPossibleImages, ESoldierType.BOWMAN);
    }

    public ImageLink getPikemenPromotionImageLink() {
        return  getPromotionImageLink(pikemenPromotionPossibleImages, pikemenPromotionNotPossibleImages, ESoldierType.PIKEMAN);
    }

    private ImageLink getPromotionImageLink(ImageLink[] possibleImages, ImageLink[] notPossibleImages, ESoldierType soldierType) {
        if (isPromotionPossible(soldierType)) {
            return possibleImages[player.getManaInformation().getLevel(soldierType)];
        } else {
            return notPossibleImages[player.getManaInformation().getLevel(soldierType)];
        }
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

    private void promote(ESoldierType soldierType) {
        if (isPromotionPossible(soldierType)) {
            actionFireable.fireAction(new SoldierAction(EActionType.UPGRADE_SOLDIERS, soldierType));
        }
    }
}
