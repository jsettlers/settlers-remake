package jsettlers.main.components.openpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.Icon;
import javax.swing.UIManager;

import jsettlers.lookandfeel.DrawHelper;

/**
 * Icon to clear search
 *
 * @author Andreas Butti
 */
public class ClearIcon implements Icon {

	/**
	 * If the mouse is over the icon
	 */
	private boolean hover = false;

	/**
	 * Background
	 */
	private final Color BACKGROUND = UIManager.getColor("ClearSearchIcon.backgroundColor");

	/**
	 * Background mouse over
	 */
	private final Color BACKGROUND_HOVER = UIManager.getColor("ClearSearchIcon.backgroundColorHover");

	/**
	 * Foreground
	 */
	private final Color FOREGROUND = UIManager.getColor("ClearSearchIcon.foregroundColor");

	@Override
	public void paintIcon(Component c, Graphics g1, int x, int y) {
		Graphics2D g = DrawHelper.antialiasingOn(g1);

		Stroke oldStroke = g.getStroke();

		Color background;

		if (hover) {
			g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			background = BACKGROUND_HOVER;
		} else {
			background = BACKGROUND;
		}
		if (background != null) {
			g.setColor(background);
			g.fillOval(x + 2, y + 2, 18, 18);
		}

		g.setColor(FOREGROUND);
		g.drawLine(x + 6, y + 6, x + 15, y + 15);
		g.drawLine(x + 15, y + 6, x + 6, y + 15);

		g.setStroke(oldStroke);
	}

	/**
	 * @param hover
	 *            If the mouse is over the icon
	 */
	public void setHover(boolean hover) {
		this.hover = hover;
	}

	/**
	 * @return If the mouse is over the icon
	 */
	public boolean isHover() {
		return hover;
	}

	@Override
	public int getIconWidth() {
		return 22;
	}

	@Override
	public int getIconHeight() {
		return 22;
	}

}
