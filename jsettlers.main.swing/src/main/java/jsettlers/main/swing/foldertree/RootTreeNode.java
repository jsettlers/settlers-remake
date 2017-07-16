/*******************************************************************************
 * Copyright (c) 2015 - 2017
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

import java.util.concurrent.ExecutorService;

import javax.swing.tree.DefaultTreeModel;

/**
 * Root node of the tree, with the functionality to load from filesystem and display blocking panel
 * 
 * Usually control operation and model should not be mixed, but in this case it's the easyest solution, and its only data loading, not really
 * controlling
 * 
 * @author Andreas Butti
 */
public class RootTreeNode extends FilesystemTreeNode {

	private final ExecutorService executorService;
	/**
	 * Tree model to fire update events
	 */
	private DefaultTreeModel model;

	/**
	 * Constructor
	 */
	public RootTreeNode(ExecutorService executorService) {
		super(null);
		this.executorService = executorService;
		loaded = true;
	}

	/**
	 * @param model
	 *            Tree model to fire update events
	 */
	public void setModel(DefaultTreeModel model) {
		this.model = model;
	}

	/**
	 * Does the loading task asynchronously
	 * 
	 * @param node
	 *            Node to load
	 */
	public void loadAsynchron(final FilesystemTreeNode node) {
		executorService.submit(node::loadChildrenNodesAsync);
	}

	public void nodeStructureChanged(FilesystemTreeNode node) {
		model.nodeStructureChanged(node);
	}
}
