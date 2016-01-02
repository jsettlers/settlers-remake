package jsettlers.exceptionhandler;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;

import javax.swing.Icon;

/**
 * Icon for error popup
 * 
 * @author Andreas Butti
 */
public class ErrorIcon implements Icon {

	/**
	 * Size
	 */
	private int size = 120;

	@Override
	public void paintIcon(Component c, Graphics g1, int x, int y) {
		Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(5));

		Polygon p = new Polygon();
		p.addPoint(5, size - 5);
		p.addPoint(size - 5, size - 5);
		p.addPoint(size / 2, 5);
		p.addPoint(5, size - 5);

		g.drawPolygon(p);

		g.drawLine(size / 2, 30, size / 2, size - 40);
		g.drawLine(size / 2, size - 22, size / 2, size - 20);
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
