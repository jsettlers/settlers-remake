package jsettlers.graphics.image;

import java.awt.Color;
import java.nio.ShortBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

/**
 * This is a null image.
 * <p>
 * It may be returned by image methods if the requested image is not available.
 * 
 * @author michael
 */
public final class NullImage extends Image {
	private static final int HALFSIZE = 3;
	private static NullImage instance;

	/**
	 * Gets an instance of the null image.
	 * 
	 * @return An instance.
	 */
	public static NullImage getInstance() {
		if (instance == null) {
			instance = new NullImage();
		}
		return instance;
	}

	private NullImage() {
		super(ShortBuffer.allocate(1), 1, 1, 0, 0);
	}

	@Override
	public void draw(GL2 gl, Color color) {
		gl.glColor4f(1, 1, 1, 0.5f);
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2i(-HALFSIZE, -HALFSIZE);
		gl.glVertex2i(+HALFSIZE, -HALFSIZE);
		gl.glVertex2i(+HALFSIZE, +HALFSIZE);
		gl.glVertex2i(-HALFSIZE, +HALFSIZE);
		gl.glEnd();

		gl.glColor3f(1, 0, 0);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex2i(-HALFSIZE, -HALFSIZE);
		gl.glVertex2i(+HALFSIZE, -HALFSIZE);
		gl.glVertex2i(+HALFSIZE, +HALFSIZE);
		gl.glVertex2i(-HALFSIZE, +HALFSIZE);
		gl.glVertex2i(+HALFSIZE, -HALFSIZE);
		gl.glEnd();

		gl.glColor3f(1, 1, 1);
	}

}
