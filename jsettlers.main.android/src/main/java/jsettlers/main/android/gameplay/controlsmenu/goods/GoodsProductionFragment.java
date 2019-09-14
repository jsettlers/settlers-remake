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

package jsettlers.main.android.gameplay.controlsmenu.goods;

import java.util.List;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import jsettlers.common.material.EMaterialType;
import jsettlers.main.android.R;
import jsettlers.main.android.core.resources.OriginalImageProvider;

/**
 * Created by tompr on 24/11/2016.
 */
@EFragment(R.layout.menu_goods_production)
public class GoodsProductionFragment extends Fragment {
	public static GoodsProductionFragment newInstance() {
		return new GoodsProductionFragment_();
	}

	private ProductionViewModel viewModel;

	@ViewById(R.id.recyclerView)
	RecyclerView recyclerView;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = ViewModelProviders.of(this, new ProductionViewModel.Factory(getActivity())).get(ProductionViewModel.class);

		ProductionAdapter productionAdapter = new ProductionAdapter(getActivity());
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(productionAdapter);

		viewModel.getProductionStates().observe(this, productionAdapter::updateProductionStates);
	}

	/**
	 * RecyclerView adapter
	 */
	private class ProductionAdapter extends RecyclerView.Adapter<ProductionItemViewHolder> {

		private final LayoutInflater layoutInflater;

		private ProductionState[] productionStates;

		ProductionAdapter(Activity activity) {
			this.layoutInflater = LayoutInflater.from(activity);
			this.productionStates = new ProductionState[0];
		}

		@Override
		public int getItemCount() {
			return productionStates.length;
		}

		@Override
		public ProductionItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = layoutInflater.inflate(R.layout.vh_production_material, parent, false);
			return new ProductionItemViewHolder(view);
		}

		@Override
		public void onBindViewHolder(ProductionItemViewHolder holder, int position) {
			ProductionState productionState = productionStates[position];
			holder.bind(productionState);
		}

		@Override
		public void onBindViewHolder(ProductionItemViewHolder holder, int position, List<Object> payloads) {
			if (payloads == null || payloads.size() == 0) {
				onBindViewHolder(holder, position);
			} else {
				holder.update(productionStates[position]);
			}
		}

		void updateProductionStates(ProductionState[] productionStates) {
			DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(this.productionStates, productionStates));
			diffResult.dispatchUpdatesTo(this);
			this.productionStates = productionStates;
		}

		/**
		 * Diff callback
		 */
		private class DiffCallback extends DiffUtil.Callback {

			private final ProductionState[] oldStates;
			private final ProductionState[] newStates;

			DiffCallback(ProductionState[] oldStates, ProductionState[] newStates) {
				this.oldStates = oldStates;
				this.newStates = newStates;
			}

			@Override
			public int getOldListSize() {
				return oldStates.length;
			}

			@Override
			public int getNewListSize() {
				return newStates.length;
			}

			@Override
			public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
				return oldStates[oldItemPosition].getMaterialType() == newStates[newItemPosition].getMaterialType();
			}

			@Override
			public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
				return oldStates[oldItemPosition].getQuantity() == newStates[newItemPosition].getQuantity()
						&& oldStates[oldItemPosition].getRatio() == newStates[newItemPosition].getRatio();
			}

			@Nullable
			@Override
			public Object getChangePayload(int oldItemPosition, int newItemPosition) {
				return Boolean.TRUE;
			}
		}
	}

	/**
	 * RecyclerView ViewHolder
	 */
	private class ProductionItemViewHolder extends RecyclerView.ViewHolder implements SeekBar.OnSeekBarChangeListener {

		private final ImageView imageView;
		private final SeekBar seekBar;
		private final TextView quantityTextView;
		private final TextView incrementTextView;
		private final TextView decrementTextView;

		private EMaterialType materialType;

		ProductionItemViewHolder(View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.imageView_material);
			seekBar = itemView.findViewById(R.id.seekBar);
			quantityTextView = itemView.findViewById(R.id.textView_quantity);
			incrementTextView = itemView.findViewById(R.id.textView_increment);
			decrementTextView = itemView.findViewById(R.id.textView_decrement);

			incrementTextView.setOnClickListener(view -> viewModel.increment(materialType));
			decrementTextView.setOnClickListener(view -> viewModel.decrement(materialType));
			seekBar.setOnSeekBarChangeListener(this);
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			viewModel.setProductionRatio(materialType, (float) seekBar.getProgress() / seekBar.getMax());
		}

		void bind(ProductionState productionState) {
			materialType = productionState.getMaterialType();
			OriginalImageProvider.get(productionState.getMaterialType()).setAsImage(imageView);
			update(productionState);
		}

		void update(ProductionState productionState) {
			quantityTextView.setText(productionState.getQuantity() + "");
			seekBar.setProgress(Math.round(productionState.getRatio() * seekBar.getMax()));
		}
	}
}
