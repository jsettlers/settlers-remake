package jsettlers.main.components.openpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.Icon;

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

	@Override
	public void paintIcon(Component c, Graphics g1, int x, int y) {
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		Stroke oldStroke = g.getStroke();

		if (hover) {
			g.setColor(Color.GRAY);
		} else {
			g.setColor(Color.LIGHT_GRAY);
		}
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.fillOval(x + 2, y + 2, 18, 18);
		g.setColor(Color.WHITE);
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
