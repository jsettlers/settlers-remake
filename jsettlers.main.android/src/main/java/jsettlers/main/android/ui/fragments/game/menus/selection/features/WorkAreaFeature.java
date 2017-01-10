package jsettlers.main.android.ui.fragments.game.menus.selection.features;

import android.view.View;
import android.widget.ImageView;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.images.ImageLink;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.ControlsAdapter;

/**
 * Created by tompr on 11/01/2017.
 */

public class WorkAreaFeature extends SelectionFeature {
    public WorkAreaFeature(IBuilding building, ControlsAdapter controls, View view) {
        super(building, controls, view);
    }

    @Override
    public void initialize(BuildingState buildingState, ControlsAdapter controls) {
        super.initialize(buildingState, controls);
        ImageView imageView = (ImageView) getView().findViewById(R.id.image_view_work_area);
        imageView.setVisibility(View.VISIBLE);

        ImageLink imageLink = ImageLink.fromName("original_3_GUI_201", 0);
        OriginalImageProvider.get(imageLink).setAsImage(imageView);
    }
}
