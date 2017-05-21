/*******************************************************************************
 * Copyright (c) 2015
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
package jsettlers.mapcreator.main.tools;

import java.util.LinkedList;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import jsettlers.mapcreator.tools.ToolBox;
import jsettlers.mapcreator.tools.ToolNode;

/**
 * This is a tree of tools
 * 
 * @author michael
 */
public class ToolTreeModel implements TreeModel {

	/**
	 * Root node
	 */
	private final ToolNode root;

	/**
	 * Constructor
	 * 
	 * @param root
	 *            Root node
	 */
	public ToolTreeModel(ToolNode root) {
		this.root = root;
	}

	private final LinkedList<TreeModelListener> listeners = new LinkedList<>();

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof ToolBox) {
			return ((ToolBox) parent).getTool(index);
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof ToolBox) {
			return ((ToolBox) parent).getToolLength();
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof ToolBox) {
			ToolBox tb = ((ToolBox) parent);

			for (int i = 0; i < tb.getToolLength(); i++) {
				ToolNode t = tb.getTool(i);
				if (t.equals(child)) {
					return i;
				}
			}
		}
		return 0;
	}

	@Override
	public Object getRoot() {
		return root;
	}

	@Override
	public boolean isLeaf(Object node) {
		return !(node instanceof ToolBox);
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		listeners.remove(l);
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		throw new UnsupportedOperationException();
	}

}
