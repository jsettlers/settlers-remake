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

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jsettlers.main.android.R;

/**
 * Created by Tom Pratt on 05/08/2017.
 */

public class TradeMaterialsAdapter extends RecyclerView.Adapter<TradeMaterialViewHolder> {
	public interface ItemClickListener {
		void onItemClick(View sender, TradeMaterialState tradeMaterialState);
	}

	private final LayoutInflater inflater;

	private ItemClickListener itemClickListener;
	private List<TradeMaterialState> tradeMaterialStates;

	public TradeMaterialsAdapter(Activity activity) {
		inflater = LayoutInflater.from(activity);
	}

	@Override
	public int getItemCount() {
		return tradeMaterialStates.size();
	}

	@Override
	public TradeMaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = inflater.inflate(R.layout.item_trade_material, parent, false);
		TradeMaterialViewHolder materialViewHolder = new TradeMaterialViewHolder(view);
		view.setOnClickListener(v -> itemClickListener.onItemClick(view, tradeMaterialStates.get(materialViewHolder.getAdapterPosition())));
		return materialViewHolder;
	}

	@Override
	public void onBindViewHolder(TradeMaterialViewHolder holder, int position) {
		TradeMaterialState tradeMaterialState = tradeMaterialStates.get(position);
		holder.bind(tradeMaterialState);
	}

	@Override
	public void onBindViewHolder(TradeMaterialViewHolder holder, int position, List<Object> payloads) {
		if (payloads == null || payloads.size() == 0) {
			onBindViewHolder(holder, position);
		} else {
			holder.updateState(tradeMaterialStates.get(position));
		}
	}

	public void setMaterialStates(List<TradeMaterialState> tradeMaterialStates) {
		if (this.tradeMaterialStates != null) {
			DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MaterialsDiffCallback(this.tradeMaterialStates, tradeMaterialStates));
			diffResult.dispatchUpdatesTo(this);
		}

		this.tradeMaterialStates = tradeMaterialStates;
	}

	public void setItemClickListener(ItemClickListener itemClickListener) {
		this.itemClickListener = itemClickListener;
	}

	/**
	 * Diff callback
	 */
	private class MaterialsDiffCallback extends DiffUtil.Callback {

		private final List<TradeMaterialState> oldStates;
		private final List<TradeMaterialState> newStates;

		MaterialsDiffCallback(List<TradeMaterialState> oldStates, List<TradeMaterialState> newStates) {
			this.oldStates = oldStates;
			this.newStates = newStates;
		}

		@Override
		public int getOldListSize() {
			return oldStates.size();
		}

		@Override
		public int getNewListSize() {
			return newStates.size();
		}

		@Override
		public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
			return oldStates.get(oldItemPosition).getMaterialType() == newStates.get(newItemPosition).getMaterialType();
		}

		@Override
		public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
			return oldStates.get(oldItemPosition).getCount() == newStates.get(newItemPosition).getCount();
		}

		@Nullable
		@Override
		public Object getChangePayload(int oldItemPosition, int newItemPosition) {
			return Boolean.TRUE;
		}
	}
}
