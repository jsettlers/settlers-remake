package go.graphics.swing.opengl;

import javax.media.opengl.GL2;

public final class TextureCalculator {
	private TextureCalculator() {

	}

	/**
	 * Makes the size a power of two, if needed.
	 * 
	 * @param gl
	 *            The gl context
	 * @param width
	 *            THe old size
	 * @return The good size.
	 */
	public static int supportedTextureSize(GL2 gl, int width) {
		if (gl.isExtensionAvailable("GL_ARB_texture_non_power_of_two")) {
			return width;
		} else {
			int real = 1;
			while (real < width) {
				real *= 2;
			}
			return real;
		}
	}
}
