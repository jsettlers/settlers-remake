/*******************************************************************************
 * Copyright (c) 2016
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
 *******************************************************************************/
package jsettlers.graphics.androidui.menu;

import jsettlers.common.map.shapes.MapRectangle;
import jsettlers.common.position.ShortPoint2D;
import jsettlers.graphics.map.MapDrawContext;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * This is a special view that displays the minimap bitmap and handles clicks on the minimap.
 * 
 * @author Michael Zangl
 */
public class MinimapView extends View {
	private static final int PADDING = 10;
	private static final float SKEW = .7f;
	private Bitmap bitmap;
	private Matrix matrix;
	private MapDrawContext mapContext;
	private boolean dragActive;
	private Paint highlightPaint;

	public MinimapView(Context context, AttributeSet attrs) {
		super(context, attrs);

		highlightPaint = new Paint();
		highlightPaint.setColor(0xffffffff);
		highlightPaint.setStyle(Paint.Style.STROKE);
		highlightPaint.setStrokeWidth(1);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (bitmap != null && mapContext != null) {
			canvas.drawBitmap(bitmap, matrix, null);

			MapRectangle area = mapContext.getConverter().getMapForScreen(mapContext.getScreen().getPosition());
			float[] corners = new float[] {
					(float) area.getLineStartX(0) / mapContext.getMap().getWidth() * bitmap.getWidth(),
					(float) area.getLineY(0) / mapContext.getMap().getHeight() * bitmap.getHeight(),
					(float) area.getLineEndX(area.getLines() - 1) / mapContext.getMap().getWidth() * bitmap.getWidth(),
					(float) area.getLineY(area.getLines() - 1) / mapContext.getMap().getHeight() * bitmap.getHeight()
			};
			matrix.mapPoints(corners);
			canvas.drawRect(corners[0], corners[1], corners[2], corners[3], highlightPaint);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if (bitmap != null) {
			matrix = new Matrix();
			float s = Math.min((w - 20f) / bitmap.getWidth() / (1 + SKEW), (h - 20f) / bitmap.getHeight());
			System.out.println(s);
			matrix.postScale(s, s);
			matrix.postTranslate(PADDING, PADDING);
			matrix.postSkew(-SKEW, 0, PADDING, h - PADDING);
		}
	}

	public void updateBitmap(Bitmap updateBitmap) {
		boolean recomputeSize = bitmap == null;
		bitmap = updateBitmap;
		if (recomputeSize) {
			onSizeChanged(getWidth(), getHeight(), 0, 0);
		}
		invalidate();
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean isStart = event.getActionMasked() == MotionEvent.ACTION_DOWN;
		boolean isEnd = event.getActionMasked() == MotionEvent.ACTION_UP;
		boolean isDrag = isStart || event.getActionMasked() == MotionEvent.ACTION_MOVE
				|| isEnd;
		if ((isStart || (dragActive && isDrag)) && bitmap != null && mapContext != null) {
			float[] points = new float[] { event.getX(), event.getY() };

			Matrix inv = new Matrix();
			matrix.invert(inv);
			inv.mapPoints(points);

			points[0] /= bitmap.getWidth();
			points[1] /= bitmap.getHeight();

			if (dragActive || (points[0] >= -.1 && points[0] <= 1.1 && points[1] >= -.1 && points[1] <= 1.1)) {
				int x = (int) (points[0] * mapContext.getMap().getWidth());
				int y = (int) (points[1] * mapContext.getMap().getHeight());
				mapContext.scrollTo(new ShortPoint2D(x, y));
				invalidate();
				dragActive = !isEnd;
				if (isEnd) {
					performClick();
				}
				return true;
			} else {
				dragActive = false;
			}
		}

		return super.onTouchEvent(event);
	}

	public void setMapContext(MapDrawContext mapContext) {
		this.mapContext = mapContext;
	}
}