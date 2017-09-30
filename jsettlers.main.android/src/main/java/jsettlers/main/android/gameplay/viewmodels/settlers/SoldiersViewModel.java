package jsettlers.main.android.gameplay.viewmodels.settlers;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import java8.util.function.Supplier;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.ImageLink;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.player.IInGamePlayer;
import jsettlers.graphics.action.SoldierAction;
import jsettlers.graphics.localization.Labels;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;

/**
 * Created by Tom Pratt on 30/09/2017.
 */

public class SoldiersViewModel extends ViewModel {
    private final ImageLink[] swordsmenPromotionPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 396, 0),
            new OriginalImageLink(EImageLinkType.GUI, 3, 402, 0), null };
    private final ImageLink[] swordsmenPromotionNotPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 399, 0),
            new OriginalImageLink(EImageLinkType.GUI, 3, 405, 0), null };
    private final ImageLink[] bowmenPromotionPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 408, 0),
            new OriginalImageLink(EImageLinkType.GUI, 3, 414, 0), null };
    private final ImageLink[] bowmenPromotionNotPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 411, 0),
            new OriginalImageLink(EImageLinkType.GUI, 3, 417, 0), null };
    private final ImageLink[] pikemenPromotionPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 420, 0),
            new OriginalImageLink(EImageLinkType.GUI, 3, 426, 0), null };
    private final ImageLink[] pikemenPromotionNotPossibleImages = new OriginalImageLink[] { new OriginalImageLink(EImageLinkType.GUI, 3, 423, 0),
            new OriginalImageLink(EImageLinkType.GUI, 3, 429, 0), null };

    private final ActionControls actionControls;
    private final DrawControls drawControls;
    private final IInGamePlayer player;

    private final DrawLiveData<String> strengthText;
    private final DrawLiveData<String> promotionText;
    private final DrawLiveData<Boolean> swordsmenPromotionEnabled;
    private final DrawLiveData<Boolean> bowmenPromotionEnabled;
    private final DrawLiveData<Boolean> pikemenPromotionEnabled;
    private final LiveData<ImageLink> swordsmenImageLink;
    private final LiveData<ImageLink> bowmenImageLink;
    private final LiveData<ImageLink> pikemenImageLink;

    public SoldiersViewModel(ActionControls actionControls, DrawControls drawControls, IInGamePlayer player) {
        this.actionControls = actionControls;
        this.drawControls = drawControls;
        this.player = player;

        strengthText = new DrawLiveData<>(this.drawControls, this::strengthText);
        promotionText = new DrawLiveData<>(this.drawControls, this::promotionText);
        swordsmenPromotionEnabled = new DrawLiveData<>(drawControls, () -> isPromotionPossible(ESoldierType.SWORDSMAN));
        bowmenPromotionEnabled = new DrawLiveData<>(drawControls, () -> isPromotionPossible(ESoldierType.BOWMAN));
        pikemenPromotionEnabled = new DrawLiveData<>(drawControls, () -> isPromotionPossible(ESoldierType.PIKEMAN));
        swordsmenImageLink = Transformations.map(swordsmenPromotionEnabled, isPromotionPossible -> getPromotionImageLink(isPromotionPossible, swordsmenPromotionPossibleImages, swordsmenPromotionNotPossibleImages, ESoldierType.SWORDSMAN));
        bowmenImageLink = Transformations.map(bowmenPromotionEnabled, isPromotionPossible -> getPromotionImageLink(isPromotionPossible, bowmenPromotionPossibleImages, bowmenPromotionNotPossibleImages, ESoldierType.BOWMAN));
        pikemenImageLink = Transformations.map(pikemenPromotionEnabled, isPromotionPossible -> getPromotionImageLink(isPromotionPossible, pikemenPromotionPossibleImages, pikemenPromotionNotPossibleImages, ESoldierType.PIKEMAN));
    }

    public LiveData<String> getStrengthText() {
        return strengthText;
    }

    public LiveData<String> getPromotionText() {
        return promotionText;
    }

    public LiveData<Boolean> getSwordsmenPromotionEnabled() {
        return swordsmenPromotionEnabled;
    }

    public LiveData<Boolean> getBowmenPromotionEnabled() {
        return bowmenPromotionEnabled;
    }

    public LiveData<Boolean> getPikemenPromotionEnabled() {
        return pikemenPromotionEnabled;
    }

    public LiveData<ImageLink> getSwordsmenImageLink() {
        return swordsmenImageLink;
    }

    public LiveData<ImageLink> getBowmenImageLink() {
        return bowmenImageLink;
    }

    public LiveData<ImageLink> getPikemenImageLink() {
        return pikemenImageLink;
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
            actionControls.fireAction(new SoldierAction(EActionType.UPGRADE_SOLDIERS, soldierType));
        }
    }

    private ImageLink getPromotionImageLink(boolean isPromotionPossible, ImageLink[] possibleImages, ImageLink[] notPossibleImages, ESoldierType soldierType) {
        if (isPromotionPossible) {
            return possibleImages[player.getMannaInformation().getLevel(soldierType)];
        } else {
            return notPossibleImages[player.getMannaInformation().getLevel(soldierType)];
        }
    }

    /**
     * LiveData class for production states
     */
    private static class DrawLiveData<T> extends LiveData<T> implements DrawListener {
        private final DrawControls drawControls;
        private final Supplier<T> supplier;

        public DrawLiveData(DrawControls drawControls, Supplier<T> supplier) {
            this.drawControls = drawControls;
            this.supplier = supplier;
            setValue(supplier.get());
        }

        @Override
        protected void onActive() {
            super.onActive();
            drawControls.addInfrequentDrawListener(this);
        }

        @Override
        protected void onInactive() {
            super.onInactive();
            drawControls.removeInfrequentDrawListener(this);
        }

        @Override
        public void draw() {
            postValue(supplier.get());
        }
    }

    /**
     * ViewModel factory
     */
    public static class Factory implements ViewModelProvider.Factory {
        private final ControlsResolver controlsResolver;

        public Factory(Activity activity) {
            this.controlsResolver = new ControlsResolver(activity);
        }

        @Override
        public <T extends ViewModel> T create(Class<T> modelClass) {
            if (modelClass == SoldiersViewModel.class) {
                return (T) new SoldiersViewModel(controlsResolver.getActionControls(), controlsResolver.getDrawControls(), controlsResolver.getPlayer());
            }
            throw new RuntimeException("SoldiersViewModel.Factory doesn't know how to create a: " + modelClass.toString());
        }
    }
}
