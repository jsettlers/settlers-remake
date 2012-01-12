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
	 * @param aleft
	 * @param abottom
	 * @param aright
	 * @param atop
	 */
	public static void drawAtRectAspect(GLDrawContext gl, Image image,
	        float aleft, float abottom, float aright, float atop) {
		float imageaspect = image.getWidth() / image.getHeight();
		float left, right, top, bottom;
		if ((aright - aleft) / (atop - abottom) > imageaspect) {
			// image is too wide
			float center = (aleft + aright) / 2.0f;
			float halfwidth = (atop - abottom) / 2.0f * imageaspect;
			left = center - halfwidth;
			right = center + halfwidth;
			bottom = abottom;
			top = atop;
		} else {
			float center = (abottom + atop) / 2.0f;
			float halfheight = (aright - aleft) / 2.0f / imageaspect;
			left = aleft;
			right = aright;
			bottom = center - halfheight;
			top = center + halfheight;
		}

		image.drawImageAtRect(gl, left, bottom, right, top);
	}
}
