package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.controls.DrawListener;
import jsettlers.main.android.ui.navigation.MenuNavigator;

/**
 * Created by tompr on 10/01/2017.
 */

public class TitleFeature extends SelectionFeature implements DrawListener {
    private TextView nameTextView;

    public TitleFeature(IBuilding building, ControlsAdapter controls, MenuNavigator menuNavigator, View view) {
        super(building, controls, menuNavigator, view);
    }

    @Override
    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        super.initialize(buildingState, controls);

        nameTextView = (TextView) getView().findViewById(R.id.text_view_name);
        ImageView imageView = (ImageView) getView().findViewById(R.id.image_view);

        String name = Labels.getName(getBuilding().getBuildingType());
        if (getBuildingState().isConstruction()) {
            name = Labels.getString("building-build-in-progress", name);
            getControls().addDrawListener(this);
        }

        nameTextView.setText(name);
        OriginalImageProvider.get(getBuilding().getBuildingType()).setAsImage(imageView);
    }

    @Override
    public void finish() {
        super.finish();
        getControls().removeDrawListener(this);
    }

    @Override
    public void draw() {
        if (!getBuildingState().isStillInState(getBuilding())) {
            setBuildingState(new BuildingState(getBuilding()));
            if (!getBuildingState().isConstruction()) {
                String name = Labels.getName(getBuilding().getBuildingType());
                nameTextView.setText(name);
                getControls().removeDrawListener(this);
            }
        }
    }
}
