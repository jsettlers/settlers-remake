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
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.utils.OriginalImageProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by tompr on 11/01/2017.
 */

public class MaterialsFeature extends SelectionFeature implements DrawListener {
	private final DrawControls drawControls;

	private LayoutInflater layoutInflater;
	private LinearLayout materialsLayout;

	private boolean hasPostConstructionMaterials = true;

	public MaterialsFeature(View view, IBuilding building, MenuNavigator menuNavigator, DrawControls drawControls) {
		super(view, building, menuNavigator);
		this.drawControls = drawControls;
	}

	@Override
	public void initialize(BuildingState buildingState) {
		super.initialize(buildingState);
		layoutInflater = LayoutInflater.from(getView().getContext());
		materialsLayout = (LinearLayout) getView().findViewById(R.id.layout_materials);

		if (getBuilding() instanceof IBuilding.IOccupied || getBuilding() instanceof IBuilding.IStock
				|| getBuilding() instanceof IBuilding.ITrading) {
			hasPostConstructionMaterials = false;
		}

		if (getBuildingState().isConstruction() || hasPostConstructionMaterials) {
			update();
		}

		drawControls.addDrawListener(this);
	}

	@Override
	public void finish() {
		super.finish();
		drawControls.removeDrawListener(this);
	}

	@Override
	public void draw() {
		// TODO would be more efficient to compare the stacks rather than the entire building state to avoid unnecessary work
		if (hasNewState()) {
			getView().post(new Runnable() {
				@Override
				public void run() {
					if (getBuildingState().isConstruction() || hasPostConstructionMaterials) {
						update();
					} else {
						materialsLayout.setVisibility(View.GONE);
					}
				}
			});
		}
	}

	private void update() {
		materialsLayout.setVisibility(View.VISIBLE);
		materialsLayout.removeAllViews();

		for (BuildingState.StackState materialStackState : getBuildingState().getStackStates()) {

			View materialItemView = layoutInflater.inflate(R.layout.view_material, materialsLayout, false);
			ImageView imageView = (ImageView) materialItemView.findViewById(R.id.image_view_material);
			TextView textView = (TextView) materialItemView.findViewById(R.id.text_view_material_count);

			textView.setText(materialStackState.getCount() + "");
			OriginalImageProvider.get(materialStackState.getType()).setAsImage(imageView);

			if (materialStackState.isOffering()) {
				materialsLayout.addView(materialItemView);
			} else {
				materialsLayout.addView(materialItemView, 0);
			}
		}
	}
}
