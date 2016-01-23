package jsettlers.lookandfeel.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.plaf.basic.BasicButtonUI;

import jsettlers.graphics.map.draw.ImageProvider;
import jsettlers.lookandfeel.DrawHelper;

/**
 * Button UI Implementation
 *
 * @author Andreas Butti
 */
public class ButtonUiStoneOriginalBg extends BasicButtonUI {

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
	public ButtonUiStoneOriginalBg() {
		ImageProvider prv = ImageProvider.getInstance();
		backgroundImage = prv.getGuiImage(3, 326).generateBufferedImage();
		backgroundPressedImage = prv.getGuiImage(3, 329).generateBufferedImage();
	}

	@Override
	public void installDefaults(AbstractButton b) {
		b.setFont(UIDefaults.FONT);
		b.setForeground(UIDefaults.LABEL_TEXT_COLOR);
	}

	@Override
	public void uninstallDefaults(AbstractButton b) {
	}

	@Override
	public void paint(Graphics g1, JComponent c) {
		Graphics2D g = DrawHelper.antialiasingOn(g1);

		AbstractButton b = (AbstractButton) c;
		ButtonModel model = b.getModel();

		boolean down;
		if (c instanceof JToggleButton) {
			down = ((JToggleButton) c).isSelected();
		} else {
			down = model.isArmed() && model.isPressed();
		}

		if (down) {
			g.drawImage(backgroundPressedImage, 0, 0, b.getWidth(), b.getHeight(), c);
		} else {
			g.drawImage(backgroundImage, 0, 0, b.getWidth(), b.getHeight(), c);
		}

		int y = b.getHeight() / 2 + (g.getFontMetrics().getAscent() / 2);
		int x = 10;

		if (down) {
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