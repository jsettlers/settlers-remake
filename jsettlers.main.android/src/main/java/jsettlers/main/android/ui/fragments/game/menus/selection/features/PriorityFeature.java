package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import android.view.View;
import android.widget.ImageView;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.common.material.EPriority;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.graphics.action.SetBuildingPriorityAction;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ActionListener;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.ui.navigation.MenuNavigator;

/**
 * Created by tompr on 10/01/2017.
 */

public class PriorityFeature extends SelectionFeature implements ActionListener {
    private static final String stoppedImage = "original_3_GUI_192";
    private static final String lowImage = "original_3_GUI_195";
    private static final String highImage = "original_3_GUI_378";

    private ImageView imageView;

    public PriorityFeature(IBuilding building, ControlsAdapter controls, MenuNavigator menuNavigator, View view) {
        super(building, controls, menuNavigator, view);
    }

    @Override
    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        super.initialize(buildingState, controls);

        EPriority[] supportedPriorities = getBuildingState().getSupportedPriorities();
        if (supportedPriorities.length > 1) {
            imageView = (ImageView) getView().findViewById(R.id.image_view_priority);
            imageView.setVisibility(View.VISIBLE);

            setImageForPriority(getBuilding().getPriority());

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    controls.fireAction(new SetBuildingPriorityAction(getNextPriority()));
                }
            });
        }

        getControls().addActionListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        getControls().removeActionListener(this);
    }

    @Override
    public void actionFired(IAction action) {
        if (action.getActionType() == EActionType.SET_BUILDING_PRIORITY) {
            SetBuildingPriorityAction priorityAction = (SetBuildingPriorityAction) action;
            setImageForPriority(priorityAction.getNewPriority());
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
        OriginalImageProvider.get(imageLink).setAsImage(imageView);
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
