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

package jsettlers.main.android.gameplay.ui.fragments.menus.goods;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

import java8.util.stream.Collectors;
import jsettlers.common.map.partition.IStockSettings;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.action.SetAcceptedStockMaterialAction;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.ActionControls;
import jsettlers.main.android.core.controls.ControlsResolver;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.core.controls.PositionControls;
import jsettlers.main.android.gameplay.ui.adapters.MaterialsAdapter;
import jsettlers.main.android.gameplay.viewstates.StockMaterialState;

import static java8.util.J8Arrays.stream;

/**
 * Created by tompr on 24/11/2016.
 */

@EFragment(R.layout.menu_goods_stock)
public class GoodsStockFragment extends Fragment implements DrawListener {

	private PositionControls positionControls;
	private ActionControls actionControls;
	private DrawControls drawControls;

	private MaterialsAdapter materialsAdapter;

	@ViewById(R.id.recyclerView)
	RecyclerView recyclerView;
	@ViewById(R.id.textView_message)
	TextView textViewMessage;


	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ControlsResolver controlsResolver = new ControlsResolver(getActivity());
		positionControls = controlsResolver.getPositionControls();
		actionControls = controlsResolver.getActionControls();
		drawControls = controlsResolver.getDrawControls();

		materialsAdapter = new MaterialsAdapter(getActivity());
		materialsAdapter.setMaterialStates(materialStates());
		materialsAdapter.setItemClickListener(this::materSelected);

		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(materialsAdapter);

		drawControls.addInfrequentDrawListener(this);

		update();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		drawControls.removeInfrequentDrawListener(this);
	}

	@Override
	public void draw() {
		getView().post(this::update);
	}

	private void update() {
		if (positionControls.isInPlayerPartition()) {
			materialsAdapter.setMaterialStates(materialStates());
			recyclerView.setVisibility(View.VISIBLE);
			textViewMessage.setVisibility(View.INVISIBLE);
		} else {
			recyclerView.setVisibility(View.INVISIBLE);
			textViewMessage.setVisibility(View.VISIBLE);
		}
	}

	private void materSelected(StockMaterialState stockMaterialState) {
		actionControls.fireAction(new SetAcceptedStockMaterialAction(positionControls.getCurrentPosition(), stockMaterialState.getMaterialType(), !stockMaterialState.isStocked(), false));
	}

	private List<StockMaterialState> materialStates() {
		IStockSettings stockSettings = positionControls.getCurrentPartitionData().getPartitionSettings().getStockSettings();

		return stream(EMaterialType.STOCK_MATERIALS)
				.map(eMaterialType -> new StockMaterialState(eMaterialType, stockSettings))
				.collect(Collectors.toList());
	}
}
