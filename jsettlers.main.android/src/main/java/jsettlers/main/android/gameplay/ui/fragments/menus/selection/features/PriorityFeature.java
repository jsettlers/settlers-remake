package jsettlers.main.android.gameplay.ui.fragments.menus.selection.features;

import android.view.View;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.common.material.EPriority;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.graphics.action.SetBuildingPriorityAction;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ActionControls;
import jsettlers.main.android.controls.ActionListener;
import jsettlers.main.android.controls.DrawControls;
import jsettlers.main.android.controls.DrawListener;
import jsettlers.main.android.gameplay.ui.customviews.InGameButton;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

/**
 * Created by tompr on 10/01/2017.
 */

public class PriorityFeature extends SelectionFeature implements ActionListener, DrawListener {
    private static final String stoppedImage = "original_3_GUI_192";
    private static final String lowImage = "original_3_GUI_195";
    private static final String highImage = "original_3_GUI_378";
    private final ActionControls actionControls;
    private final DrawControls drawControls;

    private InGameButton priorityButton;

    public PriorityFeature(View view, IBuilding building, MenuNavigator menuNavigator, ActionControls actionControls, DrawControls drawControls) {
        super(view, building, menuNavigator);
        this.actionControls = actionControls;
        this.drawControls = drawControls;
    }

    @Override
    public void initialize(BuildingState buildingState) {
        super.initialize(buildingState);

        EPriority[] supportedPriorities = getBuildingState().getSupportedPriorities();
        if (supportedPriorities.length > 1) {
            priorityButton = (InGameButton) getView().findViewById(R.id.image_view_priority);
            priorityButton.setVisibility(View.VISIBLE);

            setImageForPriority(getBuilding().getPriority());

            priorityButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionControls.fireAction(new SetBuildingPriorityAction(getNextPriority()));
                }
            });
        }

        actionControls.addActionListener(this);
        drawControls.addDrawListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        actionControls.removeActionListener(this);
        drawControls.removeDrawListener(this);
    }

    @Override
    public void actionFired(IAction action) {
        if (action.getActionType() == EActionType.SET_BUILDING_PRIORITY) {
            SetBuildingPriorityAction priorityAction = (SetBuildingPriorityAction) action;
            setImageForPriority(priorityAction.getNewPriority());
        }
    }

    @Override
    public void draw() {
        if (hasNewState()) {
            if (getBuildingState().getSupportedPriorities().length <= 1) {
                getView().post(new Runnable() {
                    @Override
                    public void run() {
                        priorityButton.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }
    }

    private EPriority getNextPriority() {
        EPriority[] supported = getBuildingState().getSupportedPriorities();
        EPriority current = getBuilding().getPriority();

        EPriority next = supported[0];
        for (int i = 0; i < supported.length; i++) {
            if (supported[i] == current) {
                next = supported[(i + 1) % supported.length];
            }
        }
        return next;
    }

    private void setImageForPriority(EPriority priority) {
        ImageLink imageLink = getImageLink(priority);

        getView().post(new Runnable() {
            @Override
            public void run() {
                OriginalImageProvider.get(imageLink).setAsImage(priorityButton.getImageView());
            }
        });
    }

    private ImageLink getImageLink(EPriority priority) {
        switch (priority) {
            case STOPPED:
                return ImageLink.fromName(stoppedImage, 0);
            case LOW:
                return ImageLink.fromName(lowImage, 0);
            case HIGH:
                return ImageLink.fromName(highImage, 0);
            default:
                throw new RuntimeException("Image not found for priority " + priority.name());
        }
    }
}
