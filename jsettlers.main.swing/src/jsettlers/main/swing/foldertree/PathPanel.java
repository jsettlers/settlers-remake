package jsettlers.main.swing.foldertree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Panel with the path
 * 
 * @author Andreas Butti
 *
 */
public class PathPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	/**
	 * Separator ">" component
	 */
	private static class Separator extends JComponent {
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g1) {
			Graphics2D g = (Graphics2D) g1;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(2));
			int w = getWidth();
			int h = getHeight();
			g.drawLine(0, 0, w - 1, h / 2);
			g.drawLine(w - 1, h / 2, 0, h);
		}
	}

	/**
	 * Home Icon
	 */
	private final JComponent HOME = new JComponent() {
		private static final long serialVersionUID = 1L;

		{
			setPreferredSize(new Dimension(25, 25));
		}

		@Override
		public void paint(Graphics g1) {
			Graphics2D g = (Graphics2D) g1;
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke(2));
			int w = getWidth();
			int h = getHeight();

			g.drawLine(2, 10, w / 2, 1);
			g.drawLine(w / 2, 1, w - 3, 10);
			g.drawLine(5, 9, 5, h);
			g.drawLine(w - 6, 9, w - 6, h);
		}
	};

	/**
	 * Listener
	 */
	private final PathPanelListener listener;

	/**
	 * Constructor
	 * 
	 * @param listener
	 *            Listener
	 */
	public PathPanel(PathPanelListener listener) {
		add(HOME);
		this.listener = listener;
		HOME.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listener.goToHome();
			}
		});
	}

	/**
	 * Add a path element
	 * 
	 * @param path
	 *            Path name
	 * @param pathToJumpTo
	 */
	private void addPath(String path, Object[] pathToJumpTo) {
		Separator s = new Separator();
		s.setPreferredSize(new Dimension(6, 25));
		add(s);

		JLabel lb = new JLabel(path);
		lb.setForeground(Color.WHITE);
		add(lb);
		lb.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listener.jumpTo(pathToJumpTo);
			}
		});
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

	/**
	 * Sets the Path to the bar
	 * 
	 * @param path
	 *            All path objects
	 */
	public void setPath(Object[] path) {
		removeAll();
		add(HOME);

		List<Object> pathToJump = new ArrayList<>();

		for (Object o : path) {
			pathToJump.add(o);

			FilesystemTreeNode ft = (FilesystemTreeNode) o;
			File f = ft.getFile();
			if (f == null) {
				continue;
			}
			String name = f.getName();
			if (name.isEmpty()) {
				name = "/";
			}

			addPath(name, pathToJump.toArray());
		}

		revalidate();
		repaint();
	}
}
