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

package jsettlers.main.android.gameplay.ui.viewholders;

/**
 * Created by Tom Pratt on 12/07/2017.
 */


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import jsettlers.main.android.R;
import jsettlers.main.android.gameplay.viewstates.StockMaterialState;
import jsettlers.main.android.utils.OriginalImageProvider;


public class MaterialViewHolder extends RecyclerView.ViewHolder {
    private final ImageView imageView;

    public MaterialViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.imageView_material);
    }

    public void bind(StockMaterialState materialState) {
        OriginalImageProvider.get(materialState.getMaterialType()).setAsImage(imageView);
        itemView.setSelected(materialState.isStocked());
    }

    public void updateState(StockMaterialState materialState) {
        itemView.setSelected(materialState.isStocked());
    }
}