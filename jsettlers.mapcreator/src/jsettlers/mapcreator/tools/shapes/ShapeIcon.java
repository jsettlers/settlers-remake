package jsettlers.mapcreator.tools.shapes;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.Icon;

/**
 * Icon for shapes
 * 
 * @author Andreas Butti
 *
 */
public abstract class ShapeIcon implements Icon, Cloneable {

	public static final ShapeIcon POINT = new ShapeIcon() {

		@Override
		protected void paint(Component c, Graphics2D g, int x, int y) {
			g.fillOval(x + 5, y + 5, 6, 6);
		}
	};

	public static final ShapeIcon LINE = new ShapeIcon() {

		@Override
		protected void paint(Component c, Graphics2D g, int x, int y) {
			g.drawLine(x + 1, y + 1, x + 15, y + 15);
		}

	};
	public static final ShapeIcon LINE_CIRCLE = new ShapeIcon() {

		@Override
		protected void paint(Component c, Graphics2D g, int x, int y) {
			g.fillOval(x + 1, y + 1, 14, 14);
		}

	};
	public static final ShapeIcon GRID_CIRCLE = new ShapeIcon() {
		@Override
		protected void paint(Component c, Graphics2D g, int x, int y) {
			g.fillOval(x + 1, y + 1, 14, 14);
		}

	};
	public static final ShapeIcon FUZZY_LINE_CIRCLE = new ShapeIcon() {
		@Override
		protected void paint(Component c, Graphics2D g, int x, int y) {
			g.drawLine(x + 1, y + 1, x + 15, y + 15);
		}

	};
	public static final ShapeIcon NOISY_LINE_CIRCLE = new ShapeIcon() {
		@Override
		protected void paint(Component c, Graphics2D g, int x, int y) {
			g.fillOval(x + 1, y + 1, 5, 5);
			g.fillOval(x + 10, y + 1, 5, 5);
			g.fillOval(x + 2, y + 4, 5, 5);
			g.fillOval(x + 5, y + 7, 5, 5);
			g.fillOval(x + 3, y + 2, 5, 5);
		}

	};

	/**
	 * Background color
	 */
	private static final Color BACKGROUND_COLOR_DISABLED = new Color(0x999999);

	/**
	 * Background color
	 */
	private static final Color BACKGROUND_COLOR = new Color(0xD6D9DF);

	/**
	 * Background color
	 */
	private static final Color BACKGROUND_COLOR_SELECTED = new Color(0x9EB1CD);

	/**
	 * Foreground color
	 */
	private static final Color COLOR = Color.BLACK;

	/**
	 * Foreground color
	 */
	private static final Color COLOR_DISABLED = Color.GRAY;

	/**
	 * Foreground color
	 */
	private static final Color COLOR_SELECTED = Color.BLACK;

	/**
	 * Selected icon
	 */
	private boolean selected = false;

	/**
	 * Disabled icon
	 */
	private boolean disabled = false;

	/**
	 * Constructor
	 */
	public ShapeIcon() {
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g2.setColor(getBackgroundColor());
		g2.fillRect(x, y, getIconWidth(), getIconHeight());
		g2.setColor(Color.WHITE);
		g2.drawRect(x, y, getIconWidth(), getIconHeight());
		g2.setColor(getColor());
		paint(c, g2, x, y);
	}

	/**
	 * @return Foreground color
	 */
	private Color getColor() {
		if (selected) {
			return COLOR_SELECTED;
		}
		if (disabled) {
			return COLOR_DISABLED;
		}
		return COLOR;
	}

	/**
	 * @return Background color of the icon
	 */
	private Color getBackgroundColor() {
		if (selected) {
			return BACKGROUND_COLOR_SELECTED;
		}
		if (disabled) {
			return BACKGROUND_COLOR_DISABLED;
		}
		return BACKGROUND_COLOR;
	}

	/**
	 * Paint the Icon
	 * 
	 * @param c
	 *            Component
	 * @param g
	 *            Graphics2D
	 * @param x
	 *            X Pos
	 * @param y
	 *            Y Pos
	 */
	protected void paint(Component c, Graphics2D g, int x, int y) {
	}

	@Override
	public int getIconWidth() {
		return 16;
	}

	@Override
	public int getIconHeight() {
		return 16;
	}

	/**
	 * @return Disabled instance of this icon
	 */
	public ShapeIcon createDisabledIcon() {
		ShapeIcon copy = this;
		try {
			copy = (ShapeIcon) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		copy.disabled = true;
		return copy;
	}

	/**
	 * @return Selected instance of this icon
	 */
	public ShapeIcon createSelectedIcon() {
		ShapeIcon copy = this;
		try {
			copy = (ShapeIcon) this.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		copy.selected = true;
		return copy;
	}

}
