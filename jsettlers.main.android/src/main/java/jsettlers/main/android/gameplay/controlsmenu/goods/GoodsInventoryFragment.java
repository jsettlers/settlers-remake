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
import android.widget.TextView;

import jsettlers.main.android.R;
import jsettlers.main.android.core.resources.OriginalImageProvider;

/**
 * Created by Tom Pratt on 24/11/2016.
 */
@EFragment(R.layout.menu_goods_inventory)
public class GoodsInventoryFragment extends Fragment {
	public static GoodsInventoryFragment newInstance() {
		return new GoodsInventoryFragment_();
	}

	private InventoryViewModel viewModel;

	@ViewById(R.id.recyclerView)
	RecyclerView recyclerView;

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		viewModel = ViewModelProviders.of(this, new InventoryViewModel.Factory(getActivity())).get(InventoryViewModel.class);

		InventoryMaterialsAdapter inventoryMaterialsAdapter = new InventoryMaterialsAdapter(getActivity());
		recyclerView.setHasFixedSize(true);
		recyclerView.setAdapter(inventoryMaterialsAdapter);

		viewModel.getMaterialStates().observe(this, inventoryMaterialsAdapter::setInventoryMaterialStates);
	}

	/**
	 * Adapter
	 */
	private class InventoryMaterialsAdapter extends RecyclerView.Adapter<InventoryMaterialViewHolder> {

		private final LayoutInflater inflater;

		private InventoryMaterialState[] inventoryMaterialStates;

		public InventoryMaterialsAdapter(Activity activity) {
			inflater = LayoutInflater.from(activity);
			inventoryMaterialStates = new InventoryMaterialState[0];
		}

		@Override
		public int getItemCount() {
			return inventoryMaterialStates.length;
		}

		@Override
		public InventoryMaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = inflater.inflate(R.layout.vh_inventory_material, parent, false);
			return new InventoryMaterialViewHolder(view);
		}

		@Override
		public void onBindViewHolder(InventoryMaterialViewHolder holder, int position) {
			InventoryMaterialState inventoryMaterialState = inventoryMaterialStates[position];
			holder.bind(inventoryMaterialState);
		}

		@Override
		public void onBindViewHolder(InventoryMaterialViewHolder holder, int position, List<Object> payloads) {
			if (payloads == null || payloads.size() == 0) {
				onBindViewHolder(holder, position);
			} else {
				holder.updateState(inventoryMaterialStates[position]);
			}
		}

		public void setInventoryMaterialStates(InventoryMaterialState[] inventoryMaterialStates) {
			DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new InventoryMaterialsDiffCallback(this.inventoryMaterialStates, inventoryMaterialStates));
			diffResult.dispatchUpdatesTo(this);
			this.inventoryMaterialStates = inventoryMaterialStates;
		}

		/**
		 * Diff callback
		 */
		private class InventoryMaterialsDiffCallback extends DiffUtil.Callback {

			private final InventoryMaterialState[] oldStates;
			private final InventoryMaterialState[] newStates;

			InventoryMaterialsDiffCallback(InventoryMaterialState[] oldStates, InventoryMaterialState[] newStates) {
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
				return oldStates[oldItemPosition].getCount() == newStates[newItemPosition].getCount();
			}

			@Nullable
			@Override
			public Object getChangePayload(int oldItemPosition, int newItemPosition) {
				return Boolean.TRUE;
			}
		}
	}

	/**
	 * ViewHolder
	 */
	private class InventoryMaterialViewHolder extends RecyclerView.ViewHolder {
		private final ImageView imageView;
		private final TextView textView;

		InventoryMaterialViewHolder(View itemView) {
			super(itemView);
			imageView = itemView.findViewById(R.id.imageView_material);
			textView = itemView.findViewById(R.id.textView_count);
		}

		void bind(InventoryMaterialState inventoryMaterialState) {
			OriginalImageProvider.get(inventoryMaterialState.getMaterialType()).setAsImage(imageView);
			updateState(inventoryMaterialState);
		}

		void updateState(InventoryMaterialState inventoryMaterialState) {
			textView.setText(inventoryMaterialState.getCount() + "");
		}
	}
}
