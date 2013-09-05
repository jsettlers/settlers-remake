package jsettlers.graphics.swing;

import java.io.IOException;
import java.util.Arrays;

/**
 * 
 * 
 * @author michael
 * @author Andreas Eberle
 * 
 * 
 */
public class InvalidSettlersDirectoryException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2543837213112240683L;
	private final String[] expectedSnds;
	private final String[] expectedGfxs;
	private final boolean badVersion;

	public InvalidSettlersDirectoryException(String[] expectedSnd, String[] expectedGfx, boolean isBadVersion) {
		this.expectedSnds = expectedSnd;
		this.expectedGfxs = expectedGfx;
		this.badVersion = isBadVersion;
	}

	public String[] getExpectedSnds() {
		return expectedSnds;
	}

	public String[] getExpectedGfxs() {
		return expectedGfxs;
	}

	public boolean isBadVersion() {
		return badVersion;
	}

	@Override
	public String toString() {
		return "SettlersDirectoryException [expectedSnds=" + Arrays.toString(expectedSnds) + ", expectedGfxs=" + Arrays.toString(expectedGfxs)
				+ ", badVersion=" + badVersion + "]";
	}
}
