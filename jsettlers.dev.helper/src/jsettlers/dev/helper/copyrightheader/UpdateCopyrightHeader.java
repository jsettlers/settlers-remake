package jsettlers.dev.helper.copyrightheader;

import java.io.IOException;

/**
 * Updates the copyright header to the current version, replaces the header where existing
 * 
 * Touches every file!
 * 
 * @author Andreas Butti
 */
public class UpdateCopyrightHeader extends BaseCopyrightHeader {

	/**
	 * Constructor
	 */
	public UpdateCopyrightHeader() {
	}

	@Override
	protected void updateJavaFile(SourceFile sf) {
		// TODO Auto-generated method stub

	}

	/**
	 * Entry point to execute this action
	 * 
	 * @param args
	 *            not used
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		new UpdateCopyrightHeader().execute();
	}

}
