package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;
import jsettlers.common.position.FloatRectangle;
import jsettlers.graphics.image.Image;
import jsettlers.graphics.image.SettlerImage;
import jsettlers.graphics.image.SingleImage;

/**
 * This class provides utilities to draw buttons and images.
 * 
 * @author michael
 */
public final class ButtonDrawer {
	private static final int BORDER = 2;

	private ButtonDrawer() {
	}

	/**
	 * Draws a button at the given position.
	 * 
	 * @param gl
	 *            The gl context
	 * @param position
	 *            The position
	 * @param image
	 *            The button.
	 */
	public static void drawButton(GLDrawContext gl, FloatRectangle position, SingleImage image) {
		gl.color(.3f, .3f, .3f, 1);
		gl.fillQuad(position.getMinX(), position.getMinY(), position.getMaxX(),
		        position.getMaxY());

		gl.color(.8f, .8f, .8f, 1);
		gl.fillQuad(position.getMinX() + BORDER, position.getMinY() + BORDER,
		        position.getMaxX() - BORDER, position.getMaxY() - BORDER);

		drawScaledImage(gl, position.bigger(-BORDER), image);

	}

	/**
	 * Draws a scaled image.
	 * 
	 * @param gl
	 *            The context
	 * @param position
	 *            The position the image should fit at.
	 * @param image
	 *            The image to draw
	 */
	public static void drawScaledImage(GLDrawContext gl, FloatRectangle position,
			SingleImage image) {

		float maxXScale = (float) (position.getWidth()) / image.getWidth();
		float maxYScale = (float) (position.getHeight()) / image.getHeight();

		float scale = Math.min(maxYScale, maxXScale) * .9f;

		float halfWidth = 0.5f * scale * image.getWidth();
		float left = position.getCenterX() - halfWidth;
		float right = position.getCenterX() + halfWidth;
		float halfHeight = 0.5f * scale * image.getHeight();
		float top = position.getCenterY() + halfHeight;
		float bottom = position.getCenterY() - halfHeight;

		gl.color(1, 1, 1, 1);
		drawImage(gl, image, left, right, top, bottom);

		if (image instanceof SettlerImage
		        && ((SettlerImage) image).getTorso() != null) {
			gl.color(1, 0, 0, 1);
			drawImage(gl, ((SettlerImage) image).getTorso(), left, right, top,
			        bottom);
		}
	}

	private static void drawImage(GLDrawContext gl, Image image, float left, float right,
	        float top, float bottom) {
		//image.bind(gl);

		/*gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBegin(GL2.GL_QUADS);
		gl.glTexCoord2f(0, 0);
		gl.glVertex2f(left, bottom);
		gl.glTexCoord2f(1, 0);
		gl.glVertex2f(right, bottom);
		gl.glTexCoord2f(1, 1);
		gl.glVertex2f(right, top);
		gl.glTexCoord2f(0, 1);
		gl.glVertex2f(left, top);
		gl.glEnd();
		gl.glDisable(GL.GL_TEXTURE_2D);*/
	}
}
