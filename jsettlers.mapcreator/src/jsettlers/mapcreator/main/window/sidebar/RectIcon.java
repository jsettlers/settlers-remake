package jsettlers.mapcreator.main.window.sidebar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * Rectangle icon
 * 
 * @author Andreas Butti
 */
public class RectIcon implements Icon {
	/**
	 * Size of the icon
	 */
	private int size;

	/**
	 * Color of the icon
	 */
	private Color color;

	/**
	 * Border color, <code>null</code> for no boarder
	 */
	private Color borderColor;

	/**
	 * Constructor
	 * 
	 * @param size
	 *            Size of the icon
	 * @param color
	 *            Color of the icon
	 */
	public RectIcon(int size, Color color) {
		this(size, color, null);
	}

	/**
	 * Constructor
	 * 
	 * @param size
	 *            Size of the icon
	 * @param color
	 *            Color of the icon
	 * @param borderColor
	 *            Border color, <code>null</code> for no boarder
	 */
	public RectIcon(int size, Color color, Color borderColor) {
		this.size = size;
		this.color = color;
		this.borderColor = borderColor;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRect(x, y, size, size);

		if (borderColor != null) {
			g.setColor(borderColor);
			g.drawRect(x, y, size, size);
		}
	}

	@Override
	public int getIconWidth() {
		return size;
	}

	@Override
	public int getIconHeight() {
		return size;
	}

}
