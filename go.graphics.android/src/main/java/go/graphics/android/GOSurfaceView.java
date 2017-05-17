/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import java.lang.reflect.Method;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import go.graphics.GLDrawContext;
import go.graphics.RedrawListener;
import go.graphics.UIPoint;
import go.graphics.area.Area;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.event.interpreter.AbstractEventConverter;

import android.content.Context;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.os.Vibrator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class GOSurfaceView extends GLSurfaceView implements RedrawListener, GOEventHandlerProvider {

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
			Method m = GLSurfaceView.class.getMethod("setPreserveEGLContextOnPause", Boolean.TYPE);
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

		protected ActionAdapter(Context context, GOEventHandlerProvider provider) {
			super(provider);
			gestureDetector = new GestureDetector(context, gestureListener);
			longPressDetector = new GestureDetector(context, longPressListener);
			scaleGestureDetector = new ScaleGestureDetector(context, scaleGestureListener);

			gestureDetector.setIsLongpressEnabled(false);

			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		}

		private final Vibrator vibrator;

		/**
		 * The pan start center, in GO space
		 */
		private UIPoint panStart = new UIPoint(0, 0);

		private final GestureDetector longPressDetector;
		private final GestureDetector.SimpleOnGestureListener longPressListener = new GestureDetector.SimpleOnGestureListener() {
			@Override
			public void onLongPress(MotionEvent e) {
				vibrator.vibrate(25);

				endPan(currentPoint(e));
				startDraw(currentPoint(e));
			}
		};

		private final GestureDetector gestureDetector;
		private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				if (drawStarted()) {
					abortDraw();
					fireMoveTo(e);
				} else {
					fireSelectPoint(e);
				}
				return true;
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				if (drawStarted()) {
					updateDrawPosition(currentPoint(e2));
				} else if (panStarted()) {
					updatePanPosition(relativePanPoint(e2));
				} else {
					panStart = currentPoint(e2);
					startPan(new UIPoint(0, 0));
				}

				return true;
			}
		};

		private final ScaleGestureDetector scaleGestureDetector;
		private final ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
			private float startSpan;

			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				startSpan = detector.getCurrentSpan();
				startZoom();
				return true;
			}

			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				updateZoomFactor(detector.getCurrentSpan() / startSpan);
				return true;
			}

			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				endZoomEvent(detector.getCurrentSpan() / startSpan, null);
			}
		};

		public void onTouchEvent(MotionEvent e) {
			scaleGestureDetector.onTouchEvent(e);
			gestureDetector.onTouchEvent(e);
			longPressDetector.onTouchEvent(e);

			if (e.getAction() == MotionEvent.ACTION_UP) {
				if (drawStarted()) {
					endDraw(currentPoint(e));
				}

				if (panStarted()) {
					endPan(relativePanPoint(e));
				}
			}

			if (e.getPointerCount() > 1) {
				if (drawStarted()) {
					abortDraw();
				}

				if (panStarted()) {
					endPan(relativePanPoint(e));
				}
			}
		}

		private UIPoint relativePanPoint(MotionEvent e) {
			return new UIPoint(e.getX() - panStart.getX(), getHeight() - e.getY() - panStart.getY());
		}

		private UIPoint currentPoint(MotionEvent e) {
			return new UIPoint(e.getX(), getHeight() - e.getY());
		}

		private void fireSelectPoint(MotionEvent e) {
			fireCommandEvent(new UIPoint(e.getX(), getHeight() - e.getY()), true);
		}

		private void fireMoveTo(MotionEvent e) {
			fireCommandEvent(new UIPoint(e.getX(), getHeight() - e.getY()), false);
		}
	}

	private class Renderer implements GLSurfaceView.Renderer {

		private Renderer(Context aContext) {
			drawcontext = new AndroidContext(aContext);
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
		public EGLContext createContext(EGL10 arg0, EGLDisplay display, EGLConfig config) {
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

	public GLDrawContext getDrawContext() {
		return drawcontext;
	}

	public void setContextDestroyedListener(IContextDestroyedListener contextDestroyedListener) {
		this.contextDestroyedListener = contextDestroyedListener;
	}
}
