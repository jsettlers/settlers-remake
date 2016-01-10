package jsettlers.lookandfeel.ui;

import jsettlers.lookandfeel.DrawHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Scroll bar up / down / left / right button
 * 
 * @author Andreas Butti
 */
public class ScrollBarButton extends JComponent {
	private static final long serialVersionUID = 1L;

	/**
	 * Orientation of this Button
	 */
	public enum Orientation {
		/**
		 * Up
		 */
		UP,

		/**
		 * Left
		 */
		LEFT,

		/**
		 * Right
		 */
		RIGHT,

		/**
		 * Down
		 */
		DOWN
	}

	/**
	 * Orientation
	 */
	private Orientation orientation = Orientation.UP;

	/**
	 * Fill color
	 */
	private final static Color INNER_COLOR = new Color(0x7b797b);

	/**
	 * Fill color
	 */
	private final static Color BRIGHT_COLOR = new Color(0xa5a6a5);

	/**
	 * Fill color
	 */
	private final static Color DARK_COLOR = new Color(0x525552);

	/**
	 * Constructor
	 */
	public ScrollBarButton() {
		setOpaque(false);
	}

	/**
	 * @param orientation
	 *            Orientation
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		revalidate();
		repaint();
	}

	@Override
	public void paint(Graphics g1) {
		Graphics2D g = DrawHelper.antialiasingOn(g1);

		// Size: width: 1, height: 2
		// a
		// **-
		// ****-
		// ******c
		// ******d
		// ****=
		// **=
		// b

		Point a;
		Point b;
		Point c;
		Point d;

		if (orientation == Orientation.UP) {
			a = new Point(1, getHeight() - 1);
			b = new Point(getWidth() - 1, getHeight() - 1);
			c = new Point(getWidth() / 2 - 1, 1);
			d = new Point(getWidth() / 2 + 1, 1);
		} else if (orientation == Orientation.DOWN) {
			a = new Point(getWidth() - 1, 1);
			b = new Point(1, 1);
			c = new Point(getWidth() / 2 + 1, getHeight() - 1);
			d = new Point(getWidth() / 2 + 1, getHeight() - 1);
		} else if (orientation == Orientation.LEFT) {
			// TODO !!!!!!!
			a = new Point(0, 0);
			b = new Point(0, getHeight());
			c = new Point(getWidth() / 2, getHeight() / 2 - 1);
			d = new Point(getWidth() / 2, getHeight() / 2 + 1);
		} else {
			// TODO !!!!!!!
			a = new Point(0, 0);
			b = new Point(0, getHeight());
			c = new Point(getWidth() / 2, getHeight() / 2 - 1);
			d = new Point(getWidth() / 2, getHeight() / 2 + 1);
		}

		g.setColor(INNER_COLOR);
		Polygon p = new Polygon();
		p.addPoint(a.x, a.y);
		p.addPoint(b.x, b.y);
		p.addPoint(d.x, d.y);
		p.addPoint(c.x, c.y);
		g.fillPolygon(p);

		if (orientation == Orientation.UP) {
			g.setColor(DARK_COLOR);
			g.drawLine(a.x, a.y, c.x, c.y);
			g.setColor(BRIGHT_COLOR);
			g.drawLine(d.x, d.y, b.x, b.y);
			g.drawLine(a.x, a.y, b.x, b.y);
		} else if (orientation == Orientation.DOWN) {
			g.setColor(DARK_COLOR);
			g.drawLine(a.x, a.y, b.x, b.y);
			g.drawLine(d.x, d.y, b.x, b.y);
			g.setColor(BRIGHT_COLOR);
			g.drawLine(a.x, a.y, c.x, c.y);
		}

	}

}
