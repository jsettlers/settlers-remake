package jsettlers.main.swing.lookandfeel.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicLabelUI;

import jsettlers.graphics.map.draw.ImageProvider;

/**
 * Label UI, with different stylings
 * 
 * @author Andreas Butti
 *
 */
public class SettlerLabelDynamicUi extends BasicLabelUI {

	/**
	 * Foreground color of the Label
	 */
	private final Color foregroundColor;

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage;

	/**
	 * Border
	 */
	private final Border border = BorderFactory.createEmptyBorder(2, 5, 2, 5);

	/**
	 * Constructor
	 * 
	 * @param foregroundColor
	 *            Foreground color of the Label
	 * @param x
	 *            Subimage position / size
	 * @param y
	 *            Subimage position / size
	 * @param width
	 *            Subimage position / size
	 * @param heigth
	 *            Subimage position / size
	 */
	public SettlerLabelDynamicUi(Color foregroundColor, int x, int y, int width, int heigth) {
		this.foregroundColor = foregroundColor;
		ImageProvider prv = ImageProvider.getInstance();
		BufferedImage img = prv.getGuiImage(2, 13).generateBufferedImage();

		backgroundImage = img.getSubimage(x, y, width, heigth);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(foregroundColor);
		c.setBorder(border);
		c.setFont(UIDefaults.FONT);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(super.getPreferredSize(c).width, backgroundImage.getHeight());
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		int w = c.getWidth();
		int bgw = backgroundImage.getWidth();
		int bgh = backgroundImage.getHeight();

		// left border
		g.drawImage(backgroundImage, 0, 0, c);

		// draw center
		for (int x = bgw; x < w; x += bgw - 6) {
			g.drawImage(backgroundImage, x, 0, x + bgw - 6, bgh, 3, 0, bgw - 3, bgh, c);
		}

		// draw right border
		g.drawImage(backgroundImage, w, 0, w - 2, bgh, bgw - 2, 0, bgw, bgh, c);
		super.paint(g, c);
	}

}
