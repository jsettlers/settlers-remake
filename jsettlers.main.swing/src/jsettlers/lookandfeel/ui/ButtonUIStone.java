package jsettlers.lookandfeel.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;

import jsettlers.graphics.map.draw.ImageProvider;

/**
 * Button UI Implementation
 * 
 * @author Andreas Butti
 */
public class ButtonUIStone extends BasicButtonUI {

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage;

	/**
	 * Background Image for pressed button
	 */
	private final BufferedImage backgroundPressedImage;

	/**
	 * Constructor
	 */
	public ButtonUIStone() {
		ImageProvider prv = ImageProvider.getInstance();
		backgroundImage = prv.getGuiImage(3, 326).generateBufferedImage();
		backgroundPressedImage = prv.getGuiImage(3, 329).generateBufferedImage();
	}

	@Override
	public void installDefaults(AbstractButton b) {
		b.setFont(new Font("Sans", Font.PLAIN, 12));
		b.setForeground(Color.YELLOW);
		b.setPreferredSize(getPreferredSize(b));
	}

	@Override
	public void uninstallDefaults(AbstractButton b) {
		// super.uninstallDefaults(b);
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(180, 40);
	}

	@Override
	public void paint(Graphics g1, JComponent c) {
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();

		boolean pressed = false;
		// perform UI specific press action, e.g. Windows L&F shifts text
		if (model.isArmed() && model.isPressed()) {
			pressed = true;
			g.drawImage(backgroundPressedImage, 0, 0, c);
		} else {
			g.drawImage(backgroundImage, 0, 0, c);
		}

		int y = (b.getHeight() - g.getFontMetrics().getHeight());
		int x = 10;

		if (pressed) {
			x += 1;
			y += 1;
		}

		// TODO !!!!!!! FONT
		g.setFont(new Font("Sans", Font.PLAIN, 12));
		g.setColor(Color.BLACK);
		g.drawString(b.getText(), x + 1, y + 1);
		g.setColor(c.getForeground());
		g.drawString(b.getText(), x, y);
	}

}