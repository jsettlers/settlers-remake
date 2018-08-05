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

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import jsettlers.main.android.R;
import jsettlers.main.android.core.resources.OriginalImageProvider;

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