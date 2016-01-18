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
import javax.swing.tree.DefaultMutableTreeNode;
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

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		File file = (File) node.getUserObject();

		if (file == null) {
			return this;
		}

		setIcon(fileSystemView.getSystemIcon(file));
		setText(fileSystemView.getSystemDisplayName(file));
		setToolTipText(file.getPath());

		if (node instanceof FilesystemTreeNode) {
			if (((FilesystemTreeNode) node).isSettlerFolder()) {
				setIcon(SETTLER_FOLDER_ICON);
			}
		}

		return this;
	}
}