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
	private final BufferedImage backgroundTextture = UiImageLoader.get("sr_ui_bg/sr_ui_background.png");

	/**
	 * Border texture for the border line
	 */
	private final BufferedImage borderTexture = UiImageLoader.get("texture-border.png");

	/**
	 * Leaf image at the right corner
	 */
	private final BufferedImage leavesLeft = UiImageLoader.get("sr_ui_leafs/sr_ui_leafs-left.png");

	/**
	 * Leaf image at the left side
	 */
	private final BufferedImage leavesRight = UiImageLoader.get("sr_ui_leafs/sr_ui_leafs-right.png");

	/**
	 * Current background cache
	 */
	private BufferedImage cachedBackground = null;

	/**
	 * Current foreground cache
	 */
	private BufferedImage cachedForeground = null;

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
	 * Paint the leaves in the foreground
	 * 
	 * @param graphics
	 *            Graphics
	 * @param component
	 *            Component
	 */
	public void paintForeground(Graphics graphics, JComponent component) {
		graphics.drawImage(cachedForeground, 0, 0, component);
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

		cachedBackground = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		cachedForeground = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = cachedBackground.createGraphics();

		// scale the background color image
		graphics.drawImage(backgroundTextture, 0, 0, width, height, component);

		BorderDrawer border = new BorderDrawer(graphics, 3, 0, 0, width, height);
		BufferedImage scaledTexture = DrawHelper.toBufferedImage(borderTexture.getScaledInstance(width, height, BufferedImage.SCALE_FAST));
		TexturePaint tp = new TexturePaint(scaledTexture, new Rectangle2D.Float(0, 0, width, height));
		border.setPaint(tp);
		border.drawRect();

		if (component instanceof SplitedBackgroundPanel) {
			border.drawVertical(((SplitedBackgroundPanel) component).getSplitPosition(), true);
		}

		graphics.dispose();

		graphics = cachedForeground.createGraphics();

		float factor = height / 2160f * 1.2f;
		factor = Math.min(factor, 0.535f);
		int w = (int) (leavesRight.getWidth() * factor);
		int h = (int) (leavesRight.getHeight() * factor);
		graphics.drawImage(leavesRight, width - w, 0, w, h, component);

		w = (int) (leavesLeft.getWidth() * factor);
		h = (int) (leavesLeft.getHeight() * factor);
		graphics.drawImage(leavesLeft, 0, 0, w, h, component);

		graphics.dispose();
	}
}
