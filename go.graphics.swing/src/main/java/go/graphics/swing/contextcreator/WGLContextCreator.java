package go.graphics.swing.contextcreator;

import org.lwjgl.opengl.WGL;
import org.lwjgl.system.jawt.JAWTWin32DrawingSurfaceInfo;
import org.lwjgl.system.windows.GDI32;
import org.lwjgl.system.windows.PIXELFORMATDESCRIPTOR;

import go.graphics.swing.AreaContainer;

public class WGLContextCreator extends JAWTContextCreator {

    private JAWTWin32DrawingSurfaceInfo win32surfaceinfo;
    private long hwnd;
    private long hdc;
    private long context;
    private int pixel_format;

    public WGLContextCreator(AreaContainer ac) {
        super(ac);
    }


    @Override
    public void stop() {
        WGL.wglDeleteContext(context);
    }

    @Override
    protected void swapBuffers() {
        GDI32.SwapBuffers(hdc);
    }

    @Override
    protected void makeCurrent(boolean draw) {
        if(draw) {
            WGL.wglMakeCurrent(hdc, context);
        } else {
            WGL.wglMakeCurrent(0, 0);
        }
    }

    @Override
    protected void initContext() {
        hwnd = win32surfaceinfo.hwnd();
        hdc = win32surfaceinfo.hdc();


        PIXELFORMATDESCRIPTOR pfd = PIXELFORMATDESCRIPTOR.calloc();
        pfd.dwFlags(GDI32.PFD_DRAW_TO_WINDOW | GDI32.PFD_SUPPORT_OPENGL | GDI32.PFD_DOUBLEBUFFER);
        pfd.iPixelType(GDI32.PFD_TYPE_RGBA);
        pfd.cColorBits((byte) 32);

        pfd.cDepthBits((byte) 24);
        pfd.cStencilBits((byte) 1);

        pixel_format = GDI32.ChoosePixelFormat(hdc, pfd);
        GDI32.SetPixelFormat(hdc, pixel_format, pfd);

        pfd.free();

        context = WGL.wglCreateContext(hdc);
    }

    @Override
    protected void createNewSurfaceInfo() {
        win32surfaceinfo = JAWTWin32DrawingSurfaceInfo.create(surfaceinfo.platformInfo());
    }
}
