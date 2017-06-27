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

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import java8.util.function.Function;
import java8.util.stream.Collectors;
import jsettlers.common.buildings.IBuilding;
import jsettlers.common.material.EMaterialType;
import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.map.controls.original.panel.selection.BuildingState;
import jsettlers.main.android.R;
import jsettlers.main.android.core.controls.DrawControls;
import jsettlers.main.android.core.controls.DrawListener;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.utils.OriginalImageProvider;

import static java8.util.J8Arrays.stream;

/**
 * Created by tompr on 10/01/2017.
 */
public class StockFeature extends SelectionFeature implements DrawListener {
	private final EMaterialType[] stockableMaterialStates = new EMaterialType[] {
			EMaterialType.PLANK,
			EMaterialType.STONE,
			EMaterialType.TRUNK,
			EMaterialType.COAL,
			EMaterialType.IRONORE,
			EMaterialType.GOLDORE,
			EMaterialType.IRON,
			EMaterialType.HAMMER,
			EMaterialType.BLADE,
			EMaterialType.AXE,
			EMaterialType.SAW,
			EMaterialType.PICK,
			EMaterialType.FISHINGROD,
			EMaterialType.SCYTHE,
			EMaterialType.SWORD,
			EMaterialType.BOW,
			EMaterialType.SPEAR,
			EMaterialType.WATER,
			EMaterialType.FISH,
			EMaterialType.PIG,
			EMaterialType.MEAT,
			EMaterialType.CROP,
			EMaterialType.FLOUR,
			EMaterialType.BREAD,
			EMaterialType.WINE,
			EMaterialType.GOLD
	};

	private final DrawControls drawControls;

	private final MaterialsAdapter materialsAdapter;
	private final RecyclerView recyclerView;
	private final ImageView buildingImageView;

	public StockFeature(View view, IBuilding building, MenuNavigator menuNavigator, DrawControls drawControls) {
		super(view, building, menuNavigator);
		this.drawControls = drawControls;

		buildingImageView = (ImageView) getView().findViewById(R.id.image_view_building);


		materialsAdapter = new MaterialsAdapter();
		recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
		recyclerView.setHasFixedSize(true);
	}

	@Override
	public void initialize(BuildingState buildingState) {
		super.initialize(buildingState);
		drawControls.addInfrequentDrawListener(this);

		List<MaterialState> materialStates = stream(stockableMaterialStates)
				.map(eMaterialType -> new MaterialState(eMaterialType, getBuildingState()))
				.collect(Collectors.toList());

		materialsAdapter.setMaterialStates(materialStates);
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
		getView().post(this::update);
	}

	private void update() {
		if (hasNewState() && getBuildingState().isStock()) {
			recyclerView.setVisibility(View.VISIBLE);
			buildingImageView.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * materials adapter
	 */
	class MaterialsAdapter extends RecyclerView.Adapter<MaterialViewHolder> {
		private final LayoutInflater inflater;

		private List<MaterialState> materialStates;

		public MaterialsAdapter() {
			inflater = LayoutInflater.from(getContext());
		}

		@Override
		public int getItemCount() {
			return materialStates.size();
		}

		@Override
		public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = inflater.inflate(R.layout.item_stock_material, parent, false);
			return new MaterialViewHolder(view);
		}

		@Override
		public void onBindViewHolder(MaterialViewHolder holder, int position) {
			MaterialState materialState = materialStates.get(position);
			holder.bind(materialState);
		}

		public void setMaterialStates(List<MaterialState> materialStates) {
			this.materialStates = materialStates;
			notifyDataSetChanged();
		}
	}

	/**
	 * stock item viewholder
	 */
	class MaterialViewHolder extends RecyclerView.ViewHolder {
		TextView textView;

		public MaterialViewHolder(View itemView) {
			super(itemView);

			 textView = (TextView) itemView.findViewById(R.id.test);
		}

		void bind(MaterialState materialState) {
			textView.setText(materialState.getMaterialType().name());
		}

		void update() {

		}
	}

	/**
	 * Model for stock item
	 */
	class MaterialState {
		private final EMaterialType materialType;
		private final boolean stored;

		MaterialState(EMaterialType materialType, BuildingState state) {
			this.materialType = materialType;
			this.stored = state.stockAcceptsMaterial(materialType);
		}

		public EMaterialType getMaterialType() {
			return materialType;
		}

		public boolean isStored() {
			return stored;
		}
	}
}
