package jsettlers.graphics.androidui.hud;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import go.graphics.UIPoint;
import jsettlers.common.utils.MathUtils;
import jsettlers.graphics.androidui.R;
import jsettlers.graphics.map.ScreenPosition;
import jsettlers.graphics.map.controls.original.panel.content.AnimateablePosition;

/**
 * This view displays a navigation arrow.
 * 
 * @author Michael Zangl
 *
 */
public class NavigationView extends View {
	private static final long PAN_TIMER_PERIOD = 50;

	private boolean panInProgress;
	// relative to center.
	private AnimateablePosition panPosition = new AnimateablePosition(0, 0);

	private final Object panTimerMutex = new Object();
	private Timer panTimer;
	private TimerTask panTimerTask;
	private ScreenPosition screenPosition;
	/**
	 * Half the width of this view.
	 */
	private int centerX = 1;
	/**
	 * Half the height of this view.
	 */
	private int centerY = 1;

	private final Handler handler = new Handler();

	public NavigationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public NavigationView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NavigationView(Context context) {
		super(context);
	}

	public void setScreenPositionToInfluence(ScreenPosition screenPosition) {
		this.screenPosition = screenPosition;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Bitmap image = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.icon_move);

		canvas.drawBitmap(image, canvas.getWidth() / 2 + panPosition.getX() - image.getWidth() / 2, canvas.getHeight() / 2 + panPosition.getY()
				- image.getHeight() / 2, null);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_SCROLL:
			setPanningPosition(event.getX(), event.getY());
			return true;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			endPanning();
			return true;
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		centerX = w / 2;
		centerY = h / 2;
		endPanning();
		panPosition = new AnimateablePosition(0, 0);
		super.onSizeChanged(w, h, oldw, oldh);
		invalidate();
	}

	private void setPanningPosition(float x, float y) {
		panInProgress = true;
		// range: -1 .. 1 for view.
		float relativeX = (x - centerX) / centerX;
		float relativeY = (y - centerY) / centerY;

		// now constraint
		double len = MathUtils.hypot(relativeX, relativeY);
		if (len > 1) {
			relativeX /= len;
			relativeY /= len;
		}

		// if (Math.abs(x - centerX) < centerX / 2 && Math.abs(y - centerY) < centerY / 2) {
		// double scale = fullSize / 2 / distanceToCenter(drawPosition);
		// x = (1 - scale) * this.x + scale * x;
		// y = (1 - scale) * this.y + scale * y;
		// }
		panPosition.setPosition(relativeX * centerX, relativeY * centerY);

		startPanTimer();
		invalidate();
	}

	private void startPanTimer() {
		synchronized (panTimerMutex) {
			if (panTimer == null) {
				panTimer = new Timer("pan timer");
			}
			if (panTimerTask == null) {
				panTimerTask = new TimerTask() {
					@Override
					public void run() {
						panTimerTick();
					}
				};
				panTimer.schedule(panTimerTask, PAN_TIMER_PERIOD,
						PAN_TIMER_PERIOD);
			}
		}
	}

	/**
	 * The rest we store for the next time
	 */
	private double panDx, panDy;

	protected void panTimerTick() {
		synchronized (panTimerMutex) {
			if (!panInProgress && panPosition.getX() == 0
					&& panPosition.getY() == 0) {
				panTimerTask.cancel();
				panTimerTask = null;
			}
			panDx -= panPosition.getX();
			panDy += panPosition.getY();
		}

		if (screenPosition != null) {
			System.out.println("scroll: " + panDx + ", " + panDy);
			screenPosition.setPanProgress(this, new UIPoint(panDx, panDy));
		}
		handler.post(new Runnable() {
			@Override
			public void run() {
				invalidate();
			}
		});
	}

	public void endPanning() {
		panInProgress = false;
		panPosition.setPosition(0, 0);
		if (screenPosition != null) {
			screenPosition.finishPanProgress(this,
					new UIPoint(panDx, panDy));
		}
		panDx = 0;
		panDy = 0;
		invalidate();
	}

	public boolean isPanInProgress() {
		return panInProgress;
	}
}
