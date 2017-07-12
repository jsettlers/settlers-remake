package jsettlers.main.android.gameplay.ui.adapters;

/**
 * Created by Tom Pratt on 12/07/2017.
 */

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.ui.viewholders.MaterialViewHolder;
import jsettlers.main.android.gameplay.viewstates.StockMaterialState;


public class MaterialsAdapter extends RecyclerView.Adapter<MaterialViewHolder> {

    public interface ItemClickListener {
        void onItemClick(StockMaterialState stockMaterialState);
    }

    private final LayoutInflater inflater;

    private ItemClickListener itemClickListener;
    private List<StockMaterialState> materialStates;

    public MaterialsAdapter(Activity activity) {
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getItemCount() {
        return materialStates.size();
    }

    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_stock_material, parent, false);
        MaterialViewHolder materialViewHolder = new MaterialViewHolder(view);
        view.setOnClickListener(v -> itemClickListener.onItemClick(materialStates.get(materialViewHolder.getAdapterPosition())));
        return materialViewHolder;
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, int position) {
        StockMaterialState materialState = materialStates.get(position);
        holder.bind(materialState);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, int position, List<Object> payloads) {
        if (payloads == null || payloads.size() == 0) {
            onBindViewHolder(holder, position);
        } else {
            holder.updateState(materialStates.get(position));
        }
    }

    public void setMaterialStates(List<StockMaterialState> materialStates) {
        if (this.materialStates != null) {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MaterialsDiffCallback(this.materialStates, materialStates));
            diffResult.dispatchUpdatesTo(this);
        }

        this.materialStates = materialStates;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }



    /**
     * Diff callback
     */
    private class MaterialsDiffCallback extends DiffUtil.Callback {

        private final List<StockMaterialState> oldStates;
        private final List<StockMaterialState> newStates;

        MaterialsDiffCallback(List<StockMaterialState> oldStates, List<StockMaterialState> newStates) {
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
            return oldStates.get(oldItemPosition).isStocked() == newStates.get(newItemPosition).isStocked();
        }

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return Boolean.TRUE;
        }
    }
}