package jsettlers.dev.helper.copyrightheader;

import java.io.IOException;

/**
 * Installs the copyright header where missing, does not touch files which already contains a copyright
 * 
 * @author Andreas Butti
 */
public class InstallCopyrightHeader extends BaseCopyrightHeader {

	/**
	 * Constructor
	 */
	public InstallCopyrightHeader() {
	}

	@Override
	protected void updateJavaFile(SourceFile sf) throws IOException {
		if (sf.containsHeader()) {
			return;
		}

		System.out.println("> Install header");
		sf.installHeader();
	}

	/**
	 * Entry point to execute this action
	 * 
	 * @param args
	 *            not used
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new InstallCopyrightHeader().execute();
	}

}
