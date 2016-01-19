package jsettlers.main.swing.foldertree;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel with the path
 * 
 * @author Andreas Butti
 *
 */
public class PathPanel extends JPanel {

	/**
	 * Constructor
	 */
	public PathPanel() {
		addPath("asfdasfd");
		addPath("asfdasfd");
		addPath("asfdasfd");
		addPath("asfdasfd");
		addPath("asfdasfd");
	}

	private void addPath(String path) {
		JLabel lb = new JLabel(path);
		lb.setForeground(Color.WHITE);
		add(lb);
	}

	@Override
	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D g = (Graphics2D) g1;
		int w = getWidth();
		int h = getHeight();
		GradientPaint gp = new GradientPaint(
				0, 0, new Color(0x444444), 0, h, new Color(0x777777));
		g.setPaint(gp);
		g.fillRect(0, 0, w, h);
	}

	public void setPath(Object[] path) {
		// TODO Auto-generated method stub

	}

}
