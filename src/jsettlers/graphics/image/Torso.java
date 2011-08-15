package jsettlers.graphics.image;

/**
 * This is an torso: An image that is colored afterwards.
 * 
 * @author michael
 */
public class Torso extends Image {

	/**
	 * Constant to multiply torso color by. Because torso color seems not to go to 255.
	 */
//	private static final float TORSO_MULTIPLY = 8;
	// FIXME: use luminance here?
	// @Override
	// protected void texImage2D(GL2 gl) {
	// gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA, width, height, 0,
	// GL2.GL_LUMINANCE_ALPHA, GL2.GL_UNSIGNED_BYTE, data);
	// }

	/**
	 * Creates a new torso.
	 * @param data The data to use.
	 */
	public Torso(ImageDataPrivider data) {
	    super(data);
    }

//	private void drawAt2(GL2 gl, int x, int y, Color color) {
//		if (color == null) {
//			color = Color.BLACK;
//		}
//		// generate without color transformation, or it will look funny.
//		tryGenerateTexture(gl);
//
//		gl.glMatrixMode(GL2GL3.GL_COLOR);
//		// matrix transposed to opengl representation!
//		float colorFactor = TORSO_MULTIPLY / 255.0f;
//		float[] matrix = { color.getRed() * colorFactor, 0.0f, 0.0f, 0.0f,//
//				0.0f, color.getGreen() * colorFactor, 0.0f, 0.0f,//
//				0.0f, 0.0f, color.getBlue() * colorFactor, 0.0f,//
//				0.0f, 0.0f, 0.0f, 1.0f };
//		// gl.glLoadMatrixf(matrix, 0);
//		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//
//		// TODO: make texture drawing use color matrix.
//		super.drawAt(gl, x, y, color);
//
//		gl.glMatrixMode(GL2GL3.GL_COLOR);
//		gl.glLoadIdentity();
//
//		gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
//	}

//	private void drawFallback(GL2 gl, int x, int y) {
//		int cx = x + this.offsetX;
//		int cy = y - this.height - this.offsetY;
//
//		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
//		gl.glRasterPos2i(cx, cy);
//		this.data.rewind();
//
//		try {
//			gl.glDrawPixels(this.width, this.height, GL.GL_LUMINANCE_ALPHA, GL.GL_UNSIGNED_BYTE, this.data);
//
//		} catch (IndexOutOfBoundsException e) {
//			System.err.println("torso image draw problem:");
//			System.err.println(e.getMessage());
//		}
//	}

}
