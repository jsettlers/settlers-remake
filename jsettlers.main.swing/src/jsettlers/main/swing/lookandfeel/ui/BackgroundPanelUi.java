/*******************************************************************************
 * Copyright (c) 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.main.swing.lookandfeel.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

import jsettlers.main.swing.lookandfeel.DrawHelper;
import jsettlers.main.swing.lookandfeel.components.SplitedBackgroundPanel;
import jsettlers.main.swing.lookandfeel.ui.img.UiImageLoader;

/**
 * Background Panel UI
 * 
 * @author Andreas Butti
 */
public class BackgroundPanelUi extends PanelUI {

	/**
	 * Background texture
	 */
	private BufferedImage backgroundTextture = UiImageLoader.get("test-pattern-bg.jpg");

	/**
	 * Current background cache
	 */
	private BufferedImage tmpBg = null;

	/**
	 * Background color image
	 */
	private BufferedImage backgroundColor = UiImageLoader.get("stone-background-colors.jpg");

	/**
	 * Border texture for the border line
	 */
	private BufferedImage borderTexture = UiImageLoader.get("texture-border.png");

	/**
	 * Leaf image at the right corner
	 */
	private BufferedImage leaves1 = UiImageLoader.get("leaves1b.png");

	/**
	 * Leaf image at the left side
	 */
	private BufferedImage leaves2 = UiImageLoader.get("leaves2.png");

	/**
	 * Leaf image at the bottom
	 */
	private BufferedImage leaves3 = UiImageLoader.get("leaves3.png");

	/**
	 * Constructor
	 */
	public BackgroundPanelUi() {
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
	}

	@Override
	public void paint(Graphics g1, JComponent c) {
		super.paint(g1, c);

		Graphics2D g = DrawHelper.antialiasingOn(g1);
		if (tmpBg == null || tmpBg.getWidth() != c.getWidth() || tmpBg.getHeight() != c.getHeight()) {
			recreateBgImage(c);
		}

		g.drawImage(tmpBg, 0, 0, c);
	}

	/**
	 * Recreate the cached background image, if needed
	 * 
	 * @param c
	 *            The component
	 */
	private void recreateBgImage(JComponent c) {
		tmpBg = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_RGB);

		Graphics2D g = tmpBg.createGraphics();

		// scale the background color image
		g.drawImage(backgroundColor, 0, 0, c.getWidth(), c.getHeight(), c);

		for (int x = 0; x < c.getWidth(); x += backgroundTextture.getWidth()) {
			for (int y = 0; y < c.getHeight(); y += backgroundTextture.getHeight()) {
				multiplyImage(tmpBg, backgroundTextture, x, y);
			}
		}

		BorderDrawer border = new BorderDrawer(g, 3, 0, 0, c.getWidth(), c.getHeight());
		BufferedImage scaledTexture = DrawHelper
				.toBufferedImage(borderTexture.getScaledInstance(c.getWidth(), c.getHeight(), BufferedImage.SCALE_FAST));
		TexturePaint tp = new TexturePaint(scaledTexture,
				new Rectangle2D.Float(0, 0, c.getWidth(), c.getHeight()));
		border.setPaint(tp);
		border.drawRect();

		if (c instanceof SplitedBackgroundPanel) {
			border.drawVertical(((SplitedBackgroundPanel) c).getSplitPosition(), true);
		}

		float factor = 0.2f;
		g.drawImage(leaves1, c.getWidth() - 120, -30, (int) (leaves1.getWidth() * factor), (int) (leaves1.getHeight() * factor), c);

		factor = 0.4f;
		g.drawImage(leaves2, -35, 45, (int) (leaves2.getWidth() * factor), (int) (leaves2.getHeight() * factor), c);

		factor = 0.3f;
		g.drawImage(leaves3, 60, c.getHeight() - 60, (int) (leaves3.getWidth() * factor), (int) (leaves3.getHeight() * factor), c);

		g.dispose();
	}

	/**
	 * Multiply two images, may can be done more efficient
	 * 
	 * @param tmpBg
	 *            Source and target
	 * @param textture
	 *            source
	 * @param targetX
	 *            tmpBg X
	 * @param targetY
	 *            tmpBg Y
	 */
	private void multiplyImage(BufferedImage tmpBg, BufferedImage textture, int targetX, int targetY) {
		for (int x = 0; x < textture.getWidth(); x++) {
			for (int y = 0; y < textture.getHeight(); y++) {
				if (x + targetX >= tmpBg.getWidth() || y + targetY >= tmpBg.getHeight()) {
					continue;
				}

				int rgb1 = tmpBg.getRGB(x + targetX, y + targetY);
				Color c1 = new Color(rgb1);
				float r1 = c1.getRed() / 255f;
				float g1 = c1.getGreen() / 255f;
				float b1 = c1.getBlue() / 255f;

				int rgb2 = textture.getRGB(x, y);
				Color c2 = new Color(rgb2);
				float addTexture = 0.15f;
				float r2 = c2.getRed() / 255f + addTexture;
				float g2 = c2.getGreen() / 255f + addTexture;
				float b2 = c2.getBlue() / 255f + addTexture;

				int r = (int) (Math.min(1.0f, r1 * r2) * 255f);
				int g = (int) (Math.min(1.0f, g1 * g2) * 255f);
				int b = (int) (Math.min(1.0f, b1 * b2) * 255f);

				tmpBg.setRGB(x + targetX, y + targetY, r << 16 | g << 8 | b);
			}
		}
	}
}
