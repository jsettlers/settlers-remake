package jsettlers.main.swing.foldertree;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.swing.tree.TreeNode;

import jsettlers.graphics.swing.resources.SettlerFolderCheck;

/**
 * Node to display file / folders
 * 
 * @author Andreas Butti
 */
public class FilesystemTreeNode implements TreeNode {

	/**
	 * If the children are already loaded
	 */
	protected boolean loaded = false;

	/**
	 * If this is a settler folder
	 */
	private boolean settlerFolder = false;

	/**
	 * To expand the node on the first selection
	 */
	private boolean wasExpanded = false;

	/**
	 * Tmp loaded child list
	 */
	private final List<FilesystemTreeNode> list = new ArrayList<>();

	/**
	 * Current child list
	 */
	private List<FilesystemTreeNode> children = new ArrayList<>();

	/**
	 * Parent node
	 */
	private TreeNode parent;

	/**
	 * The file represented by this node
	 */
	private final File file;

	/**
	 * Constructor
	 * 
	 * @param file
	 *            The file to represent
	 */
	public FilesystemTreeNode(File file) {
		this.file = file;
	}

	/**
	 * @return The file
	 */
	public File getFile() {
		return file;
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
		return children.size();
	}

	/**
	 * Add a node
	 * 
	 * @param node
	 *            Node
	 */
	public void add(FilesystemTreeNode node) {
		children.add(node);
	}

	/**
	 * Load the children to a tmp attribute, **this is the only one method which is called from another thread**
	 */
	public void loadChildren1() {
		if (file == null) {
			return;
		}

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

		if (snd && gfx) {
			SettlerFolderCheck check = new SettlerFolderCheck();
			settlerFolder = check.check(file.getAbsolutePath());
		} else {
			settlerFolder = false;
		}

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

	@Override
	public TreeNode getChildAt(int childIndex) {
		return children.get(childIndex);
	}

	@Override
	public TreeNode getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            The parent tree node
	 */
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}

	@Override
	public int getIndex(TreeNode node) {
		return children.indexOf(node);
	}

	@Override
	public boolean getAllowsChildren() {
		return true;
	}

	@Override
	public Enumeration<?> children() {
		return new Enumeration<Object>() {

			/**
			 * Iterator
			 */
			private final Iterator<FilesystemTreeNode> it = children.iterator();

			@Override
			public boolean hasMoreElements() {
				return it.hasNext();
			}

			@Override
			public Object nextElement() {
				return it.next();
			}
		};
	}

}
