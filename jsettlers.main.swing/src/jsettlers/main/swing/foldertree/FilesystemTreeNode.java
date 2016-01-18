package jsettlers.main.swing.foldertree;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * Node to display file / folders
 * 
 * @author Andreas Butti
 */
public class FilesystemTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	/**
	 * If the children are already loaded
	 */
	private boolean loaded = false;

	/**
	 * If this is a settler folder
	 */
	private boolean settlerFolder = false;

	/**
	 * To expand the node on the first selection
	 */
	private boolean wasExpanded = false;

	/**
	 * Tmp loaded child list, has to be Vector because of DefaultMutableTreeNode
	 */
	private final Vector<FilesystemTreeNode> list = new Vector<>();

	/**
	 * Constructor
	 * 
	 * @param file
	 *            The file to represent
	 */
	public FilesystemTreeNode(File file) {
		super(file);
		if (file == null) {
			throw new IllegalArgumentException("file == null");
		}
	}

	/**
	 * @return The file
	 */
	public File getFile() {
		return (File) getUserObject();
	}

	/**
	 * @return Root node
	 */
	private RootTreeNode findRoot() {
		TreeNode node = getParent();
		while (true) {
			if (node == null) {
				return null;
			}
			if (node instanceof RootTreeNode) {
				return (RootTreeNode) node;
			}
			node = node.getParent();
		}
	}

	/**
	 * Load the children of this folder, if a folder
	 */
	private void loadChildren() {
		RootTreeNode root = findRoot();
		if (root == null) {
			loadChildren1();
			applyLoadedChildren();
		} else {
			root.loadAsynchron(this);
		}
	}

	/**
	 * @return true if this node already was expanded
	 */
	public boolean wasExpanded() {
		return wasExpanded;
	}

	/**
	 * @param wasExpanded
	 *            true if this node already was expanded
	 */
	public void setWasExpanded(boolean wasExpanded) {
		this.wasExpanded = wasExpanded;
	}

	/**
	 * @return if this is a settler folder
	 */
	public boolean isSettlerFolder() {
		return settlerFolder;
	}

	/**
	 * Load children if not loaded
	 */
	private synchronized void loadIfNotLoaded() {
		if (!loaded) {
			loaded = true;

			loadChildren();
		}
	}

	@Override
	public boolean isLeaf() {
		loadIfNotLoaded();
		if (children != null) {
			return children.isEmpty();
		}
		return true;
	}

	@Override
	public int getChildCount() {
		loadIfNotLoaded();
		return super.getChildCount();
	}

	/**
	 * Load the children to a tmp attribute
	 */
	public void loadChildren1() {
		File file = getFile();

		if (!file.isDirectory()) {
			return;
		}

		File[] files = file.listFiles();
		if (files == null) {
			return;
		}

		boolean snd = false;
		boolean gfx = false;

		for (File f : files) {
			if (f.isDirectory()) {
				list.add(new FilesystemTreeNode(f));
				if ("gfx".equalsIgnoreCase(f.getName())) {
					gfx = true;
				} else if ("snd".equalsIgnoreCase(f.getName())) {
					snd = true;
				}
			}
		}
		settlerFolder = snd && gfx;
		Collections.sort(list, new Comparator<FilesystemTreeNode>() {

			@Override
			public int compare(FilesystemTreeNode o1, FilesystemTreeNode o2) {
				return o1.getFile().getName().compareTo(o2.getFile().getName());
			}
		});
	}

	/**
	 * Apply the loaded children
	 */
	public void applyLoadedChildren() {
		children = list;
	}

}
