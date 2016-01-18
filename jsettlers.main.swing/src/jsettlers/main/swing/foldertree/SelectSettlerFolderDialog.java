package jsettlers.main.swing.foldertree;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * Select settler root folder dialog
 * 
 * @author Andreas Butti
 */
public class SelectSettlerFolderDialog extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * Tree model
	 */
	private DefaultTreeModel model;

	/**
	 * The tree
	 */
	private JTree tree;

	/**
	 * Listener for Tree selection
	 */
	private final TreeSelectionListener selectionListener = new TreeSelectionListener() {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TreePath path = e.getPath();
			Object last = path.getLastPathComponent();
			if (last instanceof FilesystemTreeNode) {
				FilesystemTreeNode ft = (FilesystemTreeNode) last;
				if (!ft.wasExpanded()) {
					ft.setWasExpanded(true);
					tree.expandPath(path);
				}
			}
		}
	};

	/**
	 * Listener for expansion
	 */
	private final TreeExpansionListener expansionListener = new TreeExpansionListener() {

		@Override
		public void treeExpanded(TreeExpansionEvent event) {
			Object last = event.getPath().getLastPathComponent();
			if (last instanceof FilesystemTreeNode) {
				FilesystemTreeNode ft = (FilesystemTreeNode) last;
				ft.setWasExpanded(true);
			}
		}

		@Override
		public void treeCollapsed(TreeExpansionEvent event) {
		}
	};

	/**
	 * Constructor
	 */
	public SelectSettlerFolderDialog() {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("JSettlers - Siedler Ordner wählen");
		setLayout(new BorderLayout());

		JPanel pHeader = new JPanel();
		pHeader.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pHeader.setLayout(new BorderLayout());
		pHeader.add(new JLabel("Wählen Sie den Ordner der Sielder III Installation"), BorderLayout.CENTER);
		JButton btHelp = new JButton("Ich brauche Hilfe");
		pHeader.add(btHelp, BorderLayout.EAST);

		add(pHeader, BorderLayout.NORTH);

		initTree();

		add(new JScrollPane(tree), BorderLayout.CENTER);

		JPanel pFooter = new JPanel();
		add(pFooter, BorderLayout.SOUTH);

		setSize(640, 640);
		setLocationRelativeTo(null);
	}

	/**
	 * Initialize the Tree with the filesystem
	 */
	private void initTree() {
		RootTreeNode root = new RootTreeNode();

		for (File f : File.listRoots()) {
			root.add(new FilesystemTreeNode(f));
		}

		model = new DefaultTreeModel(root);

		// to fire change event when the loading is finished
		root.setModel(model);
		tree = new JTree(model);
		tree.addTreeSelectionListener(selectionListener);
		tree.addTreeExpansionListener(expansionListener);
		tree.setCellRenderer(new FileTreeCellRenderer());
		tree.setRootVisible(false);
		tree.expandRow(0);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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
