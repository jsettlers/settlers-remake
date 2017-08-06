package jsettlers.main.android.gameplay.ui.adapters;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.ui.viewholders.TradeMaterialViewHolder;
import jsettlers.main.android.gameplay.viewstates.TradeMaterialState;

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
            return true;// oldStates.get(oldItemPosition).isStocked() == newStates.get(newItemPosition).isStocked();
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return Boolean.TRUE;
        }
    }
}
