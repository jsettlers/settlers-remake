package go.graphics.swing.contextcreator;

import org.lwjgl.opengl.GLX;
import org.lwjgl.system.jawt.JAWTX11DrawingSurfaceInfo;
import org.lwjgl.system.linux.X11;
import org.lwjgl.system.linux.XVisualInfo;

import go.graphics.swing.AreaContainer;


public class GLXContextCreator extends JAWTContextCreator {

    private JAWTX11DrawingSurfaceInfo x11surfaceinfo;

    private long display = 0;
    private long context = 0;

    public GLXContextCreator(AreaContainer ac) {
        super(ac);
    }

    @Override
    protected void createNewSurfaceInfo() {
        x11surfaceinfo = JAWTX11DrawingSurfaceInfo.create(surfaceinfo.platformInfo());
    }

    @Override
    protected void initContext() {
        display = x11surfaceinfo.display();

        int[] xvi_attrs = new int[]{
                GLX.GLX_RGBA,
                GLX.GLX_DOUBLEBUFFER,
                GLX.GLX_STENCIL_SIZE, 1,
                0};

        int screen = X11.XDefaultScreen(display);
        XVisualInfo xvi = GLX.glXChooseVisual(display, screen, xvi_attrs);


        context = GLX.glXCreateContext(display, xvi, 0, true);

    }

    @Override
    public void stop() {
        GLX.glXDestroyContext(display, context);
    }

    @Override
    protected void swapBuffers() {
        GLX.glXSwapBuffers(display,x11surfaceinfo.drawable());
    }

    @Override
    protected void makeCurrent(boolean draw) {
        if(draw) {
            GLX.glXMakeCurrent(display, x11surfaceinfo.drawable(), context);
        } else {
            GLX.glXMakeCurrent(display, 0, 0);
        }
    }
}
