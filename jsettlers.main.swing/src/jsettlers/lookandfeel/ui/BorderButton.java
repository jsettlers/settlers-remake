package jsettlers.lookandfeel.ui;

import javax.swing.border.Border;
import java.awt.*;

/**
 * Border for Button 3D effects
 * 
 * @author Andreas Butti
 *
 */
public class BorderButton implements Border {

	/**
	 * Definitions for borders
	 */
	private static class ColorTypes {
		/**
		 * Top colors, from top down
		 */
		private final Color[] top;

		/**
		 * Left colors, from left to right
		 */
		private final Color[] left;

		/**
		 * Bottom colors, down to up
		 */
		private final Color[] bottom;

		/**
		 * Right colors, from right to left
		 */
		private final Color[] right;

		/**
		 * Constructor
		 * 
		 * @param top
		 *            Color order
		 * @param left
		 *            Color order
		 * @param bottom
		 *            Color order
		 * @param right
		 *            Color order
		 */
		public ColorTypes(Color[] top, Color[] left, Color[] bottom, Color[] right) {
			this.top = top;
			this.left = left;
			this.bottom = bottom;
			this.right = right;
		}

	}

	/**
	 * Button released
	 */
	private static final ColorTypes UP = new ColorTypes(
			new Color[] { new Color(0, 0, 0, 40), new Color(0xff, 0xff, 0xff, 180), new Color(0xff, 0xff, 0xff, 160),
					new Color(0xff, 0xff, 0xff, 60) },
			new Color[] { new Color(0, 0, 0, 40), new Color(0xff, 0xff, 0xff, 180), new Color(0xff, 0xff, 0xff, 160),
					new Color(0xff, 0xff, 0xff, 60) },
			new Color[] { Color.BLACK, new Color(0, 0, 0, 120), new Color(0, 0, 0, 40) },
			new Color[] { Color.BLACK, new Color(0, 0, 0, 120), new Color(0, 0, 0, 40) });

	/**
	 * Button pressed
	 */
	private static final ColorTypes DOWN = new ColorTypes(
			new Color[] { new Color(0, 0, 0, 40), new Color(0xff, 0xff, 0xff, 180) },
			new Color[] { new Color(0, 0, 0, 40), new Color(0xff, 0xff, 0xff, 180) },
			new Color[] { Color.BLACK, new Color(0, 0, 0, 120) },
			new Color[] { Color.BLACK, new Color(0, 0, 0, 120) });

	/**
	 * true down: false up
	 */
	private boolean down;

	/**
	 * Constructor
	 * 
	 * @param down
	 *            true down: false up
	 */
	public BorderButton(boolean down) {
		this.down = down;
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		int w = c.getWidth();
		int h = c.getHeight();

		/**
		 * Button layout
		 * 
		 * <pre>
		 * --012345--------------------------4321
		 * 0 AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA
		 * 1 BCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCM
		 * 2 BDEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEELM
		 * 3 BDFGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGGLM
		 * 4 BDFZ------------------------------LM
		 * 4 BDFZ------------------------------LM
		 * 3 BDFZKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKLM
		 * 2 BDFIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIM
		 * 1 BHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH
		 * 
		 * </pre>
		 */

		ColorTypes current;
		if (down) {
			current = DOWN;
		} else {
			current = UP;
		}

		for (int i = 0; i < current.top.length; i++) {
			g.setColor(current.top[i]);
			g.drawLine(i, i, w - i, i);
		}

		for (int i = 0; i < current.bottom.length; i++) {
			g.setColor(current.bottom[i]);
			g.drawLine(i, h - i - 1, w - i, h - i - 1);
		}

		for (int i = 0; i < current.left.length; i++) {
			g.setColor(current.left[i]);
			g.drawLine(i, i, i, h - i - 1);
		}

		for (int i = 0; i < current.right.length; i++) {
			g.setColor(current.right[i]);
			g.drawLine(w - i - 1, i + 1, w - i - 1, h - i - 1);
		}

	}

	@Override
	public Insets getBorderInsets(Component c) {
		return new Insets(7, 7, 7, 7);
	}

	@Override
	public boolean isBorderOpaque() {
		return true;
	}

}
