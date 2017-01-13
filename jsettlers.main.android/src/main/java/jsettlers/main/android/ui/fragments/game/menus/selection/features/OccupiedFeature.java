package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import java.util.List;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.EImageLinkType;
import jsettlers.common.images.OriginalImageLink;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.movable.ESoldierClass;
import jsettlers.common.movable.ESoldierType;
import jsettlers.common.movable.IMovable;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.action.ActionFireable;
import jsettlers.graphics.action.SoldierAction;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ActionClickListener;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.controls.DrawListener;
import jsettlers.main.android.ui.customviews.InGameButton;
import jsettlers.main.android.ui.navigation.MenuNavigator;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;

/**
 * Created by tompr on 12/01/2017.
 */

public class OccupiedFeature extends SelectionFeature implements DrawListener {
    private static final OriginalImageLink SOILDER_MISSING = new OriginalImageLink(EImageLinkType.GUI, 3, 45, 0);
    private static final OriginalImageLink SOILDER_COMING = new OriginalImageLink(EImageLinkType.GUI, 3, 48, 0);

    private static final String imageMaximumSoldiers = "original_3_GUI_39";
    private static final String imageAddSwordsman = "original_3_GUI_27";
    private static final String imageAddBowman = "original_3_GUI_21";
    private static final String imageAddPikeman = "original_3_GUI_24";
    private static final String imageMinimumSoldiers = "original_3_GUI_42";
    private static final String imageRemoveSwordsman = "original_3_GUI_36";
    private static final String imageRemoveBowman = "original_3_GUI_30";
    private static final String imageRemovePikeman = "original_3_GUI_33";

    private TableLayout controlsLayout;
    private LinearLayout infantryLayout;
    private LinearLayout bowmenLayout;

    private LinearLayout.LayoutParams occupiedLayoutParams;
    private LinearLayout.LayoutParams waitingLayoutParams;

    public OccupiedFeature(IBuilding building, ControlsAdapter controls, MenuNavigator menuNavigator, View view) {
        super(building, controls, menuNavigator, view);
    }

    @Override
    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        super.initialize(buildingState, controls);

        InGameButton maximumSoldiersButton = (InGameButton) getView().findViewById(R.id.image_view_maximum_soldiers);
        InGameButton addSwordsmanButton = (InGameButton) getView().findViewById(R.id.image_view_add_swordsman);
        InGameButton addBowmanButton = (InGameButton) getView().findViewById(R.id.image_view_add_bowman);
        InGameButton addPikemanButton = (InGameButton) getView().findViewById(R.id.image_view_add_pikeman);
        InGameButton minimumSolidersButton = (InGameButton) getView().findViewById(R.id.image_view_minimum_soldiers);
        InGameButton removeSwordsmanButton = (InGameButton) getView().findViewById(R.id.image_view_remove_swordsman);
        InGameButton removeBowmanButton = (InGameButton) getView().findViewById(R.id.image_view_remove_bowman);
        InGameButton removePikemanButton = (InGameButton) getView().findViewById(R.id.image_view_remove_pikeman);

        controlsLayout = (TableLayout) getView().findViewById(R.id.layout_occupier_controls);
        infantryLayout = (LinearLayout) getView().findViewById(R.id.layout_infantry);
        bowmenLayout = (LinearLayout) getView().findViewById(R.id.layout_bowmen);

        OriginalImageProvider.get(imageMaximumSoldiers).setAsImage(maximumSoldiersButton.getImageView());
        OriginalImageProvider.get(imageAddSwordsman).setAsImage(addSwordsmanButton.getImageView());
        OriginalImageProvider.get(imageAddBowman).setAsImage(addBowmanButton.getImageView());
        OriginalImageProvider.get(imageAddPikeman).setAsImage(addPikemanButton.getImageView());
        OriginalImageProvider.get(imageMinimumSoldiers).setAsImage(minimumSolidersButton.getImageView());
        OriginalImageProvider.get(imageRemoveSwordsman).setAsImage(removeSwordsmanButton.getImageView());
        OriginalImageProvider.get(imageRemoveBowman).setAsImage(removeBowmanButton.getImageView());
        OriginalImageProvider.get(imageRemovePikeman).setAsImage(removePikemanButton.getImageView());

        occupiedLayoutParams = new LinearLayout.LayoutParams(0, 0);
        occupiedLayoutParams.weight = 1;
        occupiedLayoutParams.height = getContext().getResources().getDimensionPixelSize(R.dimen.menu_tile_occupied_height);

