package jsettlers.graphics.debug.DatViewer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import go.graphics.GLDrawContext;
import go.graphics.event.GOEvent;
import go.graphics.event.GOEventHandlerProvider;
import go.graphics.swing.event.swingInterpreter.GOSwingEventConverter;
import go.graphics.swing.opengl.JOGLDrawContext;

import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

/**
 * A simple OpenGL-drawn JPanel that uses the GO Event handling mechanism.
 */
public class GLSurface extends JPanel implements GOEventHandlerProvider {

    private GLJPanel canvas;
    private JOGLDrawContext context;

    public GLSurface() {
        this.setLayout(new GridBagLayout());

        GLProfile profile = GLProfile.getDefault();
        GLCapabilities cap = new GLCapabilities(profile);
        cap.setStencilBits(1);

        GLEventListener glEventListener = new GLEventListener() {

            @Override
            public void reshape(GLAutoDrawable gl, int x, int y, int width, int height) {
                GL2 gl2 = gl.getGL().getGL2();
                gl2.glMatrixMode(GL2.GL_PROJECTION);
                gl2.glLoadIdentity();

                GLU glu = new GLU();
                glu.gluOrtho2D(0.0f, width, 0.0f, height);

                gl2.glMatrixMode(GL2.GL_MODELVIEW);
                gl2.glLoadIdentity();

                gl2.glViewport(0, 0, width, height);
            }

            @Override
            public void init(GLAutoDrawable arg0) {
                arg0.getGL().setSwapInterval(0);
            }

            @Override
            public void dispose(GLAutoDrawable arg0) {
                if (context != null) {
                    context.disposeAll();
                }
                context = null;
            }

            @Override
            public void display(GLAutoDrawable glDrawable) {
                GL2 gl2 = glDrawable.getGL().getGL2();
                gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);
                gl2.glLoadIdentity();

                if (context == null || context.getGl2() != gl2) {
                    context = new JOGLDrawContext(gl2);
                }
                context.startFrame();

                redraw(context, getWidth(), getHeight());
            }
        };

        GLJPanel panel = new GLJPanel(cap);
        panel.addGLEventListener(glEventListener);
        canvas = panel;

        new GOSwingEventConverter(canvas, this);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        this.add(canvas, gbc);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        canvas.repaint();
    }

    @Override
    public final void requestFocus() {
        canvas.requestFocus();
    }

    @Override
    public void handleEvent(GOEvent event) {

    }

    protected void redraw(GLDrawContext gl2, int width, int height) {

    }
}
