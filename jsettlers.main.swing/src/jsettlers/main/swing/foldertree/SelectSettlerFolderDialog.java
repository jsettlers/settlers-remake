package jsettlers.main.swing.foldertree;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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

import jsettlers.graphics.localization.Labels;
import jsettlers.graphics.swing.resources.SettlerFolderCheck;

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
	 * Help URL: TODO Enter a valid help URL!
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
	};

	/**
	 * Panel with the current path
	 */
	private final PathPanel pathPanel = new PathPanel(listener);

	/**
	 * Choosed folder
	 */
	private String selectedFolder = null;

	/**
	 * Panel to display found result
	 */
	private final FolderFoundPanel foundPanel = new FolderFoundPanel(new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			selectedFolder = e.getActionCommand();
			synchronized (syncObject) {
				syncObject.notifyAll();
			}
			dispose();
		}
	});

	/**
	 * For synchronization only
	 */
	private final Object syncObject = new Object();

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
		setTitle(Labels.getString("select-settlers-3-folder-header"));
		setLayout(new BorderLayout());

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				synchronized (syncObject) {
					syncObject.notifyAll();
				}
			}
		});

		initHeader();
		initTree();

		add(new JScrollPane(tree), BorderLayout.CENTER);

		add(foundPanel, BorderLayout.SOUTH);

		setSize(750, 640);
		setLocationRelativeTo(null);
	}

	/**
	 * Initialize the header panel with the label, help button and go to button
	 */
	private void initHeader() {
		JPanel pHeader = new JPanel();
		pHeader.setLayout(new BorderLayout());

		JPanel pHeaderText = new JPanel();
		pHeaderText.setLayout(new BorderLayout());
		pHeaderText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pHeaderText.add(new JLabel(Labels.getString("select-settlers-3-folder")), BorderLayout.CENTER);

		JPanel pButton = new JPanel();
		pButton.setLayout(new FlowLayout());

		JButton btGoTo = new JButton(Labels.getString("enter-path"));
		btGoTo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String path = JOptionPane.showInputDialog(SelectSettlerFolderDialog.this, Labels.getString("enter-path"));
				if (path != null) {
					SettlerFolderCheck check = new SettlerFolderCheck();
					if (check.check(path)) {
						foundPanel.setFolder(path);
					} else {
						JOptionPane.showMessageDialog(SelectSettlerFolderDialog.this, Labels.getString("settlers-folder-still-invalid"), "JSettler",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		pButton.add(btGoTo);

		JButton btHelp = new JButton(Labels.getString("i-need-help-button"));
		btHelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(new URI(HELP_URL));
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(SelectSettlerFolderDialog.this, Labels.getString("error-open-url") + ": " + HELP_URL);
				}
			}
		});
		pButton.add(btHelp);

		pHeaderText.add(pButton, BorderLayout.EAST);

		pHeader.add(pHeaderText, BorderLayout.CENTER);
		pHeader.add(pathPanel, BorderLayout.SOUTH);

		add(pHeader, BorderLayout.NORTH);

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

	/**
	 * Blocks the caller thread, returns when the user confirmed the dialog
	 * 
	 * @return Selected file
	 */
	public File waitForUserInput() {
		synchronized (syncObject) {
			try {
				syncObject.wait();
			} catch (InterruptedException e) {
				// ignore exception here
			}
		}

		if (selectedFolder == null) {
			return null;
		}
		return new File(selectedFolder);
	}
}
