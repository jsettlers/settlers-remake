package jsettlers.main.components.openpanel;

import javax.swing.*;
import java.awt.*;

/**
 * Icon to display search symbol
 *
 * @author Andreas Butti
 */
public class SearchIcon implements Icon {

	@Override
	public void paintIcon(Component c, Graphics g1, int x, int y) {
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

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
