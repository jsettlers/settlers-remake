/*******************************************************************************
 * Copyright (c) 2015
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
package go.graphics.android;

import go.graphics.GLDrawContext;
import go.graphics.RedrawListener;
import go.graphics.UIPoint;
import go.graphics.area.Area;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.event.interpreter.AbstractEventConverter;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Hashtable;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class GOSurfaceView extends GLSurfaceView implements RedrawListener,
		GOEventHandlerProvider {

	private final Area area;

	private final ActionAdapter actionAdapter = new ActionAdapter(getContext(), this);

	private AndroidContext drawcontext;

	private IContextDestroyedListener contextDestroyedListener = null;

	public GOSurfaceView(Context context, Area area) {
		super(context);
		this.area = area;

		setEGLContextFactory(new Factory());
		setRenderer(new Renderer(context));
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		tryEnableContextPreservation();
		area.addRedrawListener(this);
	}

	private void tryEnableContextPreservation() {
		// api level 11 :-(
		// super.setPreserveEGLContextOnPause(true);
		try {
			Method m =
					GLSurfaceView.class.getMethod(
							"setPreserveEGLContextOnPause", Boolean.TYPE);
			m.invoke(this, true);
		} catch (Throwable t) {
			Log.d("gl", "Could not enable context preservation");
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		actionAdapter.onTouchEvent(e);

		return true;
	}

	@Override
	public void onPause() {
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		super.onPause();
	}

	@Override
	public void onResume() {
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		super.onResume();
	}

	private class ActionAdapter extends AbstractEventConverter {
		private static final double CLICK_MOVE_TRESHOLD = 20;
		private static final double CLICK_TIME_TRSHOLD = 1;
		private static final float ZOOMSTART = 2f;
		private static final double ZOOM_MIN_POINTERDISTANCE = 150;

		protected ActionAdapter(Context context, GOEventHandlerProvider provider) {
			super(provider);
//			addReplaceRule(new EventReplacementRule(ReplacableEvent.DRAW,
//					Replacement.COMMAND_SELECT, CLICK_TIME_TRSHOLD,
//					CLICK_MOVE_TRESHOLD));
//			addReplaceRule(new EventReplacementRule(ReplacableEvent.PAN,
//					Replacement.COMMAND_ACTION, CLICK_TIME_TRSHOLD,
//					CLICK_MOVE_TRESHOLD));

			gestureDetector = new GestureDetector(context, gestureListener);
			longPressDetector = new GestureDetector(context, longPressListener);
			scaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener);

			gestureDetector.setIsLongpressEnabled(false);
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

		private final GestureDetector longPressDetector;
		private final GestureDetector.SimpleOnGestureListener longPressListener = new GestureDetector.SimpleOnGestureListener() {

			@Override
			public void onLongPress(MotionEvent e) {
				Log.d("GESTURETEST", "onLongPress SIMPLE");
				endPan(new UIPoint(e.getX() - panStart.getX() ,getHeight() -  e.getY() - panStart.getY()));
				startDraw(new UIPoint(e.getX(), getHeight() -  e.getY()));
			}
		};

		private final GestureDetector gestureDetector;
		private final GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				Log.d("GESTURETEST", "onDown");
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				Log.d("GESTURETEST", "onShowPress");

			}

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				Log.d("GESTURETEST", "onSingleTapUp");

				fireCommandEvent(new UIPoint(e.getX(), getHeight() -  e.getY()), true);

				return false;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				Log.d("GESTURETEST", "onScroll");

				if (drawStarted()) {
					updateDrawPosition(new UIPoint(e2.getX(), getHeight() -  e2.getY()));
				} else if (panStarted()) {
					updatePanPosition(new UIPoint(e2.getX() - panStart.getX(), getHeight() -  e2.getY() - panStart.getY()));
				} else {
					panStart = new UIPoint(e2.getX(), getHeight() - e2.getY());
					startPan(new UIPoint(0, 0));
				}

				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				Log.d("GESTURETEST", "onLongPress");

			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				Log.d("GESTURETEST", "onFling");
				return false;
			}
		};

		private UIPoint panPosition = new UIPoint(0,0);

		private final ScaleGestureDetector scaleGestureDetector;
		private final ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {

			private float startSpan;

			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {

				startSpan = detector.getCurrentSpan();
				startZoom();

				Log.d("SCALE_GESTURETEST", "onScaleBegin");
				return true;
			}

			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				updateZoomFactor(detector.getCurrentSpan() / startSpan);
				Log.d("SCALE_GESTURETEST", "onScale");
				return true;
			}

			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				Log.d("SCALE_GESTURETEST", "onScaleEnd");

				endZoomEvent(detector.getCurrentSpan() / startSpan);
			}
		};

		public void onTouchEvent(MotionEvent e) {
			if (e.getAction() == MotionEvent.ACTION_UP) {
				if (drawStarted()) {
					endDraw(new UIPoint(e.getX(), getHeight() - e.getY()));
				}

				if (panStarted()) {
					endPan(new UIPoint(e.getX() - panStart.getX(), getHeight() - e.getY() - panStart.getY()));
				}
			}

			if (e.getPointerCount() > 1) {
				if (panStarted()) {
					endPan(new UIPoint(e.getX() - panStart.getX(), getHeight() - e.getY() - panStart.getY()));
				}

				if (drawStarted()) {
					abortDraw();
				}

				scaleGestureDetector.onTouchEvent(e);
			} else {
				gestureDetector.onTouchEvent(e);
				longPressDetector.onTouchEvent(e);
			}

		//	temp(e);
		}

		private void temp(MotionEvent e) {

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
					Log.d("TEMP", "ACTION_POINTER_DOWN");
					int index = getPointerIndex(e);
					Integer id = Integer.valueOf(e.getPointerId(index));
					UIPoint point = new UIPoint(e.getX(index), e.getY(index));
					panPointerStarts.put(id, point);

				} else if (e.getAction() == MotionEvent.ACTION_POINTER_UP) {
					Log.d("TEMP", "ACTION_POINTER_UP");
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


		private UIPoint adjustedCoordinates(MotionEvent e) {
			return new UIPoint(e.getX(), getHeight() - e.getY());
		}
	}

	private class Renderer implements GLSurfaceView.Renderer {

		private Renderer(Context acontext) {
			drawcontext = new AndroidContext(acontext);
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			GLES10.glClearColor(0, 0, 0, 1);
			GLES10.glClear(GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_COLOR_BUFFER_BIT);

			GLES10.glDepthFunc(GLES10.GL_LEQUAL);
			GLES10.glEnable(GLES10.GL_DEPTH_TEST);

			area.drawArea(drawcontext);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			area.setWidth(width);
			area.setHeight(height);
			drawcontext.reinit(width, height);
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		}
	}

	private class Factory implements EGLContextFactory {

		@Override
		public EGLContext createContext(EGL10 arg0, EGLDisplay display,
				EGLConfig config) {
			int[] attributes = new int[] { EGL10.EGL_NONE };
			return arg0.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT,
					attributes);
		}

		@Override
		public void destroyContext(EGL10 arg0, EGLDisplay arg1, EGLContext arg2) {
			Log.w("gl", "Invalidating texture context");
			drawcontext.invalidateContext();
			AndroidTextDrawer.invalidateTextures();
			IContextDestroyedListener listener = contextDestroyedListener;
			if (listener != null) {
				listener.glContextDestroyed();
			}
			arg0.eglDestroyContext(arg1, arg2);
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

	public GLDrawContext getDrawContext() {
		return drawcontext;
	}

	public void setContextDestroyedListener(
			IContextDestroyedListener contextDestroyedListener) {
		this.contextDestroyedListener = contextDestroyedListener;
	}

}
