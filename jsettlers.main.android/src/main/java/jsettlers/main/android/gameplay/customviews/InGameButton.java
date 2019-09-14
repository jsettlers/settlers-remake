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

package jsettlers.main.android.gameplay.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

import jsettlers.main.android.R;

public class InGameButton extends FrameLayout {
	private ImageView imageView;

	public InGameButton(Context context) {
		super(context);
		init(null, 0);
	}

	public InGameButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs, 0);
	}

	public InGameButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs, defStyle);
	}

	private void init(AttributeSet attrs, int defStyle) {
		LayoutInflater layoutInflater = LayoutInflater.from(getContext());
		imageView = (ImageView) layoutInflater.inflate(R.layout.view_in_game_button, this, false);
		addView(imageView);
	}

	public void setOnClickListener(OnClickListener onClickListener) {
		imageView.setOnClickListener(onClickListener);
	}

	public ImageView getImageView() {
		return imageView;
	}
}
