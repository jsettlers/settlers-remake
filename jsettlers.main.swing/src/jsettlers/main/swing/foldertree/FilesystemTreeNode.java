package jsettlers.main.swing.foldertree;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Node to display file / folders
 * 
 * @author Andreas Butti
 */
public class FilesystemTreeNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = 1L;

	/**
	 * If the children are already loaded
	 */
	private boolean loaded = false;

	/**
	 * If this is a settler folder
	 */
	private boolean settlerFolder = false;

	/**
	 * Constructor
	 * 
	 * @param file
	 *            The file to represent
	 */
	public FilesystemTreeNode(File file) {
		super(file);
		if (file == null) {
			throw new IllegalArgumentException("file == null");
		}
	}

	/**
	 * @return The file
	 */
	public File getFile() {
		return (File) getUserObject();
	}

	/**
	 * Load the children of this folder, if a folder
	 */
	@SuppressWarnings("unchecked")
	private void loadChildren() {
		loaded = true;
		File file = getFile();

		if (!file.isDirectory()) {
			return;
		}

		File[] files = file.listFiles();
		if (files == null) {
			return;
		}

		boolean snd = false;
		boolean gfx = false;

		for (File f : files) {
			if (f.isDirectory()) {
				add(new FilesystemTreeNode(f));
				if ("gfx".equalsIgnoreCase(f.getName())) {
					gfx = true;
				} else if ("snd".equalsIgnoreCase(f.getName())) {
					snd = true;
				}
			}
		}
		if (children == null) {
			return;
		}

		settlerFolder = snd && gfx;
		Collections.sort(children, new Comparator<FilesystemTreeNode>() {

			@Override
			public int compare(FilesystemTreeNode o1, FilesystemTreeNode o2) {
				return o1.getFile().getName().compareTo(o2.getFile().getName());
			}
		});
	}

	/**
	 * @return if this is a settler folder
	 */
	public boolean isSettlerFolder() {
		return settlerFolder;
	}

	@Override
	public boolean isLeaf() {
		if (!loaded) {
			loadChildren();
		}
		return super.isLeaf();
	}

}
