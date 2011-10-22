package go.graphics.android;

import go.graphics.RedrawListener;
import go.graphics.area.Area;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GOSurfaceView extends GLSurfaceView implements RedrawListener {
	
	private final Area area;

	public GOSurfaceView(Context context, Area area) {
	    super(context);
		this.area = area;

        setRenderer(new Renderer());
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        area.addRedrawListener(this);
    }
    @Override 
    public boolean onTouchEvent(MotionEvent e) {
//        float x = e.getX();
//        float y = e.getY();

        return true;
    }
    
	private class Renderer implements GLSurfaceView.Renderer {
		private AndroidContext context;
		
		@Override
        public void onDrawFrame(GL10 gl) {
	        generateContext(gl);
	        gl.glClearColor(1, 1, 0, 1);
	        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	        
	        area.drawArea(context);
        }

		private void generateContext(GL10 gl) {
	        if (context == null) {
	        	context = new AndroidContext(gl);
	        }
        }

		@Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
	        generateContext(gl);
	        area.setWidth(width);
	        area.setHeight(height);
	        gl.glViewport(width/2, height/2, width, height);
	        System.out.println("Drawing started");
        }

		@Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
	        generateContext(gl);
	        System.out.println("Drawing created");
        }
		
	}

	@Override
    public void requestRedraw() {
	    requestRender();
    }

}
