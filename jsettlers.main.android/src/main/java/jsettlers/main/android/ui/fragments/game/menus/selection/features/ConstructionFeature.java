package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.controls.DrawListener;
import jsettlers.main.android.ui.navigation.MenuNavigator;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tompr on 11/01/2017.
 */

public class ConstructionFeature extends SelectionFeature implements DrawListener {
    private LinearLayout constructionLayout;
    private TextView planksTextView;
    private TextView stoneTextView;

    public ConstructionFeature(IBuilding building, ControlsAdapter controls, MenuNavigator menuNavigator, View view) {
        super(building, controls, menuNavigator, view);
    }

    @Override
    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        super.initialize(buildingState, controls);

        constructionLayout = (LinearLayout) getView().findViewById(R.id.layout_construction);
        planksTextView = (TextView) getView().findViewById(R.id.text_view_required_planks);
        stoneTextView = (TextView) getView().findViewById(R.id.text_view_required_stone);
        ImageView planksImageView = (ImageView) getView().findViewById(R.id.image_view_required_planks);
        ImageView stoneImageView = (ImageView) getView().findViewById(R.id.image_view_required_stone);

        constructionLayout.setVisibility(View.VISIBLE);

        for (IBuildingMaterial buildingMaterial : getBuilding ().getMaterials()) {
            switch (buildingMaterial.getMaterialType()) {
                case PLANK:
                    OriginalImageProvider.get(EMaterialType.PLANK).setAsImage(planksImageView);
                    break;
                case STONE:
                    OriginalImageProvider.get(EMaterialType.STONE).setAsImage(stoneImageView);
                    break;
            }
        }

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
        if (!getBuildingState().isStillInState(getBuilding())) {
            setBuildingState(new BuildingState(getBuilding()));

            getView().post(new Runnable() {
                @Override
                public void run() {
                    if (getBuildingState().isConstruction()) {
                        update();
                    } else {
                        constructionLayout.setVisibility(View.INVISIBLE);
                        getControls().removeDrawListener(ConstructionFeature.this);
                    }
                }
            });
        }
    }

    private void update() {
        for (BuildingState.StackState materialStackState : getBuildingState().getStackStates()) {
            switch (materialStackState.getType()) {
                case PLANK:
                    planksTextView.setText(materialStackState.getCount() + " needed");
                    break;
                case STONE:
                    stoneTextView.setText(materialStackState.getCount() + " needed");
                    break;
            }
        }
    }
}
