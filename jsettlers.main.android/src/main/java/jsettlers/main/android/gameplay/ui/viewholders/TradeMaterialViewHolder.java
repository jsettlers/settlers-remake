package jsettlers.main.android.gameplay.ui.viewholders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.viewstates.TradeMaterialState;
import jsettlers.main.android.utils.OriginalImageProvider;

/**
 * Created by Tom Pratt on 05/08/2017.
 */

public class TradeMaterialViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;
    private final TextView textView;

    public TradeMaterialViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.imageView_material);
        textView = (TextView) itemView.findViewById(R.id.textView_tradeMaterialCount);
    }

    public void bind(TradeMaterialState materialState) {
        OriginalImageProvider.get(materialState.getMaterialType()).setAsImage(imageView);
        updateState(materialState);
    }

    public void updateState(TradeMaterialState materialState) {
        if (materialState.getCount() > 0) {
            textView.setVisibility(View.VISIBLE);
            textView.setText(formatCount(materialState.getCount()));
        } else {
            textView.setVisibility(View.GONE);
        }
    }

    private String formatCount(int count) {
        if (count == 32767) {// Integer.MAX_VALUE) {
            return "\u221E";
        } else {
            return count + "";
        }
    }
}