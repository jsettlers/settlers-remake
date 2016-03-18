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
	private final BufferedImage backgroundTextture = UiImageLoader.get("test-pattern-bg.jpg");

	/**
	 * Background color image
	 */
	private final BufferedImage backgroundColor = UiImageLoader.get("stone-background-colors.jpg");

	/**
	 * Border texture for the border line
	 */
	private final BufferedImage borderTexture = UiImageLoader.get("texture-border.png");

	/**
	 * Leaf image at the right corner
	 */
	private final BufferedImage leaves1 = UiImageLoader.get("leaves1b.png");

	/**
	 * Leaf image at the left side
	 */
	private final BufferedImage leaves2 = UiImageLoader.get("leaves2.png");

	/**
	 * Leaf image at the bottom
	 */
	private final BufferedImage leaves3 = UiImageLoader.get("leaves3.png");

	/**
	 * Current background cache
	 */
	private BufferedImage cachedBackground = null;

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
	}

	@Override
	public void paint(Graphics graphics, JComponent component) {
		super.paint(graphics, component);

		Graphics2D graphics2D = DrawHelper.enableAntialiasing(graphics);
		if (cachedBackground == null || cachedBackground.getWidth() != component.getWidth()
				|| cachedBackground.getHeight() != component.getHeight()) {
			recreateBackgroundImage(component);
		}

		graphics2D.drawImage(cachedBackground, 0, 0, component);
	}

	/**
	 * Recreate the cached background image, if needed
	 * 
	 * @param component
	 *            The component
	 */
	private void recreateBackgroundImage(JComponent component) {
		final int width = component.getWidth();
		final int height = component.getHeight();

		cachedBackground = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D graphics = cachedBackground.createGraphics();

		// scale the background color image
		graphics.drawImage(backgroundColor, 0, 0, width, height, component);

		for (int x = 0; x < width; x += backgroundTextture.getWidth()) {
			for (int y = 0; y < height; y += backgroundTextture.getHeight()) {
				multiplyImage(cachedBackground, backgroundTextture, x, y);
			}
		}

		BorderDrawer border = new BorderDrawer(graphics, 3, 0, 0, width, height);
		BufferedImage scaledTexture = DrawHelper.toBufferedImage(borderTexture.getScaledInstance(width, height, BufferedImage.SCALE_FAST));
		TexturePaint tp = new TexturePaint(scaledTexture, new Rectangle2D.Float(0, 0, width, height));
		border.setPaint(tp);
		border.drawRect();

		if (component instanceof SplitedBackgroundPanel) {
			border.drawVertical(((SplitedBackgroundPanel) component).getSplitPosition(), true);
		}

		float factor = 0.2f;
		graphics.drawImage(leaves1, width - 120, -30, (int) (leaves1.getWidth() * factor), (int) (leaves1.getHeight() * factor), component);

		factor = 0.4f;
		graphics.drawImage(leaves2, -35, 45, (int) (leaves2.getWidth() * factor), (int) (leaves2.getHeight() * factor), component);

		factor = 0.3f;
		graphics.drawImage(leaves3, 60, height - 60, (int) (leaves3.getWidth() * factor), (int) (leaves3.getHeight() * factor), component);

		graphics.dispose();
	}

	/**
	 * Multiply two images, may can be done more efficient
	 * 
	 * @param targetImage
	 *            Source and target
	 * @param texture
	 *            source
	 * @param targetX
	 *            target start X coordinate
	 * @param targetY
	 *            target start y coordinate
	 */
	private void multiplyImage(BufferedImage targetImage, BufferedImage texture, int targetX, int targetY) {
		for (int x = 0; x < texture.getWidth(); x++) {
			for (int y = 0; y < texture.getHeight(); y++) {
				if (x + targetX >= targetImage.getWidth() || y + targetY >= targetImage.getHeight()) {
					continue;
				}

				int rgb1 = targetImage.getRGB(x + targetX, y + targetY);
				Color c1 = new Color(rgb1);
				float r1 = c1.getRed() / 255f;
				float g1 = c1.getGreen() / 255f;
				float b1 = c1.getBlue() / 255f;

				int rgb2 = texture.getRGB(x, y);
				Color c2 = new Color(rgb2);
				float addTexture = 0.15f;
				float r2 = c2.getRed() / 255f + addTexture;
				float g2 = c2.getGreen() / 255f + addTexture;
				float b2 = c2.getBlue() / 255f + addTexture;

				int r = (int) (Math.min(1.0f, r1 * r2) * 255f);
				int g = (int) (Math.min(1.0f, g1 * g2) * 255f);
				int b = (int) (Math.min(1.0f, b1 * b2) * 255f);

				targetImage.setRGB(x + targetX, y + targetY, r << 16 | g << 8 | b);
			}
		}
	}
}
