package jsettlers.lookandfeel.ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

import jsettlers.graphics.map.draw.ImageProvider;

/**
 * Panel UI, draw background image
 * 
 * @author Andreas Butti
 */
public class PanelUiStoneBackground extends PanelUI {

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage;

	/**
	 * Constructor
	 */
	public PanelUiStoneBackground() {
		ImageProvider prv = ImageProvider.getInstance();
		backgroundImage = prv.getGuiImage(2, 29).generateBufferedImage();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(true);
	}

	@Override
	public void paint(Graphics g, JComponent c) {
		g.drawImage(backgroundImage, 0, 0, c.getWidth(), c.getHeight(), c);
	}

}
