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
	 * Constructor
	 * 
	 * @param size
	 *            Size of the icon
	 * @param color
	 *            Color of the icon
	 */
	public RectIcon(int size, Color color) {
		this.size = size;
		this.color = color;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		g.setColor(color);
		g.fillRect(x, y, size, size);
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
