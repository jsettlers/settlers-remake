/*
 * Copyright (c) 2017
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package jsettlers.main.android.gameplay.ui.fragments.menus.selection.features;

import jsettlers.common.buildings.IBuilding;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import jsettlers.main.android.utils.OriginalImageProvider;

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

			getView().post(() -> {
				if (!getBuildingState().isConstruction()) {
					String name = Labels.getName(getBuilding().getBuildingType());
					nameTextView.setText(name);
					drawControls.removeDrawListener(TitleFeature.this);
				}
			});
		}
	}
}
