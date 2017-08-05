package jsettlers.main.android.gameplay.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.viewstates.TradeMaterialState;
import jsettlers.main.android.utils.OriginalImageProvider;

/**
 * Created by Tom Pratt on 05/08/2017.
 */

public class TradeMaterialViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;

    public TradeMaterialViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.imageView_material);
    }

    public void bind(TradeMaterialState materialState) {
        OriginalImageProvider.get(materialState.getMaterialType()).setAsImage(imageView);
    }

    public void updateState(TradeMaterialState materialState) {
    }
}