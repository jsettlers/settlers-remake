package jsettlers.main.android.gameplay.ui.customviews;

import jsettlers.main.android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * TODO: document your custom view class.
 */
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
