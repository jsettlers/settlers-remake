package jsettlers.lookandfeel.ui;

import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.lookandfeel.DrawHelper;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.image.BufferedImage;

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
		b.setFont(UIDefaults.FONT);
		b.setForeground(UIDefaults.LABEL_TEXT_COLOR);
		b.setPreferredSize(getPreferredSize(b));
	}

	@Override
	public void uninstallDefaults(AbstractButton b) {
	}

	@Override
	public void paint(Graphics g1, JComponent c) {
		Graphics2D g = DrawHelper.antialiasingOn(g1);

		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();

		boolean pressed = false;
		// perform UI specific press action, e.g. Windows L&F shifts text
		if (model.isArmed() && model.isPressed()) {
			pressed = true;
			g.drawImage(backgroundPressedImage, 0, 0, b.getWidth(), b.getHeight(), c);
		} else {
			g.drawImage(backgroundImage, 0, 0, b.getWidth(), b.getHeight(), c);
		}

		int y = b.getHeight() / 2 + (g.getFontMetrics().getAscent() / 2);
		int x = 10;

		if (pressed) {
			x += 1;
			y += 1;
		}

		g.setFont(c.getFont());

		// draw shadow
		g.setColor(Color.BLACK);
		g.drawString(b.getText(), x + 1, y + 1);
		g.setColor(c.getForeground());
		g.drawString(b.getText(), x, y);
	}

}