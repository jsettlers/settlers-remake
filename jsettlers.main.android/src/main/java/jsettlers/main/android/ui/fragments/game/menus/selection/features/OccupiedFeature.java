package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import android.util.Log;
import android.view.View;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsAdapter;
import jsettlers.main.android.ui.customviews.InGameButton;
import jsettlers.main.android.ui.navigation.MenuNavigator;

/**
 * Created by tompr on 12/01/2017.
 */

public class OccupiedFeature extends SelectionFeature {
    private static final String imageMaximumSoldiers = "original_3_GUI_39";
    private static final String imageAddSwordsman = "original_3_GUI_27";
    private static final String imageAddBowman = "original_3_GUI_21";
    private static final String imageAddPikeman = "original_3_GUI_24";
    private static final String imageMinimumSoldiers = "original_3_GUI_42";
    private static final String imageRemoveSwordsman = "original_3_GUI_36";
    private static final String imageRemoveBowman = "original_3_GUI_30";
    private static final String imageRemovePikeman = "original_3_GUI_33";

    private InGameButton maximumSoldiersButton;
    private InGameButton addSwordsmanButton;
    private InGameButton addBowmanButton;
    private InGameButton addPikemanButton;
    private InGameButton minimumSolidersButton;
    private InGameButton removeSwordsmanButton;
    private InGameButton removeBowmanButton;
    private InGameButton removePikemanButton;

    public OccupiedFeature(IBuilding building, ControlsAdapter controls, MenuNavigator menuNavigator, View view) {
        super(building, controls, menuNavigator, view);
    }

    @Override
    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        super.initialize(buildingState, controls);
        maximumSoldiersButton = (InGameButton) getView().findViewById(R.id.image_view_maximum_soldiers);
        addSwordsmanButton = (InGameButton) getView().findViewById(R.id.image_view_add_swordsman);
        addBowmanButton = (InGameButton) getView().findViewById(R.id.image_view_add_bowman);
        addPikemanButton = (InGameButton) getView().findViewById(R.id.image_view_add_pikeman);
        minimumSolidersButton = (InGameButton) getView().findViewById(R.id.image_view_minimum_soldiers);
        removeSwordsmanButton = (InGameButton) getView().findViewById(R.id.image_view_remove_swordsman);
        removeBowmanButton = (InGameButton) getView().findViewById(R.id.image_view_remove_bowman);
        removePikemanButton = (InGameButton) getView().findViewById(R.id.image_view_remove_pikeman);

        OriginalImageProvider.get(imageMaximumSoldiers).setAsImage(maximumSoldiersButton.getImageView());
        OriginalImageProvider.get(imageAddSwordsman).setAsImage(addSwordsmanButton.getImageView());
        OriginalImageProvider.get(imageAddBowman).setAsImage(addBowmanButton.getImageView());
        OriginalImageProvider.get(imageAddPikeman).setAsImage(addPikemanButton.getImageView());
        OriginalImageProvider.get(imageMinimumSoldiers).setAsImage(minimumSolidersButton.getImageView());
        OriginalImageProvider.get(imageRemoveSwordsman).setAsImage(removeSwordsmanButton.getImageView());
        OriginalImageProvider.get(imageRemoveBowman).setAsImage(removeBowmanButton.getImageView());
        OriginalImageProvider.get(imageRemovePikeman).setAsImage(removePikemanButton.getImageView());

        maximumSoldiersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("TEST", "**************************************");
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }
}
