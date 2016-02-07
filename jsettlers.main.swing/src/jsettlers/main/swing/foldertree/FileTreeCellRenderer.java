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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * Tree cell renderer to display filesystem
 * 
 * @author Andreas Butti
 *
 */
public class FileTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 1L;

	/**
	 * Filesystem view
	 */
	private final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	/**
	 * Icon to display this is a settler folder
	 */
	private static final Icon SETTLER_FOLDER_ICON = new Icon() {

		@Override
		public void paintIcon(Component c, Graphics g1, int x, int y) {
			Graphics2D g = (Graphics2D) g1;
			g.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.RED);
			Polygon arrow = new Polygon();
			arrow.addPoint(16, 8);
			arrow.addPoint(10, 16);
			arrow.addPoint(10, 11);
			arrow.addPoint(0, 11);
			arrow.addPoint(0, 5);
			arrow.addPoint(10, 5);
			arrow.addPoint(10, 0);
			g.fill(arrow);
		}

		@Override
		public int getIconWidth() {
			return 16;
		}

		@Override
		public int getIconHeight() {
			return 16;
		}
	};

	/**
	 * Constructor
	 */
	public FileTreeCellRenderer() {
	}

	@Override
	public JComponent getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (value instanceof FilesystemTreeNode) {
			FilesystemTreeNode node = (FilesystemTreeNode) value;
			File file = node.getFile();
			if (file == null) {
				return this;
			}

			setIcon(fileSystemView.getSystemIcon(file));
			setText(fileSystemView.getSystemDisplayName(file));
			setToolTipText(file.getPath());

			if (node.isSettlerFolder()) {
				setIcon(SETTLER_FOLDER_ICON);
			}
		}

		return this;
	}
}