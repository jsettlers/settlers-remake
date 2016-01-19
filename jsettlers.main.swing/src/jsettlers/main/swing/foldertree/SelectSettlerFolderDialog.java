package jsettlers.main.swing.foldertree;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
	 * Help URL
	 */
	private static final String HELP_URL = "https://github.com/jsettlers/settlers-remake/wiki";

	/**
	 * Listener for the Path panel
	 */
	private final PathPanelListener listener = new PathPanelListener() {

		@Override
		public void jumpTo(Object[] pathToJumpTo) {
			tree.setSelectionPath(new TreePath(pathToJumpTo));
		}

		@Override
		public void goToHome() {
			tree.setSelectionInterval(0, 0);
		}
	};

	/**
	 * Panel with the current path
	 */
	private final PathPanel pathPanel = new PathPanel(listener);

	/**
	 * Panel to display found result
	 */
	private final FolderFoundPanel foundPanel = new FolderFoundPanel();

	/**
	 * Listener for Tree selection
	 */
	private final TreeSelectionListener selectionListener = new TreeSelectionListener() {

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			TreePath path = e.getPath();
			pathPanel.setPath(path.getPath());

			Object last = path.getLastPathComponent();
			if (last instanceof FilesystemTreeNode) {
				FilesystemTreeNode ft = (FilesystemTreeNode) last;
				if (!ft.wasExpanded()) {
					ft.setWasExpanded(true);
					tree.expandPath(path);
				}

				if (ft.isSettlerFolder()) {
					foundPanel.setFolder(ft.getFile().getAbsolutePath());
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
		pHeader.setLayout(new BorderLayout());

		JPanel pHeaderText = new JPanel();
		pHeaderText.setLayout(new BorderLayout());
		pHeaderText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pHeaderText.add(new JLabel("Wählen Sie den Ordner der Sielder III Installation"), BorderLayout.CENTER);
		JButton btHelp = new JButton("Ich brauche Hilfe");
		btHelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(HELP_URL));
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(SelectSettlerFolderDialog.this, "Could not open URL: " + HELP_URL);
				}
			}
		});
		pHeaderText.add(btHelp, BorderLayout.EAST);

		pHeader.add(pHeaderText, BorderLayout.CENTER);
		pHeader.add(pathPanel, BorderLayout.SOUTH);

		add(pHeader, BorderLayout.NORTH);

		initTree();

		add(new JScrollPane(tree), BorderLayout.CENTER);

		add(foundPanel, BorderLayout.SOUTH);

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
