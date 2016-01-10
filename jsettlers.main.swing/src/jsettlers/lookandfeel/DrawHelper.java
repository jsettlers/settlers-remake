package jsettlers.lookandfeel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Helper class for drawing
 * 
 * @author Andreas Butti
 */
public final class DrawHelper {

	/**
	 * Utility class
	 */
	private DrawHelper() {
	}

	/**
	 * Turn antialiasing on return casted instanced of graphics
	 * 
	 * @param g1
	 *            Graphics
	 * @return casted Graphics2D
	 */
	public static Graphics2D antialiasingOn(Graphics g1) {
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		return g;
	}

	/**
	 * Converts a given Image into a BufferedImage
	 *
	 * @param img
	 *            The Image to be converted
	 * @return The converted BufferedImage
	 */
	public static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}
}
