package go.graphics.android;

import java.util.Collection;
import java.util.Hashtable;

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
import android.view.InputDevice;
import android.view.MotionEvent;

public class GOSurfaceView extends GLSurfaceView implements RedrawListener,
        GOEventHandlerProvoder {

	private final Area area;

	private final ActionAdapter actionAdapter = new ActionAdapter(this);

	public GOSurfaceView(Context context, Area area) {
		super(context);
		this.area = area;

		setRenderer(new Renderer());
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		//api level 11 :-(
		//super.setPreserveEGLContextOnPause(true);
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
		private static final float ZOOMSTART = 2f;
		private static final double ZOOM_MIN_POINTERDISTANCE = 150;

		protected ActionAdapter(GOEventHandlerProvoder provider) {
			super(provider);
			addReplaceRule(new EventReplacementRule(ReplacableEvent.DRAW,
			        Replacement.COMMAND_SELECT, CLICK_TIME_TRSHOLD,
			        CLICK_MOVE_TRESHOLD));
			addReplaceRule(new EventReplacementRule(ReplacableEvent.PAN,
			        Replacement.COMMAND_ACTION, CLICK_TIME_TRSHOLD,
			        CLICK_MOVE_TRESHOLD));
		}

		private boolean doZoom = false;

		/**
		 * The points where pointers started. In Android space
		 */
		private Hashtable<Integer, UIPoint> panPointerStarts =
		        new Hashtable<Integer, UIPoint>();

		private double endedPansX = 0;
		private double endedPansY = 0;

		/**
		 * The pan start center, in GO space
		 */
		private UIPoint panStart = new UIPoint(0, 0);

		/**
		 * Distance the pointers had on zoom start
		 */
		private double zoomStartDistance = 0;

		private float lastZoomFactor = 1;

		public void onTouchEvent(MotionEvent e) {

			boolean isPan =
			        panStarted()
			                || e.getPointerCount() > 1
			                || (e.getSource() & InputDevice.SOURCE_CLASS_MASK) == InputDevice.SOURCE_CLASS_TRACKBALL;

			if (isPan) {
				if (drawStarted()) {
					abortDraw();
				}
				if (!panStarted()) {
					endedPansX = 0;
					endedPansY = 0;
					panStart = getStartedPanAverage();
					startPan(panStart);
					zoomStartDistance = getPointerDistance(e);
					lastZoomFactor = 1;
				}

				if (e.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
					int index = getPointerIndex(e);
					Integer id = Integer.valueOf(e.getPointerId(index));
					UIPoint point = new UIPoint(e.getX(index), e.getY(index));
					panPointerStarts.put(id, point);

				} else if (e.getAction() == MotionEvent.ACTION_POINTER_UP) {
					int index = getPointerIndex(e);
					Integer id = Integer.valueOf(e.getPointerId(index));
					UIPoint start = panPointerStarts.remove(id);
					if (start != null) {
						endedPansX +=
						        (e.getX(index) - start.getX())
						                / e.getPointerCount();
						endedPansY -=
						        (e.getY(index) - start.getY())
						                / e.getPointerCount();
					}

				} else {
					float factor = computeZoomFactor(e);
					if (e.getAction() == MotionEvent.ACTION_MOVE) {
						UIPoint point = computePanPoint(e);
						updatePanPosition(point);

						if (e.getPointerCount() > 1
						        && (factor < 1 / ZOOMSTART || factor > ZOOMSTART)
						        && getPointerDistance(e) > ZOOM_MIN_POINTERDISTANCE) {
							doZoom = true;
							startZoom();
						}

						if (doZoom) {
							updateZoomFactor(factor);
						}

					} else if (e.getAction() == MotionEvent.ACTION_UP) {
						endPan(computePanPoint(e));
						if (doZoom) {
							endZoomEvent(factor);
						}
					}
				}
			} else {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					startDraw(convertToLocal(e, 0));
					panPointerStarts.clear();
					for (int i = 0; i < e.getPointerCount(); i++) {
						Integer index = e.getPointerId(i);
						panPointerStarts.put(index,
						        new UIPoint(e.getX(i), e.getY(i)));
					}
				} else if (e.getAction() == MotionEvent.ACTION_MOVE) {
					updateDrawPosition(convertToLocal(e, 0));
				} else if (e.getAction() == MotionEvent.ACTION_UP) {
					endDraw(convertToLocal(e, 0));
				}
			}
		}

		private double getPointerDistance(MotionEvent e) {
			float centerx = 0;
			float centery = 0;
			int pointerCount = e.getPointerCount();
			for (int i = 0; i < pointerCount; i++) {
				centerx += e.getX(i);
				centery += e.getY(i);
			}
			centerx /= pointerCount;
			centery /= pointerCount;

			float dx = 0;
			float dy = 0;
			for (int i = 0; i < pointerCount; i++) {
				dx += Math.abs(centerx - e.getX(i));
				dy += Math.abs(centery - e.getY(i));
			}
			return Math.hypot(dx, dy);
		}

		private float computeZoomFactor(MotionEvent e) {
			if (e.getPointerCount() > 1) {
				double currentDistance = getPointerDistance(e);
				float zoom = (float) (currentDistance / zoomStartDistance);
				lastZoomFactor = zoom;
				return zoom;
			} else {
				return lastZoomFactor;
			}
		}

		private UIPoint computePanPoint(MotionEvent e) {
			double dx = endedPansX;
			double dy = endedPansY;
			for (int i = 0; i < e.getPointerCount(); i++) {
				Integer id = Integer.valueOf(e.getPointerId(i));
				UIPoint p = panPointerStarts.get(id);
				if (p != null) {
					dx += e.getX(i) - p.getX();
					dy -= e.getY(i) - p.getY();
				}
			}

			UIPoint point =
			        new UIPoint(panStart.getX() + dx / e.getPointerCount(),
			                panStart.getY() + dy / e.getPointerCount());
			return point;
		}

		/**
		 * Gets the center of the points in the hashtable and adds the endedPans
		 * 
		 * @return
		 */
		private UIPoint getStartedPanAverage() {
			double x = 0;
			double y = 0;

			Collection<UIPoint> values = panPointerStarts.values();
			for (UIPoint start : values) {
				x += start.getX();
				y += start.getY();
			}
			return new UIPoint(x / values.size(), getHeight() - y
			        / values.size());
		}

		private int getPointerIndex(MotionEvent e) {
			int mask = MotionEvent.ACTION_POINTER_INDEX_MASK;
			int shift = MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			return (e.getActionIndex() & mask) >> shift;
		}

		private UIPoint convertToLocal(MotionEvent e, int index) {
			return new UIPoint(e.getX(index), getHeight() - e.getY(index));
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
			GLES10.glClearColor(0, 0, 0, 1);
			GLES10.glClear(GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_COLOR_BUFFER_BIT);

			area.drawArea(context);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			System.out.println("opengl suface changed");
			area.setWidth(width);
			area.setHeight(height);
			context.reinit(width, height);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			System.out.println("opengl suface created");
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
