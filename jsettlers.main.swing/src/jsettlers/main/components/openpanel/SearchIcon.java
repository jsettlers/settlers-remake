package jsettlers.main.components.openpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import javax.swing.Icon;

import jsettlers.lookandfeel.DrawHelper;

/**
 * Icon to display search symbol
 *
 * @author Andreas Butti
 */
public class SearchIcon implements Icon {

	@Override
	public void paintIcon(Component c, Graphics g1, int x, int y) {
		Graphics2D g = DrawHelper.antialiasingOn(g1);

		Stroke oldStroke = g.getStroke();

		g.setColor(Color.LIGHT_GRAY);
		g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawOval(x + 3, y + 3, 12, 12);
		g.drawLine(x + 15, y + 15, x + 20, y + 20);

		g.setStroke(oldStroke);
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
