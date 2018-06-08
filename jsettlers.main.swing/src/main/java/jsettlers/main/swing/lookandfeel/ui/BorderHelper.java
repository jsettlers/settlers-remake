package jsettlers.main.swing.lookandfeel.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

/**
 * Utility class to draw border
 * 
 * @author Andreas Butti
 */
public final class BorderHelper {

	/**
	 * Utility class to draw border
	 */
	private BorderHelper() {
	}

	/**
	 * Draw the border
	 * 
	 * @param g
	 *            Graphics
	 * @param c
	 *            Component
	 * @param border
	 *            Border
	 * @param scale
	 *            Border scale factor
	 */
	public static void drawBorder(Graphics g, JComponent c, BufferedImage[] border, float scale) {
		final int width = c.getWidth();
		final int height = c.getHeight();

		// top-left
		drawImage(g, c, scale, border[0], 0, 0);

		// top
		drawScaled(g, c, border[1], (int) (border[0].getWidth() * scale), (int) (width - scale * border[2].getWidth()), 0, (int) (scale * border[1].getHeight()));

		// top-right
		drawImage(g, c, scale, border[2], (int) (width - border[2].getWidth() * scale), 0);

		// right
		drawScaled(g, c, border[3], (int) (width - border[3].getWidth() * scale), width, (int) (border[2].getHeight() * scale), (int) (height - border[4].getHeight() * scale));

		// bottom-right
		drawImage(g, c, scale, border[4], (int) (width - border[4].getWidth() * scale), (int) (height - border[4].getWidth() * scale));

		// bottom
		drawScaled(g, c, border[5], (int) (border[0].getWidth() * scale), (int) (width - border[2].getWidth() * scale), (int) (height - border[5].getHeight() * scale), height);

		// bottom-left
		drawImage(g, c, scale, border[6], 0, (int) (height - border[6].getWidth() * scale));

		// left
		drawScaled(g, c, border[7], 0, (int) (border[7].getWidth() * scale), (int) (border[0].getHeight() * scale), (int) (height - border[6].getHeight() * scale));
	}

	public static void drawImage(Graphics g, JComponent c, float scale, BufferedImage img, int x, int y) {
		g.drawImage(img, x, y, (int) (x + img.getWidth() * scale), (int) (y + img.getHeight() * scale), 0, 0, img.getWidth(), img.getHeight(), c);
	}

	public static void drawScaled(Graphics g, JComponent c, BufferedImage img, int x, int x2, int y, int y2) {
		g.drawImage(img, x, y, x2, y2, 0, 0, img.getWidth(), img.getHeight(), c);
	}
}
