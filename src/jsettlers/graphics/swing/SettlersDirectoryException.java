package jsettlers.graphics.swing;

import java.io.File;
import java.io.IOException;

public class SettlersDirectoryException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2543837213112240683L;
	private final File expectedSnd;
	private final File expectedGfx;
	private final boolean badVersion;
	
	public SettlersDirectoryException(File expectedSnd, File expectedGfx, boolean isBadVersion) {
		this.expectedSnd = expectedSnd;
		this.expectedGfx = expectedGfx;
		this.badVersion = isBadVersion;
	}

	public File getExpectedSnd() {
		return expectedSnd;
	}

	public File getExpectedGfx() {
		return expectedGfx;
	}
	
	public boolean isBadVersion() {
		return badVersion;
	}
}
