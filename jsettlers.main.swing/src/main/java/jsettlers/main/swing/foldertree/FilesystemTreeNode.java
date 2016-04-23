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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.tree.TreeNode;

import jsettlers.main.swing.resources.SettlersFolderChecker;

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
	private boolean settlersFolder = false;

	/**
	 * To expand the node on the first selection
	 */
	private boolean wasExpanded = false;

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
			this.children = calculateChildrenNodes();
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
	public boolean isSettlersFolder() {
		return settlersFolder;
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
		node.setParent(this);
	}

	/**
	 * Calculates the children and returns them.
	 * 
	 * @return
	 */
	private List<FilesystemTreeNode> calculateChildrenNodes() {
		if (file == null) {
			return Collections.emptyList();
		}

		if (!file.isDirectory()) {
			return Collections.emptyList();
		}

		File[] files = file.listFiles();
		if (files == null) {
			return Collections.emptyList();
		}

		boolean snd = false;
		boolean gfx = false;

		List<FilesystemTreeNode> list = new ArrayList<FilesystemTreeNode>();

		for (File file : files) {
			if (file.isDirectory()) {
				FilesystemTreeNode node = new FilesystemTreeNode(file);
				list.add(node);
				node.setParent(this);
				if ("gfx".equalsIgnoreCase(file.getName())) {
					gfx = true;
				} else if ("snd".equalsIgnoreCase(file.getName())) {
					snd = true;
				}
			}
		}

		if (snd && gfx) {
			settlersFolder = SettlersFolderChecker.checkSettlersFolder(file.getAbsolutePath()).isValidSettlersFolder();
		} else {
			settlersFolder = false;
		}

		Collections.sort(list, new Comparator<FilesystemTreeNode>() {
			@Override
			public int compare(FilesystemTreeNode o1, FilesystemTreeNode o2) {
				return o1.getFile().getName().compareTo(o2.getFile().getName());
			}
		});

		return list;
	}

	public void loadChildrenNodesAsync() {
		final List<FilesystemTreeNode> childrenNodes = this.calculateChildrenNodes();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				FilesystemTreeNode.this.children = childrenNodes;
				findRoot().nodeStructureChanged(FilesystemTreeNode.this);
			}
		});
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
		return Collections.enumeration(children);
	}
}
