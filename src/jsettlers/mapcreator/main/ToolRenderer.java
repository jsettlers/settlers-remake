package jsettlers.mapcreator.main;

import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import jsettlers.mapcreator.tools.ToolNode;

public class ToolRenderer implements TreeCellRenderer {

	@Override
	public Component getTreeCellRendererComponent(JTree arg0, Object arg1,
	        boolean arg2, boolean arg3, boolean arg4, int arg5, boolean arg6) {
		String name = "";
		if (arg1 instanceof ToolNode) {
			name = ((ToolNode) arg1).getName();
		}
		return new JTextField(name);
	}

}
