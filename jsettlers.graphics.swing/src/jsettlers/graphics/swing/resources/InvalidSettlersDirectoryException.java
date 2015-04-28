/*******************************************************************************
 * Copyright (c) 2015
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *******************************************************************************/
package jsettlers.graphics.swing.resources;

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
