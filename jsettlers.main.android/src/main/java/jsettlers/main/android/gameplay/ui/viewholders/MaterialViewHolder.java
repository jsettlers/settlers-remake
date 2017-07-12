package jsettlers.main.android.gameplay.ui.viewholders;

/**
 * Created by Tom Pratt on 12/07/2017.
 */


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import jsettlers.graphics.action.SetAcceptedStockMaterialAction;
import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.viewstates.StockMaterialState;
import jsettlers.main.android.utils.OriginalImageProvider;


public class MaterialViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private StockMaterialState materialState;

    public MaterialViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.imageView_material);
    //    itemView.setOnClickListener(v -> actionControls.fireAction(new SetAcceptedStockMaterialAction(getBuilding().getPos(), materialState.getMaterialType(), !materialState.isStocked(), true)));
    }

    public void bind(StockMaterialState materialState) {
        this.materialState = materialState;
        OriginalImageProvider.get(materialState.getMaterialType()).setAsImage(imageView);
        itemView.setSelected(materialState.isStocked());
    }

    public void updateState(StockMaterialState materialState) {
        this.materialState = materialState;
        itemView.setSelected(materialState.isStocked());
    }
}