package jsettlers.mapcreator.main;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.TreeCellRenderer;

import jsettlers.mapcreator.tools.ToolNode;

public class ToolRenderer implements TreeCellRenderer {
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
	        boolean selected, boolean expanded, boolean arg4, int arg5, boolean arg6) {
		String name = "";
		if (value instanceof ToolNode) {
			name = ((ToolNode) value).getName();
		}
		JTextField jTextField = new JTextField(name);
		if (selected) {			
			jTextField.setForeground(Color.WHITE);
			jTextField.setBackground(new Color(0x0343df));
		}
		return jTextField;
	}

}
