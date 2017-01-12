package jsettlers.main.android.ui.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import jsettlers.main.android.R;

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
