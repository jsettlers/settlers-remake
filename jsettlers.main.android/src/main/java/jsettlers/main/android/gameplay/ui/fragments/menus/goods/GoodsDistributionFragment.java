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

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import jsettlers.common.material.EMaterialType;
import jsettlers.main.android.R;
import jsettlers.main.android.core.navigation.BackPressedListener;
import jsettlers.main.android.gameplay.navigation.MenuNavigator;
import jsettlers.main.android.gameplay.navigation.MenuNavigatorProvider;
import jsettlers.main.android.gameplay.viewmodels.goods.DistributionViewModel;
import jsettlers.main.android.gameplay.viewstates.DistributionState;
import jsettlers.main.android.utils.OriginalImageProvider;

/**
 * Created by tompr on 24/11/2016.
 */

@EFragment(R.layout.menu_goods_distribution)
public class GoodsDistributionFragment extends Fragment implements BackPressedListener {
	public static GoodsDistributionFragment newInstance() {
		return new GoodsDistributionFragment_();
	}

	private DistributionViewModel viewModel;
	private MenuNavigator menuNavigator;

	private PopupWindow popupWindow;

	@ViewById(R.id.recyclerView)
	RecyclerView recyclerView;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = ViewModelProviders.of(this, new DistributionViewModel.Factory(getActivity())).get(DistributionViewModel.class);
		menuNavigator = ((MenuNavigatorProvider) getActivity()).getMenuNavigator();

		MaterialsAdapter materialsAdapter = new MaterialsAdapter(getActivity(), viewModel.getDistributionMaterials());
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(materialsAdapter);
	}

	@Override
	public void onStart() {
		super.onStart();
		menuNavigator.addBackPressedListener(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		menuNavigator.removeBackPressedListener(this);
	}

	@Override
	public boolean onBackPressed() {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			return true;
		}
		return false;
	}

	void showDistributionPopup(View anchor, EMaterialType materialType) {
		DistributionState[] distributionStates = viewModel.getDistributionStates(materialType);
        LayoutInflater layoutInflater = getLayoutInflater();

        View popupView = layoutInflater.inflate(R.layout.popup_empty, null);
		LinearLayout container = popupView.findViewById(R.id.container);

		for (DistributionState distributionState : distributionStates) {
			View view = layoutInflater.inflate(R.layout.vh_distribution_building, container, false);
			ImageView imageView = view.findViewById(R.id.imageView_building);
			SeekBar seekBar = view.findViewById(R.id.seekBar);

			OriginalImageProvider.get(distributionState.getBuildingType()).setAsImage(imageView);
			seekBar.setProgress(Math.round(distributionState.getRatio() * seekBar.getMax()));

			seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
				@Override
				public void onProgressChanged(SeekBar seekBar, int i, boolean b) {}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					viewModel.setDistributionRatio(materialType, distributionState.getBuildingType (), (float)seekBar.getProgress() / seekBar.getMax());
				}
			});

            container.addView(view);
		}

        popupView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		int xOffset = -((popupView.getMeasuredWidth() - anchor.getWidth()) / 2);
		int yOffset = -(popupView.getMeasuredHeight() + anchor.getHeight());

		popupWindow = new PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(anchor, xOffset, yOffset);
	}

	/**
	 * Adapter
	 */
	private class MaterialsAdapter extends RecyclerView.Adapter<MaterialViewHolder> {

		private final LayoutInflater inflater;

		private EMaterialType[] materialTypes;

		public MaterialsAdapter(Activity activity, EMaterialType[] materialTypes) {
			inflater = LayoutInflater.from(activity);
			this.materialTypes = materialTypes;
		}

		@Override
		public int getItemCount() {
			return materialTypes.length;
		}

		@Override
		public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = inflater.inflate(R.layout.vh_distribution_material, parent, false);
			MaterialViewHolder viewHolder = new MaterialViewHolder(view);

			view.setOnClickListener(view1 -> showDistributionPopup(view1, materialTypes[viewHolder.getAdapterPosition()]));

			return viewHolder;
		}

		@Override
		public void onBindViewHolder(MaterialViewHolder holder, int position) {
			EMaterialType materialType = materialTypes[position];
			holder.bind(materialType);
		}
	}

	/**
	 * ViewHolder
	 */
	private class MaterialViewHolder extends RecyclerView.ViewHolder {
		private final ImageView imageView;

		MaterialViewHolder(View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.imageView_material);
		}

		void bind(EMaterialType materialType) {
			OriginalImageProvider.get(materialType).setAsImage(imageView);
		}
	}
}
