package jsettlers.lookandfeel.ui;

import jsettlers.lookandfeel.ui.img.UiImageLoader;
import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.plaf.basic.BasicToggleButtonUI;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Stone toggle Button UI
 * 
 * @author Andreas Butti
 */
public class ToggleButtonUiStone extends BasicToggleButtonUI {

	/**
	 * Background Image
	 */
	private final BufferedImage backgroundImage = UiImageLoader.get("granit_texture1.png");

	/**
	 * Button down
	 */
	private static final BorderButton BUTTON_DOWN = new BorderButton(true);

	/**
	 * Button up
	 */
	private static final BorderButton BUTTON_UP = new BorderButton(false);

	/**
	 * Offset
	 */
	private int shiftOffset = 0;

	/**
	 * Constructor
	 */
	public ToggleButtonUiStone() {
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setOpaque(false);
		c.setBorder(BUTTON_UP);
		c.setFont(UIDefaults.FONT_PLAIN);
		c.setForeground(UIDefaults.LABEL_TEXT_COLOR);
	}

	@Override
	public void update(Graphics g, JComponent c) {
		int w = c.getWidth();
		int h = c.getHeight();

		// TODO: Choose random!
		int sx1 = 25;
		int sy1 = 25;

		g.drawImage(backgroundImage, 0, 0, w, h, sx1, sy1, sx1 + w, sy1 + h, c);

		if (((JToggleButton) c).isSelected()) {
			c.setBorder(BUTTON_DOWN);
			g.setColor(new Color(0, 0, 0, 60));
			g.fillRect(2, 2, w - 4, h - 4);
			shiftOffset = 1;
		} else {
			c.setBorder(BUTTON_UP);
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