        waitingLayoutParams = new LinearLayout.LayoutParams(0, 0);
        waitingLayoutParams.weight = 1;
        waitingLayoutParams.height = getContext().getResources().getDimensionPixelSize(R.dimen.menu_tile_waiting_height);

        maximumSoldiersButton.setOnClickListener(new ActionClickListener(getControls(), EActionType.SOLDIERS_ALL));
        addSwordsmanButton.setOnClickListener(new SingleSoldierClickListener(getControls(), EActionType.SOLDIERS_MORE, ESoldierType.SWORDSMAN));
        addBowmanButton.setOnClickListener(new SingleSoldierClickListener(getControls(), EActionType.SOLDIERS_MORE, ESoldierType.BOWMAN));
        addPikemanButton.setOnClickListener(new SingleSoldierClickListener(getControls(), EActionType.SOLDIERS_MORE, ESoldierType.PIKEMAN));

        minimumSolidersButton.setOnClickListener(new ActionClickListener(getControls(), EActionType.SOLDIERS_ONE));
        removeSwordsmanButton.setOnClickListener(new SingleSoldierClickListener(getControls(), EActionType.SOLDIERS_LESS, ESoldierType.SWORDSMAN));
        removeBowmanButton.setOnClickListener(new SingleSoldierClickListener(getControls(), EActionType.SOLDIERS_LESS, ESoldierType.BOWMAN));
        removePikemanButton.setOnClickListener(new SingleSoldierClickListener(getControls(), EActionType.SOLDIERS_LESS, ESoldierType.PIKEMAN));

        update();
        getControls().addDrawListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        getControls().removeDrawListener(this);
    }

    @Override
    public void draw() {
        if (hasNewState()) {
            getView().post(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }
    }

    private void update() {
        if (getBuildingState().isOccupied()) {
            controlsLayout.setVisibility(View.VISIBLE);
            infantryLayout.removeAllViews();
            bowmenLayout.removeAllViews();
            addOccupiers(infantryLayout, getBuildingState().getOccupiers(ESoldierClass.INFANTRY));
            addOccupiers(bowmenLayout, getBuildingState().getOccupiers(ESoldierClass.BOWMAN));
        }
    }

    private void addOccupiers(LinearLayout container, List<BuildingState.OccupierState> occupierStates) {
        for (BuildingState.OccupierState occupierState : occupierStates) {
            ImageView occupierImageView = new ImageView(getContext());

            if (occupierState.isMissing()) {
                OriginalImageProvider.get(SOILDER_MISSING).setAsImage(occupierImageView);
                occupierImageView.setLayoutParams(waitingLayoutParams);
            } else if (occupierState.isComming()) {
                OriginalImageProvider.get(SOILDER_COMING).setAsImage(occupierImageView);
                occupierImageView.setLayoutParams(waitingLayoutParams);
            } else {
                OriginalImageProvider.get(getIconFor(occupierState.getMovable())).setAsImage(occupierImageView);
                occupierImageView.setLayoutParams(occupiedLayoutParams);
            }

            container.addView(occupierImageView);
        }
    }

    private static OriginalImageLink getIconFor(IMovable movable) {
        switch (movable.getMovableType()) {
            case SWORDSMAN_L1:
                return new OriginalImageLink(EImageLinkType.GUI, 14, 207, 0);
            case SWORDSMAN_L2:
                return new OriginalImageLink(EImageLinkType.GUI, 14, 216, 0);
            case SWORDSMAN_L3:
                return new OriginalImageLink(EImageLinkType.GUI, 14, 225, 0);
            case PIKEMAN_L1:
                return new OriginalImageLink(EImageLinkType.GUI, 14, 210, 0);
            case PIKEMAN_L2:
                return new OriginalImageLink(EImageLinkType.GUI, 14, 219, 0);
            case PIKEMAN_L3:
                return new OriginalImageLink(EImageLinkType.GUI, 14, 228, 0);
            case BOWMAN_L1:
                return new OriginalImageLink(EImageLinkType.GUI, 14, 213, 0);
            case BOWMAN_L2:
                return new OriginalImageLink(EImageLinkType.GUI, 14, 222, 0);
            case BOWMAN_L3:
                return new OriginalImageLink(EImageLinkType.GUI, 14, 231, 0);
            default:
                System.err.println("A unknown image was requested for gui. " + "Type=" + movable.getMovableType());
                return new OriginalImageLink(EImageLinkType.GUI, 24, 213, 0);
        }
    }

    private class SingleSoldierClickListener extends ActionClickListener {
        private SingleSoldierClickListener(ActionFireable actionFireable, EActionType actionType, ESoldierType soldierType) {
            super(actionFireable, new SoldierAction(actionType, soldierType));
        }
    }
}
