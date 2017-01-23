package jsettlers.main.android.gameplay.ui.fragments.menus.selection.features;

import android.support.design.widget.Snackbar;
import android.view.View;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ActionListener;
import jsettlers.main.android.core.controls.TaskControls;
import jsettlers.main.android.gameplay.ui.customviews.InGameButton;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

/**
 * Created by tompr on 11/01/2017.
 */

public class WorkAreaFeature extends SelectionFeature implements ActionListener {
    private static final String image = "original_3_GUI_201";

    private final ActionControls actionControls;
    private final TaskControls taskControls;

    private Snackbar snackbar;

    public WorkAreaFeature(View view, IBuilding building, MenuNavigator menuNavigator, ActionControls actionControls, TaskControls taskControls) {
        super(view, building, menuNavigator);
        this.actionControls = actionControls;
        this.taskControls = taskControls;
    }

    @Override
    public void initialize(BuildingState buildingState) {
        super.initialize(buildingState);
        InGameButton workAreaButton = (InGameButton) getView().findViewById(R.id.image_view_work_area);
        workAreaButton.setVisibility(View.VISIBLE);

        ImageLink imageLink = ImageLink.fromName(image, 0);
        OriginalImageProvider.get(imageLink).setAsImage(workAreaButton.getImageView());

        workAreaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                actionControls.fireAction(new Action(EActionType.ASK_SET_WORK_AREA));
            }
        });

        actionControls.addActionListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        actionControls.removeActionListener(this);
        dismissSnackbar();
    }

    @Override
    public void actionFired(IAction action) {
        switch (action.getActionType()) {
            case ASK_SET_WORK_AREA:
                snackbar = Snackbar
                        .make(getView(), "Choose work area", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                taskControls.endTask();
                            }
                        });
                snackbar.show();
                break;
            case SET_WORK_AREA:
            case ABORT:
                dismissSnackbar();
        }
    }

    private void dismissSnackbar() {
        if (snackbar != null) {
            snackbar.dismiss();
            snackbar = null;
        }
    }
}
