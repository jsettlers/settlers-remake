package jsettlers.lookandfeel.ui;

import jsettlers.lookandfeel.ui.img.UiImageLoader;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Stone toggle Button UI
 * 
 * @author Andreas Butti
 */
public class ButtonUiStone extends BasicToggleButtonUI {

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage = UiImageLoader.get("granit_texture1.png");

	/**
	 * Button down
	 */
	private final Border borderDown;

	/**
	 * Button up
	 */
	private final Border borderUp;

	/**
	 * Offset
	 */
	private int shiftOffset = 0;

	/**
	 * Constructor
	 * 
	 * @param borderUp
	 *            Button up
	 * @param borderDown
	 *            Button down
	 */
	public ButtonUiStone(Border borderUp, Border borderDown) {
		this.borderUp = borderUp;
		this.borderDown = borderDown;
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
		c.setBorder(borderUp);
		c.setFont(UIDefaults.FONT_PLAIN);
		c.setForeground(UIDefaults.LABEL_TEXT_COLOR);
	}

	@Override
	public void update(Graphics g, JComponent c) {
		int w = c.getWidth();
		int h = c.getHeight();

		Integer sx0 = (Integer) c.getClientProperty("ButtonUiStone.bgx");
		Integer sy0 = (Integer) c.getClientProperty("ButtonUiStone.bgy");

		if (sx0 == null || sy0 == null) {
			sx0 = (int) Math.random() * backgroundImage.getWidth();
			sy0 = (int) Math.random() * backgroundImage.getHeight();
			c.putClientProperty("ButtonUiStone.bgx", sx0);
			c.putClientProperty("ButtonUiStone.bgy", sy0);
		}

		int sx1 = sx0;
		int sy1 = sy0;

		for (int x = sx1; x < c.getWidth(); x += backgroundImage.getWidth()) {
			for (int y = sy1; y < c.getWidth(); y += backgroundImage.getHeight()) {
				g.drawImage(backgroundImage, x, y, c);
			}
		}

		boolean down;
		if (c instanceof JToggleButton) {
			down = ((JToggleButton) c).isSelected();
		} else {
			down = false;
			AbstractButton b = (AbstractButton) c;
			ButtonModel model = b.getModel();
			down = model.isArmed() && model.isPressed();
		}

		if (down) {
			c.setBorder(borderDown);
			g.setColor(new Color(0, 0, 0, 90));
			g.fillRect(2, 2, w - 4, h - 4);
			shiftOffset = 1;
		} else {
			c.setBorder(borderUp);
			shiftOffset = 0;
		}

		super.update(g, c);
	}

	@Override
	protected int getTextShiftOffset() {
		return shiftOffset;
	}

	@Override
	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
		FontMetrics fm = SwingUtilities2.getFontMetrics(b, g);
		int mnemonicIndex = b.getDisplayedMnemonicIndex();

		g.setColor(Color.BLACK);
		SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemonicIndex,
				textRect.x + getTextShiftOffset() + 1,
				textRect.y + fm.getAscent() + getTextShiftOffset() + 1);

		g.setColor(b.getForeground());
		SwingUtilities2.drawStringUnderlineCharAt(b, g, text, mnemonicIndex,
				textRect.x + getTextShiftOffset(),
				textRect.y + fm.getAscent() + getTextShiftOffset());
	}

	@Override
	protected void paintButtonPressed(Graphics g, AbstractButton b) {
	}
}
