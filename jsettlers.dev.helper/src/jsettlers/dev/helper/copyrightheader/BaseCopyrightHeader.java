package jsettlers.dev.helper.copyrightheader;

import java.io.File;
import java.io.IOException;

/**
 * Base class for copyright header operations
 * 
 * @author Andreas Butti
 */
public abstract class BaseCopyrightHeader {

	/**
	 * Projects to update
	 */
	private static final String[] projects = { "jsettlers.mapcreator" };

	/**
	 * Relative root folder of all projects
	 */
	private static final String PROJECT_ROOT = "../";

	/**
	 * Constructor
	 */
	public BaseCopyrightHeader() {
	}

	/**
	 * Execute this action
	 * 
	 * @throws IOException
	 */
	protected void execute() throws IOException {
		for (String p : projects) {
			updateProject(p);
		}
	}

	/**
	 * Update one specific project
	 * 
	 * @param project
	 *            The Project
	 * @throws IOException
	 */
	private void updateProject(String project) throws IOException {
		System.out.println("Scanning project " + project);
		File projectRoot = new File(PROJECT_ROOT + project);
		System.out.println(projectRoot.getAbsolutePath());
		listFilesRecursive(projectRoot);
	}

	/**
	 * List all files recursive
	 * 
	 * @param root
	 *            Root to list
	 * @throws IOException
	 */
	private void listFilesRecursive(File root) throws IOException {
		if (!root.exists()) {
			return;
		}

		if (root.isDirectory()) {
			for (File dir : root.listFiles()) {
				listFilesRecursive(dir);
			}
		}

		if (root.isFile()) {
			if (root.getName().endsWith(".java")) {
				updateJavaFile(root);
			}
		}
	}

	/**
	 * Update a java file
	 * 
	 * @param file
	 *            The file to update
	 * @throws IOException
	 */
	protected void updateJavaFile(File file) throws IOException {
		System.out.println("File " + file);
		SourceFile sf = new SourceFile(file);
		updateJavaFile(sf);
	}

	/**
	 * Update the java file
	 * 
	 * @param sf
	 *            File
	 * @throws IOException
	 */
	protected abstract void updateJavaFile(SourceFile sf) throws IOException;

}
