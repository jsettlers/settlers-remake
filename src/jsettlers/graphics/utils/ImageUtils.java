package jsettlers.graphics.utils;

import go.graphics.GLDrawContext;
import jsettlers.graphics.image.Image;

public final class ImageUtils {
	private ImageUtils() {
	}
	/**
	 * Draws an image at a rect preserving the images aspect.
	 * 
	 * @param gl
	 * @param image
	 * @param left
	 * @param bottom
	 * @param right
	 * @param top
	 */
	public static void drawAtRectAspect(GLDrawContext gl, Image image, float left,
	        float bottom, float right, float top) {
		float imageaspect = image.getWidth() / image.getHeight();
		if ((right - left) / (top - bottom) > imageaspect) {
			// image is too wide
			float center = (left + right) / 2.0f;
			float halfwidth = (top - bottom) / 2.0f * imageaspect;
			left = center - halfwidth;
			right = center + halfwidth;
		} else {
			float center = (bottom + top) / 2.0f;
			float halfheight = (right - left) / 2.0f / imageaspect;
			bottom = center - halfheight;
			top = center + halfheight;
		}
		
		image.drawImageAtRect(gl, left, bottom, right, top);
	}
}
