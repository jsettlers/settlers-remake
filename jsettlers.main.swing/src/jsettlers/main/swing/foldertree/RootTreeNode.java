package jsettlers.main.swing.foldertree;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Root node of the tree, with the functionality to load from filesystem and display blocking panel
 * 
 * Usually control operation and model should not be mixed, but in this case it's the easyest solution, and its only data loading, not really
 * controlling
 * 
 * @author Andreas Butti
 */
public class RootTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	/**
	 * Threadpool
	 */
	private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {

		@Override
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r, "fs-loader-thread");
			t.setDaemon(true);
			return t;
		}
	});

	/**
	 * Tree model to fire update events
	 */
	private DefaultTreeModel model;

	/**
	 * Constructor
	 */
	public RootTreeNode() {
	}

	/**
	 * @param model
	 *            Tree model to fire update events
	 */
	public void setModel(DefaultTreeModel model) {
		this.model = model;
	}

	/**
	 * Does the loading task asynchron
	 * 
	 * @param node
	 *            Node to load
	 */
	public void loadAsynchron(FilesystemTreeNode node) {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				node.loadChildren1();

				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						node.applyLoadedChildren();
						model.nodeStructureChanged(node);
					}
				});
			}
		});
	}

}
