package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.common.menu.action.EActionType;
import jsettlers.common.menu.action.IAction;
import jsettlers.graphics.action.Action;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ActionListener;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.ui.navigation.MenuNavigator;

/**
 * Created by tompr on 10/01/2017.
 */

public class DestroyFeature extends SelectionFeature implements ActionListener {

    public DestroyFeature(IBuilding building, ControlsAdapter controls, MenuNavigator menuNavigator, View view) {
        super(building, controls, menuNavigator, view);
    }

    @Override
    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        super.initialize(buildingState, controls);
        ImageView imageView = (ImageView) getView().findViewById(R.id.image_view_destroy);
        imageView.setVisibility(View.VISIBLE);

        ImageLink imageLink = ImageLink.fromName("original_3_GUI_198", 0);
        OriginalImageProvider.get(imageLink).setAsImage(imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getControls().fireAction(new Action(EActionType.ASK_DESTROY));
            }
        });

        getControls().addActionListener(this);
    }

    @Override
    public void finish() {
        super.finish();
        getControls().removeActionListener(this);
    }

    @Override
    public void actionFired(IAction action) {
        if (action.getActionType() == EActionType.ASK_DESTROY) {
            Snackbar
                    .make(getView(), "Destroy this building?", Snackbar.LENGTH_SHORT)
                    .setAction("Yes", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getControls().fireAction(new Action(EActionType.DESTROY));
                            getMenuNavigator().dismissMenu();
                            getMenuNavigator().removeSelectionMenu();
                        }
                    })
                    .show();
        }
    }
}
