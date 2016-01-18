package jsettlers.main.swing.foldertree;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.tree.DefaultMutableTreeNode;

import jsettlers.graphics.localization.Labels;

/**
 * Select settler root folder dialog
 * 
 * @author Andreas Butti
 */
public class SelectSettlerFolderDialog extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public SelectSettlerFolderDialog() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle(Labels.getString("select-settlers-3-folder"));
		setLayout(new BorderLayout());

		JTree tree = initTree();

		add(new JScrollPane(tree), BorderLayout.CENTER);

		JPanel pFooter = new JPanel();
		add(pFooter, BorderLayout.SOUTH);

		setSize(480, 640);
		setLocationRelativeTo(null);
	}

	/**
	 * Initialize the Tree with the filesystem
	 * 
	 * @return
	 */
	private JTree initTree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode(null);

		for (File f : File.listRoots()) {
			root.add(new FilesystemTreeNode(f));
		}

		JTree tree = new JTree(root);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.setRootVisible(false);
		tree.expandRow(0);

		return tree;
	}

	/**
	 * Test main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
		}

		SelectSettlerFolderDialog dlg = new SelectSettlerFolderDialog();
		dlg.setVisible(true);
	}
}
