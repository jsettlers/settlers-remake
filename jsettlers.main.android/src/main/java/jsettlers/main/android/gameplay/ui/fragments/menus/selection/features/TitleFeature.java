package jsettlers.main.android.gameplay.ui.fragments.menus.selection.features;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.androidui.utils.OriginalImageProvider;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.controls.DrawControls;
import jsettlers.main.android.controls.DrawListener;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by tompr on 10/01/2017.
 */

public class TitleFeature extends SelectionFeature implements DrawListener {
	private final DrawControls drawControls;

	private TextView nameTextView;

	public TitleFeature(View view, IBuilding building, MenuNavigator menuNavigator, DrawControls drawControls) {
		super(view, building, menuNavigator);
		this.drawControls = drawControls;
	}

	@Override
	public void initialize(BuildingState buildingState) {
		super.initialize(buildingState);

		nameTextView = (TextView) getView().findViewById(R.id.text_view_building_name);
		ImageView imageView = (ImageView) getView().findViewById(R.id.image_view_building);

		String name = Labels.getName(getBuilding().getBuildingType());
		if (getBuildingState().isConstruction()) {
			name = Labels.getString("building-build-in-progress", name);
			drawControls.addDrawListener(this);
		}

		nameTextView.setText(name);
		OriginalImageProvider.get(getBuilding().getBuildingType()).setAsImage(imageView);
	}

	@Override
	public void finish() {
		super.finish();
		drawControls.removeDrawListener(this);
	}

	@Override
	public void draw() {
		if (hasNewState()) {

			getView().post(new Runnable() {
				@Override
				public void run() {
					if (!getBuildingState().isConstruction()) {
						String name = Labels.getName(getBuilding().getBuildingType());
						nameTextView.setText(name);
						drawControls.removeDrawListener(TitleFeature.this);
					}
				}
			});
		}
	}
}
