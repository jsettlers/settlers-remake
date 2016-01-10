package jsettlers.lookandfeel.ui;

import jsettlers.graphics.map.draw.ImageProvider;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicLabelUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Label UI, with different stylings
 * 
 * @author Andreas Butti
 *
 */
public class SettlerLabelUi extends BasicLabelUI {

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
	private Border border = BorderFactory.createEmptyBorder(2, 5, 2, 5);

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
	public SettlerLabelUi(Color foregroundColor, int x, int y, int width, int heigth) {
		this.foregroundColor = foregroundColor;
		ImageProvider prv = ImageProvider.getInstance();
		BufferedImage img = prv.getGuiImage(2, 13).generateBufferedImage();

		backgroundImage = img.getSubimage(x, y, width, heigth);
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setForeground(foregroundColor);
		c.setPreferredSize(getPreferredSize(c));
		c.setBorder(border);
		c.setFont(UIDefaults.FONT);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(backgroundImage.getWidth(), backgroundImage.getHeight());
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		g.drawImage(backgroundImage, 0, 0, c);

		super.paint(g, c);
	}

}
