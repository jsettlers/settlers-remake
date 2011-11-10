package go.graphics.android;

import go.graphics.RedrawListener;
import go.graphics.UIPoint;
import go.graphics.area.Area;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandlerProvoder;
import go.graphics.event.interpreter.AbstractEventConverter;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GOSurfaceView extends GLSurfaceView implements RedrawListener, GOEventHandlerProvoder {

	private final Area area;

	private final ActionAdapter actionAdapter = new ActionAdapter(this);

	public GOSurfaceView(Context context, Area area) {
		super(context);
		this.area = area;

		setRenderer(new Renderer());
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

		area.addRedrawListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		actionAdapter.onTouchEvent(e);

		return true;
	}

	private class ActionAdapter extends AbstractEventConverter {

		private static final double CLICK_MOVE_TRESHOLD = 20;
		private static final double CLICK_TIME_TRSHOLD = 1;
		private static final float PAN_SIZE = .7f;

		protected ActionAdapter(GOEventHandlerProvoder provider) {
			super(provider);
			addReplaceRule(new EventReplacementRule(ReplacableEvent.DRAW, Replacement.COMMAND_SELECT, CLICK_TIME_TRSHOLD, CLICK_MOVE_TRESHOLD));
			addReplaceRule(new EventReplacementRule(ReplacableEvent.PAN, Replacement.COMMAND_ACTION, CLICK_TIME_TRSHOLD, CLICK_MOVE_TRESHOLD));
		}

		public void onTouchEvent(MotionEvent e) {
			UIPoint local = convertToLocal(e);

			boolean isPan = panStarted() || e.getPointerCount() > 1 || e.getSize(0) > PAN_SIZE;

			if (isPan) {
				if (drawStarted()) {
					abortDraw();
				}
				if (!panStarted()) {
					startPan(local);
				}

				if (e.getAction() == MotionEvent.ACTION_MOVE) {
					updatePanPosition(local);
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					endPan(local);
				}
			} else {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					startDraw(local);
				} else if (e.getAction() == MotionEvent.ACTION_MOVE) {
					updateDrawPosition(local);
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					endDraw(local);
				}
			}
		}

		private UIPoint convertToLocal(MotionEvent e) {
			return new UIPoint(e.getX(), getHeight() - e.getY());
		}

		public void fireKey(String key) {
			startKeyEvent(key);
			endKeyEvent(key);
		}

	}

	private class Renderer implements GLSurfaceView.Renderer {
		private AndroidContext context;

		private Renderer() {
			context = new AndroidContext();
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			GLES10.glClearColor(0, 0, 1, 1);
			GLES10.glClear(GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_COLOR_BUFFER_BIT);

			area.drawArea(context);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			area.setWidth(width);
			area.setHeight(height);
			context.reinit(width, height);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}

	}

	@Override
	public void requestRedraw() {
		requestRender();
	}

	@Override
	public void handleEvent(GOEvent event) {
		area.handleEvent(event);
	}

	public void fireKey(String key) {
		actionAdapter.fireKey(key);
	}

}
