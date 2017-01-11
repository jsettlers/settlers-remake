package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.buildings.IBuildingMaterial;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.ui.navigation.MenuNavigator;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by tompr on 11/01/2017.
 */

public class ConstructionFeature extends SelectionFeature {
    public ConstructionFeature(IBuilding building, ControlsAdapter controls, MenuNavigator menuNavigator, View view) {
        super(building, controls, menuNavigator, view);
    }

    @Override
    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        super.initialize(buildingState, controls);

        LinearLayout constructionLayout = (LinearLayout) getView().findViewById(R.id.layout_construction);
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
    }
}
