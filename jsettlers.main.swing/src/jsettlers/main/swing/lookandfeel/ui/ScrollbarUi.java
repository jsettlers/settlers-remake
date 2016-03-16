package jsettlers.main.swing.lookandfeel.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.basic.BasicScrollBarUI;

import jsettlers.main.swing.lookandfeel.DrawHelper;
import jsettlers.main.swing.lookandfeel.ui.img.UiImageLoader;

/**
 * Scrollbar UI implementation, not finished yet
 * 
 * @author Andreas Butti
 */
public class ScrollbarUi extends BasicScrollBarUI {

	/**
	 * Background Image
	 */
	private static final BufferedImage BACKGROUND_IMAGE = UiImageLoader.get("scrollbar-noise.png");

	/**
	 * Pattern to paint background
	 */
	private static final TexturePaint BACKGROUND_TEXTURE = new TexturePaint(BACKGROUND_IMAGE,
			new Rectangle2D.Float(0, 0, BACKGROUND_IMAGE.getWidth(), BACKGROUND_IMAGE.getHeight()));

	/**
	 * Color for the inner part of the slider
	 */
	private static final Color SLIDER_INNER_COLOR = new Color(0xD6, 0xE3, 0xF6, 120);

	/**
	 * Constructor
	 */
	public ScrollbarUi() {
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		// The scrollbar is transparent!
		c.setOpaque(false);
	}

	@Override
	protected void installDefaults() {
		super.installDefaults();

		incrGap = 0;
		decrGap = 0;
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
			return new Dimension(scrollBarWidth, scrollBarWidth * 3 + 10);
		} else { // Horizontal
			return new Dimension(scrollBarWidth * 3 + 10, scrollBarWidth);
		}
	}

	@Override
	protected JButton createDecreaseButton(int orientation) {
		return new ScrollbarUiButton(orientation, UIDefaults.ARROW_COLOR);
	}

	@Override
	protected JButton createIncreaseButton(int orientation) {
		return new ScrollbarUiButton(orientation, UIDefaults.ARROW_COLOR);
	}

	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		// nothing to paint
	}

	@Override
	protected void paintThumb(Graphics g1, JComponent c, Rectangle thumbBounds) {
		Graphics2D g = DrawHelper.antialiasingOn(g1);
		g.translate(thumbBounds.x, thumbBounds.y);

		int margin = 3;

		Stroke originalStroke = g.getStroke();
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));
		g.setPaint(BACKGROUND_TEXTURE);
		drawThumbRect(thumbBounds, g, margin);

		g.setStroke(originalStroke);
		g.setPaint(SLIDER_INNER_COLOR);
		drawThumbRect(thumbBounds, g, margin);

		g.translate(-thumbBounds.x, -thumbBounds.y);
	}

	/**
	 * Draws the Thumb rect
	 * 
	 * @param thumbBounds
	 * @param g
	 */
	private void drawThumbRect(Rectangle thumbBounds, Graphics2D g, int margin) {
		g.drawRect(1 + margin, 1 + margin, thumbBounds.width - 4 - (2 * margin), thumbBounds.height - 3 - (2 * margin));
	}

	@Override
	protected Dimension getMinimumThumbSize() {
		return new Dimension(scrollBarWidth, scrollBarWidth);
	}
}