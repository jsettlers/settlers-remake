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

import static java8.util.J8Arrays.stream;

import java.util.List;

import jsettlers.common.buildings.IBuilding;
import jsettlers.common.map.partition.IStockSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.action.SetAcceptedStockMaterialAction;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.ui.adapters.MaterialsAdapter;
import jsettlers.main.android.gameplay.viewstates.StockMaterialState;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import java8.util.stream.Collectors;

/**
 * Created by Tom Pratt on 10/01/2017.
 */
public class StockFeature extends SelectionFeature implements DrawListener {
	private final DrawControls drawControls;
	private final ActionControls actionControls;

	private final MaterialsAdapter materialsAdapter;
	private final RecyclerView recyclerView;
	private final ImageView buildingImageView;

	public StockFeature(Activity activity, View view, IBuilding building, MenuNavigator menuNavigator, DrawControls drawControls, ActionControls actionControls) {
		super(view, building, menuNavigator);
		this.drawControls = drawControls;
		this.actionControls = actionControls;

		buildingImageView = (ImageView) getView().findViewById(R.id.image_view_building);

		materialsAdapter = new MaterialsAdapter(activity);
		materialsAdapter.setItemClickListener(this::materialSelected);
		recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
		recyclerView.setHasFixedSize(true);
	}

	@Override
	public void initialize(BuildingState buildingState) {
		super.initialize(buildingState);
		drawControls.addInfrequentDrawListener(this);

		materialsAdapter.setMaterialStates(materialStates());
		recyclerView.setAdapter(materialsAdapter);

		update();
	}

	@Override
	public void finish() {
		super.finish();
		drawControls.removeInfrequentDrawListener(this);
	}

	@Override
	public void draw() {
		if (hasNewState()) {
			getView().post(this::update);
		}
	}

	private void materialSelected(StockMaterialState stockMaterialState) {
		actionControls.fireAction(new SetAcceptedStockMaterialAction(getBuilding().getPos(), stockMaterialState.getMaterialType(), !stockMaterialState.isStocked(), true));
	}

	private void update() {
		if (getBuildingState().isStock()) {
			recyclerView.setVisibility(View.VISIBLE);
			buildingImageView.setVisibility(View.INVISIBLE);

			materialsAdapter.setMaterialStates(materialStates());
		}
	}

	private List<StockMaterialState> materialStates() {
		IStockSettings stockSettings = ((IBuilding.IStock) getBuilding()).getStockSettings();

		return stream(EMaterialType.STOCK_MATERIALS)
				.map(eMaterialType -> new StockMaterialState(eMaterialType, stockSettings))
				.collect(Collectors.toList());
	}


}
