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

	private final ToolNode root;

	public ToolTreeModel(ToolNode root) {
		this.root = root;
	}

	private final LinkedList<TreeModelListener> listeners =
			new LinkedList<TreeModelListener>();

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		listeners.add(l);
	}

	@Override
	public Object getChild(Object parent, int index) {
		if (parent instanceof ToolBox) {
			return ((ToolBox) parent).getTools()[index];
		}
		return null;
	}

	@Override
	public int getChildCount(Object parent) {
		if (parent instanceof ToolBox) {
			return ((ToolBox) parent).getTools().length;
		}
		return 0;
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		if (parent instanceof ToolBox) {
			int i = 0;
			for (ToolNode t : ((ToolBox) parent).getTools()) {
				if (t == child) {
					return i;
				}
				i++;
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
