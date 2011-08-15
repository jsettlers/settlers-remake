package jsettlers.graphics.image;


/**
 * This isa gui image.
 * @author michael
 *
 */
public class GuiImage extends Image {

	/**
	 * Creates a new GUI image.
	 * @param provider The provider.
	 */
	public GuiImage(ImageDataPrivider provider) {
		super(provider);
	}

	/**
	 * draws the button at the given x and y coodringate.
	 * 
	 * @param gl
	 * @param x
	 *            left
	 * @param y
	 *            bottom
	 */
	// public void drawAt(GL2 gl, int x, int y) {
	// int left = x;
	// int bottom = y;
	//
	// bind(gl);
	// gl.glEnable(GL2.GL_TEXTURE_2D);
	// gl.glBegin(GL2.GL_QUADS);
	// gl.glTexCoord2f(0,0);
	// gl.glVertex2f(left, bottom);
	// gl.glTexCoord2f(1,0);
	// gl.glVertex2f(left + width, bottom);
	// gl.glTexCoord2f(1,1);
	// gl.glVertex2f(left + width, bottom + height);
	// gl.glTexCoord2f(0,1);
	// gl.glVertex2f(left, bottom + height);
	// gl.glEnd();
	// gl.glDisable(GL2.GL_TEXTURE_2D);
	//
	// }

}
