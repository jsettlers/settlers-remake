/*******************************************************************************
 * Copyright (c) 2015 - 2016
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
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
			Graphics2D graphics = (Graphics2D) g1;
			graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics.setColor(Color.WHITE);
			graphics.setStroke(new BasicStroke(2));
			int w = getWidth();
			int h = getHeight();
			graphics.drawLine(0, 0, w - 1, h / 2);
			graphics.drawLine(w - 1, h / 2, 0, h);
		}
	}

	private final IPathPanelListener listener;

	/**
	 * Constructor
	 * 
	 * @param listener
	 *            Listener
	 */
	public PathPanel(IPathPanelListener listener) {
		this.listener = listener;
		setPath(new Object[0]);
	}

	/**
	 * Add a path element
	 * 
	 * @param path
	 *            Path name
	 * @param newPath
	 */
	private void addPath(String path, final Object[] newPath) {
		if (getComponentCount() > 0) {
			Separator separator = new Separator();
			separator.setPreferredSize(new Dimension(6, 25));
			this.add(separator);
		}

		JLabel pathLabel = new JLabel(path);
		pathLabel.setForeground(Color.WHITE);
		this.add(pathLabel);
		pathLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				listener.pathChanged(newPath);
			}
		});
	}

	@Override
	public void paintComponent(Graphics g1) {
		super.paintComponent(g1);
		Graphics2D grapics = (Graphics2D) g1;
		int width = getWidth();
		int height = getHeight();
		GradientPaint gradientPaint = new GradientPaint(0, 0, new Color(0x444444), 0, height, new Color(0x777777));
		grapics.setPaint(gradientPaint);
		grapics.fillRect(0, 0, width, height);
	}

	/**
	 * Sets the Path to the bar
	 * 
	 * @param path
	 *            All path objects
	 */
	public void setPath(Object[] path) {
		removeAll();

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

		if (getComponentCount() < 2) {
			// to make sure, the alignment is the same without a Separator
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(1, 25));
			panel.setOpaque(false);
			add(panel);
		}

		revalidate();
		repaint();
	}
}
